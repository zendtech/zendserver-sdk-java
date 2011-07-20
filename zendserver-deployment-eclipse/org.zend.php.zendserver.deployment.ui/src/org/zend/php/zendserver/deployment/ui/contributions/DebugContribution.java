package org.zend.php.zendserver.deployment.ui.contributions;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.Messages;
import org.zend.php.zendserver.deployment.ui.actions.DeployAppInCloudAction;

public class DebugContribution extends WorkbenchWindowControlContribution {

	@Override
	protected Control createControl(Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		
		ImageHyperlink link = toolkit.createImageHyperlink(parent,SWT.NONE);
		link.setText(Messages.OverviewPage_LaunchingAndDebuggingPHPApp);
		link.setImage(Activator.getImageDescriptor(
				Activator.IMAGE_DEBUG_APPLICATION).createImage());
		link.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				new DeployAppInCloudAction().run();
			}
		});
		
		return link;
	}

}
