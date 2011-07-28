package org.zend.php.zendserver.deployment.ui.targets;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.zend.php.zendserver.deployment.core.targets.PHPLaunchConfigs;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;

public class TargetsContentProvider implements ITreeContentProvider {

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof TargetsManager) {
			TargetsManager mgr = (TargetsManager) inputElement;
			return mgr.getTargets();
		}
		
		return new Object[0];
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IZendTarget) {
			PHPLaunchConfigs cfgs = new PHPLaunchConfigs();
			return cfgs.getLaunches((IZendTarget) parentElement);
		}
		return null;
	}

	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasChildren(Object element) {
		Object[] children = getChildren(element);
		return children != null && children.length > 0; 
	}

}
