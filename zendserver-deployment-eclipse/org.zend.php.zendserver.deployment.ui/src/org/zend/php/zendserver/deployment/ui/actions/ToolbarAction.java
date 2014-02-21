package org.zend.php.zendserver.deployment.ui.actions;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.common.CommandException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.contributions.ITestingSectionContribution;

public class ToolbarAction extends Action {

	private String mode;
	private String commandId;

	public ToolbarAction(String commandId, String mode, String message,
			ImageDescriptor image) {
		super();
		this.mode = mode;
		this.commandId = commandId;
		setText(message);
		setToolTipText(message);
		setImageDescriptor(image);
	}

	@Override
	public void run() {
		IProject project = getProject();
		if (project != null) {
			ICommandService service = ((ICommandService) PlatformUI
					.getWorkbench().getService(ICommandService.class));
			Command command = service.getCommand(commandId);
			Map<String, String> params = new HashMap<String, String>();
			params.put(ITestingSectionContribution.PROJECT_NAME,
					project.getName());
			params.put(ITestingSectionContribution.MODE, mode);
			ExecutionEvent event = new ExecutionEvent(command, params, null,
					null);
			try {
				service.getCommand(commandId).executeWithChecks(event);
			} catch (CommandException e1) {
				Activator.log(e1);
			}
		} else {
			// TODO log that cannot find a descriptor file
		}
	}

	protected IProject getProject() {
		IEditorPart activeEditor = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
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
