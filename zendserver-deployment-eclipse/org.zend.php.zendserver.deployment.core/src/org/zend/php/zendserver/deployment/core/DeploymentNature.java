package org.zend.php.zendserver.deployment.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.internal.core.UserLibraryManager;
import org.eclipse.php.internal.core.PHPLanguageToolkit;
import org.eclipse.php.internal.core.includepath.IncludePath;
import org.eclipse.php.internal.core.includepath.IncludePathManager;
import org.zend.sdklib.application.ZendProject;
import org.zend.sdklib.mapping.IMappingEntry;
import org.zend.sdklib.mapping.IMappingModel;
import org.zend.sdklib.mapping.MappingModelFactory;

public class DeploymentNature implements IProjectNature {

	public static final String ID = DeploymentCore.PLUGIN_ID + ".DeploymentNature"; //$NON-NLS-1$

	private static final String ZF_NATURE_ID = "org.zend.php.framework.ZendFrameworkNature"; //$NON-NLS-1$

	public static final IPath zf2ContainerPath = new Path(
			DLTKCore.USER_LIBRARY_CONTAINER_ID).append(UserLibraryManager
			.makeLibraryName("Zend Framework 2",
					PHPLanguageToolkit.getDefault()));
	
	private IProject project;
	
	public void configure() throws CoreException {
		addBuilder(IncrementalDeploymentBuilder.ID);
		updateProject();
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
	
	@SuppressWarnings("restriction")
	public void updateProject() throws CoreException {
		File projectLocation = project.getLocation().toFile();
		ZendProject zp = new ZendProject(projectLocation);
		zp.update(null);
		if (project.hasNature(ZF_NATURE_ID)) {
			IncludePath[] paths = IncludePathManager.getInstance()
					.getIncludePaths(project);
			for (IncludePath includePath : paths) {
				if (includePath.getEntry() instanceof IBuildpathEntry) {
					IBuildpathEntry bPath = (IBuildpathEntry) includePath
							.getEntry();
					if (bPath.getEntryKind() == IBuildpathEntry.BPE_CONTAINER
							&& bPath.getPath().equals(zf2ContainerPath)) {
						IMappingModel model = MappingModelFactory
								.createDefaultModel(project.getLocation()
										.toFile());
						model.addMapping(IMappingModel.APPDIR,
								IMappingEntry.Type.EXCLUDE, "vendor/zendframework", false); //$NON-NLS-1$
						try {
							model.store();
						} catch (IOException e) {
							throw new CoreException(new Status(IStatus.ERROR,
									DeploymentCore.PLUGIN_ID, "Error occured", //$NON-NLS-1$
									e));
						}
					}
				}
			}
		}
		project.refreshLocal(IProject.DEPTH_INFINITE, new NullProgressMonitor());
	}

	public IProject getProject() {
		return this.project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

}
