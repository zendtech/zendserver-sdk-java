package org.zend.php.zendserver.deployment.debug.ui.commands;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.zend.php.zendserver.deployment.core.DeploymentNature;

public abstract class AbstractDeploymentHandler extends AbstractHandler {

	protected IProject[] getProjects(Object projectName) {
		if (projectName == null) {
			return null;
		}

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

		if (projectName instanceof String) {
			IProject project = root.getProject((String) projectName);
			if (project.exists()) {
				return new IProject[] { project };
			}
		}

		if (projectName instanceof String[]) {
			List<IProject> projects = new ArrayList<IProject>();
			String[] projectNames = (String[]) projectName;
			for (String pName : projectNames) {
				IProject project = root.getProject(pName);
				if (project.exists() && (projects.contains(project))) {
					projects.add(project);
				}
			}
			return projects.toArray(new IProject[projects.size()]);
		}

		return null;
	}

	protected IProject getProjectFromEditor() {
		IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage().getActiveEditor();
		if (activeEditor != null) {
			IEditorInput editorInput = activeEditor.getEditorInput();
			IFile descriptor = (IFile) editorInput.getAdapter(IFile.class);
			if (descriptor != null) {
				return descriptor.getProject();
			}
		}
		return null;
	}

	protected boolean hasDeploymentNature(IProject project)
			throws CoreException {
		String[] natures = project.getDescription().getNatureIds();
		for (String nature : natures) {
			if (DeploymentNature.ID.equals(nature)) {
				return true;
			}
		}
		return false;
	}

	protected void enableDeployment(IProject project) throws CoreException {
		IProjectDescription desc = project.getDescription();
		String[] natures = desc.getNatureIds();
		String[] nnatures = new String[natures.length + 1];
		System.arraycopy(natures, 0, nnatures, 0, natures.length);
		nnatures[natures.length] = DeploymentNature.ID;
		desc.setNatureIds(nnatures);

		project.setDescription(desc, new NullProgressMonitor());
	}

}
