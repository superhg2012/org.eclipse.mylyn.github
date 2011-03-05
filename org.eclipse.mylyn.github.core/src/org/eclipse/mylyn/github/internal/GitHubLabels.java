/**
 * 
 */
package org.eclipse.mylyn.github.internal;

import java.util.Arrays;

/**
 * Container of multiple GitHub Labels, used when returning JSON objects
 * 
 * @author Gabriel Ciuloaica (gciuloaica@gmail.com)
 * 
 */
public class GitHubLabels {

	private String[] labels;

	/**
	 * Get all labels.
	 * 
	 * @return an array of labels
	 */
	public final String[] getLabes() {
		return Arrays.copyOf(labels, labels.length);
	}

}
