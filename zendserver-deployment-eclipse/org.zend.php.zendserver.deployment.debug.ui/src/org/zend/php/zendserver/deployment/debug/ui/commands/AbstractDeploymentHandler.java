package org.zend.php.zendserver.deployment.debug.ui.commands;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

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
			for (String pName : (String[]) projectName) {
				IProject project = root.getProject((String) projectName);
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

}
