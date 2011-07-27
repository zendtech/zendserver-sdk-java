package org.zend.php.zendserver.deployment.debug.core.jobs;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.zend.php.zendserver.deployment.debug.core.Messages;
import org.zend.php.zendserver.deployment.debug.core.config.IDeploymentEntry;
import org.zend.php.zendserver.deployment.debug.core.config.LaunchUtils;
import org.zend.sdklib.application.ZendApplication;
import org.zend.webapi.core.connection.data.ApplicationInfo;

public class DeployLaunchJob extends AbstractLaunchJob {

	public DeployLaunchJob(IDeploymentEntry entry, IProject project) {
		super(Messages.deploymentJob_Title, entry, project);
	}

	@Override
	protected ILaunchConfiguration createLaunchConfiguration(int appId) throws CoreException {
		return LaunchUtils.createConfiguration(project, appId, entry);
	}

	@Override
	protected ApplicationInfo performOperation(ZendApplication app, String projectPath) {
		return app.deploy(project.getLocation().toString(), entry.getBasePath(),
				entry.getTargetId(), entry.getUserParams(), entry.getAppName(),
				entry.isIgnoreFailures(), entry.getVirtualHost(), entry.isDefaultServer());
	}

}
