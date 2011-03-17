/**
 * 
 */
package org.eclipse.mylyn.github.ui.internal;

import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * Comments part editor.
 * 
 * @author Gabriel Ciuloaica (gciuloaica@gmail.com)
 * @note note used yet - look to see if we need to overwrite the default
 *       functionality.
 */
public class GitHubCommentsTaskEditorPart extends AbstractTaskEditorPart {

	public GitHubCommentsTaskEditorPart() {
		setPartName("Comments");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart#createControl
	 * (org.eclipse.swt.widgets.Composite,
	 * org.eclipse.ui.forms.widgets.FormToolkit)
	 */
	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		// TODO Auto-generated method stub

	}

}
