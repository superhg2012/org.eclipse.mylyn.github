/**
 * 
 */
package org.eclipse.mylyn.github.internal;

import java.util.Comparator;

/**
 * Enumerate possible ordering by values.
 * 
 * @author Gabriel Ciuloaica (gciuloaica@gmail.com)
 * 
 */
public enum GitHubIssueOrderHandler {

	BY_ID(0, "Id", GitHubIssueOrder.ID), BY_VOTES(1, "Votes",
			GitHubIssueOrder.VOTES), BY_CREATION_DATE(2, "Creation date",
			GitHubIssueOrder.CREATION_DATE), BY_MODIFIED_DATE(3, "Update date",
			GitHubIssueOrder.MODIFIED_DATE), BY_CLOSE_DATE(4, "Closed date",
			GitHubIssueOrder.CLOSE_DATE);

	private final int index;
	private final String label;
	private final Comparator<GitHubIssue> comparator;

	private GitHubIssueOrderHandler(int index, String label,
			Comparator<GitHubIssue> comparator) {
		this.index = index;
		this.label = label;
		this.comparator = comparator;
	}

	public int getIndex() {
		return index;
	}

	public String getLabel() {
		return label;
	}

	public Comparator<GitHubIssue> getComparator() {
		return comparator;
	}

}
