/**
 * 
 */
package org.eclipse.mylyn.github.ui.internal;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * People editor part
 * 
 * @author Gabriel Ciuloaica (gciuloaica@gmail.com)
 * 
 */
public class GitHubPeopleTaskEditorPart extends AbstractTaskEditorPart {
	private static final int COLUMN_MARGIN = 5;

	public GitHubPeopleTaskEditorPart() {
		setPartName("People");
	}

	@Override
	public final void createControl(Composite parent, FormToolkit toolkit) {
		Section section = createSection(parent, toolkit, true);
		Composite peopleComposite = toolkit.createComposite(section);
		GridLayout layout = new GridLayout(2, false);
		peopleComposite.setLayout(layout);
		addAttribute(peopleComposite, toolkit, getTaskData().getRoot().getMappedAttribute(TaskAttribute.USER_ASSIGNED));
		toolkit.paintBordersFor(peopleComposite);
		section.setClient(peopleComposite);
		setSection(toolkit, section);

	}

	private void addAttribute(Composite composite, FormToolkit toolkit,
			TaskAttribute attribute) {
		AbstractAttributeEditor editor = createAttributeEditor(attribute);
		if (editor != null) {
			editor.createLabelControl(composite, toolkit);
			GridDataFactory.defaultsFor(editor.getLabelControl())
					.indent(COLUMN_MARGIN, 0).applyTo(editor.getLabelControl());
			editor.createControl(composite, toolkit);
			getTaskEditorPage().getAttributeEditorToolkit().adapt(editor);
			GridDataFactory.fillDefaults().grab(true, false)
					.align(SWT.FILL, SWT.TOP).applyTo(editor.getControl());
		}
	}

}
