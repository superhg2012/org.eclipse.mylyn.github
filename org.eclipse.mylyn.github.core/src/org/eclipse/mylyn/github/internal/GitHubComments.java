package org.eclipse.mylyn.github.internal;

import java.util.Arrays;
import java.util.Collection;

public class GitHubComments {
	private GitHubComment[] comments;

	public final Collection<GitHubComment> getComments() {
		return Arrays.asList(comments);
	}

}
