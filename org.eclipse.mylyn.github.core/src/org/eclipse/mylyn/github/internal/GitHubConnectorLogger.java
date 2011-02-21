/**
 * 
 */
package org.eclipse.mylyn.github.internal;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;

/**
 * <p>
 * Provides Eclipse uniform logging.
 * </p>
 * 
 * @author Gabriel Ciuloaica (gciuloaica@gmail.com)
 * 
 */
public class GitHubConnectorLogger {

	private GitHubConnectorLogger() {

	}

	public static IStatus createStatus(int severity, String message) {
		return new Status(severity, GitHub.BUNDLE_ID, message);
	}

	public static IStatus createStatus(int severity, String message, Throwable e) {
		return new Status(severity, GitHub.BUNDLE_ID, message, e);
	}

	public static IStatus createErrorStatus(String message) {
		return createStatus(IStatus.ERROR, message);
	}

	public static IStatus createErrorStatus(String message, Throwable t) {
		return createStatus(IStatus.ERROR, message, t);
	}

	public static IStatus createErrorStatus(Throwable e) {
		return createStatus(IStatus.ERROR,
				"Unexpected error: " + e.getMessage(), e);
	}

	public static ILog getLog() {
		return Platform.getLog(Platform.getBundle(GitHub.BUNDLE_ID));
	}

	public static void logError(String message, Throwable t) {
		getLog().log(createErrorStatus(message, t));
	}

	public static void logError(Throwable t) {
		getLog().log(createErrorStatus(t.getMessage(), t));
	}
}
