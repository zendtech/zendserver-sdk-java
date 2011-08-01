package org.zend.php.zendserver.deployment.debug.core.jobs;

import org.eclipse.core.resources.IProject;
import org.zend.php.zendserver.deployment.debug.core.Messages;
import org.zend.php.zendserver.deployment.debug.core.config.IDeploymentHelper;
import org.zend.sdklib.application.ZendApplication;
import org.zend.webapi.core.connection.data.ApplicationInfo;

public class DeployLaunchJob extends AbstractLaunchJob {

	public DeployLaunchJob(IDeploymentHelper helper, IProject project) {
		super(Messages.deploymentJob_Title, helper, project);
	}

	@Override
	protected ApplicationInfo performOperation(ZendApplication app, String projectPath) {
		return app.deploy(project.getLocation().toString(), helper.getBasePath(),
				helper.getTargetId(), helper.getUserParams(), helper.getAppName(),
				helper.isIgnoreFailures(), helper.getVirtualHost(), helper.isDefaultServer());
	}

}
