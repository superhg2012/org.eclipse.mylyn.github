/**
 * 
 */
package org.eclipse.mylyn.github.internal;

import static org.eclipse.mylyn.github.internal.GitHub.API_ISSUES_ROOT;
import static org.eclipse.mylyn.github.internal.GitHub.API_URL_BASE;
import static org.eclipse.mylyn.github.internal.GitHub.CLOSE;
import static org.eclipse.mylyn.github.internal.GitHub.EDIT;
import static org.eclipse.mylyn.github.internal.GitHub.LIST;
import static org.eclipse.mylyn.github.internal.GitHub.OPEN;
import static org.eclipse.mylyn.github.internal.GitHub.REOPEN;
import static org.eclipse.mylyn.github.internal.GitHub.SEARCH;
import static org.eclipse.mylyn.github.internal.GitHub.SHOW;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * Provides support to operate issues remotely.
 * 
 * @author Gabriel Ciuloaica (gciuloaica@gmail.com)
 * 
 */
public class GitHubIssueService extends AbstractGitHubService {

	public GitHubIssueService(TaskRepository repository) {
		super(repository);
	}

	/**
	 * Open a new issue using the GitHub Issues API.
	 * 
	 * @param issue
	 *            - The GitHub issue object to create on the issue tracker.
	 * 
	 * @return the issue that was created
	 * 
	 * @throws GitHubServiceException
	 * 
	 * @note API Doc: issues/open/:user/:repo
	 * @note API POST Variables: login,api-token, title, body
	 * @see org.eclipse.mylyn.github.internal.AbstractGitHubService#create(java.lang.Object)
	 */
	public final GitHubIssue create(GitHubIssue issue)
			throws GitHubServiceException {
		StringBuilder uri = new StringBuilder(API_URL_BASE)
				.append(API_ISSUES_ROOT).append(OPEN)
				.append(getTaskRepositoryUserName()).append("/")
				.append(getTaskRepositoryProjectName());
		return executeRetrieveIssue(uri.toString(), setRequestBody(issue));
	}

	/**
	 * Retrieve all GitHub Issues
	 * 
	 * @return A GitHubIssues object containing all issues from the search
	 *         results
	 * 
	 * @throws GitHubServiceException
	 *             - in case there is an error during remote operation
	 * 
	 * @note API Doc: /issues/search/:user/:repo/:state
	 * 
	 * @see org.eclipse.mylyn.github.internal.AbstractGitHubService#retrieve()
	 */
	public final List<GitHubIssue> retrieve() throws GitHubServiceException {
		List<GitHubIssue> issues = new ArrayList<GitHubIssue>();
		issues.addAll(getClosedIssues().getIssues());
		issues.addAll(getOpenedIssues().getIssues());
		return issues;
	}

	/**
	 * Search the GitHub Issues API for a given search term.
	 * 
	 * @return A GitHubIssues object containing all issues from the search
	 *         results
	 * 
	 * @throws GitHubServiceException
	 *             - in case there is an error during remote operation
	 * 
	 * @note API Doc: /issues/search/:user/:repo/:state/:search_term
	 * 
	 * @see org.eclipse.mylyn.github.internal.AbstractGitHubService#search(java.lang.String)
	 */
	public final List<GitHubIssue> search(String filter)
			throws GitHubServiceException {
		List<GitHubIssue> issues = new ArrayList<GitHubIssue>();
		issues.addAll(getFilteredIssues(filter, GitHub.TASK_STATUS_CLOSED)
				.getIssues());
		issues.addAll(getFilteredIssues(filter, GitHub.TASK_STATUS_OPEN)
				.getIssues());
		return issues;
	}

	/**
	 * Get a collection of filtered issues.
	 * 
	 * @param filter
	 *            - filter to be applied
	 * @param status
	 *            - status of the issue (open or closed)
	 * @return a collection of GitHub issues.
	 * @throws GitHubServiceException
	 *             in case that the issues could not be retrieved from server.
	 */
	public final GitHubIssues getFilteredIssues(String filter, String status)
			throws GitHubServiceException {
		String uri;
		if (filter != null && (!filter.isEmpty())) {
			uri = getFilteredIssuesUri(filter, status);
		} else {
			uri = getIssuesUri(status);
		}

		return executeRetrieveIssues(uri, getCredentials());
	}

	/**
	 * <p>
	 * Get a specified issue.
	 * </p>
	 * 
	 * @param id
	 *            - issue number
	 * @return issue
	 * @throws GitHubServiceException
	 *             - in case that the get operation is failing
	 * 
	 * @see org.eclipse.mylyn.github.internal.AbstractGitHubService#retrieve(java.lang.String)
	 */
	public final GitHubIssue retrieve(String id) throws GitHubServiceException {
		StringBuilder uri = new StringBuilder(API_URL_BASE)
				.append(API_ISSUES_ROOT).append(SHOW)
				.append(getTaskRepositoryUserName()).append("/")
				.append(getTaskRepositoryProjectName()).append("/").append(id);
		return executeRetrieveIssue(uri.toString(), getCredentials());
	}

