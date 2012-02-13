package org.zend.php.common;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.internal.p2.discovery.Catalog;
import org.eclipse.equinox.internal.p2.discovery.DiscoveryCore;
import org.eclipse.equinox.internal.p2.ui.discovery.repository.RepositoryDiscoveryStrategy;
import org.eclipse.equinox.internal.p2.ui.discovery.util.WorkbenchUtil;
import org.eclipse.equinox.internal.p2.ui.discovery.wizards.CatalogConfiguration;
import org.eclipse.equinox.internal.p2.ui.discovery.wizards.DiscoveryWizard;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;

public class Customization {

	public static void ShowCustomizationDialog() {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				Catalog catalog = new Catalog();
				catalog.setEnvironment(DiscoveryCore.createEnvironment());
				catalog.setVerifyUpdateSiteAvailability(false);

				// add strategy for retrieving remote catalog
				RepositoryDiscoveryStrategy strategy = new RepositoryDiscoveryStrategy();
				try {
					String uri = System.getProperty("com.zend.php.customization.site.url");
					if (uri == null) {
						uri = System.getProperty("org.zend.php.customization.site.url");
					}
					strategy.addLocation(new URI(uri));
				} catch (URISyntaxException e) {
					ErrorDialog
							.openError(
									Display.getCurrent().getActiveShell(),
									Messages.ConnectorDiscoveryWizardMainPage_error_title,
									Messages.ConnectorDiscoveryWizardMainPage_error_msg,
													new Status(IStatus.ERROR, Activator.PLUGIN_ID,
															Activator.INTERNAL_ERROR, e.getReason(), e));
					Activator.log(e);
					return;
				}
				catalog.getDiscoveryStrategies().add(strategy);

				CatalogConfiguration configuration = new CatalogConfiguration();
				configuration.setShowTagFilter(false);

				DiscoveryWizard wizard = new DiscoveryWizard(catalog,
						configuration);
				wizard.setWindowTitle(Messages.ConnectorDiscoveryWizard_title);
				wizard.setNeedsProgressMonitor(true);
				wizard.getCatalogPage().setTitle(
						Messages.ConnectorDiscoveryWizardMainPage_title);
				wizard.getCatalogPage().setDescription(
						Messages.ConnectorDiscoveryWizardMainPage_description);
				WizardDialog dialog = new WizardDialog(
						WorkbenchUtil.getShell(), wizard);
				dialog.open();
			}
		});
	}

	public void run() {
		ShowCustomizationDialog();
	}
}
