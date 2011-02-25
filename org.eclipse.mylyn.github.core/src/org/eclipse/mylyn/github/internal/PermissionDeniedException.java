package org.eclipse.mylyn.github.internal;

import org.apache.commons.httpclient.StatusLine;

/**
 * <p>
 * Exception thrown when credentials provided to login are not valid.
 * </p>
 * 
 * @author Gabriel Ciuloaica (gciuloaica@gmail.com)
 * 
 */
public class PermissionDeniedException extends GitHubServiceException {

	private static final long serialVersionUID = 1L;

	protected PermissionDeniedException(Exception exception) {
		super(exception);
	}

	protected PermissionDeniedException(StatusLine statusLine) {
		super(statusLine);
	}

	protected PermissionDeniedException(String message, Throwable cause) {
		super(message, cause);
	}

	protected PermissionDeniedException(String message) {
		super(message);
	}

}
