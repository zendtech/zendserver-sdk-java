package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;
import org.zend.php.zendserver.deployment.ui.editors.DescriptorEditorPage.FormDecoration;

public interface EditorField {

	void setFocus();

	void refresh();

	void setInput(IModelObject input);

	void create(Composite client, FormToolkit toolkit);

	Control getText();

	void setVisible(boolean b);

	Feature getKey();

	void setDecoration(FormDecoration value);

}
