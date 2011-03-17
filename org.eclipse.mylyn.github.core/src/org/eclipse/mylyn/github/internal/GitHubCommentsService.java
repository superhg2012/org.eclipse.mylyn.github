/**
 * 
 */
package org.eclipse.mylyn.github.internal;

import static org.eclipse.mylyn.github.internal.GitHub.API_ISSUES_ROOT;
import static org.eclipse.mylyn.github.internal.GitHub.API_URL_BASE;
import static org.eclipse.mylyn.github.internal.GitHub.COMMENTS;

import java.io.IOException;
import java.util.List;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * Comments service
 * 
 * @author Gabriel Ciuloaica (gciuloaica@gmail.com)
 * 
 */
public class GitHubCommentsService extends
		AbstractGitHubService<GitHubComments> {

	protected GitHubCommentsService(TaskRepository repository) {
		super(repository);
	}

	@Override
	public GitHubComments create(GitHubComments comments)
			throws GitHubServiceException {

		return null;
	}

	/**
	 * @param comments
	 * @return
	 * @throws GitHubServiceException
	 * @note API /issues/comment/:user/:repo/:id
	 */
	public void create(GitHubComment comment) throws GitHubServiceException {
		StringBuilder uri = new StringBuilder(API_URL_BASE);
		uri.append(API_ISSUES_ROOT).append("comment/")
				.append(getTaskRepositoryUserName()).append("/")
				.append(getTaskRepositoryProjectName()).append("/")
				.append(comment.getId());
		executeCreateComment(uri.toString(), setRequestBody(comment));

	}

	@Override
	public List<GitHubComments> retrieve() throws GitHubServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<GitHubComments> search(String filter)
			throws GitHubServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Retrieve comments for a specified issue.
	 * 
	 * @param id
	 *            - issue id
	 * @note API issues/comments/:user/:repo/:number
	 * @see org.eclipse.mylyn.github.internal.AbstractGitHubService#retrieve(java.lang.String)
	 */
	@Override
	public GitHubComments retrieve(String id) throws GitHubServiceException {
		StringBuilder uri = new StringBuilder(API_URL_BASE);
		uri.append(API_ISSUES_ROOT).append(COMMENTS)
				.append(getTaskRepositoryUserName()).append("/")
				.append(getTaskRepositoryProjectName()).append("/").append(id);

		return executeRetrieveComments(uri.toString(), getCredentials());
	}

	@Override
	public GitHubComments update(GitHubComments comments)
			throws GitHubServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(String id) throws GitHubServiceException {
		// TODO Auto-generated method stub

	}

	private GitHubComments executeRetrieveComments(String uri,
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
		GitHubComments comments = getGson().fromJson(responseBody,
				GitHubComments.class);
		return comments;
	}

	private void executeCreateComment(String uri, NameValuePair[] parametersBody)
			throws GitHubServiceException {
		HttpMethod operation = null;
		try {
			operation = executeOperation(uri, parametersBody);
		} finally {
			if (operation != null) {
				operation.releaseConnection();
			}
		}

	}

	private NameValuePair[] setRequestBody(final GitHubComment comment) {
		final NameValuePair credentials[] = getCredentials();
		final NameValuePair body = new NameValuePair("comment",
				comment.getBody());
		return new NameValuePair[] { credentials[0], credentials[1], body, };
	}

}
