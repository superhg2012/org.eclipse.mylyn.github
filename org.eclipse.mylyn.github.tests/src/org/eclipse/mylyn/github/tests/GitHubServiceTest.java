/**
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
package org.eclipse.mylyn.github.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.github.internal.GitHub;
import org.eclipse.mylyn.github.internal.GitHubIssue;
import org.eclipse.mylyn.github.internal.GitHubIssues;
import org.eclipse.mylyn.github.internal.GitHubService;
import org.eclipse.mylyn.github.internal.GitHubServiceException;
import org.eclipse.mylyn.github.internal.GitHubUser;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Run All the JUnit Tests for the GitHub API implementation
 */
@SuppressWarnings("restriction")
@RunWith(JUnit4.class)
public class GitHubServiceTest {

	private static final String GITHUB_TEST_PROJECT_URL = "https://github.com/eclipse-github-plugin/org.eclipse.mylyn.github.issues";

	private static final String API_KEY = "8b35af675fcdca9d254ae7a6ad4d0be8";

	private static final String TEST_USER = "eclipse-github-plugin";

	private static TaskRepository getRepository() {
		TaskRepository repository = new TaskRepository(GitHub.CONNECTOR_KIND,
				GITHUB_TEST_PROJECT_URL);
		AuthenticationCredentials credentials = new AuthenticationCredentials(
				TEST_USER, API_KEY);
		repository.setCredentials(AuthenticationType.REPOSITORY, credentials,
				true);

		return repository;
	}

	/**
	 * Verify GitHub service verify credentials implementation
	 * 
	 * @throws GitHubServiceException
	 */
	@Test
	public final void verifyCredentials() throws GitHubServiceException {
		assertTrue(GitHubService.getUserService(getRepository())
				.validateCredentials());
	}

	/**
	 * Test the GitHubService issue searching implementation
	 * 
	 * @throws GitHubServiceException
	 */
	@Test
	public final void searchIssues() throws GitHubServiceException {
		final GitHubIssues issues = GitHubService.getIssueService(
				getRepository()).getFilteredIssues("test", "open");
		assertEquals(0, issues.getIssues().size());
	}

	/**
	 * Test the GitHubService issue searching implementation, multiple query
	 * keys
	 * 
	 * @throws GitHubServiceException
	 */
	@Test
	public final void searchIssuesWithMultipleKeys()
			throws GitHubServiceException {
		final List<GitHubIssue> issues = GitHubService.getIssueService(
				getRepository()).search("task or issue");
		assertEquals(0, issues.size());
	}

	/**
	 * Test the GitHubService implementation for opening a new issue.
	 * 
	 * @throws GitHubServiceException
	 */
	@Test
	public final void openIssue() throws GitHubServiceException {

		final GitHubIssue issue = new GitHubIssue();
		issue.setUser(TEST_USER);
		issue.setBody("This is a test body");
		issue.setTitle("Issue Title");
		GitHubIssue newIssue = GitHubService.getIssueService(getRepository())
				.create(issue);
		assertTrue(newIssue != null);
		assertEquals(issue.getUser(), newIssue.getUser());
		assertEquals(issue.getBody(), newIssue.getBody());
		assertEquals(issue.getTitle(), newIssue.getTitle());
		assertTrue(newIssue.getNumber() != null
				&& newIssue.getNumber().length() > 0);
	}

	/**
	 * Test the GitHubService implementation for opening a new issue.
	 * 
	 * @throws GitHubServiceException
	 */
	@Test
	public final void editIssue() throws GitHubServiceException {
		final GitHubIssue issue = new GitHubIssue();
		issue.setUser(TEST_USER);
		issue.setBody("This is a test body");
		issue.setTitle("Issue Title");
		GitHubIssue newIssue = GitHubService.getIssueService(getRepository())
				.create(issue);
		assertTrue(newIssue != null);

		newIssue.setTitle(newIssue.getTitle() + " - modified");
		newIssue.setBody(newIssue.getBody() + " - modified");

		GitHubService.getIssueService(getRepository()).update(newIssue);

		GitHubIssue showIssue = GitHubService.getIssueService(getRepository())
				.retrieve(newIssue.getNumber());

		assertTrue(showIssue != null);
		assertEquals(newIssue.getTitle(), showIssue.getTitle());
	}

	/**
	 * Test the GitHubService implementation for adding a label to an existing
	 * issue.
	 * 
	 * @throws GitHubServiceException
	 */
	@Test
	public final void addLabel() throws GitHubServiceException {
		String result = GitHubService.getLabelsService(getRepository()).create(
				"lame");
		assertTrue(result.equalsIgnoreCase("lame"));
	}

	/**
	 * Test the GitHubService implementation for removing an existing label from
	 * any GitHub issue.
	 * 
	 * @throws GitHubServiceException
	 */
	@Test
	public final void removeLable() throws GitHubServiceException {
		GitHubService.getLabelsService(getRepository()).delete("lame");
	}

	/**
	 * Test the GitHubService implementation is able to retrieve user profile.
	 * @throws GitHubServiceException
	 */
	@Test
	public final void retrieveUserProfile() throws GitHubServiceException {
		GitHubUser user = GitHubService.getUserService(getRepository())
				.retrieve(TEST_USER);
		assertEquals(TEST_USER, user.getLogin());
	}
}