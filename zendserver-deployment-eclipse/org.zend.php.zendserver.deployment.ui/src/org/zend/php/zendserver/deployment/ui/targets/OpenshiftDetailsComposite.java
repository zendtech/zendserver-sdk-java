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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.statushandlers.StatusManager;
import org.zend.php.zendserver.deployment.core.targets.EclipseSSH2Settings;
import org.zend.php.zendserver.deployment.core.targets.JSCHPubKeyDecryptor;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.HelpContextIds;
import org.zend.php.zendserver.deployment.ui.Messages;
import org.zend.sdklib.SdkException;
import org.zend.sdklib.internal.target.OpenShiftTarget;
import org.zend.sdklib.internal.target.PublicKeyNotFoundException;
import org.zend.sdklib.internal.target.ZendDevCloud;
import org.zend.sdklib.target.IZendTarget;

/**
 * DevCloud details editing composite: username and password.
 */
public class OpenshiftDetailsComposite extends AbstractTargetDetailsComposite {

	private static final String HREF_RESTORE_PASSWORD = "restorePassword"; //$NON-NLS-1$
	private static final String HREF_CREATE_ACCOUNT = "createAccount"; //$NON-NLS-1$

	private static final String RESTORE_PASSWORD_URL = "https://openshift.redhat.com/app/account/password/new"; //$NON-NLS-1$
	private static final String CREATE_ACCOUNT_URL = "https://openshift.redhat.com/app/account/new"; //$NON-NLS-1$

	private static final String GENERATED_KEY_FILENAME = "openshift"; //$NON-NLS-1$

	private Text usernameText;
	private Text passwordText;
	private Text privateKeyText;
	private String uploadedKeyName;

	public Composite create(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(4, false));

		ModifyListener modifyListener = new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				changeSupport.firePropertyChange(PROP_MODIFY, null,
						((Text) e.getSource()).getText());
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
		label.setText("Password:");
		passwordText = new Text(composite, SWT.BORDER | SWT.PASSWORD);
		passwordText.setLayoutData(layoutData);
		passwordText.setToolTipText("Restore Password");
		passwordText.addModifyListener(modifyListener);

		Composite hyperlinks = new Composite(composite, SWT.NONE);
		GridData gd = new GridData(SWT.RIGHT, SWT.TOP, true, false, 4, 1);
		hyperlinks.setLayoutData(gd);
		hyperlinks.setLayout(new GridLayout(2, false));

		Hyperlink createAccount = new Hyperlink(hyperlinks, SWT.NONE);
		createAccount.setUnderlined(true);
		createAccount.setForeground(Display.getDefault().getSystemColor(
				SWT.COLOR_BLUE));
		createAccount.setText("Create new OpenShift account");
		createAccount.setHref(HREF_CREATE_ACCOUNT);

		Hyperlink forgotPassword = new Hyperlink(hyperlinks, SWT.NONE);
		forgotPassword.setUnderlined(true);
		forgotPassword.setForeground(Display.getDefault().getSystemColor(
				SWT.COLOR_BLUE));
		forgotPassword
				.setText(Messages.DevCloudDetailsComposite_RestorePassword);
		forgotPassword.setHref(HREF_RESTORE_PASSWORD);

		IHyperlinkListener hrefListener = new HyperlinkAdapter() {

			public void linkActivated(HyperlinkEvent e) {
				handleHyperlink(e.getHref());
			}
		};

		createAccount.addHyperlinkListener(hrefListener);
		forgotPassword.addHyperlinkListener(hrefListener);

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
		btnBrowse.setText(Messages.DevCloudDetailsComposite_2);
		Button btnGenerate = new Button(composite, SWT.PUSH);
		btnGenerate.setText(Messages.DevCloudDetailsComposite_3);
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

	public void setDefaultTargetSettings(IZendTarget defaultTarget) {
		String username = defaultTarget
				.getProperty(OpenShiftTarget.TARGET_USERNAME);
		if (username != null) {
			usernameText.setText(username);
		}
		String privateKey = defaultTarget
				.getProperty(OpenShiftTarget.SSH_PRIVATE_KEY_PATH);
		if (privateKey != null) {
			privateKeyText.setText(privateKey);
		}
	}

	public String[] getData() {
		return new String[] { usernameText.getText(), passwordText.getText(),
				privateKeyText.getText() };
	}

	public IZendTarget[] createTarget(String[] data, IProgressMonitor monitor)
			throws CoreException, IOException {
		String username = data[0];
		String password = data[1];
		String privateKey = data[2];

		OpenShiftTarget detect = new OpenShiftTarget(username, password);

		monitor.subTask("Validating account information");

		if (username == null || username.trim().length() == 0) {
			throw new CoreException(new Status(IStatus.ERROR,
					Activator.PLUGIN_ID, "Username is required."));
		}

		if (password == null || password.trim().length() == 0) {
			throw new CoreException(new Status(IStatus.ERROR,
					Activator.PLUGIN_ID, "Password is required."));
		}

		if (privateKey == null || privateKey.trim().length() == 0) {
			throw new CoreException(new Status(IStatus.ERROR,
					Activator.PLUGIN_ID, "Private SSH key is required."));
		}

		File keyFile = new File(privateKey);
		if (!keyFile.exists()) {
			throw new CoreException(
					new Status(IStatus.ERROR, Activator.PLUGIN_ID,
							"Private SSH key file does not exist."));
		}

		JSCHPubKeyDecryptor decryptor = new JSCHPubKeyDecryptor();
		try {
			decryptor.isValidPrivateKey(privateKey);
		} catch (PublicKeyNotFoundException e) {
			throw new CoreException(new Status(IStatus.ERROR,
					Activator.PLUGIN_ID, "Private SSH key is not valid.", e));
		}

		try {
			uploadedKeyName = detect.uploadPublicKey(privateKey, decryptor);
		} catch (SdkException ex) {
			throw new CoreException(
					new Status(IStatus.ERROR, Activator.PLUGIN_ID,
							"Could not upload new public key.", ex));
		}

		monitor.subTask("Detecting targets for " + username);

		IZendTarget[] targets = null;
		try {
			targets = detect.detectTargets(privateKey,
					TargetsManagerService.INSTANCE.getTargetManager(),
					decryptor);
		} catch (SdkException e) {
			throw new CoreException(new Status(IStatus.ERROR,
					Activator.PLUGIN_ID, e.getMessage(), e));
		}
		if (targets == null) {
			throw new CoreException(
					new Status(
							IStatus.ERROR,
							Activator.PLUGIN_ID,
							"Could not detect any valid targets. "
									+ "You have to create at least one OpenShift application with zend-5.6 type."));
		}
		return targets;
	}

	@Override
	public boolean hasPage() {
		return true;
	}

	@Override
	protected String getHelpResource() {
		return HelpContextIds.CREATING_A_ZEND_DEVELOPER_CLOUD_TARGET;
	}

	protected void handleHyperlink(Object href) {
		if (HREF_CREATE_ACCOUNT.equals(href)) {
			Program.launch(CREATE_ACCOUNT_URL);
		} else if (HREF_RESTORE_PASSWORD.equals(href)) {
			Program.launch(RESTORE_PASSWORD_URL);
		}
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

			boolean confirm = MessageDialog
					.openConfirm(
							privateKeyText.getShell(),
							"Generate Key",
							Messages.bind(
									"New SSH RSA private key will be written to {0}. Do you want to continue?",
									file));
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

}
