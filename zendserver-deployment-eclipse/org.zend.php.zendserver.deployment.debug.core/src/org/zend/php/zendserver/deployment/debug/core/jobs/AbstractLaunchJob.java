package org.zend.php.zendserver.deployment.debug.core.jobs;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.jobs.Job;
import org.zend.php.zendserver.deployment.core.debugger.IDeploymentHelper;

public abstract class AbstractLaunchJob extends Job {

	protected IDeploymentHelper helper;
	protected IProject project;

	protected AbstractLaunchJob(String name, IDeploymentHelper helper, IProject project) {
		super(name);
		this.helper = helper;
		this.project = project;
	}

	public IDeploymentHelper getHelper() {
		return helper;
	}

	public void setHelper(IDeploymentHelper helper) {
		this.helper = helper;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

}
