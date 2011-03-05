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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylyn.github.internal.GitHub;
import org.eclipse.mylyn.github.internal.GitHubRepositoryConnector;
import org.eclipse.mylyn.github.internal.GitHubServiceException;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * GitHub connector specific extensions.
 * 
 * @author Christian Trutz
 * @since 0.1.0
 */
public class GitHubRepositoryQueryPage extends AbstractRepositoryQueryPage {

	private static final String ATTR_QUERY_TEXT = "queryText";
	private static final String ATTR_QUERY_STATUS = "status";
	private static final String ATTR_QUERY_LABEL = "queryLabel";

	private Text queryText = null;
	private Text queryTitle = null;

	private Combo status = null;
	private Combo label = null;

	private Button updateButton;
	private boolean firstTime = true;

	private final TaskRepository taskRepository;

	/**
	 * @param taskRepository
	 * @param query
	 */
	public GitHubRepositoryQueryPage(final TaskRepository taskRepository,
			final IRepositoryQuery query) {
		super("GitHub", taskRepository, query);
		setTitle("Enter query parameters");
		setDescription("Please specify a title for the query.");
		setPageComplete(false);
		this.taskRepository = taskRepository;
	}

	@Override
	public String getQueryTitle() {
		return queryTitle.getText();
	}

	@Override
	public void applyTo(IRepositoryQuery query) {
		query.setSummary(queryTitle.getText());
		query.setAttribute(ATTR_QUERY_STATUS, status.getText());
		query.setAttribute(ATTR_QUERY_TEXT, queryText.getText());
		query.setAttribute(ATTR_QUERY_LABEL, label.getText());
	}

	/**
	 * 
	 * 
	 */
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginTop = 20;
		gridLayout.marginLeft = 25;
		gridLayout.verticalSpacing = 8;
		gridLayout.horizontalSpacing = 8;
		composite.setLayout(gridLayout);

		ModifyListener modifyListener = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setPageComplete(isPageComplete());
			}
		};

		// create the query title entry box
		new Label(composite, SWT.NONE).setText("Query Title:");
		queryTitle = new Text(composite, SWT.BORDER);
		queryTitle
				.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		queryTitle.addModifyListener(modifyListener);
		queryTitle.setFocus();

		// create the status option combo box
		new Label(composite, SWT.NONE).setText("Attributes:");

		createQueryWidgets(composite);

		// create the query entry box
		new Label(composite, SWT.NONE).setText("Query Text:");
		queryText = new Text(composite, SWT.BORDER);
		queryText
				.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		String queryModelText = getQuery() == null ? null : getQuery()
				.getAttribute(ATTR_QUERY_TEXT);
		queryText.setText(queryModelText == null ? "" : queryModelText);

		if (getQuery() != null) {
			queryTitle.setText(getQuery().getSummary());
			queryText.setText(getQuery().getAttribute(ATTR_QUERY_TEXT));

		}
		Dialog.applyDialogFont(composite);
		setControl(composite);
	}

	private Control createQueryWidgets(final Composite control) {
		Composite group = new Composite(control, SWT.NONE);
		GridLayout layout = new GridLayout(5, true);
		layout.marginLeft = 0;
		layout.marginRight = 0;
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setLayout(layout);

		new Label(group, SWT.NONE).setText("Issue Status:");
		status = new Combo(group, SWT.READ_ONLY);
		String[] queryValues = new String[] { "all", "open", "closed" };
		status.setItems(queryValues);
		status.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		status.select(0);
		String queryModelStatus = getQuery() == null ? null : getQuery()
				.getAttribute(ATTR_QUERY_STATUS);
		if (queryModelStatus != null) {
			for (int x = 0; x < queryValues.length; ++x) {
				if (queryValues[x].equals(queryModelStatus)) {
					status.select(x);
					break;
				}
			}
		}

		// create the status option combo box
		new Label(group, SWT.NONE).setText("Issue Label:");
		label = new Combo(group, SWT.READ_ONLY);
		String queryLabelsValues[] = new String[] { "all" };
		label.setItems(queryLabelsValues);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		label.select(0);
		String queryModelLabelStatus = getQuery() == null ? null : getQuery()
				.getAttribute(ATTR_QUERY_LABEL);
		if (queryModelLabelStatus != null) {
			for (int x = 0; x < queryLabelsValues.length; ++x) {
				if (queryLabelsValues[x].equals(queryModelLabelStatus)) {
					label.select(x);
					break;
				}
			}
		}

		updateButton = new Button(group, SWT.PUSH);
		updateButton.setText("Update ");
		updateButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		updateButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (getTaskRepository() != null) {
					updateAttributesFromRepository();
				} else {
					MessageDialog.openInformation(Display.getCurrent()
							.getActiveShell(), "Failed to update attributes",
							"No repository available");
				}
			}

		});

		return group;
	}

	private void updateAttributesFromRepository() {
		GitHubRepositoryConnector connector = (GitHubRepositoryConnector) TasksUi
				.getRepositoryManager().getRepositoryConnector(
						GitHub.CONNECTOR_KIND);
		String labelsValues[] = null;
		try {
			labelsValues = connector.getService()
					.retrieveLabels(taskRepository);
		} catch (GitHubServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (labelsValues != null) {
			label.setItems(labelsValues);
		}
		label.add("all", 0);
		label.select(0);

	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		if (getSearchContainer() != null) {
			getSearchContainer().setPerformActionEnabled(true);
		}

		if (visible && firstTime) {
			firstTime = false;

			// delay the execution so the dialog's progress bar is visible
			// when the attributes are updated
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (getControl() != null && !getControl().isDisposed()) {
						updateAttributesFromRepository();
					}
				}

			});

		}
	}

	@Override
	public boolean isPageComplete() {
		if (queryTitle != null && queryTitle.getText().length() > 0) {
			return true;
		}
		return false;
	}

}
