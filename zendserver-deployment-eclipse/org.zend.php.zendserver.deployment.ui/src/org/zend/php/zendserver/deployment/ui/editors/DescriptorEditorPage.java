package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;

public abstract class DescriptorEditorPage extends FormPage {

	public DescriptorEditorPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
	}

	@Override
	public void setActive(boolean active) {
		refresh();
	}
	
	public abstract void refresh();
}
