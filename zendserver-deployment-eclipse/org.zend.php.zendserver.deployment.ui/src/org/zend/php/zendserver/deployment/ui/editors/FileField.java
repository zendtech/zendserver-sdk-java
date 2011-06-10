package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.core.resources.IContainer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptorModifier;

public class FileField extends TextField {

	private Button licenseBrowseButton;
	private IContainer root;
	
	public FileField(IDeploymentDescriptor target,
			IDeploymentDescriptorModifier modifier, String key, String label, IContainer root) {
		super(target, modifier, key, label);
		this.root = root;
	}

	@Override
	protected void createControls(Composite parent, FormToolkit toolkit) {
		toolkit.createLabel(parent, label);
		nameText = toolkit.createText(parent, "");
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		nameText.setLayoutData(gd);
		licenseBrowseButton = toolkit.createButton(parent, "Browse...", SWT.PUSH);
	}
	
	@Override
	protected void createActions() {
		super.createActions();
		licenseBrowseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Shell shell = e.widget.getDisplay().getActiveShell();
				OpenFileDialog dialog = new OpenFileDialog(shell, root, label, "Select file with "+label+":", nameText.getText());
				String newSelection = openDialog(dialog);
				if (newSelection != null) {
					nameText.setText(newSelection);
				}
			}
		});
	}
	
	protected String openDialog(OpenFileDialog dialog) {
		return dialog.openFile();
	}
}
