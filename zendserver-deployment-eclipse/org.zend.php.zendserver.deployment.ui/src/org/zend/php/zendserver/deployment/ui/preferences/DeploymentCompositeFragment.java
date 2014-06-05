/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies.
 * All rights reserved. This program and the accompanying materials
 * are the copyright of Zend Technologies and is protected under
 * copyright laws of the United States.
 * You must not copy, adapt or redistribute this document for 
 * any use.
 *******************************************************************************/
package org.zend.php.zendserver.deployment.ui.preferences;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.ui.wizards.IControlHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.zend.php.server.ui.fragments.AbstractCompositeFragment;
import org.zend.php.zendserver.deployment.core.targets.EclipseApiKeyDetector;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.sdklib.SdkException;
import org.zend.sdklib.internal.target.ApiKeyDetector;
import org.zend.sdklib.internal.target.ZendTarget;
import org.zend.sdklib.manager.TargetException;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;
import org.zend.sdklib.target.InvalidCredentialsException;
import org.zend.sdklib.target.LicenseExpiredException;

/**
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public class DeploymentCompositeFragment extends AbstractCompositeFragment {

	private static final String DEFAULT_HOST = "http://"; //$NON-NLS-1$

	public static String ID = "org.zend.php.zendserver.deployment.ui.preferences.DeploymentCompositeFragment"; //$NON-NLS-1$

	private Button enableButton;
	private Text hostText;
	private Text keyText;
	private Text secretText;

	private boolean enable;
	private String host;
	private String key;
	private String secret;

	private IZendTarget target;

	private Button detectButton;

	/**
	 * PlatformCompositeFragment constructor
	 * 
	 * @param parent
	 * @param handler
	 * @param isForEditing
	 */
	public DeploymentCompositeFragment(Composite parent,
			IControlHandler handler, boolean isForEditing) {
		super(parent, handler, isForEditing,
				Messages.DeploymentCompositeFragment_Title,
				Messages.DeploymentCompositeFragment_Description);
		createControl(isForEditing);
	}

	public IZendTarget getTarget() {
		return target;
	}

	@Override
	public boolean performOk() {
		if (!enable) {
			if (target != null) {
				TargetsManager manager = TargetsManagerService.INSTANCE
						.getTargetManager();
				if (manager.getTargetById(target.getId()) != null) {
					manager.remove(target);
				}
			}
			return true;
		}
		if (!isForEditing()) {
			return isComplete();
		}
		if (target != null && !isModified()) {
			return true;
		}
		try {
			controlHandler.run(true, true, new IRunnableWithProgress() {

				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					performTesting(monitor);
				}
			});
		} catch (InvocationTargetException e) {
			Activator.log(e);
			return false;
		} catch (InterruptedException e) {
			Activator.log(e);
			return false;
		}
		return isComplete();
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public void validate() {
		if (enableButton.getSelection()) {
			if (host != null && host.trim().isEmpty()) {
				setMessage(
						Messages.DeploymentCompositeFragment_EmptyHostMessage,
						IMessageProvider.ERROR);
				return;
			}
			if (key != null && key.trim().isEmpty()) {
				setMessage(
						Messages.DeploymentCompositeFragment_EmptyKeyMessage,
						IMessageProvider.ERROR);
				return;
			}
			if (secret != null && secret.trim().isEmpty()) {
				setMessage(
						Messages.DeploymentCompositeFragment_EmptySecretMessage,
						IMessageProvider.ERROR);
				return;
			}
		}
		setMessage(getDescription(), IMessageProvider.NONE);
	}

	public void performTesting(IProgressMonitor monitor) {
		if (!enable) {
			return;
		}
		if (target != null && !isModified()) {
			return;
		}
		saveValues();
		monitor.beginTask(
				Messages.DeploymentCompositeFragment_TestingConnection,
				IProgressMonitor.UNKNOWN);
		TargetsManager manager = TargetsManagerService.INSTANCE
				.getTargetManager();
		IStatus status = null;
		TargetConnectionTester tester = new TargetConnectionTester();
		IZendTarget[] targets = manager.getTargets();
		for (IZendTarget t : targets) {
			if (t.getHost().equals(target.getHost())) {
				ZendTarget oldTarget = (ZendTarget) copyTemp((ZendTarget) t);
				Server server = getServer();
				oldTarget.setServerName(server.getName());
				oldTarget.setDefaultServerURL(target.getDefaultServerURL());
				oldTarget.setHost(target.getHost());
				oldTarget.setKey(target.getKey());
				oldTarget.setSecretKey(target.getSecretKey());
				status = tester.testConnection(oldTarget, monitor);
				break;
			}
		}
		if (status == null) {
			if (target.isTemporary()) {
				status = tester.testConnection(target, monitor);
			} else {
				status = tester.testConnection(target, monitor);
			}
		}
		switch (status.getSeverity()) {
		case IStatus.OK:
			ArrayList<IZendTarget> finalTargets = tester.getFinalTargets();
			for (IZendTarget target : finalTargets) {
				if (manager.getTargetById(target.getId()) != null) {
					manager.updateTarget(target, true);
				} else {
					try {
						manager.add(target, true);
					} catch (TargetException e) {
						// cannot occur, suppress connection
					} catch (LicenseExpiredException e) {
						// cannot occur, suppress connection
					}
				}
			}
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

	@Override
	public void setData(Object server) throws IllegalArgumentException {
		super.setData(server);
		if (getServer() != null && hostText != null && !hostText.isDisposed()) {
			if (DEFAULT_HOST.equals(hostText.getText())) {
				String suggestedHost = DEFAULT_HOST + getServer().getHost()
						+ ":10081"; //$NON-NLS-1$
				hostText.setText(suggestedHost);
			}
		}
	}

	@Override
	protected void createControl(Composite parent) {
		ModifyListener modifyListener = new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				updateData();
				validate();
			}
		};
		enableButton = new Button(parent, SWT.CHECK);
		enableButton.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true,
				false, 3, 1));
		enableButton.setText(Messages.DeploymentCompositeFragment_EnableLabel);
		enableButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateState(enableButton.getSelection());
				validate();
			}
		});
		Label label = new Label(parent, SWT.NONE);
		label.setText(Messages.DeploymentCompositeFragment_Host);
		hostText = new Text(parent, SWT.BORDER);
		hostText.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false,
				2, 1));
		hostText.addModifyListener(modifyListener);
		hostText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				if (hostText != null && detectButton != null) {
					String host = hostText.getText().trim();
					if (!host.isEmpty() && !DEFAULT_HOST.equals(host)) {
						try {
							new URL(host);
							if (enable) {
								detectButton.setEnabled(true);
							}
							return;
						} catch (MalformedURLException e) {
							// set detect to false
						}
					}
					detectButton.setEnabled(false);
				}
			}
		});
		hostText.setText(DEFAULT_HOST);
		hostText.setSelection(hostText.getText().length());

		label = new Label(parent, SWT.NONE);
		label.setText(Messages.DeploymentCompositeFragment_KeyName);
		keyText = new Text(parent, SWT.BORDER);
		keyText.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false,
				2, 1));
		keyText.addModifyListener(modifyListener);

		label = new Label(parent, SWT.NONE);
		label.setText(Messages.DeploymentCompositeFragment_KeySecret);
		secretText = new Text(parent, SWT.BORDER);
		secretText.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true,
				false, 2, 1));
		secretText.addModifyListener(modifyListener);

		detectButton = new Button(parent, SWT.PUSH);
		detectButton.setText(Messages.DeploymentCompositeFragment_DetectLabel);
		detectButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 3, 1));
		detectButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleDetect(hostText.getText());
			}
		});
	}

	@Override
	protected void init() {
		TargetsManager manager = TargetsManagerService.INSTANCE
				.getTargetManager();
		Server server = getServer();
		if (server != null) {
			String serverName = server.getName();
			IZendTarget[] targets = manager.getTargets();
			for (IZendTarget target : targets) {
				if (serverName.equals(target.getServerName())) {
					this.target = target;
					hostText.setText(target.getHost().toString());
					keyText.setText(target.getKey());
					secretText.setText(target.getSecretKey());
					enableButton.setSelection(true);
					detectButton.setEnabled(true);
					updateState(true);
					break;
				}
			}
		}
		if (target == null) {
			String id = manager.createUniqueId(null);
			target = new ZendTarget(id, null, null, null, true);
			enableButton.setSelection(false);
			updateState(false);
		}
		updateData();
		validate();
	}

	private void saveValues() {
		ZendTarget t = (ZendTarget) target;
		Server server = getServer();
		if (server != null) {
			t.setServerName(server.getName());
			try {
				t.setHost(new URL(host));
				t.setDefaultServerURL(new URL(server.getBaseURL()));
			} catch (MalformedURLException e) {
				Activator.log(e);
			}
			t.setKey(key);
			t.setSecretKey(secret);
		}
	}

	private void updateData() {
		if (enableButton != null) {
			enable = enableButton.getSelection();
		}
		if (hostText != null) {
			host = hostText.getText();
		}
		if (keyText != null) {
			key = keyText.getText();
		}
		if (secretText != null) {
			secret = secretText.getText();
		}
	}

	private IZendTarget copyTemp(ZendTarget t) {
		ZendTarget target = new ZendTarget(t.getId(), t.getHost(),
				t.getDefaultServerURL(), t.getKey(), t.getSecretKey(), true);
		String[] keys = t.getPropertiesKeys();
		for (String key : keys) {
			target.addProperty(key, t.getProperty(key));
		}
		return target;
	}

	private void handleDetect(final String host) {
		try {
			controlHandler.run(true, true, new IRunnableWithProgress() {

				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					try {
						monitor.beginTask(
								Messages.DeploymentCompositeFragment_DetectingCredentials,
								IProgressMonitor.UNKNOWN);
						detectApiKey(null);
					} catch (SdkException e) {
						String message = e.getMessage();
						Throwable cause = e.getCause();
						if (cause != null) {
							message = cause.getMessage();
						}
						setMessage(message, IMessageProvider.ERROR);
						Activator.log(e);
					}
				}
			});
			validate();
		} catch (InvocationTargetException e) {
			Activator.log(e);
		} catch (InterruptedException e) {
			Activator.log(e);
		}
	}

	private void detectApiKey(String message) throws SdkException {
		try {
			final ApiKeyDetector detector = new EclipseApiKeyDetector(host
					+ "/ZendServer"); //$NON-NLS-1$
			if (detector.createApiKey(message)) {
				Display.getDefault().asyncExec(new Runnable() {

					public void run() {
						String key = detector.getKey();
						String secret = detector.getSecretKey();
						if (key != null && secret != null) {
							keyText.setText(key);
							secretText.setText(secret);
						}
					}
				});
			}
		} catch (InvalidCredentialsException e) {
			detectApiKey(Messages.LocalTargetDetector_InvalidCredentialsMessage);
		}
	}

	private void updateState(boolean enabled) {
		hostText.setEnabled(enabled);
		keyText.setEnabled(enabled);
		secretText.setEnabled(enabled);
		detectButton.setEnabled(enabled);
	}

	private boolean isModified() {
		URL targetHost = target.getHost();
		return !(targetHost != null && host.equals(targetHost.toString())
				&& key.equals(target.getKey()) && secret.equals(target
				.getSecretKey()));
	}
}
