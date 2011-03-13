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

import static org.eclipse.mylyn.github.internal.GitHub.ADD_LABEL;
import static org.eclipse.mylyn.github.internal.GitHub.API_ISSUES_ROOT;
import static org.eclipse.mylyn.github.internal.GitHub.API_URL_BASE;
import static org.eclipse.mylyn.github.internal.GitHub.API_USER_ROOT;
import static org.eclipse.mylyn.github.internal.GitHub.CLOSE;
import static org.eclipse.mylyn.github.internal.GitHub.EDIT;
import static org.eclipse.mylyn.github.internal.GitHub.EMAILS;
import static org.eclipse.mylyn.github.internal.GitHub.LIST;
import static org.eclipse.mylyn.github.internal.GitHub.OPEN;
import static org.eclipse.mylyn.github.internal.GitHub.REMOVE_LABEL;
import static org.eclipse.mylyn.github.internal.GitHub.REOPEN;
import static org.eclipse.mylyn.github.internal.GitHub.SEARCH;
import static org.eclipse.mylyn.github.internal.GitHub.SHOW;
import static org.eclipse.mylyn.github.internal.GitHubRepositoryUrlBuilder.buildTaskRepositoryProject;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.tasks.core.TaskRepository;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * Facility to perform API operations on a GitHub issue tracker.
 */
public class GitHubService {

	private static final String FAILED_TO_DESERIALIZE_JSON_OBJECT_EXCEPTION_MESSAGE = "Failed to deserialize json object.";

	private static final String FAILED_TO_READ_RESPONSE_BODY_EXCEPTION_MESSAGE = "Failed to read response body.";

	private static final Log LOG = LogFactory.getLog(GitHubService.class);

	private final HttpClient httpClient;

	private final Gson gson;

	/**
	 * Create the client and JSON/Java interface object.
	 */
	public GitHubService() {
		httpClient = new HttpClient();
		gson = new Gson();
	}

	/**
	 * Verify that the provided credentials are correct
	 * 
	 * @param credentials
	 *            - user credentials
	 * 
	 * @return true if and only if the credentials are correct
	 */
	public final boolean verifyCredentials(
			final AuthenticationCredentials credentials)
			throws GitHubServiceException {
		PostMethod method = null;
		boolean success = false;
		try {
			method = new PostMethod(API_URL_BASE + API_USER_ROOT + EMAILS);
			method.setRequestBody(getCredentials(credentials));
			executeMethod(method);
			success = true;
		} catch (PermissionDeniedException e) {
			LOG.error("Invalid credentials.", e);
			return false;
		} finally {
			if (method != null) {
				method.releaseConnection();
			}
		}
		return success;
	}

	/**
	 * Search the GitHub Issues API for a given search term
	 * 
	 * @param user
	 *            - The user the repository is owned by
	 * @param repo
	 *            - The Git repository where the issue tracker is hosted
	 * @param state
	 *            - The issue state you want to filter your search by
	 * @param searchTerm
	 *            - The text search term to find in the issues.
	 * 
	 * @return A GitHubIssues object containing all issues from the search
	 *         results
	 * 
	 * @throws GitHubServiceException
	 * 
	 * @note API Doc: /issues/search/:user/:repo/:state/:search_term
	 */
	public final GitHubIssues searchIssues(final String user,
			final String repo, final String state, final String searchTerm,
			final AuthenticationCredentials credentials)
			throws GitHubServiceException {
		GitHubIssues issues = null;
		PostMethod method = null;
		try {
			if (searchTerm.trim().length() == 0) {
				method = new PostMethod(API_URL_BASE + API_ISSUES_ROOT + LIST
						+ user + "/" + repo + "/" + state);
			} else {
				method = new PostMethod(URIUtil.encodePath(API_URL_BASE
						+ API_ISSUES_ROOT + SEARCH + user + "/" + repo + "/"
						+ state + "/" + searchTerm));
			}
			method.setRequestBody(getCredentials(credentials));
			executeMethod(method);
			String responseBody = new String(method.getResponseBody());
			issues = gson.fromJson(responseBody, GitHubIssues.class);
		} catch (JsonSyntaxException e) {
			throw new GitHubServiceException(
					FAILED_TO_DESERIALIZE_JSON_OBJECT_EXCEPTION_MESSAGE, e);
		} catch (IOException e) {
			throw new GitHubServiceException(
					FAILED_TO_READ_RESPONSE_BODY_EXCEPTION_MESSAGE, e);
		} finally {
			if (method != null) {
				method.releaseConnection();
			}
		}
		return issues;
	}

