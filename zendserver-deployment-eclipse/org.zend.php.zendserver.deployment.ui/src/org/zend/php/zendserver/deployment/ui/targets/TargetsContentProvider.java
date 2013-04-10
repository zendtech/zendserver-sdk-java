package org.zend.php.zendserver.deployment.ui.targets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.zend.php.zendserver.deployment.core.database.TargetsDatabaseManager;
import org.zend.php.zendserver.deployment.core.debugger.PHPLaunchConfigs;
import org.zend.sdklib.internal.target.OpenShiftTarget;
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
			IZendTarget[] targets = mgr.getTargets();
			List<IZendTarget> result = new ArrayList<IZendTarget>();
			for (IZendTarget target : targets) {
				if (!target.isTemporary()) {
					result.add(target);
				}
			}
			return result.toArray(new IZendTarget[result.size()]);
		}
		return new Object[0];
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IZendTarget) {
			PHPLaunchConfigs cfgs = new PHPLaunchConfigs();
			IZendTarget target = (IZendTarget) parentElement;
			ILaunchConfiguration[] configs = cfgs.getLaunches(target);
			if (hasDatabaseSupport(target)) {
				Object[] result = new Object[configs.length + 1];
				System.arraycopy(configs, 0, result, 1, configs.length);
				result[0] = TargetsDatabaseManager.getManager().getConnection(
						(IZendTarget) parentElement);
				return result;
			} else {
				return configs;
			}
		}
		return null;
	}

	private boolean hasDatabaseSupport(IZendTarget target) {
		if (TargetsManager.isPhpcloud(target)) {
			return true;
		}
		if (TargetsManager.isOpenShift(target)
				&& OpenShiftTarget.hasDatabaseSupport(target)) {
			return true;
		}
		return false;
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
