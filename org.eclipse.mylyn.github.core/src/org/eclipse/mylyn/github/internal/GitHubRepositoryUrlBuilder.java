/**
 * 
 */
package org.eclipse.mylyn.github.internal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * Helper class providing utility methods to generate GitHub URLs.
 * </p>
 * 
 * 
 * @author Gabriel Ciuloaica (gciuloaica@gmail.com)
 * 
 */
public class GitHubRepositoryUrlBuilder {

	private static final String FORWARD_SLASH = "/";

	private GitHubRepositoryUrlBuilder() {

	}

	public static String buildTaskRepositoryUser(String repositoryUrl) {
		Matcher matcher = GitHub.URL_PATTERN.matcher(repositoryUrl);
		if (matcher.matches()) {
			return matcher.group(1);
		}
		return null;
	}

	public static String buildTaskRepositoryProject(String repositoryUrl) {
		Matcher matcher = GitHub.URL_PATTERN.matcher(repositoryUrl);
		if (matcher.matches()) {
			return matcher.group(2);
		}
		return null;
	}

	/**
	 * Uses github.com
	 * 
	 * @see #buildGitHubUrlAlternate(String, String)
	 */
	public static String buildGitHubUrl(String user, String project) {
		return GitHub.HTTP_GITHUB_COM + FORWARD_SLASH + user + FORWARD_SLASH
				+ project;
	}

	/**
	 * Uses www.github.org
	 * 
	 * @see #buildGitHubUrl(String, String)
	 */
	public static String buildGitHubUrlAlternate(String user, String project) {
		return GitHub.HTTP_WWW_GITHUB_ORG + FORWARD_SLASH + user
				+ FORWARD_SLASH + project;
	}
	
	public static String obtainRepositoryUrlFromTaskUrl(String taskFullUrl) {
		if (taskFullUrl != null) {
			Matcher matcher = Pattern.compile(
					"(http://.+?)/issues/issue/([^/]+)").matcher(taskFullUrl);
			if (matcher.matches()) {
				return matcher.group(1);
			}
		}
		return null;
	}
	
	public static String obtainTaskIdFromTaskUrl(String taskFullUrl) {
		if (taskFullUrl != null) {
			Matcher matcher = Pattern.compile(".+?/issues/issue/([^/]+)")
					.matcher(taskFullUrl);
			if (matcher.matches()) {
				return matcher.group(1);
			}
		}
		return null;
	}
	
	public static String obtainTaskUrl(String repositoryUrl, String taskId) {
		return repositoryUrl + "/issues/issue/" + taskId;
	}

}
