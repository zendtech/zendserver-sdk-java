package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;
import org.zend.php.zendserver.deployment.core.internal.validation.ValidationStatus;
import org.zend.php.zendserver.deployment.ui.editors.DescriptorEditorPage.FormDecoration;

public class CheckboxField implements EditorField {

	protected Button control;
	protected String labelTxt;
	protected IModelObject target;
	protected Feature key;
	protected boolean isRefresh;
	protected ControlDecoration controlDecoration;
	
	public CheckboxField(IModelObject target,Feature key, String label) {
		this.target = target;
		this.key = key;
		this.labelTxt = label;
	}
	
	public Feature getKey() {
		return key;
	}
	
	public void refresh() {
		isRefresh = true;
		try {
			String value = target != null ? target.get(key) : null;
			control.setSelection(value != null && Boolean.parseBoolean(value));
		} finally {
			isRefresh = false;
		}
	}
	
	public void create(Composite parent, FormToolkit toolkit) {
		createControls(parent, toolkit);
		createActions();
	}
	
	protected void createControls(Composite parent, FormToolkit toolkit) {
		control = toolkit.createButton(parent, labelTxt, SWT.CHECK);
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.horizontalSpan = 2;
		control.setLayoutData(gd);
		controlDecoration = new ControlDecoration(control, SWT.LEFT);		
	}
	
	public void setErrorMessage(String message) {
		if (message == null) {
			controlDecoration.hide();
			return;
		}
		FieldDecoration img = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
		controlDecoration.setImage(img.getImage());
		controlDecoration.setDescriptionText(message);
		controlDecoration.show();
	}
	
	public void setWarningMessage(String message) {
		if (message == null) {
			controlDecoration.hide();
			return;
		}
		FieldDecoration img = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_WARNING);
		controlDecoration.setImage(img.getImage());
		controlDecoration.setDescriptionText(message);
		controlDecoration.show();
	}
	
	protected void createActions() {
		control.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (isRefresh) {
					return;
				}
				
				if (target != null) {
					target.set(key, ((Button)e.widget).getSelection());
				}
			}
		});
	}
	
	public void dispose() {
		
	}

	public void setFocus() {
		control.setFocus();
	}

	public void setInput(IModelObject input) {
		target = input;
	}
	
	public Button getButton() {
		return control;
	}

	public void setVisible(boolean visible) {
		control.setVisible(visible);
		((GridData)control.getLayoutData()).exclude = visible;
	}

	public Control getText() {
		return control;
	}
	
	public void setDecoration(FormDecoration value) {
		if (value == null) {
			setErrorMessage(null);
		} else {
			switch (value.severity) {
			case (ValidationStatus.ERROR):
				setErrorMessage(value.message);
			break;
			case (ValidationStatus.WARNING):
				setWarningMessage(value.message);
			break;
			}
		}
	}
	
}
