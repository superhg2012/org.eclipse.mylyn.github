package org.eclipse.mylyn.github.internal;

import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * <p>
 * Credential data type.
 * </p>
 * 
 * @author Gabriel Ciuloaica (gciuloaica@gmail.com)
 * 
 */
public class GitHubCredentials {
	private final String username;
	private final String token;

	public GitHubCredentials(String username, String apiToken) {
		this.username = username;
		this.token = apiToken;
	}

	public GitHubCredentials(AuthenticationCredentials credentials) {
		this(credentials.getUserName(), credentials.getPassword());
	}

	public static GitHubCredentials create(TaskRepository repository) {
		return new GitHubCredentials(
				repository.getCredentials(AuthenticationType.REPOSITORY));
	}

	public final String getUsername() {
		return username;
	}

	public final String getApiToken() {
		return token;
	}

}
