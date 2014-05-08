package org.zend.php.zendserver.deployment.ui.targets;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.statushandlers.StatusManager;
import org.zend.php.zendserver.deployment.core.targets.EclipseSSH2Settings;
import org.zend.php.zendserver.deployment.core.targets.JSCHPubKeyDecryptor;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.HelpContextIds;
import org.zend.php.zendserver.deployment.ui.Messages;
import org.zend.sdklib.SdkException;
import org.zend.sdklib.internal.target.PublicKeyNotFoundException;
import org.zend.sdklib.internal.target.ZendDevCloud;
import org.zend.sdklib.internal.target.ZendTarget;
import org.zend.sdklib.manager.TargetException;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;
import org.zend.sdklib.target.LicenseExpiredException;

/**
 * DevCloud details editing composite: username and password.
 */
public class DevCloudDetailsComposite extends AbstractTargetDetailsComposite {

	private static final String HREF_RESTORE_PASSWORD = "restorePassword"; //$NON-NLS-1$
	private static final String HREF_CREATE_ACCOUNT = "createAccount"; //$NON-NLS-1$

	private static final String RESTORE_PASSWORD_URL = "http://www.zend.com/user/lost"; //$NON-NLS-1$
	private static final String CREATE_ACCOUNT_URL = "https://" + ZendDevCloud.DEVPASS_HOST; //$NON-NLS-1$

	private static final String GENERATED_KEY_FILENAME = "phpcloud"; //$NON-NLS-1$

	private Text usernameText;
	private Text passwordText;
	private Text privateKeyText;
	private Button shouldStoreButton;

	public Composite create(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(4, false));

