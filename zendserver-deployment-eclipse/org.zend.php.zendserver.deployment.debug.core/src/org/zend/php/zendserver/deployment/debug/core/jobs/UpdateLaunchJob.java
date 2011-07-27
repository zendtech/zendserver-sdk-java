package org.zend.php.zendserver.deployment.debug.core.jobs;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.zend.php.zendserver.deployment.debug.core.Messages;
import org.zend.php.zendserver.deployment.debug.core.config.IDeploymentEntry;
import org.zend.sdklib.application.ZendApplication;
import org.zend.webapi.core.connection.data.ApplicationInfo;

public class UpdateLaunchJob extends AbstractLaunchJob {

	public UpdateLaunchJob(IDeploymentEntry entry, IProject project) {
		super(Messages.updateJob_Title, entry, project);
	}

	@Override
	protected ILaunchConfiguration createLaunchConfiguration(int appId) throws CoreException {
		return null;
	}

	@Override
	protected ApplicationInfo performOperation(ZendApplication app, String projectPath) {
		String appId = String.valueOf(entry.getAppId());
		return app.update(project.getLocation().toString(), entry.getTargetId(), appId,
				entry.getUserParams(), entry.isIgnoreFailures());
	}

}
