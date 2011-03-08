package org.eclipse.mylyn.github.internal;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
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
	private final TaskRepository taskRepository;
	private static final Log LOG = LogFactory
			.getLog(GitHubTaskAttributeMapper.class);

	public GitHubTaskAttributeMapper(TaskRepository taskRepository) {
		super(taskRepository);
		this.taskRepository = taskRepository;
	}

	@Override
	public String mapToRepositoryKey(TaskAttribute parent, String key) {
		return key;
	}

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

	@Override
	public Map<String, String> getOptions(TaskAttribute attribute) {
		TaskAttribute mappedLabelAttribute = attribute.getTaskData().getRoot()
				.getMappedAttribute(GitHub.GITHUB_TASK_LABEL);
		if (mappedLabelAttribute != null
				&& mappedLabelAttribute.getValue().length() > 0) {
			GitHubService service = new GitHubService();
			String labels[] = null;
			try {
				labels = service.retrieveLabels(taskRepository);
			} catch (GitHubServiceException e) {
				LOG.error("Failed to retrieve labels from server."
						+ e.getMessage());
			}

			if (labels != null && labels.length > 0) {
				Map<String, String> newLabels = new LinkedHashMap<String, String>();
				for (String label : labels) {
					newLabels.put(label, label);
				}
				return newLabels;
			}
		}
		return super.getOptions(attribute);
	}

}
