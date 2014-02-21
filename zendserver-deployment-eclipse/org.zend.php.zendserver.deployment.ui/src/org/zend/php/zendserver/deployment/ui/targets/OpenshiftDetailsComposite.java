package org.zend.php.zendserver.deployment.ui.targets;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.statushandlers.StatusManager;
import org.zend.php.zendserver.deployment.core.targets.EclipseApiKeyDetector;
import org.zend.php.zendserver.deployment.core.targets.EclipseSSH2Settings;
import org.zend.php.zendserver.deployment.core.targets.JSCHPubKeyDecryptor;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.HelpContextIds;
import org.zend.php.zendserver.deployment.ui.Messages;
import org.zend.php.zendserver.deployment.ui.wizards.OpenShiftTargetData;
import org.zend.php.zendserver.deployment.ui.wizards.OpenShiftTargetWizard;
import org.zend.php.zendserver.deployment.ui.wizards.OpenShiftTargetWizardDialog;
import org.zend.sdklib.SdkException;
import org.zend.sdklib.internal.target.OpenShiftTarget;
import org.zend.sdklib.internal.target.PublicKeyNotFoundException;
import org.zend.sdklib.internal.target.ZendDevCloud;
import org.zend.sdklib.manager.TargetException;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;
import org.zend.sdklib.target.LicenseExpiredException;

/**
 * DevCloud details editing composite: username and password.
 */
public class OpenshiftDetailsComposite extends AbstractTargetDetailsComposite {

	private static final String RESTORE_PASSWORD_URL = "https://openshift.redhat.com/app/account/password/new"; //$NON-NLS-1$
	private static final String CREATE_ACCOUNT_URL = "https://openshift.redhat.com/app/account/new"; //$NON-NLS-1$

	private static final String GENERATED_KEY_FILENAME = "openshift"; //$NON-NLS-1$

	private Text usernameText;
	private Text passwordText;
	private Text privateKeyText;
	private String uploadedKeyName;
	
	private IStatus status;

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
		usernameText.setToolTipText(Messages.OpenshiftDetailsComposite_0);
		usernameText.addModifyListener(modifyListener);

		label = new Label(composite, SWT.NONE);
		label.setText(Messages.OpenshiftDetailsComposite_1);
		passwordText = new Text(composite, SWT.BORDER | SWT.PASSWORD);
		layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		layoutData.horizontalSpan = 2;
		passwordText.setLayoutData(layoutData);
		passwordText.setToolTipText(Messages.OpenshiftDetailsComposite_2);
		passwordText.addModifyListener(modifyListener);
		
