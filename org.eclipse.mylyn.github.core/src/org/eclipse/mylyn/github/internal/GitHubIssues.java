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

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.mylyn.github.internal.collections.CollectionUtils;
import org.eclipse.mylyn.github.internal.collections.Predicate;

/**
 * Container of multiple GitHub Issues, used when returning JSON objects
 */
public class GitHubIssues {

	private GitHubIssue[] issues;

	/**
	 * Getter for all issues inside this object
	 * 
	 * @return The array of individual GitHub Issues
	 */
	public final Collection<GitHubIssue> getIssues() {
		return Arrays.asList(issues);
	}

	/**
	 * Filter the collection of issues, based on the label
	 * 
	 * @param filter
	 *            - label value
	 * @return a filtered array.
	 */
	public final Collection<GitHubIssue> getIssuesLabeled(final String filter) {
		Collection<GitHubIssue> filteredIssues = null;
		if (filter.equalsIgnoreCase("all")) {
			filteredIssues = getIssues();
		} else {
			filteredIssues = CollectionUtils.filter(getIssues(),
					new Predicate<GitHubIssue>() {
						@Override
						public boolean apply(GitHubIssue issue) {
							return issue.getLabels().contains(filter);
						}
					});
		}
		return filteredIssues;
	}

}
