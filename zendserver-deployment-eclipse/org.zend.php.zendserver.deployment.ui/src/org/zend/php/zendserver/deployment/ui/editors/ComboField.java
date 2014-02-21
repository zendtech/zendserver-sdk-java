package org.zend.php.zendserver.deployment.ui.editors;

import java.util.Arrays;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;
import org.zend.php.zendserver.deployment.core.internal.validation.ValidationStatus;
import org.zend.php.zendserver.deployment.ui.editors.DescriptorEditorPage.FormDecoration;

public class ComboField implements EditorField {

	protected Label label;
	protected Combo text;
	protected String labelTxt;
	private int style;
	protected IModelObject target;
	protected Feature key;
	protected boolean isRefresh;
	protected ControlDecoration controlDecoration;
	private String[] items;
	private String defaultValue;
	
	public ComboField(IModelObject target, Feature key, String label) {
		this(target, key, label, SWT.NONE);
	}

	public ComboField(IModelObject target, Feature key, String label,
			String defaultValue) {
		this(target, key, label, SWT.NONE, null);
	}

	public ComboField(IModelObject target, Feature key, String label, int style) {
		this(target, key, label, style, null);
	}

	public ComboField(IModelObject target, Feature key, String label,
			int style, String defaultValue) {
		this.target = target;
		this.key = key;
		this.labelTxt = label;
		this.style = style;
		this.defaultValue = defaultValue;
	}

	public Feature getKey() {
		return key;
	}
	
	public void refresh() {
		isRefresh = true;
		try {
			text.deselectAll();
			
			String value = target != null ? target.get(key) : null;
			
			// read-writeable combo fields
			text.setText(value == null ? "" : value); //$NON-NLS-1$
			
			if (value == null && defaultValue != null) {
				value = defaultValue;
			}
			// read-only combo fields
			if (value != null) {
				String[] items = text.getItems();
				
				for (int i = 0; i < items.length; i++) {
					if (value.equals(items[i])) {
						text.select(i);
						break;
					}
				}
			}		
		} finally {
			isRefresh = false;
		}
	}
	
	public void create(Composite parent, FormToolkit toolkit) {
		createControls(parent, toolkit);
		createActions();
	}
	
	protected void createControls(Composite parent, FormToolkit toolkit) {
		label = toolkit.createLabel(parent, labelTxt);
		GridData gd = new GridData();
		label.setLayoutData(gd);
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		text = new Combo(parent, style);
		if (this.items != null) {
			text.setItems(items);
		}
		toolkit.adapt(text, true, true);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.horizontalSpan = 2;
		text.setLayoutData(gd);
		controlDecoration = new ControlDecoration(text, SWT.LEFT);		
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
		text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (isRefresh) {
					return;
				}
				
				Combo combo = (Combo)e.widget;
				String text;
				int idx = combo.getSelectionIndex();
				if (idx != -1) {
					text = combo.getItem(idx);
				} else {
					text = combo.getText();
				}
				if (target != null) {
					target.set(key, text);
				}
			}
		});
	}
	
	public void dispose() {
		
	}

	public void setFocus() {
		text.setFocus();
	}

	public void setInput(IModelObject input) {
		target = input;
	}
	
	public Combo getCombo() {
		return text;
	}

	public void setVisible(boolean visible) {
		label.setVisible(visible);
		text.setVisible(visible);
		((GridData)text.getLayoutData()).exclude = !visible;
		((GridData)label.getLayoutData()).exclude = !visible;
		
	}

	public void setItems(String[] strings) {
		this.items = strings;
		if (text == null) {
			return;
		}
		
		isRefresh = true;
		try {
			String currentDefault = text.getText();
			text.setItems(this.items);
			
			int newIndex = Arrays.asList(items).indexOf(currentDefault);
			if (newIndex != -1) {
				text.select(newIndex);
			} else {
				text.setText(currentDefault);
			}
		} finally {
			isRefresh = false;
		}
	}

	public Control getText() {
		return text;
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
