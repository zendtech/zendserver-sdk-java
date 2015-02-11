package org.zend.php.zendserver.deployment.ui.targets;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.core.utils.DeploymentUtils;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.HelpContextIds;
import org.zend.php.zendserver.deployment.ui.Messages;
import org.zend.sdklib.internal.target.ZendTarget;
import org.zend.sdklib.manager.TargetException;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;
import org.zend.sdklib.target.LicenseExpiredException;

/**
 * Basic zend target details composite, consisting of Host, Key and Key secret.
 */
public class ZendTargetDetailsComposite extends AbstractTargetDetailsComposite {

	private Text hostText;
	private Text keyText;
	private Text secretText;
	private Text baseUrlText;
	private Label baseUrlLabel;
	private Button baseUrlButton;
	
	private boolean editMode;

	public Composite create(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(3, false));

		ModifyListener modifyListener = new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				changeSupport.firePropertyChange(PROP_MODIFY, null,
						validatePage());
			}
		};

		Label label = new Label(composite, SWT.NONE);
		label.setText(Messages.TargetDialog_Host);
		hostText = new Text(composite, SWT.BORDER);
		hostText.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false,
				2, 1));
		hostText.setToolTipText(Messages.TargetDialog_HostTooltip);
		hostText.addModifyListener(modifyListener);
		hostText.setText("http://"); //$NON-NLS-1$
		hostText.setSelection(hostText.getText().length());

		label = new Label(composite, SWT.NONE);
		label.setText(Messages.TargetDialog_KeyName);
		keyText = new Text(composite, SWT.BORDER);
		keyText.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false,
				2, 1));
		keyText.setToolTipText(Messages.TargetDialog_KeyTooltip);
		keyText.addModifyListener(modifyListener);

		label = new Label(composite, SWT.NONE);
		label.setText(Messages.TargetDialog_KeySecret);
		secretText = new Text(composite, SWT.BORDER);
		secretText.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true,
				false, 2, 1));
		secretText.setToolTipText(Messages.TargetDialog_SecretTooltip);
		secretText.addModifyListener(modifyListener);

		baseUrlLabel = new Label(composite, SWT.NONE);
		baseUrlLabel.setText(Messages.TargetDialog_BaseUrl);
		baseUrlLabel.setVisible(false);
		baseUrlText = new Text(composite, SWT.BORDER);
		baseUrlText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		baseUrlText.addModifyListener(modifyListener);
		baseUrlText.setVisible(false);
		baseUrlText.setEditable(false);
		
		baseUrlButton = new Button(composite, SWT.PUSH);
		baseUrlButton.setText(Messages.TargetDialog_ConfigureBaseUrl);
		baseUrlButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleServerConfigButton();
			}
		});
		baseUrlButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false));
		baseUrlButton.setVisible(false);
		return composite;
	}

	public void setDefaultTargetSettings(IZendTarget defaultTarget) {
		editMode = true;
		if (hostText != null) {
			hostText.setText(defaultTarget.getHost().toString());
		}
		if (keyText != null) {
			keyText.setText(defaultTarget.getKey());
		}
		if (secretText != null) {
			secretText.setText(defaultTarget.getSecretKey());
		}
		if (baseUrlText != null) {
			URL baseUrl = DeploymentUtils.getServerBaseURL(defaultTarget);
			if (baseUrl != null && !TargetsManager.isOpenShift(defaultTarget)) {
				baseUrlText.setText(baseUrl.toString());
				baseUrlLabel.setVisible(true);
				baseUrlText.setVisible(true);
				baseUrlButton.setVisible(true);
			}
		}
	}

	public String[] getData() {
		return new String[] { hostText.getText(), keyText.getText(),
				secretText.getText(), baseUrlText.getText() };
	}

	public IZendTarget[] createTarget(String[] data, IProgressMonitor monitor) throws CoreException {
		URL host = null;
		try {
			host = new URL(data[0]);
		} catch (MalformedURLException e) {
			throw new CoreException(new Status(IStatus.ERROR,
					Activator.PLUGIN_ID, e.getMessage()));
		}
		TargetsManager tm = TargetsManagerService.INSTANCE.getTargetManager();
		if (!editMode) {
			IZendTarget[] targets = tm.getTargets();
			for (IZendTarget t : targets) {
				if (t.getHost().equals(host)) {
					throw new CoreException(new Status(IStatus.ERROR,
							Activator.PLUGIN_ID,
							Messages.TargetDialog_HostConflictError));
				}
			}
		}
		String id = tm.createUniqueId(null);
		IZendTarget target = new ZendTarget(id, host, data[1], data[2], true);
		String baseUrl = data[3];
		if (baseUrl != null && !baseUrl.isEmpty()) {
			DeploymentUtils.setServerBaseURL(target, data[3]);
		}
		try {
			tm.add(target, true);
		} catch (TargetException e) {
			// should not appear cause we do not try to connect to it
		} catch (LicenseExpiredException e) {
			// should not appear cause we do not try to connect to it
		}
		return new IZendTarget[] { target };
	}

	@Override
	public boolean hasPage() {
		return true;
	}
	
	@Override
	protected String getHelpResource() {
		return HelpContextIds.CREATING_A_REMOTE_ZEND_SERVER_TARGET;
	}
	
	@Override
	protected boolean validatePage() {
		if (hostText != null && hostText.getText().trim().isEmpty()) {
			return false;
		}
		if (keyText != null && keyText.getText().trim().isEmpty()) {
			return false;
		}
		if (secretText != null && secretText.getText().trim().isEmpty()) {
			return false;
		}
		return true;
	}
	
	@SuppressWarnings("restriction")
	private void handleServerConfigButton() {
	}

}
