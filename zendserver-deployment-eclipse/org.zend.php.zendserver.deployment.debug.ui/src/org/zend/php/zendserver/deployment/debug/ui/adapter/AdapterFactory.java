package org.zend.php.zendserver.deployment.debug.ui.adapter;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.zend.php.zendserver.deployment.debug.ui.listeners.LaunchConfigurationDoubleClickListener;

public class AdapterFactory implements IAdapterFactory {

	private Class[] adapterList = new Class[] { IDoubleClickListener.class };

	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adaptableObject instanceof ILaunchConfiguration) {
			if (adapterType == IDoubleClickListener.class) {
				return new LaunchConfigurationDoubleClickListener();
			}
		}
		return null;
	}

	public Class[] getAdapterList() {
		return adapterList ;
	}

}
