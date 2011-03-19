package org.eclipse.mylyn.github.internal;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;

/**
 * Maps GitHub attributes to task attributes.
 * 
 * @author Gabriel Ciuloaica (gciuloaica@gmail.com)
 */
public final class GitHubTaskAttributeMapper extends TaskAttributeMapper {

	private DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
	private static final Log LOG = LogFactory
			.getLog(GitHubTaskAttributeMapper.class);

	private List<String> labels;

	/**
	 * Create a new task attribute mapper for a specified repository
	 * 
	 * @param taskRepository
	 */
	public GitHubTaskAttributeMapper(TaskRepository taskRepository) {
		super(taskRepository);
	}

	/**
	 * Get the key of the attributes map.
	 * 
	 * @see org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper#mapToRepositoryKey(org.eclipse.mylyn.tasks.core.data.TaskAttribute,
	 *      java.lang.String)
	 */
	@Override
	public String mapToRepositoryKey(TaskAttribute parent, String key) {
		return key;
	}

	/**
	 * Get the value of a date attribute properly formatted.
	 * 
	 * @see org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper#getDateValue(org.eclipse.mylyn.tasks.core.data.TaskAttribute)
	 */
	@Override
	public Date getDateValue(TaskAttribute attribute) {
		String value = attribute.getValue();
		if (value != null) {
			try {
				return dateFormat.parse(value);
			} catch (ParseException e) {
				return super.getDateValue(attribute);
			}
		}
		return null;
	}

	/**
	 * Provide the available list of labels from repository as options for Task
	 * Attribute
	 * 
	 * @see org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper#getOptions(org.eclipse.mylyn.tasks.core.data.TaskAttribute)
	 */
	@Override
	public Map<String, String> getOptions(TaskAttribute attribute) {
		TaskAttribute mappedLabelAttribute = attribute.getTaskData().getRoot()
				.getMappedAttribute(GitHub.GITHUB_TASK_LABEL);
		if (mappedLabelAttribute != null) {
			if (labels == null) {
				gatherLabels();
			}
			if (labels != null && (!labels.isEmpty())) {
				Map<String, String> newLabels = new LinkedHashMap<String, String>();
				for (String label : labels) {
					newLabels.put(label, label);
				}
				return newLabels;
			}
		}
		return super.getOptions(attribute);
	}

	/**
	 * Gather labels from server.
	 */
	private void gatherLabels() {
		try {
			labels = GitHubService.getLabelsService(getTaskRepository())
					.retrieve();
		} catch (GitHubServiceException e) {
			LOG.error("Failed to retrieve labels from server." + e.getMessage());
		}
	}

}
