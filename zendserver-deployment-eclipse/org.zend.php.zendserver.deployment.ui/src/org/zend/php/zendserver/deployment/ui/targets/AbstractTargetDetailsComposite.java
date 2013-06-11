package org.zend.php.zendserver.deployment.ui.targets;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.Messages;
import org.zend.php.zendserver.deployment.ui.wizards.OpenShiftInitializationWizard;
import org.zend.sdklib.SdkException;
import org.zend.sdklib.internal.target.ZendTarget;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;
import org.zend.sdklib.target.LicenseExpiredException;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.data.values.ServerType;
import org.zend.webapi.core.connection.data.values.WebApiVersion;
import org.zend.webapi.core.connection.response.ResponseCode;
import org.zend.webapi.internal.core.connection.exception.UnexpectedResponseCode;
import org.zend.webapi.internal.core.connection.exception.WebApiCommunicationError;

/**
 * Abstract subclass for editing target details.
 * 
 */
public abstract class AbstractTargetDetailsComposite {
	
	protected class CancelCreationException extends Exception {

		private static final long serialVersionUID = 1L;
		
		public CancelCreationException(String message) {
			super(message);
		}
		
	}

	private class OpenShiftInitializer {

		private IZendTarget target;
		private IProgressMonitor monitor;
		private IStatus status;

		public OpenShiftInitializer(IZendTarget target, IProgressMonitor monitor) {
			super();
			this.target = target;
		}

		public IZendTarget getTarget() {
			return target;
		}

		public IStatus getStatus() {
			return status;
		}

