/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies.
 * All rights reserved. This program and the accompanying materials
 * are the copyright of Zend Technologies and is protected under
 * copyright laws of the United States.
 * You must not copy, adapt or redistribute this document for 
 * any use.
 *******************************************************************************/
package org.zend.php.zendserver.deployment.ui.openshift;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.server.core.manager.ServersManager;
import org.eclipse.php.internal.ui.wizards.IControlHandler;
import org.eclipse.php.server.ui.types.IServerType;
import org.eclipse.swt.SWT;
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
import org.zend.php.server.ui.types.OpenShiftServerType;
import org.zend.php.zendserver.deployment.core.targets.EclipseApiKeyDetector;
import org.zend.php.zendserver.deployment.core.targets.EclipseSSH2Settings;
import org.zend.php.zendserver.deployment.core.targets.JSCHPubKeyDecryptor;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.core.tunnel.SSHTunnelConfiguration;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.servers.AbstractCloudCompositeFragment;
import org.zend.php.zendserver.deployment.ui.servers.TargetConnectionTester;
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
public class OpenShiftCompositeFragment extends AbstractCloudCompositeFragment {

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
		usernameText.forceFocus();

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
				final String key = generateKey();
				if (key != null) {
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							privateKeyText.setText(key);
						}
					});
				}
				updateData();
				validate();
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

	@Override
	protected void detectServers(IProgressMonitor monitor) {
		try {
			IZendTarget[] targets = createTargets(monitor);
			IStatus finalStatus = Status.OK_STATUS;
			if (targets != null) {
				TargetConnectionTester tester = new TargetConnectionTester();
				IStatus[] results = tester.testConnection(targets, monitor);
				boolean dataInitialized = false;
				List<IZendTarget> finalTargets = tester.getFinalTargets();
				TargetsManager manager = TargetsManagerService.INSTANCE
						.getTargetManager();
				for (int i = 0; i < results.length; i++) {
					IStatus status = results[i];
					IZendTarget target = finalTargets.get(i);
					switch (status.getSeverity()) {
					case IStatus.OK:
						String host = target.getHost().getHost();
						URL baseUrl = new URL("http", host, ""); //$NON-NLS-1$ //$NON-NLS-2$
						ZendTarget t = (ZendTarget) target;
						Server server = null;
						Server existingServer = getExistingServer(host);
						if (existingServer != null) {
							server = existingServer;
						} else {
							server = new Server();
							server.setName(host);
						}
						server.setHost(host);
						server.setName(target.getHost().getHost());
						server.setBaseURL(baseUrl.toString());
						server.setAttribute(IServerType.TYPE,
								OpenShiftServerType.ID);
						setupSSHConfiguration(server, target);
						t.setDefaultServerURL(baseUrl);
						t.setServerName(server.getName());
						IZendTarget existingTarget = manager.getTargetById(t
								.getId());
						try {
							if (existingTarget != null) {
								manager.updateTarget(copy(t), true);
							} else {
								manager.add(copy(t), true);
							}
						} catch (TargetException e) {
							// cannot occur, suppress connection
						} catch (LicenseExpiredException e) {
							// cannot occur, suppress connection
						}
						if (dataInitialized) {
							ServersManager.addServer(server);
						} else {
							setData(server);
							dataInitialized = true;
						}
						break;
					case IStatus.ERROR:
						finalStatus = new Status(
								IStatus.WARNING,
								Activator.PLUGIN_ID,
								MessageFormat
										.format(Messages.OpenShiftCompositeFragment_WarningMessage,
												status.getMessage()));
						break;
					default:
						break;
					}
				}
				if (dataInitialized) {
					ServersManager.save();
					switch (finalStatus.getSeverity()) {
					case IStatus.WARNING:
						showWarningMessage(
								Messages.OpenShiftCompositeFragment_WarningTitle,
								finalStatus.getMessage());
						break;
					default:
						break;
					}
				} else {
					setMessage(finalStatus.getMessage(), IMessageProvider.ERROR);
				}
			}
		} catch (CoreException e) {
			setMessage(e.getMessage(), IMessageProvider.ERROR);
		} catch (IOException e) {
			setMessage(e.getMessage(), IMessageProvider.ERROR);
		}
	}

	@Override
	protected void updateData() {
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

	@Override
	protected void setupSSHConfiguration(Server server, IZendTarget target) {
		SSHTunnelConfiguration config = SSHTunnelConfiguration
				.createOpenShiftConfiguration(target);
		config.store(server);
	}

	@Override
	protected String getGeneratedKeyName() {
		return GENERATED_KEY_FILENAME;
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
		
		targets = removeExistingTargets(targets, tm);
		
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

}
