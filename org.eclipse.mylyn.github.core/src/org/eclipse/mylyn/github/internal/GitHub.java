package org.eclipse.mylyn.github.internal;

import java.util.regex.Pattern;

/**
 * <p>
 * GitHub connector definitions.
 * </p>
 * 
 * @author Gabriel Ciuloaica (gciuloaica@gmail.com)
 * 
 */
public final class GitHub {

	private GitHub() {

	}

	public static final String BUNDLE_ID = "org.eclipse.mylyn.github.core";
	public static final String CONNECTOR_KIND = "github";

	public static final String HTTP_WWW_GITHUB_ORG = "http://www.github.org";
	public static final String HTTP_GITHUB_COM = "http://github.com";
	public static final String HTTPS_GITHUB_COM = "https://github.com";

	public static final String TASK_STATUS = "status";
	public static final String TASK_STATUS_ALL = "all";
	public static final String TASK_STATUS_OPEN = "open";
	public static final String TASK_STATUS_CLOSED = "closed";
	
	public static final String MONITOR_STATUS_IN_PROGRESS="Querying repository ...";
	
	public static final String QUERY_TEXT_ATTRIBUTE="queryText";

	public static final Pattern URL_PATTERN = Pattern.compile("(?:"
			+ Pattern.quote(HTTP_WWW_GITHUB_ORG) + "|"
			+ Pattern.quote(HTTP_GITHUB_COM) + "|"
			+ Pattern.quote(HTTPS_GITHUB_COM) + ")/([^/]+)/([^/]+)");

}