	/**
	 * Add a label to an existing GitHub issue.
	 * 
	 * @param user
	 *            - The user the repository is owned by
	 * @param repo
	 *            - The git repository where the issue tracker is hosted
	 * @param label
	 *            - The text label to add to the existing issue
	 * @param issueNumber
	 *            - The issue number to add a label to
	 * @param api
	 *            - The users GitHub
	 * 
	 * @return A boolean representing the success of the function call
	 * 
	 * @throws GitHubServiceException
	 * 
	 * @note API Doc: issues/label/add/:user/:repo/:label/:number API POST
	 *       Variables: login, api-token
	 */
	public final boolean addLabel(final String user, final String repo,
			final String label, final int issueNumber,
			final AuthenticationCredentials credentials)
			throws GitHubServiceException {
		PostMethod method = null;
		boolean success = false;
		try {
			method = new PostMethod(API_URL_BASE + API_ISSUES_ROOT + ADD_LABEL
					+ user + "/" + repo + "/" + label + "/"
					+ Integer.toString(issueNumber));
			method.setRequestBody(getCredentials(credentials));
			executeMethod(method);
			String response = method.getResponseBodyAsString();
			if (response.contains(label.subSequence(0, label.length()))) {
				success = true;
			}
		} catch (IOException e) {
			throw new GitHubServiceException(
					FAILED_TO_READ_RESPONSE_BODY_EXCEPTION_MESSAGE, e);
		} finally {
			if (method != null) {
				method.releaseConnection();
			}
		}
		return success;
	}

	/**
	 * Remove an existing label from an existing GitHub issue.
	 * 
	 * @param user
	 *            - The user the repository is owned by
	 * @param repo
	 *            - The git repository where the issue tracker is hosted
	 * @param label
	 * @param issueNumber
	 * @param api
	 * 
	 * @return A list of GitHub issues in the response text.
	 * 
	 * @throws GitHubServiceException
	 * 
	 *             API Doc: issues/label/remove/:user/:repo/:label/:number API
	 *             POST Variables: login, api-token
	 */
	public final boolean removeLabel(final String user, final String repo,
			final String label, final int issueNumber,
			final AuthenticationCredentials credentials)
			throws GitHubServiceException {
		PostMethod method = null;
		boolean success = false;
		try {
			method = new PostMethod(API_URL_BASE + API_ISSUES_ROOT
					+ REMOVE_LABEL + user + "/" + repo + "/" + label + "/"
					+ Integer.toString(issueNumber));
			method.setRequestBody(getCredentials(credentials));
			executeMethod(method);
			String response = method.getResponseBodyAsString();
			if (!response.contains(label.subSequence(0, label.length()))) {
				success = true;
			}
		} catch (IOException e) {
			throw new GitHubServiceException(
					FAILED_TO_READ_RESPONSE_BODY_EXCEPTION_MESSAGE, e);
		} finally {
			if (method != null) {
				method.releaseConnection();
			}
		}
		return success;
	}

	/**
	 * Open a new issue using the GitHub Issues API.
	 * 
	 * @param user
	 *            - The user the repository is owned by
	 * @param repo
	 *            - The git repository where the issue tracker is hosted
	 * @param issue
	 *            - The GitHub issue object to create on the issue tracker.
	 * 
	 * @return the issue that was created
	 * 
	 * @throws GitHubServiceException
	 * 
	 *             API Doc: issues/open/:user/:repo API POST Variables: login,
	 *             api-token, title, body
	 */
	public final GitHubIssue openIssueForView(final String user,
			final String repo, final GitHubIssue issue,
			final AuthenticationCredentials credentials)
			throws GitHubServiceException {

		StringBuilder uri = new StringBuilder(API_URL_BASE)
				.append(API_ISSUES_ROOT).append(OPEN).append(user).append("/")
				.append(repo);
		return openIssue(uri.toString(), issue, credentials);
	}

	/**
	 * Edit an existing issue using the GitHub Issues API.
	 * 
	 * @param user
	 *            - The user the repository is owned by
	 * @param repo
	 *            - The git repository where the issue tracker is hosted
	 * @param issue
	 *            - The GitHub issue object to create on the issue tracker.
	 * 
	 * @return the issue with changes
	 * 
	 * @throws GitHubServiceException
	 * 
	 *             API Doc: issues/edit/:user/:repo/:number API POST Variables:
	 *             login, api-token, title, body
	 */
	public final GitHubIssue openIssueForEdit(final String user,
			final String repo, final GitHubIssue issue,
			final AuthenticationCredentials credentials)
			throws GitHubServiceException {
		StringBuilder uri = new StringBuilder(API_URL_BASE)
				.append(API_ISSUES_ROOT).append(EDIT).append(user).append("/")
				.append(repo).append("/").append(issue.getNumber());
		return openIssue(uri.toString(), issue, credentials);
	}

