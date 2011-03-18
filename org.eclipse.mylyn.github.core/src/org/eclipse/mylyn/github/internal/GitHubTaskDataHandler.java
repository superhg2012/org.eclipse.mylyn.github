package org.eclipse.mylyn.github.internal;

import static org.eclipse.mylyn.github.internal.GitHubConnectorLogger.createErrorStatus;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.RepositoryResponse.ResponseKind;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMetaData;
import org.eclipse.mylyn.tasks.core.data.TaskCommentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskOperation;

/**
 * 
 * @author Christian Trutz
 */
public class GitHubTaskDataHandler extends AbstractTaskDataHandler {

	private static final String DATA_VERSION = "1";

	private DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();

	private final DateFormat githubDateFormat = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm:ss Z");

	/**
	 * @see org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler#getAttributeMapper(org.eclipse.mylyn.tasks.core.TaskRepository)
	 */
	@Override
	public final TaskAttributeMapper getAttributeMapper(
			TaskRepository taskRepository) {
		TaskAttributeMapper taskAttributeMapper = new GitHubTaskAttributeMapper(
				taskRepository);
		return taskAttributeMapper;
	}

	/**
	 * @see org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler#initializeTaskData(org.eclipse.mylyn.tasks.core.TaskRepository,
	 *      org.eclipse.mylyn.tasks.core.data.TaskData,
	 *      org.eclipse.mylyn.tasks.core.ITaskMapping,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public final boolean initializeTaskData(TaskRepository repository,
			TaskData data, ITaskMapping initializationData,
			IProgressMonitor monitor) throws CoreException {

		data.setVersion(DATA_VERSION);

		for (GitHubTaskAttributes attr : GitHubTaskAttributes.values()) {
			if (attr.isInitTask()) {
				createAttribute(data, attr, null);
			}
		}

		return true;
	}

	/**
	 * Post a task data.
	 * 
	 * @see org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler#postTaskData(org.eclipse.mylyn.tasks.core.TaskRepository,
	 *      org.eclipse.mylyn.tasks.core.data.TaskData, java.util.Set,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public final RepositoryResponse postTaskData(TaskRepository repository,
			TaskData taskData, Set<TaskAttribute> oldAttributes,
			IProgressMonitor monitor) throws CoreException {

		GitHubIssue issue = createIssue(taskData);
		GitHubIssueService issueService = GitHubService
				.getIssueService(repository);
		try {
			if (taskData.isNew()) {
				issue = issueService.create(issue);
			} else {
				TaskAttribute operationAttribute = taskData.getRoot()
						.getAttribute(TaskAttribute.OPERATION);

				GitHubTaskOperation operation = null;

				if (operationAttribute != null) {
					String opId = operationAttribute.getValue();
					operation = GitHubTaskOperation.fromId(opId);

				}
				if (operation != null && operation != GitHubTaskOperation.LEAVE) {
					issueService.update(issue);
					switch (operation) {
					case REOPEN:
						issueService.reopenIssue(issue);
						break;
					case CLOSE:
						issueService.closeIssue(issue);
						break;
					default:
						throw new IllegalStateException("not implemented: "
								+ operation);
					}
				} else {
					issueService.update(issue);
				}
			}
			updateTaskLabels(repository, issue, oldAttributes);
			updateComments(repository, taskData, issue);
			return new RepositoryResponse(
					taskData.isNew() ? ResponseKind.TASK_CREATED
							: ResponseKind.TASK_UPDATED, issue.getNumber());
		} catch (GitHubServiceException e) {
			throw new CoreException(createErrorStatus(e));
		}

	}

	private void updateComments(TaskRepository repository, TaskData taskData,
			GitHubIssue issue) throws GitHubServiceException {
		TaskAttribute newCommentAttribute = taskData.getRoot()
				.getMappedAttribute(TaskAttribute.COMMENT_NEW);
		if (newCommentAttribute != null) {
			String newComment = newCommentAttribute.getValue();
			GitHubComment comment = new GitHubComment();
			comment.setBody(newComment);
			comment.setGravatarId(issue.getGravatarId());
			comment.setUser(issue.getUser());
			comment.setCreatedAt(issue.getCreatedAt());
			comment.setUpdatedAt(issue.getUpdatedAt());
			comment.setId(issue.getNumber());
			GitHubService.getCommentsService(repository).create(comment);

		}

	}

	private void updateTaskLabels(TaskRepository repository, GitHubIssue issue,
			Set<TaskAttribute> oldAttributes) throws GitHubServiceException {
		GitHubLabelsService labelsService = GitHubService
				.getLabelsService(repository);
		List<String> oldLabels = getOldLabelsValue(oldAttributes);
		List<String> currentLabels = issue.getLabels();
		oldLabels.removeAll(currentLabels);
		for (String label : oldLabels) {
			labelsService.deleteLabelFromIssue(label, issue.getNumber());
		}

		for (String label : issue.getLabels()) {
			labelsService.addLabelToIssue(label, issue.getNumber());
		}

	}

	private List<String> getOldLabelsValue(Set<TaskAttribute> oldAttributes) {
		Iterator<TaskAttribute> it = oldAttributes.iterator();
		List<String> labels = new ArrayList<String>();
		while (it.hasNext()) {
			TaskAttribute attribute = it.next();

			if (attribute != null) {
				if (attribute.getId().equalsIgnoreCase(
						GitHubTaskAttributes.LABEL.getId())) {
					String attributeValue = attribute.getValue();
					StringTokenizer st = new StringTokenizer(attributeValue,
							",", false);
					while (st.hasMoreTokens()) {
						labels.add(st.nextToken().trim());
					}
					break;
				}

			}
		}
		return labels;
	}

	/**
	 * Create a partial task
	 * 
	 * @param repository
	 *            - repository instance
	 * @param monitor
	 *            - monitor object
	 * @param user
	 *            - user
	 * @param project
	 *            - project
	 * @param issue
	 *            - issue instance
	 * @return a new task data.
	 * @throws GitHubServiceException
	 */
	public final TaskData createTaskData(TaskRepository repository,
			IProgressMonitor monitor, GitHubIssue issue, boolean isPartialData)
			throws GitHubServiceException {

		TaskData data = new TaskData(getAttributeMapper(repository),
				GitHub.CONNECTOR_KIND, repository.getRepositoryUrl(),
				issue.getNumber());
		data.setVersion(DATA_VERSION);

		createOperations(data, issue);

		createAttribute(data, GitHubTaskAttributes.KEY, issue.getNumber());
		createAttribute(data, GitHubTaskAttributes.TITLE, issue.getTitle());
		createAttribute(data, GitHubTaskAttributes.BODY, issue.getBody());
		createAttribute(data, GitHubTaskAttributes.STATUS, issue.getState());
		createAttribute(data, GitHubTaskAttributes.CREATION_DATE,
				toLocalDate(issue.getCreatedAt()));
		createAttribute(data, GitHubTaskAttributes.MODIFICATION_DATE,
				toLocalDate(issue.getCreatedAt()));
		createAttribute(data, GitHubTaskAttributes.CLOSED_DATE,
				toLocalDate(issue.getClosedAt()));
		createLabelAttribute(data, GitHubTaskAttributes.LABEL,
				issue.getLabels());
		createVotesAttribute(data, GitHubTaskAttributes.VOTES, issue.getVotes());
		createAttribute(data, GitHubTaskAttributes.REPORTED_BY, issue.getUser());
		createAttribute(data, GitHubTaskAttributes.REPORTER_GRAVATAR_ID,
				issue.getGravatarId());
		updateTaskDataWithComments(repository, data, issue);
		createAttribute(data, GitHubTaskAttributes.NEW_COMMENTS, null);
		if (isPartial(data)) {
			data.setPartial(isPartialData);
		}

		return data;
	}

