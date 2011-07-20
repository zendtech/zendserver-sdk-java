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
import org.zend.php.zendserver.deployment.ui.actions.RunApplicationAction;

/**
 * Sample contribution to Overview/Testing page of descriptor editor
 */
public class RunApplicationContribution extends WorkbenchWindowControlContribution {
	
	@Override
	protected Control createControl(Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		
		ImageHyperlink link = toolkit.createImageHyperlink(parent,
				SWT.NONE);
		link.setText(Messages.OverviewPage_LaunchingPHPApp);
		link.setImage(Activator.getImageDescriptor(
				Activator.IMAGE_RUN_APPLICATION).createImage());
		link.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				new RunApplicationAction().run();
			}
		});
		
		return link;
	}
}
