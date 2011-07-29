package org.zend.php.zendserver.deployment.ui.targets;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.sdklib.internal.target.ZendTarget;
import org.zend.sdklib.target.IZendTarget;
import org.zend.webapi.core.WebApiException;

/**
 * Tries to connect to target.
 */
public class ValidateTargetJob extends Job {

	private IZendTarget target;
	
	public ValidateTargetJob(IZendTarget target) {
		super(NLS.bind("Validating target {0}", target.getHost()));
		this.target = target;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		String message = ((ZendTarget) target).validateTarget();
		if (message != null) {
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, message);
		}
		
		try {
			target.connect();
		} catch (WebApiException ex) {
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, ex.getMessage(), ex);
		}
		
		return Status.OK_STATUS;
	}

}
