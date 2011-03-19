/**
 * 
 */
package org.eclipse.mylyn.github.internal;

import static org.eclipse.mylyn.github.internal.GitHubRepositoryUrlBuilder.buildTaskRepositoryProject;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.util.URIUtil;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.tasks.core.TaskRepository;

import com.google.gson.Gson;

/**
 * Provides GitHub Services
 * 
 * @author Gabriel Ciuloaica (gciuloaica@gmail.com)
 * 
 */
public abstract class AbstractGitHubService {

	private final TaskRepository taskRepository;

	private final HttpClient httpClient;
	private final Gson gson;

	protected static final String FAILED_TO_READ_RESPONSE_BODY_EXCEPTION_MESSAGE = "Failed to read response body.";

	protected AbstractGitHubService(TaskRepository repository) {
		this.taskRepository = repository;
		this.httpClient = new HttpClient();
		this.gson = new Gson();
	}

	protected final TaskRepository getTaskRepository() {
		return taskRepository;
	}

	protected final Gson getGson() {
		return gson;
	}

	protected final HttpMethod executeOperation(String uri,
			NameValuePair[] parametersBody) throws GitHubServiceException {
		PostMethod method = null;
		try {
			method = new PostMethod(URIUtil.encodePath(uri));
		} catch (URIException e) {
			throw new GitHubServiceException(e);
		}
		setHeaders(method);
		method.setRequestBody(parametersBody);
		executeMethod(method);
		return method;
	}

	protected final void executeMethod(HttpMethod method)
			throws GitHubServiceException {
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

	protected final NameValuePair[] getCredentials() {
		AuthenticationCredentials credentials = taskRepository
				.getCredentials(AuthenticationType.REPOSITORY);
		final NameValuePair login = new NameValuePair("login",
				credentials.getUserName());
		final NameValuePair token = new NameValuePair("token",
				credentials.getPassword());
		return new NameValuePair[] { login, token };
	}

	protected final String getTaskRepositoryUserName() {
		return getTaskRepository().getUserName();
	}

	protected final String getTaskRepositoryProjectName() {
		return buildTaskRepositoryProject(getTaskRepository().getUrl());
	}

	private void setHeaders(PostMethod method) {
		method.addRequestHeader("Content-type",
				"application/x-www-form-urlencoded; charset=UTF-8");
	}

}
