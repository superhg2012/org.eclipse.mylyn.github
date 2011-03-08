/**
 * 
 */
package org.eclipse.mylyn.github.ui.internal;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.github.internal.GitHub;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * Attributes editor part.
 * 
 * @author Gabriel Ciuloaica (gciuloaica@gmail.com)
 * 
 */
public class GitHubAttributesTaskEditorPart extends AbstractTaskEditorPart {
	private static final int COLUMN_MARGIN = 5;

	public GitHubAttributesTaskEditorPart() {
		setPartName("Attributes");

	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		Section section = createSection(parent, toolkit, true);
		Composite attributesComposite = toolkit.createComposite(section);
		GridLayout layout = new GridLayout(4, false);
		layout.marginWidth = 5;
		attributesComposite.setLayout(layout);

		addAttribute(attributesComposite, toolkit, getTaskData().getRoot()
				.getMappedAttribute(TaskAttribute.TASK_KEY));
		addAttribute(attributesComposite, toolkit, getTaskData().getRoot()
				.getMappedAttribute(TaskAttribute.DATE_CREATION));
		addLabelAttribute(attributesComposite, toolkit, getTaskData().getRoot()
				.getMappedAttribute(GitHub.GITHUB_TASK_LABEL));
		addAttribute(attributesComposite, toolkit, getTaskData().getRoot()
				.getMappedAttribute(TaskAttribute.DATE_MODIFICATION));
		addAttribute(attributesComposite, toolkit, getTaskData().getRoot()
				.getMappedAttribute(GitHub.GITHUB_TASK_VOTES));
		addAttribute(attributesComposite, toolkit, getTaskData().getRoot()
				.getMappedAttribute(TaskAttribute.DATE_COMPLETION));

		toolkit.paintBordersFor(attributesComposite);
		section.setClient(attributesComposite);
		setSection(toolkit, section);
	}

	private void addLabelAttribute(Composite composite, FormToolkit toolkit,
			TaskAttribute mappedAttribute) {
		AbstractAttributeEditor editor = new GitHubLabelAttributeEditor(
				getModel(), mappedAttribute);
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
