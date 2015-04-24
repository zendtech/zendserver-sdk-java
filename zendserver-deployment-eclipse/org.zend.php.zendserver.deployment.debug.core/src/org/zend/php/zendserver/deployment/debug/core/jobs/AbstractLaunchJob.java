package org.zend.php.zendserver.deployment.debug.core.jobs;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.jobs.Job;
import org.zend.php.zendserver.deployment.core.debugger.IDeploymentHelper;

public abstract class AbstractLaunchJob extends Job {

	protected IDeploymentHelper helper;

	protected String projectPath;

	protected AbstractLaunchJob(String name, IDeploymentHelper helper,
			String projectPath) {
		super(name);
		this.helper = helper;
		this.projectPath = projectPath;
	}

	public IDeploymentHelper getHelper() {
		return helper;
	}

	public void setHelper(IDeploymentHelper helper) {
		this.helper = helper;
	}

	public void setProjectPath(IProject project) {
		this.projectPath = project.getLocation().toString();
	}

	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}

}
