/**
 * 
 */
package org.eclipse.mylyn.github.internal;

import static org.eclipse.mylyn.github.internal.GitHub.API_URL_BASE;
import static org.eclipse.mylyn.github.internal.GitHub.API_USER_ROOT;
import static org.eclipse.mylyn.github.internal.GitHub.EMAILS;
import static org.eclipse.mylyn.github.internal.GitHub.SHOW;

import java.io.IOException;

import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * Read only implementation of user remote interaction.
 * 
 * @author Gabriel Ciuloaica (gciuloaica@gmail.com)
 * 
 */
public class GitHubUserService extends AbstractGitHubService {

	protected GitHubUserService(TaskRepository repository) {
		super(repository);
	}

	/**
	 * Retrieve a user entity
	 * 
	 * @note API doc: /user/show/:username [GET]
	 * @see org.eclipse.mylyn.github.internal.AbstractGitHubService#retrieve(java.lang.String)
	 */
	public final GitHubUser retrieve(String username) throws GitHubServiceException {
		StringBuffer uri = new StringBuffer();
		uri.append(API_URL_BASE).append(API_USER_ROOT).append(SHOW)
				.append(username);
		return executeRetrieveUser(uri.toString());
	}

	/**
	 * Validate credentials.
	 * 
	 * @return true in case of success, false otherwise
	 * @throws GitHubServiceException
	 */
	public final boolean validateCredentials() throws GitHubServiceException {
		StringBuffer uri = new StringBuffer();
		uri.append(API_URL_BASE).append(API_USER_ROOT).append(EMAILS);
		return executeValidateCredentials(uri.toString());
	}

	/**
	 * Download the gravatar raw data.
	 * 
	 * @param gravatarId
	 * @return a byte array representing the jpg gravatar.
	 * @throws GitHubServiceException
	 */
	public final byte[] retrieveGravatar(String gravatarId)
			throws GitHubServiceException {
		StringBuffer uri = new StringBuffer();
		uri.append(GitHub.GRAVATAR_API_URL).append(gravatarId);
		return executeRetrieveGravatar(uri.toString());
	}

	private byte[] executeRetrieveGravatar(String uri)
			throws GitHubServiceException {
		GetMethod method = new GetMethod(uri);
		method.setQueryString("s=20");
		try {
			executeMethod(method);
			return method.getResponseBody();
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
	 * @param areCredentialsValid
	 * @param uri
	 * @return
	 * @throws GitHubServiceException
	 */
	private boolean executeValidateCredentials(String uri)
			throws GitHubServiceException {
		PostMethod method = null;
		boolean areCredentialsValid = true;
		try {
			method = new PostMethod(uri);
			method.setRequestBody(getCredentials());
			executeMethod(method);

		} catch (PermissionDeniedException e) {
			areCredentialsValid = false;
		} finally {
			if (method != null) {
				method.releaseConnection();
			}
		}
		return areCredentialsValid;
	}

	private GitHubUser executeRetrieveUser(String uri)
			throws GitHubServiceException {
		GetMethod method = new GetMethod(uri);
		String responseBody = null;
		try {
			executeMethod(method);
			responseBody = new String(method.getResponseBody());
		} catch (IOException e) {
			throw new GitHubServiceException(
					FAILED_TO_READ_RESPONSE_BODY_EXCEPTION_MESSAGE, e);
		} finally {
			if (method != null) {
				method.releaseConnection();
			}
		}
		GitHubShowUser user = getGson().fromJson(responseBody,
				GitHubShowUser.class);
		return user.getUser();
	}

}
