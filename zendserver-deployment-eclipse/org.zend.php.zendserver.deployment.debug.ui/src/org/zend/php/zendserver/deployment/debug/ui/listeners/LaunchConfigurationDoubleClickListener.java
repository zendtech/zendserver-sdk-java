package org.zend.php.zendserver.deployment.debug.ui.listeners;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;

public class LaunchConfigurationDoubleClickListener implements IDoubleClickListener {

	public void doubleClick(DoubleClickEvent event) {
		ISelection selection = event.getSelection();
		IStructuredSelection sselection = (IStructuredSelection) selection;
		Object obj = sselection.getFirstElement();
		ILaunchConfiguration config = (ILaunchConfiguration) obj;
		
		Shell shell = event.getViewer().getControl().getShell();
		
		DebugUITools.openLaunchConfigurationPropertiesDialog(shell, config, "org.eclipse.debug.ui.launchGroup.run"); //$NON-NLS-1$
	}
	
}