	/**
	 * Edit an existing issue using the GitHub Issues API.
	 * 
	 * @param issue
	 *            - The GitHub issue object to create on the issue tracker.
	 * 
	 * @return the issue with changes
	 * 
	 * @throws GitHubServiceException
	 * 
	 *             API Doc: issues/edit/:user/:repo/:number API POST Variables:
	 *             login, api-token, title, body
	 * 
	 * @see org.eclipse.mylyn.github.internal.AbstractGitHubService#update(java.lang.Object)
	 */
	public final GitHubIssue update(GitHubIssue issue)
			throws GitHubServiceException {
		StringBuilder uri = new StringBuilder(API_URL_BASE)
				.append(API_ISSUES_ROOT).append(EDIT)
				.append(getTaskRepositoryUserName()).append("/")
				.append(getTaskRepositoryProjectName()).append("/")
				.append(issue.getNumber());
		return executeRetrieveIssue(uri.toString(), setRequestBody(issue));
	}

	/**
	 * Edit an existing issue using the GitHub Issues API and change its status
	 * to open.
	 * 
	 * @param issue
	 *            - The GitHub issue object to create on the issue tracker.
	 * 
	 * @return the issue with changes
	 * 
	 * @throws GitHubServiceException
	 * 
	 *             API Doc: issues/reopen/:user/:repo/:number API POST
	 *             Variables: login, api-token, title, body
	 */
	public final GitHubIssue reopenIssue(GitHubIssue issue)
			throws GitHubServiceException {
		update(issue);
		StringBuilder uri = new StringBuilder(API_URL_BASE)
				.append(API_ISSUES_ROOT).append(REOPEN)
				.append(getTaskRepositoryUserName()).append("/")
				.append(getTaskRepositoryProjectName()).append("/")
				.append(issue.getNumber());
		return executeRetrieveIssue(uri.toString(), getCredentials());

	}

	/**
	 * Edit an existing issue using the GitHub Issues API and change its status
	 * to closed.
	 * 
	 * @param issue
	 *            - The GitHub issue object to create on the issue tracker.
	 * 
	 * @return the issue with changes
	 * 
	 * @throws GitHubServiceException
	 * 
	 *             API Doc: issues/close/:user/:repo/:number API POST Variables:
	 *             login, api-token, title, body
	 */
	public final GitHubIssue closeIssue(GitHubIssue issue)
			throws GitHubServiceException {
		update(issue);
		StringBuilder uri = new StringBuilder(API_URL_BASE)
				.append(API_ISSUES_ROOT).append(CLOSE)
				.append(getTaskRepositoryUserName()).append("/")
				.append(getTaskRepositoryProjectName()).append("/")
				.append(issue.getNumber());
		return executeRetrieveIssue(uri.toString(), getCredentials());

	}

	private GitHubIssues getOpenedIssues() throws GitHubServiceException {
		String uri = getIssuesUri(GitHub.TASK_STATUS_OPEN);
		return executeRetrieveIssues(uri, getCredentials());
	}

	private GitHubIssues getClosedIssues() throws GitHubServiceException {
		String uri = getIssuesUri(GitHub.TASK_STATUS_CLOSED);
		return executeRetrieveIssues(uri, getCredentials());
	}

	private String getFilteredIssuesUri(String filter, String state) {
		StringBuilder uri = new StringBuilder(API_URL_BASE);
		uri.append(API_ISSUES_ROOT).append(SEARCH)
				.append(getTaskRepositoryUserName()).append("/")
				.append(getTaskRepositoryProjectName()).append("/")
				.append(state).append("/").append(filter);
		return uri.toString();

	}

	private String getIssuesUri(String state) {
		StringBuilder uri = new StringBuilder(API_URL_BASE);
		uri.append(API_ISSUES_ROOT).append(LIST)
				.append(getTaskRepositoryUserName()).append("/")
				.append(getTaskRepositoryProjectName()).append("/")
				.append(state);
		return uri.toString();
	}

	private GitHubIssues executeRetrieveIssues(String uri,
			NameValuePair[] parametersBody) throws GitHubServiceException {
		HttpMethod operation = executeOperation(uri, parametersBody);
		String responseBody = null;
		try {
			responseBody = new String(operation.getResponseBody());
		} catch (IOException e) {
			throw new GitHubServiceException(
					FAILED_TO_READ_RESPONSE_BODY_EXCEPTION_MESSAGE, e);
		} finally {
			if (operation != null) {
				operation.releaseConnection();
			}
		}
		GitHubIssues issues = getGson().fromJson(responseBody,
				GitHubIssues.class);
		return issues;
	}

	private GitHubIssue executeRetrieveIssue(String uri,
			NameValuePair[] parametersBody) throws GitHubServiceException {
		HttpMethod operation = executeOperation(uri, parametersBody);
		String responseBody = null;
		try {
			responseBody = operation.getResponseBodyAsString();
		} catch (IOException e) {
			throw new GitHubServiceException(
					FAILED_TO_READ_RESPONSE_BODY_EXCEPTION_MESSAGE, e);
		} finally {
			if (operation != null) {
				operation.releaseConnection();
			}
		}
		GitHubShowIssue showIssue = getGson().fromJson(responseBody,
				GitHubShowIssue.class);
		return showIssue.getIssue();
	}

	private NameValuePair[] setRequestBody(final GitHubIssue issue) {
		final NameValuePair credentials[] = getCredentials();
		final NameValuePair body = new NameValuePair("body", issue.getBody());
		final NameValuePair title = new NameValuePair("title", issue.getTitle());
		return new NameValuePair[] { credentials[0], credentials[1], body,
				title };
	}

}
