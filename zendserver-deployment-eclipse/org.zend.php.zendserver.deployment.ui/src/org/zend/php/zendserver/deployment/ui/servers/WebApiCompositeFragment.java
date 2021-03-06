/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies.
 * All rights reserved. This program and the accompanying materials
 * are the copyright of Zend Technologies and is protected under
 * copyright laws of the United States.
 * You must not copy, adapt or redistribute this document for 
 * any use.
 *******************************************************************************/
package org.zend.php.zendserver.deployment.ui.servers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.ui.wizards.IControlHandler;
import org.eclipse.php.internal.ui.wizards.IControlHandler.Kind;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.zend.php.server.ui.fragments.AbstractCompositeFragment;
import org.zend.php.zendserver.deployment.core.debugger.DeploymentAttributes;
import org.zend.php.zendserver.deployment.core.targets.EclipseApiKeyDetector;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.debug.core.DebugUtils;
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
public class WebApiCompositeFragment extends AbstractCompositeFragment {

	private static String ID = "org.zend.php.zendserver.deployment.ui.servers.WebApiCompositeFragment"; //$NON-NLS-1$

	private class KeyDetectionRunnable implements IRunnableWithProgress {

		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			monitor.beginTask(Messages.WebApiCompositeFragment_DetectingCredentials, IProgressMonitor.UNKNOWN);
			try {
				detectApiKey(null);
			} catch (SdkException e) {
				throw new InvocationTargetException(e);
			} finally {
				monitor.done();
			}
		}
	}

	private class ServerPropertyChangeListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent event) {
			if (event.getPropertyName().equals(Server.HOSTNAME)) {
				detectOnEnter = true;
			}
		}
	}

	private static final String DEFAULT_HOST = "http://"; //$NON-NLS-1$

	private Button enableButton;
	private Text hostText;
	private Text keyText;
	private Text secretText;
	private Button detectButton;

	private boolean enable;
	private String host;
	private String key;
	private String secret;

	private IZendTarget target;
	private boolean detectOnEnter;
	private boolean keysDetected;
	private boolean isModified = false;
	private ServerPropertyChangeListener phpServerListener;

	/**
	 * PlatformCompositeFragment constructor
	 * 
	 * @param parent
	 * @param handler
	 * @param isForEditing
	 */
	public WebApiCompositeFragment(Composite parent, IControlHandler handler, boolean isForEditing) {
		super(parent, handler, isForEditing, Messages.WebApiCompositeFragment_Name,
				Messages.WebApiCompositeFragment_Title, Messages.WebApiCompositeFragment_Description);
		setImageDescriptor(Activator.getImageDescriptor(Activator.IMAGE_WIZ_DEPLOYMENT));
		handler.setImageDescriptor(getImageDescriptor());
		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				unregisterListeners();
				removeDisposeListener(this);
			}
		});
	}

	@Override
	public boolean performOk() {
		// remove temporary attributes
		getServer().removeAttribute(DeploymentAttributes.ENABLED.getName());
		getServer().removeAttribute(DeploymentAttributes.TARGET_HOST.getName());
		if (!enable) {
			if (target != null) {
				TargetsManager manager = TargetsManagerService.INSTANCE.getTargetManager();
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
		isModified = false;
		boolean webApiTest = webApiTest();
		if (!webApiTest)
			return false;
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
				setMessage(Messages.WebApiCompositeFragment_EmptyHostMessage, IMessageProvider.ERROR);
				return;
			}
			if (key != null && key.trim().isEmpty()) {
				setMessage(Messages.WebApiCompositeFragment_EmptyKeyMessage, IMessageProvider.ERROR);
				return;
			}
			if (secret != null && secret.trim().isEmpty()) {
				setMessage(Messages.WebApiCompositeFragment_EmptySecretMessage, IMessageProvider.ERROR);
				return;
			}
		}
		setMessage(getDescription(), IMessageProvider.NONE);
	}

	public void performTesting(IProgressMonitor monitor) {
		if (!enable) {
			return;
		}
		saveValues();
		monitor.beginTask(Messages.WebApiCompositeFragment_TestingConnection, IProgressMonitor.UNKNOWN);
		TargetsManager manager = TargetsManagerService.INSTANCE.getTargetManager();
		IStatus status = null;
		TargetConnectionTester tester = new TargetConnectionTester();
		ZendTarget oldTarget = getOldTarget(target);
		if (oldTarget != null) {
			status = tester.testConnection(oldTarget, monitor);
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
			List<IZendTarget> finalTargets = tester.getFinalTargets();
			for (IZendTarget target : finalTargets) {
				if (target != null) {
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
		unregisterListeners();
		super.setData(server);
		if (getServer() != null && hostText != null && !hostText.isDisposed()) {
			String suggestedHost = DEFAULT_HOST + getServer().getHost() + ":10081"; //$NON-NLS-1$
			hostText.setText(suggestedHost);
			registerListeners();
		}
		initialDetect();
	}

	public void setDetectOnEnter(boolean value) {
		this.detectOnEnter = value;
	}
	
	private void initialDetect() {
		if (controlHandler.getKind() == Kind.WIZARD && detectOnEnter) {
			detectOnEnter = false;
			handleDetect();
			if (keysDetected) {
				enableButton.setSelection(keysDetected);
				enable = enableButton.getSelection();
				updateState(keysDetected);
				webApiTest();
				IZendTarget zendTarget = getOldTarget(target) != null ? getOldTarget(target) : target;
				getServer().setDebuggerId(DebugUtils.getDebuggerId(zendTarget));
			}
		}
	}

	@Override
	protected void createContents(Composite parent) {
		ModifyListener modifyListener = new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				updateData();
				isModified = true;
				validate();
			}
		};
		enableButton = new Button(parent, SWT.CHECK);
		enableButton.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false, 3, 1));
		enableButton.setText(Messages.WebApiCompositeFragment_EnableLabel);
		enableButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateState(enableButton.getSelection());
				if (!enableButton.getSelection() && target != null) {
					TargetsManager manager = TargetsManagerService.INSTANCE.getTargetManager();
					if (manager.getTargetById(target.getId()) != null) {
						manager.remove(target);
					}
				}
				updateData();
				validate();
				webApiTest();
			}
		});

		CLabel noteIcon = new CLabel(parent, SWT.NONE);
		noteIcon.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		noteIcon.setImage(Dialog.getImage(Dialog.DLG_IMG_MESSAGE_INFO));
		noteIcon.setText(Messages.WebApiCompositeFragment_Enabling_web_api_info_message);

		Label label = new Label(parent, SWT.NONE);
		label.setText(Messages.WebApiCompositeFragment_Host);
		hostText = new Text(parent, SWT.BORDER);
		hostText.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false, 2, 1));
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

		label = new Label(parent, SWT.NONE);
		label.setText(Messages.WebApiCompositeFragment_KeyName);
		keyText = new Text(parent, SWT.BORDER);
		keyText.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false, 2, 1));
		keyText.addModifyListener(modifyListener);

		label = new Label(parent, SWT.NONE);
		label.setText(Messages.WebApiCompositeFragment_KeySecret);
		secretText = new Text(parent, SWT.BORDER);
		secretText.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false, 2, 1));
		secretText.addModifyListener(modifyListener);

		detectButton = new Button(parent, SWT.PUSH);
		detectButton.setText(Messages.WebApiCompositeFragment_DetectLabel);
		detectButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		detectButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleDetect();
			}
		});

		Link link = new Link(parent, SWT.WRAP);
		link.setText(Messages.WebApiCompositeFragment_WebApiDetails);
		GridData layoutData = new GridData(SWT.FILL, SWT.TOP, true, false);
		layoutData.widthHint = 400;
		layoutData.horizontalSpan = 3;
		link.setLayoutData(layoutData);
		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				try {
					PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(new URL(event.text));
				} catch (PartInitException e) {
					Activator.log(e);
				} catch (MalformedURLException e) {
					Activator.log(e);
				}
			}
		});
	}

	@Override
	protected void init() {
		TargetsManager manager = TargetsManagerService.INSTANCE.getTargetManager();
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
					server.setAttribute(DeploymentAttributes.ENABLED.getName(), String.valueOf(true));
					server.setAttribute(DeploymentAttributes.TARGET_HOST.getName(), hostText.getText());
					updateState(true);
					break;
				}
			}
		}
		if (target == null) {
			String id = manager.createUniqueId(null);
			target = new ZendTarget(id, null, null, null, true);
			enableButton.setSelection(false);
			if (server != null) {
				server.setAttribute(DeploymentAttributes.ENABLED.getName(), String.valueOf(false));
			}
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
		Server server = getServer();
		if (enableButton != null) {
			enable = enableButton.getSelection();
			if (server != null) {
				server.setAttribute(DeploymentAttributes.ENABLED.getName(), String.valueOf(enable));
			}
		}
		if (hostText != null) {
			host = hostText.getText();
			if (enable) {
				if (server != null) {
					server.setAttribute(DeploymentAttributes.TARGET_HOST.getName(), host);
				}
			} else {
				if (server != null) {
					server.removeAttribute(DeploymentAttributes.TARGET_HOST.getName());
				}
			}
		}
		if (keyText != null) {
			key = keyText.getText();
		}
		if (secretText != null) {
			secret = secretText.getText();
		}
	}

	private ZendTarget getOldTarget(IZendTarget target) {
		TargetsManager manager = TargetsManagerService.INSTANCE.getTargetManager();
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
				return oldTarget;
			}
		}
		return null;
	}

	private IZendTarget copyTemp(ZendTarget t) {
		ZendTarget target = new ZendTarget(t.getId(), t.getHost(), t.getDefaultServerURL(), t.getKey(),
				t.getSecretKey(), true);
		String[] keys = t.getPropertiesKeys();
		for (String key : keys) {
			target.addProperty(key, t.getProperty(key));
		}
		return target;
	}

	private void handleDetect() {
		KeyDetectionRunnable detector = new KeyDetectionRunnable();
		try {
			controlHandler.run(true, true, detector);
		} catch (InvocationTargetException e) {
			String message = MessageFormat.format(Messages.WebApiCompositeFragment_DetectingWebApi_Error,
					e.getCause().getLocalizedMessage());
			setMessage(message, IMessageProvider.ERROR);
			Activator.logError(message, e);
			return;
		} catch (InterruptedException e) {
			String message = Messages.WebApiCompositeFragment_DetectingWebApiInterrupted_Info;
			setMessage(message, IMessageProvider.INFORMATION);
			Activator.logInfo(message);
			return;
		}

		webApiTest();
		validate();
	}

	private void detectApiKey(String message) throws SdkException {
		try {
			final ApiKeyDetector detector = new EclipseApiKeyDetector(host + "/ZendServer"); //$NON-NLS-1$
			if (detector.createApiKey(message)) {
				keysDetected = true;
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
			detectApiKey(Messages.WebApiCompositeFragment_InvalidCredentialsError);
		}
	}

	private void updateState(boolean enabled) {
		hostText.setEnabled(enabled);
		keyText.setEnabled(enabled);
		secretText.setEnabled(enabled);
		detectButton.setEnabled(enabled);
	}

	private boolean isModified() {
		return isModified;
	}

	private boolean webApiTest() {
		try {
			controlHandler.run(true, true, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
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
		return true;
	}

	private void registerListeners() {
		if(phpServerListener == null)
			phpServerListener = new ServerPropertyChangeListener();
		
		getServer().addPropertyChangeListener(phpServerListener);
	}

	private void unregisterListeners() {
		Server server = getServer();
		if(server == null)
			return;
		
		server.removePropertyChangeListener(phpServerListener);
	}

}
