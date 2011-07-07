package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.editors.ScriptsContentProvider.Script;

public class ScriptsLabelProvider extends LabelProvider {

	private ScriptsSection section;

	public ScriptsLabelProvider(ScriptsSection section) {
		this.section = section;
	}
	
	@Override
	public String getText(Object element) {
		if (element instanceof ScriptsContentProvider.ScriptType) {
			return ((ScriptsContentProvider.ScriptType) element).name;
		}
		
		if (element instanceof ScriptsContentProvider.Script) {
			return ((ScriptsContentProvider.Script) element).name;
		}
		
		return super.getText(element);
	}
	
	@Override
	public Image getImage(Object element) {
		if (element instanceof ScriptsContentProvider.ScriptType) {
			return Activator.getDefault().getImage(Activator.IMAGE_SCRIPT_TYPE);
		
		} else if (element instanceof ScriptsContentProvider.Script) {
			ScriptsContentProvider.Script script = (Script) element;
			IFile file = section.getScript(script.name);
			if (file != null && file.exists()) {
				return Activator.getDefault().getImage(Activator.IMAGE_SCRIPT);
			} else {
				return Activator.getDefault().getImage(Activator.IMAGE_SCRIPT_NOTEXISTS);
			}
		}
		
		return super.getImage(element);
	}
}