	private void updateTaskDataWithComments(TaskRepository repository,
			TaskData data, GitHubIssue issue) throws GitHubServiceException {
		// Initialize a counter, since you want to number each task, since the
		// editor part likes to display
		// them with numbers.
		int count = 0;
		GitHubComments comments = null;
		comments = GitHubService.getCommentsService(repository).retrieve(
				issue.getNumber());
		// Loop through the comments in your native database.
		for (GitHubComment comment : comments.getComments()) {
			TaskCommentMapper mapper = new TaskCommentMapper(); // Create a new
																// one each
																// time, to be
																// safe.
			// Set properties and text associated with this comment.

			mapper.setAuthor(repository.createPerson(comment.getUser()));
			String createdAt = comment.getCreatedAt();
			Date createdAtDate = new Date();
			try {
				createdAtDate = githubDateFormat.parse(createdAt);
			} catch (ParseException e) {
				// ignore for now.
			}
			mapper.setCreationDate(createdAtDate);
			mapper.setText(comment.getBody());
			mapper.setNumber(count);

			// Create, in the task data object, a new attribute that will hold
			// this comment.
			TaskAttribute attribute = data.getRoot().createAttribute(
					TaskAttribute.PREFIX_COMMENT + count);

			// Ask the mapper to copy the properties and text into the new
			// attribute.
			mapper.applyTo(attribute);

			count++;
		}

	}

	private void createVotesAttribute(TaskData data,
			GitHubTaskAttributes attribute, Integer value) {
		TaskAttribute attr = data.getRoot().createAttribute(attribute.getId());
		TaskAttributeMetaData metaData = attr.getMetaData();
		metaData.defaults().setType(attribute.getType())
				.setKind(attribute.getKind()).setLabel(attribute.getLabel())
				.setReadOnly(attribute.isReadOnly());

		if (value != null) {
			attr.setValue(String.valueOf(value));
		}

	}

	private void createLabelAttribute(TaskData data,
			GitHubTaskAttributes attribute, List<String> labels) {
		TaskAttribute attr = data.getRoot().createAttribute(attribute.getId());
		TaskAttributeMetaData metaData = attr.getMetaData();
		metaData.defaults().setType(attribute.getType())
				.setKind(attribute.getKind()).setLabel(attribute.getLabel())
				.setReadOnly(attribute.isReadOnly());

		if (labels != null) {
			attr.setValues(labels);
		}

	}

