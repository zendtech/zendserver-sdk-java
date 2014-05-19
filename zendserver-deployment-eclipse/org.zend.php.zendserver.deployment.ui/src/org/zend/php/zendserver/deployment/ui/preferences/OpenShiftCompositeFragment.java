/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies.
 * All rights reserved. This program and the accompanying materials
 * are the copyright of Zend Technologies and is protected under
 * copyright laws of the United States.
 * You must not copy, adapt or redistribute this document for 
 * any use.
 *******************************************************************************/
package org.zend.php.zendserver.deployment.ui.preferences;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.server.core.manager.ServersManager;
import org.eclipse.php.internal.ui.wizards.IControlHandler;
import org.eclipse.php.server.ui.types.IServerType;
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
import org.zend.php.server.ui.fragments.AbstractCompositeFragment;
import org.zend.php.server.ui.types.OpenShiftServerType;
import org.zend.php.zendserver.deployment.core.targets.EclipseApiKeyDetector;
import org.zend.php.zendserver.deployment.core.targets.EclipseSSH2Settings;
import org.zend.php.zendserver.deployment.core.targets.JSCHPubKeyDecryptor;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.core.tunnel.PortForwarding;
import org.zend.php.zendserver.deployment.core.tunnel.SSHTunnelConfiguration;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.wizards.OpenShiftTargetData;
import org.zend.php.zendserver.deployment.ui.wizards.OpenShiftTargetWizard;
import org.zend.php.zendserver.deployment.ui.wizards.OpenShiftTargetWizardDialog;
import org.zend.sdklib.SdkException;
import org.zend.sdklib.internal.target.OpenShiftTarget;
import org.zend.sdklib.internal.target.PublicKeyNotFoundException;
import org.zend.sdklib.internal.target.ZendDevCloud;
import org.zend.sdklib.internal.target.ZendTarget;
import org.zend.sdklib.manager.TargetException;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;
import org.zend.sdklib.target.LicenseExpiredException;

