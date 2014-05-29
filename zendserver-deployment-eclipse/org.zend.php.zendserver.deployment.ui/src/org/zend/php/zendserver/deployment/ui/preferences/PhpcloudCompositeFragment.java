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
import java.net.URL;
import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IMessageProvider;
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
import org.eclipse.swt.widgets.Text;
import org.zend.php.server.ui.types.PhpcloudServerType;
import org.zend.php.zendserver.deployment.core.targets.EclipseSSH2Settings;
import org.zend.php.zendserver.deployment.core.targets.JSCHPubKeyDecryptor;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.core.tunnel.SSHTunnelConfiguration;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.sdklib.SdkException;
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
public class PhpcloudCompositeFragment extends AbstractCloudCompositeFragment {

	public static String ID = "org.zend.php.zendserver.deployment.ui.preferences.PhpcloudCompositeFragment"; //$NON-NLS-1$

	private static final String RESTORE_PASSWORD_URL = "http://www.zend.com/user/lost"; //$NON-NLS-1$

	private static final String GENERATED_KEY_FILENAME = "phpcloud"; //$NON-NLS-1$

	private Text usernameText;
	private Text passwordText;
	private Text privateKeyText;
	private Button shouldStoreButton;

	private String username;
	private String password;
	private String privateKey;
	private boolean shouldStore;

	public PhpcloudCompositeFragment(Composite parent, IControlHandler handler,
			boolean isForEditing) {
		super(parent, handler, isForEditing,
				Messages.PhpcloudCompositeFragment_Title,
				Messages.PhpcloudCompositeFragment_Desc);
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public void validate() {
		if (usernameText != null && usernameText.getText().trim().isEmpty()) {
			setMessage(Messages.PhpcloudCompositeFragment_EmptyUsernameError,
					IMessageProvider.ERROR);
			return;
		}
		if (passwordText != null && passwordText.getText().trim().isEmpty()) {
			setMessage(Messages.PhpcloudCompositeFragment_EmptyPasswordError,
					IMessageProvider.ERROR);
			return;
		}
		if (privateKeyText != null && privateKeyText.getText().trim().isEmpty()) {
			setMessage(Messages.PhpcloudCompositeFragment_EmptyKeyError,
					IMessageProvider.ERROR);
			return;
		}
		File keyFile = new File(privateKey);
		if (!keyFile.exists()) {
			setMessage(Messages.PhpcloudCompositeFragment_NotExistKeyError,
					IMessageProvider.ERROR);
			return;
		}

		JSCHPubKeyDecryptor decryptor = new JSCHPubKeyDecryptor();
		try {
			decryptor.isValidPrivateKey(privateKey);
		} catch (PublicKeyNotFoundException e) {
			setMessage(Messages.PhpcloudCompositeFragment_InvalidKeyError,
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
		label.setText(Messages.PhpcloudCompositeFragment_UsernameLabel);
		usernameText = new Text(composite, SWT.BORDER);
		GridData layoutData = new GridData(SWT.FILL, SWT.DEFAULT, true, false);
		layoutData.horizontalSpan = 3;
		usernameText.setLayoutData(layoutData);
		usernameText
				.setToolTipText(Messages.PhpcloudCompositeFragment_UsernameTooltip);
		usernameText.addModifyListener(modifyListener);
		usernameText.forceFocus();

		label = new Label(composite, SWT.NONE);
		label.setText(Messages.PhpcloudCompositeFragment_PasswordLabel);
		passwordText = new Text(composite, SWT.BORDER | SWT.PASSWORD);
		layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		layoutData.horizontalSpan = 2;
		passwordText.setLayoutData(layoutData);
		passwordText
				.setToolTipText(Messages.PhpcloudCompositeFragment_PasswordTooltip);
		passwordText.addModifyListener(modifyListener);

		Button restorePassword = new Button(composite, SWT.PUSH);
		layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		layoutData.minimumWidth = 80;
		restorePassword.setLayoutData(layoutData);
		restorePassword
				.setText(Messages.PhpcloudCompositeFragment_RestoreLabel);
		restorePassword.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Program.launch(RESTORE_PASSWORD_URL);
			}
		});

		new Label(composite, SWT.NONE);

		shouldStoreButton = new Button(composite, SWT.CHECK);
		layoutData = new GridData(SWT.FILL, SWT.TOP, true, false);
		layoutData.horizontalSpan = 3;
		shouldStoreButton
				.setText(Messages.PhpcloudCompositeFragment_SavePasswordMessage);
		shouldStoreButton.setLayoutData(layoutData);
		shouldStoreButton.setSelection(true);
		label = new Label(composite, SWT.NONE);
		label.setText(Messages.PhpcloudCompositeFragment_KeyLabel);
		privateKeyText = new Text(composite, SWT.BORDER);
		privateKeyText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		privateKeyText
				.setToolTipText(Messages.PhpcloudCompositeFragment_KeyTooltip);
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
		btnBrowse.setText(Messages.PhpcloudCompositeFragment_BrowseLabel);
		Button btnGenerate = new Button(composite, SWT.PUSH);
		btnGenerate.setText(Messages.PhpcloudCompositeFragment_GenerateLabel);
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
		label.setText(Messages.PhpcloudCompositeFragment_KeyDesc);
		layoutData = new GridData(SWT.FILL, SWT.TOP, true, false);
		layoutData.widthHint = 400;
		layoutData.horizontalSpan = 4;
		label.setLayoutData(layoutData);
	}

