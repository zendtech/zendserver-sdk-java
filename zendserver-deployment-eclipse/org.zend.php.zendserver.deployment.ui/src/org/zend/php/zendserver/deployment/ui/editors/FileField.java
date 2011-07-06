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
import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;
import org.zend.php.zendserver.deployment.ui.Messages;

public class FileField extends TextField {

	private Button licenseBrowseButton;
	private IContainer root;
	
	public FileField(IDeploymentDescriptor target, Feature key, String label, IContainer root) {
		super(target, key, label);
		this.root = root;
	}

	@Override
	protected void createTextControl(Composite parent, FormToolkit toolkit) {
		text = toolkit.createText(parent, ""); //$NON-NLS-1$
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		text.setLayoutData(gd);
		gd.horizontalSpan = labelTxt != null ? 1 : 2;
		licenseBrowseButton = toolkit.createButton(parent, Messages.FileField_Browse, SWT.PUSH);
	}
	
	@Override
	protected void createActions() {
		super.createActions();
		licenseBrowseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Shell shell = e.widget.getDisplay().getActiveShell();
				OpenFileDialog dialog = new OpenFileDialog(shell, root, labelTxt, "Select file with "+labelTxt+":", text.getText());
				String newSelection = openDialog(dialog);
				if (newSelection != null) {
					text.setText(newSelection);
				}
			}
		});
	}
	
	protected String openDialog(OpenFileDialog dialog) {
		return dialog.openFile();
	}
}
