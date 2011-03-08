/**
 * 
 */
package org.eclipse.mylyn.github.ui.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.mylyn.internal.tasks.ui.editors.CheckboxMultiSelectAttributeEditor;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;

/**
 * Label attribute editor.
 * 
 * @author Gabriel Ciuloaica (gciuloaica@gmail.com)
 * 
 * @note Implementation is based on a internal common.ui class.
 * 
 */
@SuppressWarnings("restriction")
public class GitHubLabelAttributeEditor extends
		CheckboxMultiSelectAttributeEditor {

	public GitHubLabelAttributeEditor(TaskDataModel manager,
			TaskAttribute taskAttribute) {
		super(manager, taskAttribute);
	}

	@Override
	public List<String> getValues() {
		List<String> values = new ArrayList<String>();
		String selectedLabels = getAttributeMapper().getValue(
				getTaskAttribute());
		StringTokenizer st = new StringTokenizer(selectedLabels, ",", false);
		while (st.hasMoreTokens()) {
			values.add(st.nextToken().trim());
		}
		return values;

	}

	@Override
	public void setValues(List<String> newValues) {
		StringBuilder sb = new StringBuilder();
		Collections.sort(newValues);
		for (int i = 0; i < newValues.size(); i++) {
			sb.append(newValues.get(i));
			if (i != newValues.size() - 1) {
				sb.append(",");
			}
		}
		getAttributeMapper().setValue(getTaskAttribute(), sb.toString());
		attributeChanged();
	}

}