	private GitHubIssue openIssue(final String uri, final GitHubIssue issue,
			final AuthenticationCredentials credentials)
			throws GitHubServiceException {
		PostMethod method = null;
		try {
			method = new PostMethod(uri);
			method.setRequestBody(createRequestBody(issue, credentials));
			method.addRequestHeader("Content-type",
					"application/x-www-form-urlencoded; charset=UTF-8");
			executeMethod(method);
			GitHubShowIssue showIssue = gson.fromJson(
					method.getResponseBodyAsString(), GitHubShowIssue.class);
			if (showIssue == null || showIssue.getIssue() == null) {
				if (LOG.isErrorEnabled()) {
					LOG.error("Unexpected server response: "
							+ method.getResponseBodyAsString());
				}
				throw new GitHubServiceException("Unexpected server response");
			}
			return showIssue.getIssue();
		} catch (JsonSyntaxException e) {
			throw new GitHubServiceException(
					FAILED_TO_DESERIALIZE_JSON_OBJECT_EXCEPTION_MESSAGE, e);
		} catch (IOException e) {
			throw new GitHubServiceException(
					FAILED_TO_READ_RESPONSE_BODY_EXCEPTION_MESSAGE, e);
		} finally {
			if (method != null) {
				method.releaseConnection();
			}
		}
	}

	/**
	 * <p>
	 * Get a specified issue.
	 * </p>
	 * 
	 * @param user
	 *            - username
	 * @param repo
	 *            - repository
	 * @param issueNumber
	 *            - issue number
	 * @return
	 * @throws GitHubServiceException
	 *             - in case that the get operation is failing
	 */
	public final GitHubIssue showIssue(final String user, final String repo,
			final String issueNumber,
			final AuthenticationCredentials credentials)
			throws GitHubServiceException {
		StringBuilder uri = new StringBuilder(API_URL_BASE)
				.append(API_ISSUES_ROOT).append(SHOW).append(user).append("/")
				.append(repo).append("/").append(issueNumber);

		PostMethod method = null;
		try {
			method = new PostMethod(uri.toString());
			method.setRequestBody(getCredentials(credentials));
			executeMethod(method);
			GitHubShowIssue issue = gson
					.fromJson(new String(method.getResponseBody()),
							GitHubShowIssue.class);
			return issue.getIssue();
		} catch (JsonSyntaxException e) {
			throw new GitHubServiceException(
					FAILED_TO_DESERIALIZE_JSON_OBJECT_EXCEPTION_MESSAGE, e);
		} catch (IOException e) {
			throw new GitHubServiceException(
					FAILED_TO_READ_RESPONSE_BODY_EXCEPTION_MESSAGE, e);
		} finally {
			if (method != null) {
				method.releaseConnection();
			}
		}

	}

