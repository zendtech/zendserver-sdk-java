package org.zend.php.zendserver.deployment.debug.core.jobs;

import org.eclipse.core.resources.IProject;
import org.zend.php.zendserver.deployment.core.debugger.IDeploymentHelper;
import org.zend.php.zendserver.deployment.debug.core.Messages;
import org.zend.sdklib.application.ZendApplication;
import org.zend.webapi.core.connection.data.ApplicationInfo;

public class DeployLaunchJob extends DeploymentLaunchJob {

	public DeployLaunchJob(IDeploymentHelper helper, IProject project) {
		super(Messages.deploymentJob_Title, helper, project);
	}

	@Override
	protected ApplicationInfo performOperation(ZendApplication app, String projectPath) {
		String vHost = helper.getBaseURL().getHost();
		String basePath = helper.getBaseURL().getPath();
		return app.deploy(project.getLocation().toString(), basePath, helper.getTargetId(),
				helper.getUserParams(), helper.getAppName(), helper.isIgnoreFailures(), vHost,
				helper.isDefaultServer());
	}

}
