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

import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * Facility to perform API operations on a GitHub issue tracker.
 */
public final class GitHubService {

	/**
	 * Create the client and JSON/Java interface object.
	 */
	private GitHubService() {

	}

	public static GitHubIssueService getIssueService(TaskRepository repository) {
		return new GitHubIssueService(repository);
	}

	public static GitHubLabelsService getLabelsService(TaskRepository repository) {
		return new GitHubLabelsService(repository);
	}

	public static GitHubUserService getUserService(TaskRepository repository) {
		return new GitHubUserService(repository);
	}

	public static GitHubCommentsService getCommentsService(TaskRepository repository) {
		return new GitHubCommentsService(repository);
	}

}