	/**
	 * Edit an existing issue using the GitHub Issues API and change its status
	 * to open.
	 * 
	 * @param user
	 *            - The user the repository is owned by
	 * @param repo
	 *            - The git repository where the issue tracker is hosted
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
	public final GitHubIssue reopenIssue(String user, String repo,
			GitHubIssue issue, AuthenticationCredentials credentials)
			throws GitHubServiceException {
		GitHubIssue editedIssue = openIssueForEdit(user, repo, issue,
				credentials);
		return changeIssueStatus(user, repo, REOPEN, editedIssue, credentials);
	}

	/**
	 * Edit an existing issue using the GitHub Issues API and change its status
	 * to closed.
	 * 
	 * @param user
	 *            - The user the repository is owned by
	 * @param repo
	 *            - The git repository where the issue tracker is hosted
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
	public final GitHubIssue closeIssue(String user, String repo,
			GitHubIssue issue, AuthenticationCredentials credentials)
			throws GitHubServiceException {
		GitHubIssue editedIssue = openIssueForEdit(user, repo, issue,
				credentials);
		return changeIssueStatus(user, repo, CLOSE, editedIssue, credentials);

	}

	private GitHubIssue changeIssueStatus(final String user, final String repo,
			String githubOperation, final GitHubIssue issue,
			final AuthenticationCredentials credentials)
			throws GitHubServiceException {
		PostMethod method = null;
		try {
			method = new PostMethod(API_URL_BASE + API_ISSUES_ROOT
					+ githubOperation + user + "/" + repo + "/"
					+ issue.getNumber());
			method.setRequestBody(getCredentials(credentials));
			executeMethod(method);
			GitHubShowIssue showIssue = gson.fromJson(
					method.getResponseBodyAsString(), GitHubShowIssue.class);
			if (showIssue == null || showIssue.getIssue() == null) {
				if (LOG.isErrorEnabled()) {
					LOG.error("Unexpected server response: "
							+ method.getResponseBodyAsString());
				}
				throw new GitHubServiceException("Unexpected server response");
			}
			return showIssue.getIssue();
		} catch (JsonSyntaxException e) {
			throw new GitHubServiceException(
					FAILED_TO_DESERIALIZE_JSON_OBJECT_EXCEPTION_MESSAGE, e);
		} catch (IOException e) {
			throw new GitHubServiceException(
					FAILED_TO_READ_RESPONSE_BODY_EXCEPTION_MESSAGE, e);
		} finally {
			if (method != null) {
				method.releaseConnection();
			}
		}
	}

	private void executeMethod(HttpMethod method) throws GitHubServiceException {
		int status;
		try {
			status = httpClient.executeMethod(method);
		} catch (HttpException e) {
			throw new GitHubServiceException(e);
		} catch (IOException e) {
			throw new GitHubServiceException(e);
		}
		if ((status != HttpStatus.SC_OK) && (status != HttpStatus.SC_CREATED)) {
			switch (status) {
			case HttpStatus.SC_UNAUTHORIZED:
			case HttpStatus.SC_FORBIDDEN:
				throw new PermissionDeniedException(method.getStatusLine());
			default:
				throw new GitHubServiceException(method.getStatusLine());
			}
		}
	}

	private NameValuePair[] getCredentials(AuthenticationCredentials credentials) {
		final NameValuePair login = new NameValuePair("login",
				credentials.getUserName());
		final NameValuePair token = new NameValuePair("token",
				credentials.getPassword());
		return new NameValuePair[] { login, token };
	}

	private NameValuePair[] createRequestBody(final GitHubIssue issue,
			final AuthenticationCredentials credentials) {
		final NameValuePair login = new NameValuePair("login",
				credentials.getUserName());
		final NameValuePair token = new NameValuePair("token",
				credentials.getPassword());
		final NameValuePair body = new NameValuePair("body", issue.getBody());
		final NameValuePair title = new NameValuePair("title", issue.getTitle());
		return new NameValuePair[] { login, token, body, title };

	}

	/**
	 * Retrieve all labels available for a project.
	 * 
	 * @param taskRepository
	 * @return null or a list of labels
	 * @throws GitHubServiceException
	 * @note API Doc:/issues/labels/:user/:repo
	 */
	public final String[] retrieveLabels(final TaskRepository repository)
			throws GitHubServiceException {
		PostMethod method = null;
		AuthenticationCredentials auth = repository
				.getCredentials(AuthenticationType.REPOSITORY);
		String project = buildTaskRepositoryProject(repository.getUrl());
		GitHubLabels labels = null;
		try {
			method = new PostMethod(API_URL_BASE + API_ISSUES_ROOT + "labels/"
					+ auth.getUserName() + "/" + project);

			method.setRequestBody(getCredentials(auth));
			executeMethod(method);
			String response = method.getResponseBodyAsString();
			labels = gson.fromJson(response, GitHubLabels.class);

		} catch (IOException e) {
			throw new GitHubServiceException(
					FAILED_TO_READ_RESPONSE_BODY_EXCEPTION_MESSAGE, e);
		} finally {
			if (method != null) {
				method.releaseConnection();
			}
		}

		return labels.getLabes();
	}

	/**
	 * Retrieve a GitHub user;
	 * 
	 * @param username
	 * @return
	 * @throws GitHubServiceException
	 * @note API doc: /user/show/:username [GET]
	 */
	public final GitHubUser retriveUser(String username)
			throws GitHubServiceException {
		GetMethod method = null;
		method = new GetMethod(API_URL_BASE + API_USER_ROOT + "show/"
				+ username);
		GitHubShowUser user = null;
		try {
			executeMethod(method);
			String response = new String(method.getResponseBody());
			user = gson.fromJson(response, GitHubShowUser.class);
		} catch (IOException e) {
			throw new GitHubServiceException(
					FAILED_TO_READ_RESPONSE_BODY_EXCEPTION_MESSAGE, e);
		} finally {
			if (method != null) {
				method.releaseConnection();
			}
		}

		return user.getUser();
	}
}
