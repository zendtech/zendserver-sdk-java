package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.zend.php.zendserver.deployment.core.descriptor.ChangeEvent;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorChangeListener;
import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;
import org.zend.php.zendserver.deployment.core.internal.validation.ValidationStatus;
import org.zend.php.zendserver.deployment.ui.editors.DescriptorEditorPage.FormDecoration;

public class TextField implements EditorField {

	protected Control label;
	protected Text text;
	protected String textValue;
	protected String labelTxt;
	protected IModelObject target;
	protected Feature key;
	protected boolean isRefresh;
	protected boolean linkLabel;
	protected int style;
	protected IMessageManager mmng;
	protected IDescriptorChangeListener modelChangeListener;
	
	public TextField(IModelObject target,Feature key, String label, IMessageManager mmng) {
		this(target, key, label, SWT.SINGLE, false, mmng);
	}
	
	public TextField(IModelObject target,Feature key, String label, int style, boolean linkLabel, IMessageManager mmng) {
		this.key = key;
		this.labelTxt = label;
		this.style = style;
		this.linkLabel = linkLabel;
		this.mmng = mmng;
		setInput(target);
	}
	
	public Feature getKey() {
		return key;
	}
	
	public void refresh() {
		isRefresh = true;
		try {
			String value = target != null ? target.get(key) : null;
			text.setText(value == null ? "" : value); //$NON-NLS-1$
		} finally {
			isRefresh = false;
		}
	}
	
	public void create(Composite parent, FormToolkit toolkit) {
		createControls(parent, toolkit);
		createActions();
	}
	
	protected void createControls(Composite parent, FormToolkit toolkit) {
		createLabel(parent, toolkit);
		createTextControl(parent, toolkit);
	}
	
	protected void createLabel(Composite parent, FormToolkit toolkit) {
		if (labelTxt == null) {
			return;
		}
		
		if (linkLabel) {
			label = toolkit.createHyperlink(parent, labelTxt, SWT.NULL);
		} else {
			label = toolkit.createLabel(parent, labelTxt);
			label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		}
		label.setLayoutData(new GridData());	
	}
	
	protected void createTextControl(Composite parent, FormToolkit toolkit) {
		text = toolkit.createText(parent, "", style); //$NON-NLS-1$
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 2;
		text.setLayoutData(gd);
		text.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
	}
	
	public void setErrorMessage(String message) {
		if (message == null) {
			mmng.removeMessage(this, text);
		} else {
			mmng.addMessage(this, message, null, IMessageProvider.ERROR, text);
		}
	}
	
	public void setWarningMessage(String message) {
		if (message == null) {
			mmng.removeMessage(this, text);
		} else {
			mmng.addMessage(this, message, null, IMessageProvider.WARNING, text);
		}
	}
	
	protected void createActions() {
		text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (isRefresh) {
					return;
				}
				
				String text = ((Text)e.widget).getText();
				if (target != null) {
					if (("".equals(text) && (key.flags & Feature.SET_EMPTY_TO_NULL) > 0)) { //$NON-NLS-1$
						target.set(key, null);
					} else {
						target.set(key, text);
					}
				}
			}
		});
	}
	
	public void dispose() {
		if ((target != null) && (modelChangeListener != null)) {
			target.removeListener(modelChangeListener);
		}
	}

	public void setFocus() {
		text.setFocus();
	}

	public void setInput(IModelObject input) {
		if (target != null) {
			target.removeListener(modelChangeListener);
		}
		target = input;
		textValue = null;
		if (target != null) {
			if (modelChangeListener == null) {
				modelChangeListener = createModelChangeListener();
			}
			target.addListener(modelChangeListener);
		}
	}
	
	private IDescriptorChangeListener createModelChangeListener() {
		return new IDescriptorChangeListener() {
			
			public void descriptorChanged(final ChangeEvent event) {
				if (event.feature != TextField.this.key) {
					return;
				}
				
				if (event.target != TextField.this.target) {
					return;
				}
				
				if ((text == null) || (text.isDisposed())) {
					return;
				}
				
				text.getDisplay().syncExec(new Runnable() {

					public void run() {
						if (text.isFocusControl()) {
							return;
						}
										
						String currText = text.getText();
						String newText = (String) event.newValue;
						
						if (currText.equals(newText)) {
							return;
						}
						if (newText == null) {
							newText = ""; //$NON-NLS-1$
						}
						
						Point sel = text.getSelection();
						isRefresh = true;
						try {
							text.setText(newText);
							text.setSelection(Math.min(sel.x, newText.length() - 1), Math.min(sel.y, newText.length() - 1));
						} finally {
							isRefresh = false;
						}
					}
					
				});
			}
		};
	}

	public Text getText() {
		return text;
	}

	/**
	 * Makes field invisible and removes the value from
	 * underlying model object. When user enables the field,
	 * it's data is set back to model.
	 */
	public void setVisible(boolean visible) {
		if (text.getVisible() == visible) {
			return;
		}
		text.setVisible(visible);
		((GridData)text.getLayoutData()).exclude = !visible;
		
		if (label != null) {
			label.setVisible(visible);
			((GridData)label.getLayoutData()).exclude = !visible;
		}
		
		
		if (visible) {
			if (textValue != null) {
				text.setText(textValue);
				textValue = null;
			}
		} else {
			textValue = text.getText();
			text.setText(""); //$NON-NLS-1$
		}
	}
	
	public void setEnabled(boolean enabled) {
		if (text.getEnabled() == enabled) {
			return;
		}
		text.setEnabled(enabled);	
		if (enabled) {
			if (textValue != null) {
				text.setText(textValue);
				textValue = null;
			}
		} else {
			textValue = text.getText();
			text.setText(""); //$NON-NLS-1$
		}
	}

	public void setText(String string) {
		text.setText(string);
	}

	public void pack() {
		if (label != null) 
			label.pack();
		if (text != null) 
			text.pack();
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

	public void setLabel(String text) {
		if (label == null) {
			return;
		}
		if (label instanceof Label) {
			((Label) label).setText(text);
		} else if (label instanceof Hyperlink) {
			((Hyperlink) label).setText(text);
		}
		
	}
	
}
