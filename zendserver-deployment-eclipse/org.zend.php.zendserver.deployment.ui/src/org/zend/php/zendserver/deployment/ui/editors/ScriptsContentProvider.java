package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.zend.php.zendserver.deployment.ui.Messages;

public class ScriptsContentProvider implements ITreeContentProvider {

	public static class ScriptType {
		String name;
		Script[] scripts;
		
		public ScriptType(String name, Script[] scripts) {
			this.name = name;
			this.scripts = scripts;
		}
	}
	
	public static class Script {
		String name;
		public Script(String name) {
			this.name = name;
		}
	}
	
	public ScriptType[] model;
	
	public ScriptsContentProvider() {
		model = new ScriptType[4];
		model[0] = new ScriptType(Messages.ScriptsContentProvider_Staging, new Script[] { new Script("preStage"), new Script("postStage") }); //$NON-NLS-1$ //$NON-NLS-2$
		model[1] = new ScriptType(Messages.ScriptsContentProvider_Activation, new Script[] { new Script("preActivate"), new Script("postActivate") }); //$NON-NLS-1$ //$NON-NLS-2$
		model[2] = new ScriptType(Messages.ScriptsContentProvider_Deactivation, new Script[] { new Script("preDeactivate"), new Script("postDeactivate") }); //$NON-NLS-1$ //$NON-NLS-2$
		model[3] = new ScriptType(Messages.ScriptsContentProvider_Unstaging, new Script[] { new Script("preUnstage"), new Script("postUnstage") }); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	private static Object[] EMPTY = new Object[0];
	
	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

	public Object[] getElements(Object inputElement) {
		return model;
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof ScriptType) {
			return ((ScriptType) parentElement).scripts;
		}
		
		return EMPTY;
	}

	public Object getParent(Object element) {
		return null;
	}

	public boolean hasChildren(Object element) {
		return (element instanceof ScriptType);
	}

}
