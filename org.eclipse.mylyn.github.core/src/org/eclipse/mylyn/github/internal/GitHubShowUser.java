/**
 * 
 */
package org.eclipse.mylyn.github.internal;

/**
 * Wrapper class for GitHubUser
 * 
 * @author Gabriel Ciuloaica (gciuloaica@gmail.com)
 * 
 */
public final class GitHubShowUser {
	private GitHubUser user;

	public void setUser(GitHubUser user) {
		this.user = user;
	}

	public GitHubUser getUser() {
		return user;
	}
	

}
