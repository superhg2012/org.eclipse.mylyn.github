/*
 * Copyright 2009 Christian Trutz 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *  
 */
package org.eclipse.mylyn.github.ui.internal;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.mylyn.github.internal.GitHub;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.mylyn.tasks.ui.editors.AttributeEditorFactory;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorPartDescriptor;
import org.eclipse.ui.IEditorSite;

/**
 * Editor page for GitHub.
 * 
 * @author Christian Trutz, Gabriel Ciuloaica
 * @since 0.1.0
 */
public class GitHubTaskEditorPage extends AbstractTaskEditorPage {

	/**
	 * Constructor for the GitHubTaskEditorPage
	 * 
	 * @param editor
	 *            The task editor to create for GitHub
	 */
	public GitHubTaskEditorPage(final TaskEditor editor) {
		super(editor, GitHub.CONNECTOR_KIND);
		setNeedsPrivateSection(true);
		setNeedsSubmitButton(true);
	}

	@Override
	protected final Set<TaskEditorPartDescriptor> createPartDescriptors() {
		Set<TaskEditorPartDescriptor> partDescriptors = super
				.createPartDescriptors();
		Iterator<TaskEditorPartDescriptor> descriptorIt = partDescriptors
				.iterator();
		while (descriptorIt.hasNext()) {
			TaskEditorPartDescriptor partDescriptor = descriptorIt.next();
			if (partDescriptor.getId().equals(ID_PART_ATTRIBUTES)) {
				descriptorIt.remove();
			} else if (partDescriptor.getId().equals(ID_PART_PEOPLE)) {
				descriptorIt.remove();
			}
		}
		partDescriptors.add(new GitHubAtrributesTaskEditorPartDescriptor()
				.setPath(PATH_ATTRIBUTES));
		partDescriptors.add(new GitHubPeopleTaskEditorPartDescriptor()
				.setPath(PATH_PEOPLE));
		return partDescriptors;
	}

	@Override
	protected final AttributeEditorFactory createAttributeEditorFactory() {
		return new GitHubAttributeEditorFactory(getModel(),
				getTaskRepository(), getEditorSite());
	}

	private final class GitHubAttributeEditorFactory extends
			AttributeEditorFactory {

		public GitHubAttributeEditorFactory(TaskDataModel model,
				TaskRepository taskRepository, IEditorSite editorSite) {
			super(model, taskRepository, editorSite);
		}

		@Override
		public AbstractAttributeEditor createEditor(String type,
				TaskAttribute taskAttribute) {
			return super.createEditor(type, taskAttribute);
		}

	}

	private final class GitHubAtrributesTaskEditorPartDescriptor extends
			TaskEditorPartDescriptor {
		public GitHubAtrributesTaskEditorPartDescriptor() {
			super(ID_PART_ATTRIBUTES);
		}

		@Override
		public AbstractTaskEditorPart createPart() {
			return new GitHubAttributesTaskEditorPart();
		}
	}

	private final class GitHubPeopleTaskEditorPartDescriptor extends
			TaskEditorPartDescriptor {

		public GitHubPeopleTaskEditorPartDescriptor() {
			super(ID_PART_PEOPLE);
		}

		@Override
		public AbstractTaskEditorPart createPart() {
			return new GitHubPeopleTaskEditorPart();
		}
	}
}
