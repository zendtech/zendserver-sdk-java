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
import org.eclipse.php.internal.server.ui.ServerEditPage;
import org.eclipse.php.internal.ui.wizards.CompositeFragment;
import org.eclipse.php.internal.ui.wizards.IControlHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.zend.php.zendserver.deployment.core.targets.EclipseApiKeyDetector;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.sdklib.SdkException;
import org.zend.sdklib.internal.target.ApiKeyDetector;
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
public class DeploymentCompositeFragment extends CompositeFragment {

	private static final String DEFAULT_HOST = "http://"; //$NON-NLS-1$

	public static String ID = "org.zend.php.zendserver.deployment.ui.preferences.DeploymentCompositeFragment"; //$NON-NLS-1$

	private Text hostText;
	private Text keyText;
	private Text secretText;

	private String host;
	private String key;
	private String secret;

	private IZendTarget target;

	/**
	 * PlatformCompositeFragment constructor
	 * 
	 * @param parent
	 * @param handler
	 * @param isForEditing
	 */
	public DeploymentCompositeFragment(Composite parent,
			IControlHandler handler, boolean isForEditing) {
		super(parent, handler, isForEditing);

		setDisplayName(Messages.DeploymentCompositeFragment_Title);

		setTitle(Messages.DeploymentCompositeFragment_Title);
		setDescription(Messages.DeploymentCompositeFragment_Description);

		controlHandler.setTitle(Messages.DeploymentCompositeFragment_Title);
		controlHandler
				.setDescription(Messages.DeploymentCompositeFragment_Description);

		controlHandler.setImageDescriptor(Activator
				.getImageDescriptor(Activator.IMAGE_WIZBAN_DEP));

		if (isForEditing) {
			setData(((ServerEditPage) controlHandler).getServer());
		}
		createControl(isForEditing);
	}

	/**
	 * Override the super setData to handle only Server types.
	 * 
	 * @throws IllegalArgumentException
	 *             if the given object is not a {@link Server}
	 */
	@Override
	public void setData(Object server) throws IllegalArgumentException {
		if (server != null && !(server instanceof Server)) {
			throw new IllegalArgumentException(""); //$NON-NLS-1$
		}
		if (server != null && hostText != null && !hostText.isDisposed()) {
			String suggestedHost = DEFAULT_HOST + ((Server) server).getHost()
					+ ":10081"; //$NON-NLS-1$
			hostText.setText(suggestedHost);
		}
		super.setData(server);
	}

	@Override
	public boolean performOk() {
		if (target != null) {
			URL targetHost = target.getHost();
			if (targetHost != null && host.equals(targetHost.toString())
					&& key.equals(target.getKey())
					&& secret.equals(target.getSecretKey())) {
				return true;
			}
		}
		saveValues();
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
		if (host != null && host.trim().isEmpty()) {
			setMessage(Messages.DeploymentCompositeFragment_EmptyHostMessage,
					IMessageProvider.ERROR);
			return;
		}
		if (key != null && key.trim().isEmpty()) {
			setMessage(Messages.DeploymentCompositeFragment_EmptyKeyMessage,
					IMessageProvider.ERROR);
			return;
		}
		if (secret != null && secret.trim().isEmpty()) {
			setMessage(Messages.DeploymentCompositeFragment_EmptySecretMessage,
					IMessageProvider.ERROR);
			return;
		}
		setMessage(getDescription(), IMessageProvider.NONE);
	}

	@Override
	public boolean isComplete() {
		return super.isComplete();
	}

	/**
	 * Returns the Server that is attached to this fragment.
	 * 
	 * @return The attached Server.
	 */
	private Server getServer() {
		return (Server) getData();
	}

	private void saveValues() {
		ZendTarget t = (ZendTarget) target;
		Server server = getServer();
		if (server != null) {
			t.setServerName(server.getName());
			try {
				t.setHost(new URL(hostText.getText()));
				t.setDefaultServerURL(new URL(server.getBaseURL()));
			} catch (MalformedURLException e) {
				Activator.log(e);
			}
			t.setKey(keyText.getText());
			t.setSecretKey(secretText.getText());
		}
	}

	private void createControl(boolean isForEditing) {
		// set layout for this composite (whole page)
		GridLayout pageLayout = new GridLayout();
		setLayout(pageLayout);

		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		composite.setLayout(new GridLayout(3, false));

		ModifyListener modifyListener = new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				updateData();
				validate();
			}
		};

		Label label = new Label(composite, SWT.NONE);
		label.setText(Messages.DeploymentCompositeFragment_Host);
		hostText = new Text(composite, SWT.BORDER);
		hostText.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false,
				2, 1));
		hostText.addModifyListener(modifyListener);
		hostText.setText(DEFAULT_HOST);
		hostText.setSelection(hostText.getText().length());

		label = new Label(composite, SWT.NONE);
		label.setText(Messages.DeploymentCompositeFragment_KeyName);
		keyText = new Text(composite, SWT.BORDER);
		keyText.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false,
				2, 1));
		keyText.addModifyListener(modifyListener);

		label = new Label(composite, SWT.NONE);
		label.setText(Messages.DeploymentCompositeFragment_KeySecret);
		secretText = new Text(composite, SWT.BORDER);
		secretText.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true,
				false, 2, 1));
		secretText.addModifyListener(modifyListener);

		Button detectButton = new Button(composite, SWT.PUSH);
		detectButton.setText(Messages.DeploymentCompositeFragment_DetectLabel);
		detectButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleDetect(hostText.getText());
			}
		});
		init();
	}

	private void setMessage(String message, int type) {
		controlHandler.setMessage(message, type);
		setComplete(type != IMessageProvider.ERROR);
		controlHandler.update();
	}

	private void updateData() {
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

	private void init() {
		TargetsManager manager = TargetsManagerService.INSTANCE
				.getTargetManager();
		Server server = getServer();
		if (server != null) {
			String serverName = server.getName();
			controlHandler
					.setDescription(Messages.DeploymentCompositeFragment_Description);
			controlHandler.setTitle(Messages.DeploymentCompositeFragment_Title);
			IZendTarget[] targets = manager.getTargets();
			for (IZendTarget target : targets) {
				if (serverName.equals(target.getServerName())) {
					this.target = target;
					hostText.setText(target.getHost().toString());
					keyText.setText(target.getKey());
					secretText.setText(target.getSecretKey());
					break;
				}
			}
		}
		if (target == null) {
			String id = manager.createUniqueId(null);
			target = new ZendTarget(id, null, null, null, true);
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
						final ApiKeyDetector detector = new EclipseApiKeyDetector(
								host + "/ZendServer"); //$NON-NLS-1$
						detector.createApiKey(null);
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
					} catch (SdkException e) {
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

	private void performTesting(IProgressMonitor monitor) {
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

}
