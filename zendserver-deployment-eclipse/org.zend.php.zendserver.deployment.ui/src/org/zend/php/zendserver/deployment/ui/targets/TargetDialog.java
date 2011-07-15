package org.zend.php.zendserver.deployment.ui.targets;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.zend.php.zendserver.deployment.ui.Messages;
import org.zend.sdklib.internal.target.ZendTarget;
import org.zend.sdklib.target.IZendTarget;
import org.zend.webapi.core.WebApiException;

/**
 * Dialog to edit deployment target details.
 */
public class TargetDialog extends Dialog {

	private static final int DEFAULT_WIDTH = 300;
	
	private ZendTarget target;
	private Text idText;
	private Text hostText;
	private Text keyText;
	private Text secretText;
	private IZendTarget defaultTarget;
	private String message;

	private Label errorLabel;

	private String title;

	public TargetDialog(Shell parentShell) {
		super(parentShell);
	}
	
	/**
	 * Sets message to be shown at the top of the dialog.
	 * 
	 * @param message Message to be shown at the top of the dialog
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * Sets title to be shown at the dialog title bar.
	 * 
	 * @param title Title to be shown at the dialog title bar.
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		
		if (title != null) {
			newShell.setText(title);
		}
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		// create a composite with standard margins and spacing
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.widthHint = DEFAULT_WIDTH;
		composite.setLayoutData(gd);
		
		Label label;
		if (message != null) {
			label = new Label(composite, SWT.NONE);
			label.setText(message);
			gd = new GridData(SWT.FILL, SWT.DEFAULT, true, false);
			gd.horizontalSpan = 2;
			label.setLayoutData(gd);
		}
		
		label = new Label(composite, SWT.NONE);
		label.setText(Messages.TargetDialog_Id);
		idText = new Text(composite, SWT.BORDER);
		idText.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));
		idText.setToolTipText(Messages.TargetDialog_IdTooltip);
		
		label = new Label(composite, SWT.NONE);
		label.setText(Messages.TargetDialog_Host);
		hostText = new Text(composite, SWT.BORDER);
		hostText.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));
		hostText.setToolTipText(Messages.TargetDialog_HostTooltip);
		
		label = new Label(composite, SWT.NONE);
		label.setText(Messages.TargetDialog_KeyName);
		keyText = new Text(composite, SWT.BORDER);
		keyText.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));
		keyText.setToolTipText(Messages.TargetDialog_KeyTooltip);
		
		label = new Label(composite, SWT.NONE);
		label.setText(Messages.TargetDialog_KeySecret);
		secretText = new Text(composite, SWT.BORDER);
		secretText.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));
		secretText.setToolTipText(Messages.TargetDialog_SecretTooltip);
		
		Button validateButton = new Button(composite, SWT.NONE);
		validateButton.setText(Messages.TargetDialog_validate);
		validateButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				createTarget();
				String message = target.validateTarget();
				if (message != null) {
					setErrorMessage(message);
					return;
				}
				
				try {
					target.connect();
					
					setErrorMessage(null); // all is fine
				} catch (WebApiException ex) {
					setErrorMessage(ex.getMessage());
				}
			}
		});
		errorLabel = new Label(composite, SWT.NONE);
		
		applyDialogFont(composite);
		
		if (defaultTarget != null) {
			copyDefaultTargetDetails();
		}
		
		return composite;
	}
	
	private void setErrorMessage(String message) {
		errorLabel.setText(message == null ? "" : message); //$NON-NLS-1$
	}
	
	@Override
	protected void okPressed() {
		createTarget();
		super.okPressed();
	}
	
	/**
	 * Creates target details provided in the dialog controls.
	 * Target is automatically validated based on the logic in ZendTarget constructor.
	 */
	protected void createTarget() {
		URL host = null;
		try {
			host = new URL(hostText.getText());
		} catch (MalformedURLException e) {
			// should be checked earlier
		}
		
		target = new ZendTarget(idText.getText(), host, keyText.getText(), secretText.getText());
	}
	
	/**
	 * Returns target specified by the user
	 * 
	 * @return
	 */
	public ZendTarget getTarget() {
		if (target == null) {
			createTarget();
		}
		return target;
	}

	/**
	 * Sets default values for the fields.
	 * It also disables idField, because usually we don't want to change id.
	 * 
	 * @param target
	 */
	public void setDefaultTarget(IZendTarget target) {
		this.defaultTarget = target;
		if (target != null) {
			copyDefaultTargetDetails();
		}
	}

	private void copyDefaultTargetDetails() {
		if (idText != null) {
			idText.setText(defaultTarget.getId());
			idText.setEnabled(false);
		}
		if (hostText != null) {
			hostText.setText(defaultTarget.getHost().toString());
		}
		if (keyText != null) {
		keyText.setText(defaultTarget.getKey());
		}
		if (secretText != null) {
			secretText.setText(defaultTarget.getSecretKey());
		}
	}

}
