package org.zend.php.zendserver.deployment.ui.targets;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.net.UnknownHostException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.sdklib.SdkException;
import org.zend.sdklib.internal.target.ZendTarget;
import org.zend.sdklib.target.IZendTarget;
import org.zend.webapi.core.WebApiException;

/**
 * Abstract subclass for editing target details.
 * 
 */
public abstract class AbstractTargetDetailsComposite {

	/**
	 * Fired when validation event occurs.
	 */
	public static final String PROP_ERROR_MESSAGE = "errorMessage"; //$NON-NLS-1$
	public static final String PROP_MODIFY = "modify"; //$NON-NLS-1$

	protected PropertyChangeSupport changeSupport = new PropertyChangeSupport(
			this);

	private String[] data;

	private String errorMessage;

	protected IZendTarget[] result;

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
	 * Creates target based on data gathered in UI. Can be time consuming
	 * operation. May be called from non-UI thread, so should avoid accessing
	 * GUI elements.
	 * 
	 * @param data
	 *            data from getData()
	 * @return created target
	 * 
	 * @throws SdkException
	 * @throws IOException
	 */
	abstract protected IZendTarget[] createTarget(String[] data)
			throws SdkException, IOException, CoreException;

	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(propertyName, listener);
	}

	public void setErrorMessage(String errorMessage) {
		String oldMessage = this.errorMessage;
		this.errorMessage = errorMessage;
		changeSupport.firePropertyChange(PROP_ERROR_MESSAGE, oldMessage,
				errorMessage);
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public IStatus validate(final IProgressMonitor monitor) {
		result = null;
		monitor.beginTask("Validating target", 4);
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
		IStatus result = doValidate(data);
		monitor.worked(1);

		return result;
	}

	private IStatus doValidate(String[] data) {
		IZendTarget[] targets;
		try {
			targets = createTarget(data);
		} catch (SdkException e) {
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					e.getMessage(), e);
		} catch (UnknownHostException e) {
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Unknown host " + e.getMessage(), e);
		} catch (IOException e) {
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					e.getMessage(), e);
		} catch (CoreException e) {
			return e.getStatus();
		} catch (RuntimeException e) {
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					e.getMessage(), e);
		}

		for (IZendTarget target : targets) {

			if (target == null) {
				return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
						"Target was not created.");
			}

			String message = ((ZendTarget)target).validateTarget();
			if (message != null) {
				return new Status(IStatus.ERROR, Activator.PLUGIN_ID, message);
			}

			if (!target.isTemporary()) {
				try {
					target.connect();
				} catch (WebApiException ex) {
					return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
							ex.getMessage(), ex);
				} catch (RuntimeException e) {
					return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
							e.getMessage(), e);
				}
			}
		}
		result = targets;
		return Status.OK_STATUS;
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
}
