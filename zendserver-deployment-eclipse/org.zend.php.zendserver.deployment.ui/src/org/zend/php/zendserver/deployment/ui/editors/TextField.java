package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptorModifier;

public class TextField {

	protected Text nameText;
	protected String label;
	protected IDeploymentDescriptor target;
	protected IDeploymentDescriptorModifier modifier;
	protected String key;
	protected boolean isRefresh;
	
	public TextField(IDeploymentDescriptor target, IDeploymentDescriptorModifier modifier,String key, String label) {
		this.target = target;
		this.modifier = modifier;
		this.key = key;
		this.label = label;
	}
	
	public void refresh() {
		isRefresh = true;
		try {
			String value = target.get(key);
			nameText.setText(value == null ? "" : value);
		} finally {
			isRefresh = false;
		}
	}
	
	public void create(Composite parent, FormToolkit toolkit) {
		createControls(parent, toolkit);
		createActions();
	}
	
	protected void createControls(Composite parent, FormToolkit toolkit) {
		toolkit.createLabel(parent, label);
		nameText = toolkit.createText(parent, "");
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.horizontalSpan = 2;
		nameText.setLayoutData(gd);
	}
	
	protected void createActions() {
		nameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (isRefresh) {
					return;
				}
				
				String text = ((Text)e.widget).getText();
				try {
					modifier.set(target, key, text);
				} catch (CoreException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			}
		});
	}
	
	public void dispose() {
		
	}

	public void setFocus() {
		nameText.setFocus();
	}
	
}
