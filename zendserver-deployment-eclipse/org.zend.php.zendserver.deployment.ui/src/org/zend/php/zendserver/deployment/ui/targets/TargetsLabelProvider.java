package org.zend.php.zendserver.deployment.ui.targets;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.zend.php.zendserver.deployment.core.database.ConnectionState;
import org.zend.php.zendserver.deployment.core.database.ITargetDatabase;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.Messages;
import org.zend.sdklib.target.IZendTarget;

public class TargetsLabelProvider extends LabelProvider {

	@Override
	public String getText(Object element) {
		if (element instanceof IZendTarget) {
			IZendTarget target = (IZendTarget) element;
			return NLS.bind(Messages.TargetsLabelProvider_TargetLabel, new Object[] { target.getHost(), target.getId()});
		}
		if (element instanceof ITargetDatabase) {
			ConnectionState state = ((ITargetDatabase) element).getState();
			return "MySQL Connection (" + state.getLabel() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		return super.getText(element);
	}
	
	@Override
	public Image getImage(Object element) {
		if (element instanceof IZendTarget) {
			return Activator.getDefault().getImage(Activator.IMAGE_TARGET);
		}
		if (element instanceof ITargetDatabase) {
			Image result = null;
			ConnectionState state = ((ITargetDatabase) element).getState();
			switch (state) {
			case CONNECTED:
				result = Activator.getDefault().getImage(
						Activator.IMAGE_DATABASE_ON);
				break;
			case DISCONNECTED:
				result = Activator.getDefault().getImage(
						Activator.IMAGE_DATABASE_OFF);
				break;
			case UNAVAILABLE:
				result = Activator.getDefault().getImage(
						Activator.IMAGE_DATABASE_CREATE);
				break;
			default:
				result = Activator.getDefault().getImage(
						Activator.IMAGE_DATABASE);
				break;
			}
			return result;
		}
		if (element instanceof ILaunchConfiguration) {
			return Activator.getDefault().getImage(Activator.IMAGE_APPLICATION);
		}
		
		return super.getImage(element);
	}
}
