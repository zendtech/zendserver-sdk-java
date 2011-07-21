package org.zend.php.zendserver.deployment.ui.contributions;

import org.eclipse.jface.dialogs.MessageDialog;
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

/**
 * Sample contribution to Overview/Testing page of descriptor editor
 */
public class PHPUnitTestContribution extends WorkbenchWindowControlContribution {

	@Override
	protected Control createControl(final Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		
		ImageHyperlink link = toolkit.createImageHyperlink(parent, SWT.NONE);
		link.setText(Messages.OverviewPage_LaunchingPHPTest);
		link.setImage(Activator.getImageDescriptor(
				Activator.IMAGE_RUN_TEST).createImage());
		link.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				MessageDialog.openInformation(parent.getDisplay().getActiveShell(), "Not ready", "Can you implement it?"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		});
		
		return link;
	}

}
