package org.zend.php.zendserver.deployment.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

public class DeploymentNature implements IProjectNature {

	public static final String ID = DeploymentCore.PLUGIN_ID + ".DeploymentNature";
	
	private IProject project;
	
	public void configure() throws CoreException {
		addBuilder(IncrementalDeploymentBuilder.ID);
	}

	public void deconfigure() throws CoreException {
		removeBuilder(IncrementalDeploymentBuilder.ID);
	}

	private void addBuilder(String id) throws CoreException {
		IProjectDescription description = getProject().getDescription();
		List<ICommand> commands = new ArrayList<ICommand>(Arrays.asList(description.getBuildSpec()));
		boolean found = false;
		for (int i = commands.size() -1; i >= 0; --i) {
			if (commands.get(i).getBuilderName().equals(id)) {
				commands.remove(i);
				found = true;
				break;
			}
		}
		
		if (!found) {
			ICommand command = description.newCommand();
			command.setBuilderName(id);
			commands.add(command);
			description.setBuildSpec(commands.toArray(new ICommand[commands.size()]));
			getProject().setDescription(description, new NullProgressMonitor());
		}
		
	}
	
	private void removeBuilder(String id) throws CoreException {
		IProjectDescription description = getProject().getDescription();
		List<ICommand> commands = new ArrayList<ICommand>(Arrays.asList(description.getBuildSpec()));
		boolean found = false;
		for (int i = commands.size() -1; i >= 0; --i) {
			if (commands.get(i).getBuilderName().equals(id)) {
				commands.remove(i);
				found = true;
				break;
			}
		}
		
		if (found) {
			description.setBuildSpec(commands.toArray(new ICommand[commands.size()]));
		}
	}

	public IProject getProject() {
		return this.project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

}