		ModifyListener modifyListener = new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				changeSupport.firePropertyChange(PROP_MODIFY, null,
						validatePage());
			}
		};

		Label label = new Label(composite, SWT.NONE);
		label.setText(Messages.DevCloudDetailsComposite_Username);
		usernameText = new Text(composite, SWT.BORDER);
		GridData layoutData = new GridData(SWT.FILL, SWT.DEFAULT, true, false);
		layoutData.horizontalSpan = 3;
		usernameText.setLayoutData(layoutData);
		usernameText
				.setToolTipText(Messages.DevCloudDetailsComposite_UsernameTooltip);
		usernameText.addModifyListener(modifyListener);

		label = new Label(composite, SWT.NONE);
		label.setText(Messages.DevCloudDetailsComposite_Password);
		passwordText = new Text(composite, SWT.BORDER | SWT.PASSWORD);
		layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		layoutData.horizontalSpan = 2;
		passwordText.setLayoutData(layoutData);
		passwordText
				.setToolTipText(Messages.DevCloudDetailsComposite_PasswordTooltip);
		passwordText.addModifyListener(modifyListener);

		Button restorePassword = new Button(composite, SWT.PUSH);
		layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		layoutData.minimumWidth = 80;
		restorePassword.setLayoutData(layoutData);
		restorePassword
				.setText(Messages.DevCloudDetailsComposite_RestorePassword);
		restorePassword.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Program.launch(RESTORE_PASSWORD_URL);
			}
		});

		new Label(composite, SWT.NONE);

		shouldStoreButton = new Button(composite, SWT.CHECK);
		layoutData = new GridData(SWT.FILL, SWT.TOP, true, false);
		layoutData.horizontalSpan = 3;
		shouldStoreButton.setText(Messages.DevCloudDetailsComposite_7);
		shouldStoreButton.setLayoutData(layoutData);
		shouldStoreButton.setSelection(true);
		label = new Label(composite, SWT.NONE);
		label.setText(Messages.DevCloudDetailsComposite_0);
		privateKeyText = new Text(composite, SWT.BORDER);
		privateKeyText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		privateKeyText.setToolTipText(Messages.DevCloudDetailsComposite_1);
		File existingKey = EclipseSSH2Settings
				.getPrivateKey(ZendDevCloud.KEY_TYPE);
		if (existingKey != null) {
			privateKeyText.setText(existingKey.getPath());
		}
		privateKeyText.addModifyListener(modifyListener);

		Button btnBrowse = new Button(composite, SWT.PUSH);
		layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		layoutData.minimumWidth = 80;
		btnBrowse.setLayoutData(layoutData);
		btnBrowse.setText(Messages.DevCloudDetailsComposite_2);
		Button btnGenerate = new Button(composite, SWT.PUSH);
		btnGenerate.setText(Messages.DevCloudDetailsComposite_3);
		layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		layoutData.minimumWidth = 80;
		btnGenerate.setLayoutData(layoutData);
		btnBrowse.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				final FileDialog d = new FileDialog(e.display.getActiveShell(),
						SWT.OPEN);
				final String file = d.open();
				if (file != null) {
					privateKeyText.setText(file);
				}
			}
		});
		btnGenerate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				generateKey();
				changeSupport.firePropertyChange(PROP_MODIFY, null,
						validatePage());
				changeSupport.firePropertyChange(PROP_MESSAGE, null, null);
			}
		});

		label = new Label(composite, SWT.WRAP);
		label.setText(Messages.DevCloudDetailsComposite_4
				+ Messages.DevCloudDetailsComposite_5
				+ Messages.DevCloudDetailsComposite_6);
		layoutData = new GridData(SWT.FILL, SWT.TOP, true, false);
		layoutData.widthHint = 500;
		layoutData.horizontalSpan = 4;
		label.setLayoutData(layoutData);

		return composite;
	}

	private void generateKey() {

		String sshHome = EclipseSSH2Settings.getSSHHome();
		String file;
		if (sshHome != null) {
			File tmpFile = new File(sshHome, GENERATED_KEY_FILENAME);
			int i = 1;
			while (tmpFile.exists()) {
				tmpFile = new File(sshHome, GENERATED_KEY_FILENAME + i);
				i++;
			}

			file = tmpFile.getAbsolutePath();

			boolean confirm = MessageDialog.openConfirm(
					privateKeyText.getShell(),
					Messages.DevCloudDetailsComposite_8,
					Messages.bind(Messages.DevCloudDetailsComposite_9, file));
			if (!confirm) {
				return;
			}
		} else {
			FileDialog d = new FileDialog(usernameText.getShell(), SWT.SAVE);
			file = d.open();
			if (file == null) {
				return;
			}
		}

		try {
			EclipseSSH2Settings.createPrivateKey(ZendDevCloud.KEY_TYPE, file);

			privateKeyText.setText(file);
		} catch (CoreException e) {
			StatusManager.getManager().handle(
					new Status(IStatus.ERROR, Activator.PLUGIN_ID,
							e.getMessage(), e), StatusManager.SHOW);
		}
	}

	protected void handleHyperlink(Object href) {
		if (HREF_CREATE_ACCOUNT.equals(href)) {
			Program.launch(CREATE_ACCOUNT_URL);
		} else if (HREF_RESTORE_PASSWORD.equals(href)) {
			Program.launch(RESTORE_PASSWORD_URL);
		}
	}

	public void setDefaultTargetSettings(IZendTarget defaultTarget) {
		String username = defaultTarget
				.getProperty(ZendDevCloud.TARGET_USERNAME);
		if (username != null) {
			usernameText.setText(username);
		}
		String password = TargetsManagerService.INSTANCE
				.getPhpcloudPassword(defaultTarget);
		if (password != null) {
			passwordText.setText(password);
			shouldStoreButton.setSelection(true);
		}
		String privateKey = defaultTarget
				.getProperty(ZendDevCloud.SSH_PRIVATE_KEY_PATH);
		if (privateKey != null) {
			privateKeyText.setText(privateKey);
		}
	}

	public String[] getData() {
		return new String[] { usernameText.getText(), passwordText.getText(),
				privateKeyText.getText(),
				String.valueOf(shouldStoreButton.getSelection()) };
	}

	public IZendTarget[] createTarget(String[] data, IProgressMonitor monitor)
			throws CoreException, IOException {
		ZendDevCloud detect = new ZendDevCloud();
		String username = data[0];
		String password = data[1];
		String privateKeyPath = data[2];
		String shouldStore = data[3];

		monitor.subTask("Validating account information"); //$NON-NLS-1$

		if (username == null || username.trim().length() == 0) {
			throw new CoreException(new Status(IStatus.ERROR,
					Activator.PLUGIN_ID, "Username is required.")); //$NON-NLS-1$
		}

		if (password == null || password.trim().length() == 0) {
			throw new CoreException(new Status(IStatus.ERROR,
					Activator.PLUGIN_ID, "Password is required.")); //$NON-NLS-1$
		}

		if (privateKeyPath == null || privateKeyPath.trim().length() == 0) {
			throw new CoreException(new Status(IStatus.ERROR,
					Activator.PLUGIN_ID, "Private SSH key is required.")); //$NON-NLS-1$
		}

		monitor.subTask("Verifying SSH key"); //$NON-NLS-1$

		File keyFile = new File(privateKeyPath);
		if (!keyFile.exists()) {
			throw new CoreException(
					new Status(IStatus.ERROR, Activator.PLUGIN_ID,
							"Private SSH key file does not exist.")); //$NON-NLS-1$
		}

		JSCHPubKeyDecryptor decryptor = new JSCHPubKeyDecryptor();
		try {
			decryptor.isValidPrivateKey(privateKeyPath);
		} catch (PublicKeyNotFoundException e) {
			throw new CoreException(new Status(IStatus.ERROR,
					Activator.PLUGIN_ID, "Private SSH key is not valid.", e)); //$NON-NLS-1$
		}

		monitor.subTask("Detecting targets for " + username); //$NON-NLS-1$

		IZendTarget[] target;
		try {
			target = detect.detectTarget(username, password, privateKeyPath);
		} catch (SdkException e) {
			throw new CoreException(new Status(IStatus.ERROR,
					Activator.PLUGIN_ID, e.getMessage(), e));
		}
		if (target == null || target.length == 0) {
			return null;
		}

		TargetsManager tm = TargetsManagerService.INSTANCE.getTargetManager();
		ZendTarget[] zts = new ZendTarget[target.length];

		String uniqueId = tm.createUniqueId(null);
		for (int i = 0; i < target.length; i++) {
			zts[i] = new ZendTarget(uniqueId + "_" + i, target[i].getHost(), //$NON-NLS-1$
					target[i].getDefaultServerURL(), target[i].getKey(),
					target[i].getSecretKey(), true);
			zts[i].addProperty(ZendDevCloud.TARGET_USERNAME, username);
			zts[i].addProperty(ZendDevCloud.STORE_PASSWORD, shouldStore);
			zts[i].addProperty(ZendDevCloud.TARGET_PASSWORD, password);
			zts[i].addProperty(ZendDevCloud.TARGET_CONTAINER,
					target[i].getProperty(ZendDevCloud.TARGET_CONTAINER));
			zts[i].addProperty(ZendDevCloud.TARGET_TOKEN,
					target[i].getProperty(ZendDevCloud.TARGET_TOKEN));
			zts[i].addProperty(ZendDevCloud.SSH_PRIVATE_KEY_PATH,
					privateKeyPath);
			try {
				tm.add(zts[i], true);
			} catch (TargetException e) {
				// should not appear cause we do not try to connect to it
			} catch (LicenseExpiredException e) {
				// should not appear cause we do not try to connect to it
			}
		}
		return zts;
	}

	@Override
	public boolean hasPage() {
		return true;
	}

	@Override
	protected String getHelpResource() {
		return HelpContextIds.CREATING_A_ZEND_DEVELOPER_CLOUD_TARGET;
	}

	@Override
	protected boolean validatePage() {
		if (usernameText != null && usernameText.getText().trim().isEmpty()) {
			return false;
		}
		if (passwordText != null && passwordText.getText().trim().isEmpty()) {
			return false;
		}
		if (privateKeyText != null && privateKeyText.getText().trim().isEmpty()) {
			return false;
		}
		return true;
	}

}
