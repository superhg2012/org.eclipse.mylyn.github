/**
 * 
 */
package org.eclipse.mylyn.github.internal;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Provides utility methods to order GitHubIssue collections
 * 
 * @author Gabriel Ciuloaica (gciuloaica@gmail.com)
 * 
 */
public class GitHubIssueOrder {
	private static final Log LOG = LogFactory.getLog(GitHubService.class);

	public static Comparator<GitHubIssue> ID = new Comparator<GitHubIssue>() {

		@Override
		public int compare(GitHubIssue firstIssue, GitHubIssue secondIssue) {
			Integer firstId = Integer.valueOf(firstIssue.getNumber());
			Integer secondId = Integer.valueOf(secondIssue.getNumber());
			return firstId.compareTo(secondId);
		}

	};

	public static Comparator<GitHubIssue> VOTES = new Comparator<GitHubIssue>() {

		@Override
		public int compare(GitHubIssue firstIssue, GitHubIssue secondIssue) {
			Integer firstId = Integer.valueOf(firstIssue.getVotes());
			Integer secondId = Integer.valueOf(secondIssue.getVotes());
			return secondId.compareTo(firstId);
		}
	};

	public static Comparator<GitHubIssue> CREATION_DATE = new Comparator<GitHubIssue>() {

		@Override
		public int compare(GitHubIssue firstIssue, GitHubIssue secondIssue) {
			DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss Z");

			try {
				Date secondCreationDate = df.parse(secondIssue.getCreatedAt());
				Date firstCreationDate = df.parse(firstIssue.getCreatedAt());
				return firstCreationDate.compareTo(secondCreationDate);
			} catch (ParseException e) {
				LOG.error("Parsing error. GitHub may changed format. "
						+ e.getMessage());
				return 0;
			}

		}
	};
	public static Comparator<GitHubIssue> CLOSE_DATE = new Comparator<GitHubIssue>() {

		@Override
		public int compare(GitHubIssue firstIssue, GitHubIssue secondIssue) {
			DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss Z");

			try {
				Date secondCreationDate = df.parse(secondIssue.getClosedAt());
				Date firstCreationDate = df.parse(firstIssue.getClosedAt());
				return firstCreationDate.compareTo(secondCreationDate);
			} catch (ParseException e) {
				LOG.error("Parsing error. GitHub may changed format. "
						+ e.getMessage());
				return 0;
			}

		}
	};

	public static Comparator<GitHubIssue> MODIFIED_DATE = new Comparator<GitHubIssue>() {

		@Override
		public int compare(GitHubIssue firstIssue, GitHubIssue secondIssue) {
			DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss Z");

			try {
				Date secondCreationDate = df.parse(secondIssue.getUpdatedAt());
				Date firstCreationDate = df.parse(firstIssue.getUpdatedAt());
				return firstCreationDate.compareTo(secondCreationDate);
			} catch (ParseException e) {
				LOG.error("Parsing error. GitHub may changed format. "
						+ e.getMessage());
				return 0;
			}

		}
	};

}