		Button restorePassword = new Button(composite, SWT.PUSH);
		layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		layoutData.minimumWidth = 80;
		restorePassword.setLayoutData(layoutData);
		restorePassword.setText(Messages.DevCloudDetailsComposite_RestorePassword);
		restorePassword.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				Program.launch(RESTORE_PASSWORD_URL);
			}
		});
		
		Composite newButtonsGroup = new Composite(composite, SWT.NONE);
		layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
		newButtonsGroup.setLayoutData(layoutData);
		GridLayout gd = new GridLayout(2, false);
		gd.marginWidth = 0;
		gd.marginHeight = 0;
		gd.horizontalSpacing = 0;
		newButtonsGroup.setLayout(gd);
		
		Button createAccount = new Button(newButtonsGroup, SWT.PUSH);
		layoutData = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		createAccount.setLayoutData(layoutData);
		createAccount.setText(Messages.OpenshiftDetailsComposite_3);
		createAccount.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				Program.launch(CREATE_ACCOUNT_URL);
			}
		});
		
		Button createTarget = new Button(newButtonsGroup, SWT.PUSH);
		layoutData = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		createTarget.setLayoutData(layoutData);
		createTarget.setText(Messages.OpenshiftDetailsComposite_4);
		createTarget.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				openTargetWizard();
			}
		});

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

		OpenShiftTarget detect = new OpenShiftTarget(username, password, new EclipseApiKeyDetector());
		monitor.subTask("Validating account information"); //$NON-NLS-1$

		if (username == null || username.trim().length() == 0) {
			throw new CoreException(new Status(IStatus.ERROR,
					Activator.PLUGIN_ID, "Username is required.")); //$NON-NLS-1$
		}

		if (password == null || password.trim().length() == 0) {
			throw new CoreException(new Status(IStatus.ERROR,
					Activator.PLUGIN_ID, "Password is required.")); //$NON-NLS-1$
		}

		if (privateKey == null || privateKey.trim().length() == 0) {
			throw new CoreException(new Status(IStatus.ERROR,
					Activator.PLUGIN_ID, "Private SSH key is required.")); //$NON-NLS-1$
		}

		File keyFile = new File(privateKey);
		if (!keyFile.exists()) {
			throw new CoreException(
					new Status(IStatus.ERROR, Activator.PLUGIN_ID,
							"Private SSH key file does not exist.")); //$NON-NLS-1$
		}

		JSCHPubKeyDecryptor decryptor = new JSCHPubKeyDecryptor();
		try {
			decryptor.isValidPrivateKey(privateKey);
		} catch (PublicKeyNotFoundException e) {
			throw new CoreException(new Status(IStatus.ERROR,
					Activator.PLUGIN_ID, "Private SSH key is not valid.", e)); //$NON-NLS-1$
		}

		try {
			uploadedKeyName = detect.uploadPublicKey(privateKey, decryptor);
		} catch (SdkException ex) {
			throw new CoreException(new Status(IStatus.ERROR,
					Activator.PLUGIN_ID, ex.getMessage(), ex));
		}

		monitor.subTask("Detecting targets for " + username); //$NON-NLS-1$

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
							"Could not detect any valid targets. " //$NON-NLS-1$
									+ "You can create new OpenShift target by using 'Create New Target' button below.")); //$NON-NLS-1$
		}
		TargetsManager tm = TargetsManagerService.INSTANCE.getTargetManager();
		for (IZendTarget target : targets) {
			try {
				tm.add(target, true);
			} catch (TargetException e) {
				// should not appear cause we do not try to connect to it
			} catch (LicenseExpiredException e) {
				// should not appear cause we do not try to connect to it
			}
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

	private void openTargetWizard() {
		final List<String> gearProfiles = new ArrayList<String>();
		final List<String> zendTargets = new ArrayList<String>();
		final List<String> zendCartridges = new ArrayList<String>();
		final String username = usernameText.getText();
		final String password = passwordText.getText();
		if (username == null || username.trim().length() == 0
				|| password == null || password.trim().length() == 0) {
			setErrorMessage("Username and password are required."); //$NON-NLS-1$
			return;
		}
		try {
			doOpenTargetWizard(gearProfiles, zendTargets, zendCartridges,
					username, password);
		} catch (InvocationTargetException e) {
			Activator.log(e);
			Throwable a = e.getTargetException();
			if (a != null) {
				setErrorMessage(OpenShiftTarget.getOpenShiftMessage(a));
				return;
			}
		} catch (InterruptedException e) {
			Activator.log(e);
		}
	}

	private void doOpenTargetWizard(final List<String> gearProfiles,
			final List<String> zendTargets, final List<String> zendCartridges,
			final String username, final String password)
			throws InvocationTargetException, InterruptedException {
		runnableContext.run(true, false, new IRunnableWithProgress() {

			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				OpenShiftTarget target = new OpenShiftTarget(username, password);
				monitor.beginTask("Retrieving data from OpenShift account...", //$NON-NLS-1$
						IProgressMonitor.UNKNOWN);
				try {
					if (target.hasDomain()) {
						gearProfiles.addAll(target.getAvaliableGearProfiles());
						zendTargets.addAll(target.getAllZendTargets());
						zendCartridges.addAll(target.getZendCartridges());
					}
					Display.getDefault().asyncExec(new Runnable() {

						public void run() {
							OpenShiftTargetData data = new OpenShiftTargetData();
							data.setGearProfiles(gearProfiles);
							data.setZendTargets(zendTargets);
							data.setZendCartridges(zendCartridges);
							Shell shell = PlatformUI.getWorkbench()
									.getActiveWorkbenchWindow().getShell();
							WizardDialog dialog = new OpenShiftTargetWizardDialog(
									shell, new OpenShiftTargetWizard(
											username, password, data), data);
							if (dialog.open() == Window.OK) {
								setMessage(Messages.OpenshiftDetailsComposite_5);
							}
							changeSupport.firePropertyChange(PROP_MODIFY, null, validatePage());
						}
					});
				} catch (final SdkException e) {
					monitor.done();
					Display.getDefault().asyncExec(new Runnable() {

						public void run() {
							setErrorMessage(e.getCause() != null ? e.getCause()
									.getMessage() : e.getMessage());
						}
					});
				}
				monitor.done();
			}
		});
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
							Messages.OpenshiftDetailsComposite_6,
							Messages.bind(
									Messages.OpenshiftDetailsComposite_7,
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
