package org.zend.php.zendserver.deployment.debug.core.jobs;

import org.eclipse.core.resources.IProject;
import org.zend.php.zendserver.deployment.core.debugger.IDeploymentHelper;
import org.zend.php.zendserver.deployment.debug.core.Messages;
import org.zend.sdklib.application.ZendApplication;
import org.zend.webapi.core.connection.data.ApplicationInfo;

public class DeployLaunchJob extends DeploymentLaunchJob {

	public DeployLaunchJob(IDeploymentHelper helper, IProject project) {
		super(Messages.deploymentJob_Title, helper, project.getLocation()
				.toString());
	}

	public DeployLaunchJob(IDeploymentHelper helper, String projectPath) {
		super(Messages.deploymentJob_Title, helper, projectPath);
	}

	@Override
	protected ApplicationInfo performOperation(ZendApplication app,
			String projectPath) {
		String basePath = helper.getBaseURL().getPath();
		DeploymentEventsService.getInstance().fireEvent(
				new DeploymentEvent(helper.getProjectName(), helper
						.getBaseURL().toString()));
		return app.deploy(projectPath, basePath, helper.getTargetId(),
				helper.getUserParams(), helper.getAppName(),
				helper.isIgnoreFailures(), helper.getBaseURL(),
				helper.isDefaultServer());
	}

}
