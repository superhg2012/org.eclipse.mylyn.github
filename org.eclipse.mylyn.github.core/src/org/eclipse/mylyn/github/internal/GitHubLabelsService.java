/**
 * 
 */
package org.eclipse.mylyn.github.internal;

import static org.eclipse.mylyn.github.internal.GitHub.API_ISSUES_ROOT;
import static org.eclipse.mylyn.github.internal.GitHub.API_URL_BASE;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * Operates over labels remotely.
 * 
 * @author Gabriel Ciuloaica (gciuloaica@gmail.com)
 * 
 */
public class GitHubLabelsService extends AbstractGitHubService<String> {

	public GitHubLabelsService(TaskRepository repository) {
		super(repository);
	}

	/**
	 * Add a new label to the project.
	 * 
	 * @note API issues/label/add/:user/:repo/:label
	 * @see org.eclipse.mylyn.github.internal.AbstractGitHubService#create(java.lang.Object)
	 */
	@Override
	public String create(String label) throws GitHubServiceException {
		StringBuilder uri = new StringBuilder(API_URL_BASE);
		uri.append(API_ISSUES_ROOT).append(GitHub.ADD_LABEL)
				.append(getTaskRepositoryUserName()).append("/")
				.append(getTaskRepositoryProjectName()).append("/")
				.append(label);
		executeRetrieveLabels(uri.toString(), getCredentials());
		return label;
	}

	/**
	 * Add a label to an issue. If the label do not exist then it will be
	 * created.
	 * 
	 * @note API issues/label/add/:user/:repo/:label/:number
	 * 
	 */
	public String addLabelToIssue(String label, String issueId)
			throws GitHubServiceException {
		StringBuilder uri = new StringBuilder(API_URL_BASE);
		uri.append(API_ISSUES_ROOT).append(GitHub.ADD_LABEL)
				.append(getTaskRepositoryUserName()).append("/")
				.append(getTaskRepositoryProjectName()).append("/")
				.append(label).append("/").append(issueId);
		executeRetrieveLabels(uri.toString(), getCredentials());
		return label;
	}

	/**
	 * Retrieves all labels from project.
	 * 
	 * @note API issues/labels/:user/:repo
	 * @see org.eclipse.mylyn.github.internal.AbstractGitHubService#retrieve()
	 */
	@Override
	public List<String> retrieve() throws GitHubServiceException {
		StringBuilder uri = new StringBuilder(API_URL_BASE);
		uri.append(API_ISSUES_ROOT).append("labels/")
				.append(getTaskRepositoryUserName()).append("/")
				.append(getTaskRepositoryProjectName());
		return executeRetrieveLabels(uri.toString(), getCredentials());
	}

	@Override
	public List<String> search(String filter) throws GitHubServiceException {
		throw new GitHubServiceException("Unsupported operation");
	}

	@Override
	public String retrieve(String issueId) throws GitHubServiceException {
		throw new GitHubServiceException("Unsupported operation");
	}

	@Override
	public String update(String t) throws GitHubServiceException {
		throw new GitHubServiceException("Unsupported operation");
	}

	/**
	 * Remove a label from project
	 * 
	 * @note API issues/label/remove/:user/:repo/:label
	 * @see org.eclipse.mylyn.github.internal.AbstractGitHubService#delete(java.lang.String)
	 */
	@Override
	public void delete(String label) throws GitHubServiceException {
		StringBuilder uri = new StringBuilder(API_URL_BASE);
		uri.append(API_ISSUES_ROOT).append(GitHub.REMOVE_LABEL)
				.append(getTaskRepositoryUserName()).append("/")
				.append(getTaskRepositoryProjectName()).append("/")
				.append(label);
		executeRetrieveLabels(uri.toString(), getCredentials());
	}

	/**
	 * Remove a label from an issue.
	 * 
	 * @note API issues/label/remove/:user/:repo/:label:/number
	 * 
	 */
	public void deleteLabelFromIssue(String label, String issueId)
			throws GitHubServiceException {
		StringBuilder uri = new StringBuilder(API_URL_BASE);
		uri.append(API_ISSUES_ROOT).append(GitHub.REMOVE_LABEL)
				.append(getTaskRepositoryUserName()).append("/")
				.append(getTaskRepositoryProjectName()).append("/")
				.append(label).append("/").append(issueId);
		executeRetrieveLabels(uri.toString(), getCredentials());
	}

	private List<String> executeRetrieveLabels(String uri,
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
		GitHubLabels labels = getGson().fromJson(responseBody,
				GitHubLabels.class);
		return Arrays.asList(labels.getLabes());
	}

}
