package org.eclipse.mylyn.github.internal;

/**
 * Simple issue wrapper.
 * 
 * @author gciuloaica
 * 
 * 
 *         FIXME: Check to see if it should be removed.
 * 
 */
public final class GitHubShowIssue {
	private GitHubIssue issue;

	public GitHubIssue getIssue() {
		return issue;
	}

	public void setIssue(GitHubIssue issue) {
		this.issue = issue;
	}

}
