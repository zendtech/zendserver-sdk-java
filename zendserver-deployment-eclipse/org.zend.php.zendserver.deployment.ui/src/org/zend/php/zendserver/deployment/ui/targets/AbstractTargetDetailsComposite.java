package org.zend.php.zendserver.deployment.ui.targets;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.swt.widgets.Composite;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.sdklib.SdkException;
import org.zend.sdklib.internal.target.ZendTarget;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;

/**
 * Abstract subclass for editing target details.
 * 
 */
public abstract class AbstractTargetDetailsComposite {

	/**
	 * Fired when validation event occurs.
	 */
	public static final String PROP_ERROR_MESSAGE = "errorMessage"; //$NON-NLS-1$

	private PropertyChangeSupport changeSupport = new PropertyChangeSupport(
			this);

	private String errorMessage;

	protected IZendTarget result;

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
	abstract protected IZendTarget createTarget(String[] data)
			throws SdkException, IOException;

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

	public Job validate() {
		ValidateTargetJob job = new ValidateTargetJob(this);
		job.setUser(true);
		job.schedule();
		job.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				result = ((ValidateTargetJob) event.getJob()).getResultTarget();
				IStatus jobresult = event.getJob().getResult();
				String message = jobresult.getMessage();

				setErrorMessage(jobresult.getSeverity() == IStatus.OK ? null
						: message);
			}
		});

		return job;
	}

	/**
	 * Helps generating new target
	 * 
	 * @param host
	 * @param key
	 * @param sk
	 * @return
	 */
	protected IZendTarget createTarget(URL host, String key, String sk) {
		TargetsManager tm = TargetsManagerService.INSTANCE.getTargetManager();
		int idgenerator = tm.getTargets().length;
		String id;
		do {
			id = Integer.toString(idgenerator++);
		} while (tm.getTargetById(id) != null);
		return new ZendTarget(id, host, key, sk);
	}

	public IZendTarget getTarget() {
		return result;
	}
}