	@Override
	protected void init() {
		shouldStoreButton.setSelection(true);
	}

	@Override
	protected void detectServers(IProgressMonitor monitor) {
		try {
			IZendTarget[] targets = createTargets(monitor);
			if (targets != null) {
				TargetConnectionTester tester = new TargetConnectionTester();
				IStatus status = tester.testConnection(targets, monitor);
				switch (status.getSeverity()) {
				case IStatus.OK:
					TargetsManagerService.INSTANCE.getTargetManager()
							.removeAllTemporary();
					ArrayList<IZendTarget> finalTargets = tester
							.getFinalTargets();
					TargetsManager manager = TargetsManagerService.INSTANCE
							.getTargetManager();
					boolean dataInitialized = false;
					for (IZendTarget target : finalTargets) {
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
						server.setBaseURL(baseUrl.toString());
						server.setAttribute(IServerType.TYPE,
								PhpcloudServerType.ID);
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
					}
					ServersManager.save();
					break;
				case IStatus.WARNING:
					setMessage(status.getMessage(), IMessageProvider.WARNING);
					break;
				case IStatus.ERROR:
					setMessage(status.getMessage(), IMessageProvider.ERROR);
					break;
				default:
					break;
				}
			}
		} catch (CoreException e) {
			setMessage(e.getMessage(), IMessageProvider.ERROR);
		} catch (IOException e) {
			setMessage(e.getMessage(), IMessageProvider.ERROR);
		} finally {
			TargetsManagerService.INSTANCE.getTargetManager()
					.removeAllTemporary();
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
		if (shouldStoreButton != null) {
			shouldStore = shouldStoreButton.getSelection();
		}
		if (privateKeyText != null) {
			privateKey = privateKeyText.getText();
		}
	}

	@Override
	protected void setupSSHConfiguration(Server server, IZendTarget target) {
		SSHTunnelConfiguration config = SSHTunnelConfiguration
				.createPhpcloudConfiguration(server, target);
		config.store(server);
	}

	@Override
	protected String getGeneratedKeyName() {
		return GENERATED_KEY_FILENAME;
	}

	private IZendTarget[] createTargets(IProgressMonitor monitor)
			throws CoreException, IOException {
		ZendDevCloud detect = new ZendDevCloud();
		monitor.beginTask(
				Messages.PhpcloudCompositeFragment_DetectingContainersTitle,
				IProgressMonitor.UNKNOWN);
		IZendTarget[] targets = null;
		try {
			targets = detect.detectTarget(username, password, privateKey);
		} catch (SdkException e) {
			throw new CoreException(new Status(IStatus.ERROR,
					Activator.PLUGIN_ID, e.getMessage(), e));
		}
		if (targets == null || targets.length == 0) {
			return null;
		}

		TargetsManager tm = TargetsManagerService.INSTANCE.getTargetManager();
		ZendTarget[] updatedTargets = new ZendTarget[targets.length];

		String uniqueId = tm.createUniqueId(null);
		for (int i = 0; i < targets.length; i++) {
			IZendTarget target = targets[i];
			IZendTarget existingTarget = findExistingTarget(target);
			if (existingTarget != null) {
				updatedTargets[i] = (ZendTarget) existingTarget;
			} else {
				updatedTargets[i] = new ZendTarget(uniqueId + '_' + i,
						target.getHost(), target.getDefaultServerURL(),
						target.getKey(), target.getSecretKey(), true);
			}
			updatedTargets[i].addProperty(ZendDevCloud.TARGET_USERNAME,
					username);
			updatedTargets[i].addProperty(ZendDevCloud.STORE_PASSWORD,
					String.valueOf(shouldStore));
			updatedTargets[i].addProperty(ZendDevCloud.TARGET_PASSWORD,
					password);
			updatedTargets[i].addProperty(ZendDevCloud.TARGET_CONTAINER,
					target.getProperty(ZendDevCloud.TARGET_CONTAINER));
			updatedTargets[i].addProperty(ZendDevCloud.TARGET_TOKEN,
					target.getProperty(ZendDevCloud.TARGET_TOKEN));
			updatedTargets[i].addProperty(ZendDevCloud.SSH_PRIVATE_KEY_PATH,
					privateKey);
			try {
				tm.add(updatedTargets[i], true);
			} catch (TargetException e) {
				// should not appear cause we do not try to connect to it
			} catch (LicenseExpiredException e) {
				// should not appear cause we do not try to connect to it
			}
		}
		return updatedTargets;
	}

}
