/**
 * 
 */
package org.eclipse.mylyn.github.ui.internal;

import java.io.ByteArrayInputStream;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.github.internal.GitHub;
import org.eclipse.mylyn.github.internal.GitHubService;
import org.eclipse.mylyn.github.internal.GitHubServiceException;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
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
		GridLayout layout = new GridLayout(3, false);
		peopleComposite.setLayout(layout);
		String gravatarId = getTaskData().getRoot()
				.getMappedAttribute(GitHub.GITHUB_PEOPLE_GRAVATAR).getValue();
		TaskAttribute user = getTaskData().getRoot()
		.getMappedAttribute(TaskAttribute.USER_REPORTER);
		user.getMetaData().setType(TaskAttribute.TYPE_URL);
		user.setValue(getUserProfileUrl(user.getValue()));
		addAttribute(peopleComposite, toolkit,user,
				getGravatar(gravatarId));
		
		toolkit.paintBordersFor(peopleComposite);
		section.setClient(peopleComposite);
		setSection(toolkit, section);

	}

	private String getUserProfileUrl(String username) {
		return "http://github.com/"+username;
	}

	private void addAttribute(Composite composite, FormToolkit toolkit,
			TaskAttribute attribute, Image gravatarImage) {
		AbstractAttributeEditor editor = createAttributeEditor(attribute);
		if (editor != null) {
			editor.createLabelControl(composite, toolkit);
			Label gravatar = new Label(composite, SWT.BITMAP);
			gravatar.setImage(gravatarImage);
			GridDataFactory.defaultsFor(editor.getLabelControl())
					.indent(COLUMN_MARGIN, 0).applyTo(editor.getLabelControl());
			editor.createControl(composite, toolkit);
			getTaskEditorPage().getAttributeEditorToolkit().adapt(editor);
			GridDataFactory.fillDefaults().grab(true, false)
					.align(SWT.FILL, SWT.TOP).applyTo(editor.getControl());
		}
	}


	private Image getGravatar(String gravatarId) {
		byte gravatarRaw[] = null;
		try {
			gravatarRaw = GitHubService.getUserService(null).retrieveGravatar(
					gravatarId);
		} catch (GitHubServiceException e) {
			// ignore for now
		}
		ByteArrayInputStream inputStream = new ByteArrayInputStream(gravatarRaw);
		Image image = new Image(null, inputStream);
		return image;

	}

}
