package org.eclipse.mylyn.github.internal;

import java.util.regex.Pattern;

/**
 * <p>
 * GitHub connector definitions.
 * </p>
 * <p>
 * GitHub Issues API Documentation: http://develop.github.com/p/issues.html
 * 
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

	public static final String MONITOR_STATUS_IN_PROGRESS = "Querying repository ...";

	public static final String QUERY_TEXT_ATTRIBUTE = "queryText";

	public static final Pattern URL_PATTERN = Pattern.compile("(?:"
			+ Pattern.quote(HTTP_WWW_GITHUB_ORG) + "|"
			+ Pattern.quote(HTTP_GITHUB_COM) + "|"
			+ Pattern.quote(HTTPS_GITHUB_COM) + ")/([^/]+)/([^/]+)");

	public static final String API_URL_BASE = "https://github.com/api/v2/json/";
	public static final String API_ISSUES_ROOT = "issues/";
	public static final String API_USER_ROOT = "user/";
	
	public static final String OPEN = "open/"; 
	public static final String REOPEN = "reopen/";
	public static final String CLOSE = "close/";
	public static final String EDIT = "edit/"; 
	public static final String SHOW = "show/"; 
	public static final String LIST = "list/";
	public static final String SEARCH = "search/"; 
	public static final String ADD_LABEL = "label/add/"; 
	public static final String REMOVE_LABEL = "label/remove/";
	public static final String EMAILS = "emails";
	public static final String GITHUB_TASK_LABEL="task.common.label";

}
