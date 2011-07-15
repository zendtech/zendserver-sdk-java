package org.zend.php.zendserver.deployment.ui.preferences;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.zend.php.zendserver.deployment.core.DeploymentCore;
import org.zend.php.zendserver.deployment.core.sdk.Sdk;
import org.zend.php.zendserver.deployment.core.sdk.SdkManager;


public class SdkPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public SdkPreferencePage() {
		super("Zend SDK", FLAT);
		setPreferenceStore(new ScopedPreferenceStore(new InstanceScope(), DeploymentCore.PLUGIN_ID));
	}
	
	@Override
	protected void createFieldEditors() {
		DirectoryFieldEditor editor = new DirectoryFieldEditor(SdkManager.SDK_PATH, "Zend SDK Path", getFieldEditorParent()) {
			@Override
			public String getErrorMessage() {
				// TODO Auto-generated method stub
				return super.getErrorMessage();
			}
			protected boolean doCheckState() {
				boolean result = super.doCheckState();
				if (result) {
					String fileName = getTextControl().getText();
					String error = new Sdk(fileName).validate();
					if (error != null) {
						setErrorMessage(error);
						return false;
					} else {
						setErrorMessage(null);
						return true;
					}
				}
				
				return result;
			};
		};
		addField(editor);
	}

	public void init(IWorkbench workbench) {
		// empty
	}

}
