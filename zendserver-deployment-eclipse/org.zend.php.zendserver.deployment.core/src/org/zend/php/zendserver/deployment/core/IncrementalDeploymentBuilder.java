package org.zend.php.zendserver.deployment.core;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class IncrementalDeploymentBuilder extends IncrementalProjectBuilder {

	public static final String ID = DeploymentCore.PLUGIN_ID + ".DeploymentBuilder";

	public IncrementalDeploymentBuilder() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected IProject[] build(int kind, Map<String, String> args,
			IProgressMonitor monitor) throws CoreException {
		//System.out.println("Build "+getProject()+" "+getDelta(getProject()).getResource());
		
		return null;
	}

}