		public void init() {
			Display.getDefault().syncExec(new Runnable() {

				public void run() {
					Shell shell = PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getShell();
					WizardDialog dialog = new WizardDialog(shell,
							new OpenShiftInitializationWizard(target));
					dialog.open();
				}
			});
			try {
				target = testConnectAndDetectPort(target, monitor);
			} catch (WebApiException ex) {
				status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
						ex.getMessage(), ex);
			} catch (RuntimeException e) {
				status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
						e.getMessage(), e);
			} catch (LicenseExpiredException e) {
				status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
						e.getMessage(), e);
			}
		}

	}

	private static final int[] possiblePorts = new int[] { 10081, 10082, 10088 };
	private static final int[] possiblePhpcloudPorts = new int[] { 10082 };
	
	/**
	 * Fired when validation event occurs.
	 */
	public static final String PROP_ERROR_MESSAGE = "errorMessage"; //$NON-NLS-1$
	public static final String PROP_WARNING_MESSAGE = "warningMessage"; //$NON-NLS-1$
	public static final String PROP_MESSAGE = "message"; //$NON-NLS-1$
	public static final String PROP_MODIFY = "modify"; //$NON-NLS-1$

	protected PropertyChangeSupport changeSupport = new PropertyChangeSupport(
			this);

	private String[] data;

	private String errorMessage;
	
	private String warningMessage;

	protected IZendTarget[] result;
	
	protected IRunnableContext runnableContext;

	/**
	 * Creates composite contents and returns control.
	 * 
	 * @param parent
	 * @return created control.
	 */
	abstract public Composite create(Composite parent);

	/**
	 * Sets default values on the composite based on provided default target.
	 * 
	 * @param defaultTarget
	 *            target with default values.
	 */
	abstract public void setDefaultTargetSettings(IZendTarget defaultTarget);

	/**
	 * Returns essential data provided by user, that is necessary to create
	 * target. Called from UI thread, so should not block.
	 * 
	 * @return data to be passed to #createTarget(String[]).
	 */
	abstract protected String[] getData();
	
	/**
	 * @return help resource
	 */
	abstract protected String getHelpResource();

	/**
	 * Creates target based on data gathered in UI. Can be time consuming
	 * operation. May be called from non-UI thread, so should avoid accessing
	 * GUI elements.
	 * 
	 * @param data
	 *            data from getData()
	 * @param monitor 
	 * @return created target
	 * 
	 * @throws SdkException
	 * @throws IOException
	 * @throws CancelCreationException 
	 */
	abstract protected IZendTarget[] createTarget(String[] data, IProgressMonitor monitor)
			throws SdkException, IOException, CoreException, CancelCreationException;

	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(propertyName, listener);
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
		changeSupport
				.firePropertyChange(PROP_ERROR_MESSAGE, null, errorMessage);
	}

	public String getErrorMessage() {
		return errorMessage;
	}
	
	public void setWarningMessage(String warningMessage) {
		String oldMessage = this.warningMessage;
		this.warningMessage = warningMessage;
		changeSupport.firePropertyChange(PROP_WARNING_MESSAGE, oldMessage,
				warningMessage);
	}
	
	public void setMessage(String message) {
		changeSupport.firePropertyChange(PROP_MESSAGE, null,
				message);
	}

	public String getWarningMessage() {
		return warningMessage;
	}

	public IStatus validate(final IProgressMonitor monitor) {
		result = null;
		monitor.beginTask("Validating target", 4); //$NON-NLS-1$
		monitor.worked(1);
		Display.getDefault().syncExec(new Runnable() {

			public void run() {
				data = getData();
			}
		});
		monitor.worked(2);
		final Thread toCancel = Thread.currentThread();
		Thread cancelThread = new Thread(new Runnable() {

			public void run() {
				while (toCancel.isAlive() && !monitor.isCanceled()) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// ignore
					}
				}

				while (toCancel.isAlive() && monitor.isCanceled()) {
					toCancel.interrupt();
				}
			}

		});
		cancelThread.start();
		try {
			IStatus result = doValidate(data, monitor);
			monitor.worked(1);
			return result;
		} finally {
			TargetsManagerService.INSTANCE.getTargetManager()
					.removeAllTemporary();
		}
	}

	public void setRunnableContext(IRunnableContext runnableContext) {
		this.runnableContext= runnableContext;
	}

	public IZendTarget[] getTarget() {
		return result;
	}

	/**
	 * Early checking if create() returns any GUI, or not. Some target types
	 * don't need GUI - e.g. local target detection.
	 * 
	 * @return
	 */
	abstract public boolean hasPage();

	abstract protected boolean validatePage();

	private IStatus doValidate(String[] data, IProgressMonitor monitor) {
		IZendTarget[] targets;
		monitor.subTask("Creating targets"); //$NON-NLS-1$
		try {
			targets = createTarget(data, monitor);
		} catch (SdkException e) {
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					e.getMessage());
		} catch (UnknownHostException e) {
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Unknown host " + e.getMessage(), e); //$NON-NLS-1$
		} catch (IOException e) {
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					e.getMessage(), e);
		} catch (CoreException e) {
			return e.getStatus();
		} catch (RuntimeException e) {
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					e.getMessage(), e);
		} catch (CancelCreationException e) {
			return new Status(IStatus.CANCEL, Activator.PLUGIN_ID,
					e.getMessage());
		}
		if (targets == null || targets.length == 0) {
			return new Status(
					IStatus.ERROR,
					Activator.PLUGIN_ID,
					"Could not find any containers associated with Phpcloud account. " //$NON-NLS-1$
							+ "Create at least one container to be able to add Phpcloud target."); //$NON-NLS-1$
		}
		monitor.subTask("Found " + targets.length + " target" //$NON-NLS-1$ //$NON-NLS-2$
				+ (targets.length == 1 ? "" : "s")); //$NON-NLS-1$ //$NON-NLS-2$
		List<IZendTarget> finalTargets = new ArrayList<IZendTarget>(targets.length);
		IStatus status = null;
		for (IZendTarget target : targets) {
			if (target == null) {
				status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
						"Target was not created."); //$NON-NLS-1$
				continue;
			}

			String message = ((ZendTarget)target).validateTarget();
			if (message != null) {
				status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, message);
				continue;
			}

			if (target.isTemporary()) {
				try {
					target = testConnectAndDetectPort(target, monitor);
				} catch (UnexpectedResponseCode e) {
					if (TargetsManager.isOpenShift(target)) {
						if (e.getResponseCode() == ResponseCode.SERVER_NOT_CONFIGURED) {
							OpenShiftInitializer initializer = new OpenShiftInitializer(
									target, monitor);
							initializer.init();
							if (status == null) {
								target = initializer.getTarget();
							} else {
								status = initializer.getStatus();
								continue;
							}
						}
					}
					if (target == null) {
						status = new Status(
								IStatus.ERROR,
								Activator.PLUGIN_ID,
								MessageFormat
										.format(Messages.TargetDialog_AddingTargetError,
												e.getMessage()), e);
						continue;
					}
				} catch (WebApiException e) {
					status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
							MessageFormat.format(
									Messages.TargetDialog_AddingTargetError,
									e.getMessage()), e);
					continue;
				} catch (RuntimeException e) {
					status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
							MessageFormat.format(
									Messages.TargetDialog_AddingTargetError,
									e.getMessage()), e);
					continue;
				} catch (LicenseExpiredException e) {
					status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
							MessageFormat.format(
									Messages.TargetDialog_AddingTargetError,
									e.getMessage()), e);
					continue;
				}
			}
			
			if (target != null) {
				finalTargets.add(copy((ZendTarget) target));
			}
		}
		result = finalTargets.toArray(new IZendTarget[finalTargets.size()]);
		if (status != null) {
			if (finalTargets.size() == 0) {
				return status;
			} else {
				return new Status(
						IStatus.WARNING,
						Activator.PLUGIN_ID,
						"Cannot connect to at least one of containers on specified account. Only valid containers will be added."); //$NON-NLS-1$
			}
		}
		return Status.OK_STATUS;
	}

	private IZendTarget testConnectAndDetectPort(IZendTarget target,
			IProgressMonitor monitor) throws WebApiException, LicenseExpiredException {
		WebApiException catchedException = null;
		int[] portToTest = possiblePorts;
		if (TargetsManager.isPhpcloud(target)) {
			portToTest = possiblePhpcloudPorts;
		}
		if (target.getHost().getPort() == -1) {
			for (int port : portToTest) {
				URL old = target.getHost();
				URL host;
				try {
					host = new URL(old.getProtocol(), old.getHost(), port,
							old.getFile());
					((ZendTarget) target).setHost(host);
				} catch (MalformedURLException e) {
					// should never happen
				}
				monitor.subTask("Testing port " + port + " of detected target " //$NON-NLS-1$ //$NON-NLS-2$
						+ target.getHost().getHost());
				try {
					return testTargetConnection(target);
				} catch (WebApiException e) {
					catchedException = e;
				}
			}
		} else {
			try {
				return testTargetConnection(target);
			} catch (WebApiException e) {
				catchedException = e;
			}
		}
		if (catchedException != null) {
			throw catchedException;
		}
		return null;
	}

	private IZendTarget testTargetConnection(IZendTarget target)
			throws WebApiException, LicenseExpiredException {
		try {
			if (target.connect(WebApiVersion.V1_3, ServerType.ZEND_SERVER)) {
				return target;
			}
		} catch (WebApiCommunicationError e) {
			throw e;
		} catch (UnexpectedResponseCode e) {
			ResponseCode code = e.getResponseCode();
			switch (code) {
			case INTERNAL_SERVER_ERROR:
			case AUTH_ERROR:
			case INSUFFICIENT_ACCESS_LEVEL:
				throw e;
			default:
				break;
			}
		}
		try {
			if (target.connect(WebApiVersion.UNKNOWN, ServerType.ZEND_SERVER)) {
				return target;
			}
		} catch (WebApiException ex) {
			if (target.connect()) {
				return target;
			}
		}
		return null;
	}

	private IZendTarget copy(ZendTarget t) {
		ZendTarget target = new ZendTarget(t.getId(), t.getHost(),
				t.getDefaultServerURL(), t.getKey(), t.getSecretKey());
		String[] keys = t.getPropertiesKeys();
		for (String key : keys) {
			target.addProperty(key, t.getProperty(key));
		}
		return target;
	}
	
}
