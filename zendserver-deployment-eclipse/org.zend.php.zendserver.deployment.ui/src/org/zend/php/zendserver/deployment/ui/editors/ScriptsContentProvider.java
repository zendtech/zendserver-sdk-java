package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

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
		model[0] = new ScriptType("Staging", new Script[] { new Script("preStage"), new Script("postStage") });
		model[1] = new ScriptType("Activation", new Script[] { new Script("preActivate"), new Script("postActivate") });
		model[2] = new ScriptType("Deactivation", new Script[] { new Script("preDeactivate"), new Script("postDeactivate") });
		model[3] = new ScriptType("Unstaging", new Script[] { new Script("preUnstage"), new Script("postUnstage") });
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
