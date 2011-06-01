package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;
import org.zend.php.zendserver.deployment.ui.Activator;


public class OpenFileDialog {

	protected static final IStatus OK = new Status(IStatus.OK, Activator.PLUGIN_ID, "");

	public static String openFile(Shell shell, IContainer root, String title, String description, String initialPath) {
		String[] out = open(shell, root, title, description, initialPath, false, new Class[] {IResource.class}, IFile.class);
		return out == null ? null : out[0];
	}
	
	public static String openFolder(Shell shell, IContainer root, String title, String description, String initialPath) {
		String[] out = open(shell, root, title, description, initialPath, false, new Class[] {IContainer.class}, IFolder.class);
		return out == null ? null : out[0];
	}
	
	public static String open(Shell shell, IContainer root, String title, String description, String initialPath) {
		String[] out = open(shell, root, title, description, initialPath, false, new Class[] {IResource.class}, IResource.class);
		return out == null ? null : out[0];
	}
	
	public static String[] openMany(Shell shell, IContainer root, String title, String description, String initialPath) {
		return open(shell, root, title, description, initialPath, true, new Class[] {IResource.class}, IResource.class);
	}
	
	private static String[] open(Shell shell, IContainer root, String title, String description, String initialPath, boolean allowMultiple, final Class[] visibleTypes, final Class selectable) {
		ISelectionStatusValidator validator = new ISelectionStatusValidator() {

			public IStatus validate(Object[] selection) {
				return OK;
			}
			
		};
		ViewerFilter filter = new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				for (int i = 0; i < visibleTypes.length; i++) {
					if (visibleTypes[i].isAssignableFrom(element.getClass())) {
						return true;
					}
				}
				return false;
			}
			
		};

		ILabelProvider lp = new WorkbenchLabelProvider();
		ITreeContentProvider cp = new WorkbenchContentProvider();

		IResource initialElement = null;
		if (initialPath != null) {
			initialElement = root.findMember(
					new Path(initialPath));
			if (initialElement == null || !initialElement.exists()) {
				initialElement = null;
			}
		}

		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(shell, lp, cp);
		dialog.setTitle(title);
		dialog.setValidator(validator);
		dialog.setMessage(description);
		dialog.addFilter(filter);
		dialog.setInput(root);
		dialog.setInitialSelection(initialElement);
		dialog.setComparator(new ResourceComparator(
				ResourceComparator.NAME));
		dialog.setHelpAvailable(false);
		dialog.setAllowMultiple(allowMultiple);

		if (dialog.open() == Window.OK) {
			Object[] objects = dialog.getResult();
			if (objects.length == 0) {
				return null;
			}
			String[] paths = new String[objects.length];
			for (int i = 0; i < objects.length; i++) {
				IResource file = (IResource) objects[i];
				paths[i] = file.getProjectRelativePath().toString();
			}
			return paths;
		}
		
		return null;
	}

}