	private boolean isPartial(TaskData data) {
		for (GitHubTaskAttributes attribute : GitHubTaskAttributes.values()) {
			if (attribute.isRequiredForFullTaskData()) {
				TaskAttribute taskAttribute = data.getRoot().getAttribute(
						attribute.getId());
				if (taskAttribute == null) {
					return true;
				}
			}
		}
		return false;
	}

	private void createOperations(TaskData data, GitHubIssue issue) {
		TaskAttribute operationAttribute = data.getRoot().createAttribute(
				TaskAttribute.OPERATION);
		operationAttribute.getMetaData().setType(TaskAttribute.TYPE_OPERATION);
		if (!data.isNew() && issue.getState() != null) {
			addOperation(data, issue, GitHubTaskOperation.LEAVE, true);
			if (issue.getState().equals("open")) {
				addOperation(data, issue, GitHubTaskOperation.CLOSE, false);
			} else if (issue.getState().equals("closed")) {
				addOperation(data, issue, GitHubTaskOperation.REOPEN, false);
			}
		}
	}

	private void addOperation(TaskData data, GitHubIssue issue,
			GitHubTaskOperation operation, boolean asDefault) {
		TaskAttribute attribute = data.getRoot().createAttribute(
				TaskAttribute.PREFIX_OPERATION + operation.getId());
		String label = createOperationLabel(issue, operation);
		TaskOperation.applyTo(attribute, operation.getId(), label);

		if (asDefault) {
			TaskAttribute operationAttribute = data.getRoot().getAttribute(
					TaskAttribute.OPERATION);
			TaskOperation.applyTo(operationAttribute, operation.getId(), label);
		}
	}

	private String createOperationLabel(GitHubIssue issue,
			GitHubTaskOperation operation) {
		return operation == GitHubTaskOperation.LEAVE ? operation.getLabel()
				+ issue.getState() : operation.getLabel();
	}

	private String toLocalDate(String date) {
		String localDate = date;
		if (date != null && date.trim().length() > 0) {
			// expect "2010/02/02 22:58:39 -0800"
			try {
				Date d = githubDateFormat.parse(date);
				localDate = dateFormat.format(d);
			} catch (ParseException e) {
				// ignore
			}
		}
		return localDate;
	}

	private String toGitHubDate(TaskData taskData, GitHubTaskAttributes attr) {
		TaskAttribute attribute = taskData.getRoot().getAttribute(attr.name());
		String value = attribute == null ? null : attribute.getValue();
		if (value != null) {
			try {
				Date d = dateFormat.parse(value);
				value = githubDateFormat.format(d);
			} catch (ParseException e) {
				// ignore
			}
		}
		return value;
	}

	private GitHubIssue createIssue(TaskData taskData) {
		GitHubIssue issue = new GitHubIssue();
		if (!taskData.isNew()) {
			issue.setNumber(taskData.getTaskId());
		}
		issue.setBody(getAttributeValue(taskData, GitHubTaskAttributes.BODY));
		issue.setTitle(getAttributeValue(taskData, GitHubTaskAttributes.TITLE));
		issue.setState(getAttributeValue(taskData, GitHubTaskAttributes.STATUS));
		issue.setCreatedAt(toGitHubDate(taskData,
				GitHubTaskAttributes.CREATION_DATE));
		issue.setCreatedAt(toGitHubDate(taskData,
				GitHubTaskAttributes.MODIFICATION_DATE));
		issue.setCreatedAt(toGitHubDate(taskData,
				GitHubTaskAttributes.CLOSED_DATE));
		issue.setLabels(toGitHubLabel(taskData, GitHubTaskAttributes.LABEL));
		return issue;
	}

	private List<String> toGitHubLabel(TaskData taskData,
			GitHubTaskAttributes attr) {
		TaskAttribute attribute = taskData.getRoot().getAttribute(attr.getId());
		String value = attribute == null ? "" : attribute.getValue();
		List<String> labels = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(value, ",", false);
		while (st.hasMoreTokens()) {
			labels.add(st.nextToken().trim());
		}
		return labels;
	}

	private String getAttributeValue(TaskData taskData,
			GitHubTaskAttributes attr) {
		TaskAttribute attribute = taskData.getRoot().getAttribute(attr.getId());
		return attribute == null ? null : attribute.getValue();
	}

	private void createAttribute(TaskData data, GitHubTaskAttributes attribute,
			String value) {
		TaskAttribute attr = data.getRoot().createAttribute(attribute.getId());
		TaskAttributeMetaData metaData = attr.getMetaData();
		metaData.defaults().setType(attribute.getType())
				.setKind(attribute.getKind()).setLabel(attribute.getLabel())
				.setReadOnly(attribute.isReadOnly());

		if (value != null) {
			attr.addValue(value);
		}
	}

}
