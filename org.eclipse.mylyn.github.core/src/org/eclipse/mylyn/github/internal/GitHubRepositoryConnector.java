/*
 * Copyright 2009 Christian Trutz 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *  
 */
package org.eclipse.mylyn.github.internal;

import static org.eclipse.mylyn.github.internal.GitHubConnectorLogger.createErrorStatus;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;
import org.eclipse.mylyn.tasks.core.sync.ISynchronizationSession;

/**
 * GitHub connector.
 * 
 * @author Christian Trutz
 */
public class GitHubRepositoryConnector extends AbstractRepositoryConnector {

	/**
	 * GitHub kind.
	 */
	protected static final String LABEL = GitHub.CONNECTOR_KIND;

	/**
	 * GitHub specific {@link AbstractTaskDataHandler}.
	 */
	private final GitHubTaskDataHandler taskDataHandler;

	public GitHubRepositoryConnector() {
		taskDataHandler = new GitHubTaskDataHandler();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return always {@code true}
	 */
	@Override
	public final boolean canCreateNewTask(TaskRepository repository) {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return always {@code true}
	 */
	@Override
	public final boolean canCreateTaskFromKey(TaskRepository repository) {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see #KIND
	 */
	@Override
	public final String getConnectorKind() {
		return GitHub.CONNECTOR_KIND;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getLabel() {
		return LABEL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final AbstractTaskDataHandler getTaskDataHandler() {
		return this.taskDataHandler;
	}

	@Override
	public final IStatus performQuery(TaskRepository repository,
			IRepositoryQuery query, TaskDataCollector collector,
			ISynchronizationSession session, IProgressMonitor monitor) {

		String queryStatus = query.getAttribute(GitHub.TASK_STATUS);

		String[] statuses;
		if (queryStatus.equals(GitHub.TASK_STATUS_ALL)) {
			statuses = new String[] { GitHub.TASK_STATUS_OPEN,
					GitHub.TASK_STATUS_CLOSED };
		} else {
			statuses = new String[] { queryStatus };
		}

		IStatus result;
		monitor.beginTask(GitHub.MONITOR_STATUS_IN_PROGRESS, statuses.length);
		try {
			String label = query.getAttribute(GitHub.QUERY_TEXT_LABEL);
			List<GitHubIssue> filteredIssues = new ArrayList<GitHubIssue>();
			GitHubIssueService issueService = GitHubService
					.getIssueService(repository);
			for (String status : statuses) {
				GitHubIssues issues = issueService
						.getFilteredIssues(
								query.getAttribute(GitHub.QUERY_TEXT_ATTRIBUTE),
								status);
				filteredIssues.addAll(issues
						.getIssuesLabeled(label == null ? "all" : label));
			}

			for (GitHubIssue issue : filteredIssues) {
				TaskData taskData = taskDataHandler.createTaskData(repository,
						monitor, issue, true);
				collector.accept(taskData);
			}
			monitor.worked(1);
			result = Status.OK_STATUS;
		} catch (GitHubServiceException e) {
			result = createErrorStatus(e);
		}
		monitor.done();
		return result;
	}

	@Override
	public final TaskData getTaskData(TaskRepository repository, String taskId,
			IProgressMonitor monitor) throws CoreException {

		try {
			GitHubIssue issue = GitHubService.getIssueService(repository)
					.retrieve(taskId);
			TaskData taskData = taskDataHandler.createTaskData(repository,
					monitor, issue, false);

			return taskData;
		} catch (GitHubServiceException e) {
			throw new CoreException(createErrorStatus(e));
		}
	}

	@Override
	public final String getRepositoryUrlFromTaskUrl(String taskFullUrl) {
		return GitHubRepositoryUrlBuilder
				.obtainRepositoryUrlFromTaskUrl(taskFullUrl);
	}

	@Override
	public final String getTaskIdFromTaskUrl(String taskFullUrl) {
		return GitHubRepositoryUrlBuilder.obtainTaskIdFromTaskUrl(taskFullUrl);
	}

	@Override
	public final String getTaskUrl(String repositoryUrl, String taskId) {
		return GitHubRepositoryUrlBuilder.obtainTaskUrl(repositoryUrl, taskId);
	}

	@Override
	public final void updateRepositoryConfiguration(
			TaskRepository taskRepository, IProgressMonitor monitor)
			throws CoreException {
	}

	@Override
	public final boolean hasTaskChanged(TaskRepository repository, ITask task,
			TaskData taskData) {
		return new TaskMapper(taskData).hasChanges(task);
	}

	@Override
	public final void updateTaskFromTaskData(TaskRepository taskRepository,
			ITask task, TaskData taskData) {
		if (!taskData.isNew()) {
			task.setUrl(getTaskUrl(taskRepository.getUrl(),
					taskData.getTaskId()));
		}
		new TaskMapper(taskData).applyTo(task);
	}
}