/**
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public class OpenShiftCompositeFragment extends AbstractCompositeFragment {

	public static String ID = "org.zend.php.zendserver.deployment.ui.preferences.PhpcloudCompositeFragment"; //$NON-NLS-1$

	private static final String RESTORE_PASSWORD_URL = "https://openshift.redhat.com/app/account/password/new"; //$NON-NLS-1$
	private static final String CREATE_ACCOUNT_URL = "https://openshift.redhat.com/app/account/new"; //$NON-NLS-1$

	private static final String GENERATED_KEY_FILENAME = "openshift"; //$NON-NLS-1$

	private Text usernameText;
	private Text passwordText;
	private Text privateKeyText;

	private String username;
	private String password;
	private String privateKey;

	public OpenShiftCompositeFragment(Composite parent,
			IControlHandler handler, boolean isForEditing) {
		super(parent, handler, isForEditing,
				Messages.OpenShiftCompositeFragment_Title,
				Messages.OpenShiftCompositeFragment_Desc);
		createControl(isForEditing);
	}

	@Override
	public boolean performOk() {
		return isComplete();
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public void validate() {
		if (usernameText != null && usernameText.getText().trim().isEmpty()) {
			setMessage(Messages.OpenShiftCompositeFragment_EmptyUsernameError,
					IMessageProvider.ERROR);
			return;
		}
		if (passwordText != null && passwordText.getText().trim().isEmpty()) {
			setMessage(Messages.OpenShiftCompositeFragment_EmptyPasswordError,
					IMessageProvider.ERROR);
			return;
		}
		if (privateKeyText != null && privateKeyText.getText().trim().isEmpty()) {
			setMessage(Messages.OpenShiftCompositeFragment_EmptyKeyError,
					IMessageProvider.ERROR);
			return;
		}
		File keyFile = new File(privateKey);
		if (!keyFile.exists()) {
			setMessage(Messages.OpenShiftCompositeFragment_NotExistKeyError,
					IMessageProvider.ERROR);
			return;
		}

		JSCHPubKeyDecryptor decryptor = new JSCHPubKeyDecryptor();
		try {
			decryptor.isValidPrivateKey(privateKey);
		} catch (PublicKeyNotFoundException e) {
			setMessage(Messages.OpenShiftCompositeFragment_InvalidKeyError,
					IMessageProvider.ERROR);
			return;
		}
		setMessage(getDescription(), IMessageProvider.NONE);
	}

	@Override
	protected void createControl(Composite parent) {
		ModifyListener modifyListener = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateData();
				validate();
			}
		};

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3,
				1));
		GridLayout layout = new GridLayout(4, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		composite.setLayout(layout);

		Label label = new Label(composite, SWT.NONE);
		label.setText(Messages.OpenShiftCompositeFragment_UsernameLabel);
		usernameText = new Text(composite, SWT.BORDER);
		GridData layoutData = new GridData(SWT.FILL, SWT.DEFAULT, true, false);
		layoutData.horizontalSpan = 3;
		usernameText.setLayoutData(layoutData);
		usernameText
				.setToolTipText(Messages.OpenShiftCompositeFragment_UsernameTooltip);
		usernameText.addModifyListener(modifyListener);

		label = new Label(composite, SWT.NONE);
		label.setText(Messages.OpenShiftCompositeFragment_PasswordLabel);
		passwordText = new Text(composite, SWT.BORDER | SWT.PASSWORD);
		layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		layoutData.horizontalSpan = 2;
		passwordText.setLayoutData(layoutData);
		passwordText
				.setToolTipText(Messages.OpenShiftCompositeFragment_PasswordTooltip);
		passwordText.addModifyListener(modifyListener);

		Button restorePassword = new Button(composite, SWT.PUSH);
		layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		layoutData.minimumWidth = 80;
		restorePassword.setLayoutData(layoutData);
		restorePassword
				.setText(Messages.OpenShiftCompositeFragment_RestoreLabel);
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
		createAccount
				.setText(Messages.OpenShiftCompositeFragment_CreateAccountLabel);
		createAccount.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				Program.launch(CREATE_ACCOUNT_URL);
			}
		});

		Button createTarget = new Button(newButtonsGroup, SWT.PUSH);
		layoutData = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		createTarget.setLayoutData(layoutData);
		createTarget
				.setText(Messages.OpenShiftCompositeFragment_CreateTargetLabel);
		createTarget.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				openTargetWizard();
			}
		});

		label = new Label(composite, SWT.NONE);
		label.setText(Messages.OpenShiftCompositeFragment_KeyLabel);
		privateKeyText = new Text(composite, SWT.BORDER);
		privateKeyText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		privateKeyText
				.setToolTipText(Messages.OpenShiftCompositeFragment_KeyTooltip);
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
		btnBrowse.setText(Messages.OpenShiftCompositeFragment_BrowseLabel);
		Button btnGenerate = new Button(composite, SWT.PUSH);
		btnGenerate.setText(Messages.OpenShiftCompositeFragment_GenerateLabel);
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
		label.setText(Messages.OpenShiftCompositeFragment_KeyDesc);
		layoutData = new GridData(SWT.FILL, SWT.TOP, true, false);
		layoutData.widthHint = 400;
		layoutData.horizontalSpan = 4;
		label.setLayoutData(layoutData);

	}

	@Override
	protected void init() {
	}

	protected void detectServers(IProgressMonitor monitor) {
		try {
			IZendTarget[] targets = createTargets(monitor);
			if (targets != null) {
				TargetConnectionTester tester = new TargetConnectionTester();
				IStatus status = tester.testConnection(targets, monitor);
				switch (status.getSeverity()) {
				case IStatus.OK:
					ArrayList<IZendTarget> finalTargets = tester
							.getFinalTargets();
					TargetsManager manager = TargetsManagerService.INSTANCE
							.getTargetManager();
					boolean dataInitialized = false;
					for (IZendTarget target : finalTargets) {
						URL baseUrl = new URL("http", target.getHost() //$NON-NLS-1$
								.getHost(), ""); //$NON-NLS-1$
						ZendTarget t = (ZendTarget) target;
						Server server = null;
						if (dataInitialized) {
							server = new Server();
						} else {
							server = getServer();
						}
						server.setName(target.getHost().getHost());
						server.setBaseURL(baseUrl.toString());
						server.setAttribute(IServerType.TYPE,
								OpenShiftServerType.ID);
						setupSSHConfiguration(server, target);
						if (dataInitialized) {
							ServersManager.addServer(server);
						} else {
							dataInitialized = true;
						}
						t.setDefaultServerURL(baseUrl);
						t.setServerName(server.getName());
						if (manager.getTargetById(t.getId()) != null) {
							manager.remove(manager.getTargetById(t.getId()));
						}
						try {
							manager.add(copy(t), true);
						} catch (TargetException e) {
							// cannot occur, suppress connection
						} catch (LicenseExpiredException e) {
							// cannot occur, suppress connection
						}
					}
					ServersManager.save();
					break;
				case IStatus.WARNING:
					final String warning = status.getMessage();
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							setMessage(warning, IMessageProvider.WARNING);
						}
					});
					break;
				case IStatus.ERROR:
					final String error = status.getMessage();
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							setMessage(error, IMessageProvider.ERROR);
						}
					});
					break;
				default:
					break;
				}
			}
		} catch (CoreException e) {
			setMessage(e.getMessage(), IMessageProvider.ERROR);
		} catch (IOException e) {
			setMessage(e.getMessage(), IMessageProvider.ERROR);
		}
	}

	private void updateData() {
		if (usernameText != null) {
			username = usernameText.getText();
		}
		if (passwordText != null) {
			password = passwordText.getText();
		}
		if (privateKeyText != null) {
			privateKey = privateKeyText.getText();
		}
	}

	private IZendTarget[] createTargets(IProgressMonitor monitor)
			throws CoreException, IOException {
		OpenShiftTarget detect = new OpenShiftTarget(username, password,
				new EclipseApiKeyDetector());
		monitor.beginTask(
				Messages.OpenShiftCompositeFragment_DetectingAppsTitle,
				IProgressMonitor.UNKNOWN);
		JSCHPubKeyDecryptor decryptor = new JSCHPubKeyDecryptor();
		try {
			detect.uploadPublicKey(privateKey, decryptor);
		} catch (SdkException ex) {
			throw new CoreException(new Status(IStatus.ERROR,
					Activator.PLUGIN_ID, ex.getMessage(), ex));
		}

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
			throw new CoreException(new Status(IStatus.ERROR,
					Activator.PLUGIN_ID,
					Messages.OpenShiftCompositeFragment_NoValidAppsError));
		}
		if (targets == null || targets.length == 0) {
			return null;
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

	private void openTargetWizard() {
		final String username = usernameText.getText();
		final String password = passwordText.getText();
		if (username == null || username.trim().length() == 0
				|| password == null || password.trim().length() == 0) {
			setMessage(Messages.OpenShiftCompositeFragment_NoCredentialsError,
					IMessageProvider.ERROR);
			return;
		}
		try {
			OpenShiftTarget target = new OpenShiftTarget(username, password);
			doOpenTargetWizard(target);
		} catch (InvocationTargetException e) {
			Activator.log(e);
			Throwable a = e.getTargetException();
			if (a != null) {
				setMessage(OpenShiftTarget.getOpenShiftMessage(a),
						IMessageProvider.ERROR);
				return;
			}
		} catch (InterruptedException e) {
			Activator.log(e);
		}
	}

	private void doOpenTargetWizard(final OpenShiftTarget target)
			throws InvocationTargetException, InterruptedException {
		controlHandler.run(true, false, new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				monitor.beginTask("Retrieving data from OpenShift account...", //$NON-NLS-1$
						IProgressMonitor.UNKNOWN);
				try {
					final OpenShiftTargetData data = new OpenShiftTargetData();
					data.setTarget(target);
					if (target.hasDomain()) {
						data.setGearProfiles(target.getAvaliableGearProfiles());
						data.setZendTargets(target.getAllZendTargets());
						data.setMySqlCartridges(target.getMySqlCartridges());
					} else {
						data.setGearProfiles(new ArrayList<String>());
						data.setZendTargets(new ArrayList<String>());
						data.setMySqlCartridges(new ArrayList<String>());
					}
					Display.getDefault().asyncExec(new Runnable() {

						public void run() {
							Shell shell = PlatformUI.getWorkbench()
									.getActiveWorkbenchWindow().getShell();
							WizardDialog dialog = new OpenShiftTargetWizardDialog(
									shell, new OpenShiftTargetWizard(data),
									data);
							if (dialog.open() == Window.OK) {
								setMessage(
										Messages.OpenShiftCompositeFragment_AppCreatedMessage,
										IMessageProvider.INFORMATION);
							}
						}
					});
				} catch (final SdkException e) {
					monitor.done();
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							setMessage(e.getCause() != null ? e.getCause()
									.getMessage() : e.getMessage(),
									IMessageProvider.ERROR);
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
							Messages.OpenShiftCompositeFragment_GenerateKeyTitle,
							Messages.bind(
									Messages.OpenShiftCompositeFragment_GenerateKeyMessage,
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

	private void setupSSHConfiguration(Server server, IZendTarget target) {
		SSHTunnelConfiguration config = new SSHTunnelConfiguration();
		config.setEnabled(true);
		String uuid = target.getProperty(OpenShiftTarget.TARGET_UUID);
		config.setUsername(uuid);
		config.setPrivateKey(target
				.getProperty(OpenShiftTarget.SSH_PRIVATE_KEY_PATH));
		List<PortForwarding> portForwardings = new ArrayList<PortForwarding>();
		String internalHost = target
				.getProperty(OpenShiftTarget.TARGET_INTERNAL_HOST);
		portForwardings.add(PortForwarding.createRemote(internalHost, 17000,
				"127.0.0.1", 17000)); //$NON-NLS-1$
		// TODO set correct db port
		portForwardings.add(PortForwarding.createLocal(12333, internalHost,
				3306));
		config.setPortForwardings(portForwardings);
		config.store(server);
	}

	private IZendTarget copy(ZendTarget t) {
		ZendTarget target = new ZendTarget(t.getId(), t.getHost(),
				t.getDefaultServerURL(), t.getKey(), t.getSecretKey(), false);
		String[] keys = t.getPropertiesKeys();
		for (String key : keys) {
			target.addProperty(key, t.getProperty(key));
		}
		return target;
	}

}
