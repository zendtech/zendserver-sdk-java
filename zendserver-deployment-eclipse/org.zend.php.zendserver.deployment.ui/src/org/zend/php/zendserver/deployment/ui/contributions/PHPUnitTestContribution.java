package org.zend.php.zendserver.deployment.ui.contributions;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
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

	protected static final String TEST_COMMAND = "org.zend.php.zendserver.deployment.ui.phpunit"; //$NON-NLS-1$

	@Override
	protected Control createControl(final Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		
		ImageHyperlink link = toolkit.createImageHyperlink(parent, SWT.NONE);
		link.setText(Messages.OverviewPage_LaunchingPHPTest);
		link.setImage(Activator.getImageDescriptor(
				Activator.IMAGE_RUN_TEST).createImage());
		link.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				try {
					ICommandService srvce = ((ICommandService)PlatformUI.getWorkbench().getService(ICommandService.class));
					ExecutionEvent event = new ExecutionEvent();
					srvce.getCommand(TEST_COMMAND).executeWithChecks(event);
				} catch (ExecutionException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				} catch (NotDefinedException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				} catch (NotEnabledException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				} catch (org.eclipse.core.commands.NotHandledException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			}
		});
		
		return link;
	}

}
