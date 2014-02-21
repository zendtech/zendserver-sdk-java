package org.zend.php.zendserver.deployment.ui.targets;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.php.internal.ui.util.PHPPluginImages;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.zend.core.notifications.NotificationManager;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.Messages;
import org.zend.sdklib.target.IZendTarget;

public class CreateTargetWizard extends Wizard {

	private SelectTargetTypePage typePage;
	private TargetDetailsPage detailsPage;
	private NewTargetWizardDialog dialog;
	private String type;
	private IZendTarget defaultTarget;
	
	private static class NewTargetWizardDialog extends WizardDialog {

		public NewTargetWizardDialog(Shell parentShell, IWizard newWizard) {
			super(parentShell, newWizard);
			
			addPageChangedListener(new IPageChangedListener() {
				
				public void pageChanged(PageChangedEvent event) {
					IWizardPage page = (IWizardPage) event
							.getSelectedPage();
					if (page instanceof TargetDetailsPage) {
						if (((TargetDetailsPage) page).getType().equals(
								DetectLocal.class.getName())) {
							((WizardPage) page).setPageComplete(true);
						}
					}
				}
			});
		}
		
		/* 
		 * make this method public, so that we can close wizard when user double-clicks target that doesn't require
		 * configuration.
		 */
		@Override
		public void finishPressed() {
			super.finishPressed();
		}
		
		@Override
		public void run(final boolean fork, final boolean cancelable,
				final IRunnableWithProgress runnable)
				throws InvocationTargetException, InterruptedException {
			Control currentFocus = getShell().getDisplay().getFocusControl();
			getButton(IDialogConstants.CANCEL_ID).setFocus();
			super.run(fork, cancelable, runnable);
			currentFocus.setFocus();
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.wizard.WizardDialog#backPressed()
		 */
		protected void backPressed() {
			if (getCurrentPage() instanceof TargetDetailsPage) {
				((WizardPage) getCurrentPage()).setPageComplete(false);
			}
			super.backPressed();
		}
		
	}

	public static final String LOCAL_PHP_CREATION_WIZARD = "local_php_creation_wizard"; //$NON-NLS-1$
	private static URL fgIconBaseURL = Activator.getDefault().getBundle()
			.getEntry("/icons/"); //$NON-NLS-1$

	private static final ImageDescriptor WIZARD_IMAGE = PHPPluginImages.create(
			fgIconBaseURL, PHPPluginImages.T_WIZBAN,
			"addtargetwizard.png"); //$NON-NLS-1$
	public CreateTargetWizard() {
		super();
		setWindowTitle(Messages.AddTargetAction_AddTarget);
		setDefaultPageImageDescriptor(WIZARD_IMAGE);
		setNeedsProgressMonitor(true);
	}
	
	public void addPages() {
		Contribution[] elements = NewTargetContributionsFactory.getElements();
		
		detailsPage = new TargetDetailsPage(elements,this);
		if (defaultTarget != null) {
			detailsPage.setDefaultTargetSettings(defaultTarget);
		}
		
		if (type != null) {
			detailsPage.setType(type);
		} else {
			typePage = new SelectTargetTypePage(elements);
			typePage.getSelectTargetType().addPropertyChangeListener(SelectTargetType.PROP_TYPE, new PropertyChangeListener() {
				
				public void propertyChange(PropertyChangeEvent evt) {
					detailsPage.setType(typePage.getSelectTargetType().getType());
					dialog.updateButtons();
				}
			});
			typePage.getSelectTargetType().addPropertyChangeListener(SelectTargetType.PROP_DOUBLECLICK, new PropertyChangeListener() {
				
				public void propertyChange(PropertyChangeEvent evt) {
					if (!detailsPage.hasPage(typePage.getSelectTargetType().getType())) {
						dialog.finishPressed();
					} else {
						dialog.showPage(detailsPage);
					}
				}
			});
			addPage(typePage);
		}
		addPage(detailsPage);
	}
	
	@Override
	public boolean performFinish() {
		try {
			IStatus status = detailsPage.validate();
			if (status.getSeverity() == IStatus.WARNING) {
				NotificationManager.registerWarning("Add Target", //$NON-NLS-1$
						status.getMessage(), 6000);
			}
		} catch (InvocationTargetException e) {
			// errored
			return false;
		} catch (InterruptedException e) {
			// cancelled
			return false;
		}
		
		if (!detailsPage.isPageComplete()) {
			return false;
		}
		
		return detailsPage.getTarget() != null;
	}
	
	public WizardDialog createDialog(Shell parentShell) {
		dialog = new NewTargetWizardDialog(parentShell, this);
		dialog.setTitle(Messages.AddTargetAction_AddTarget);
		
		return dialog;
	}

	public IZendTarget[] getTarget() {
		return detailsPage.getTarget();
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * Sets default values for the fields.
	 * It also disables idField, because usually we don't want to change id.
	 * 
	 * @param targetComposite
	 */
	public void setDefaultTarget(IZendTarget target) {
		this.defaultTarget = target;
		if (detailsPage != null) {
			detailsPage.setDefaultTargetSettings(target);
		}
	}
	
	@Override
	public boolean canFinish() {
		return detailsPage.isPageComplete();
	}

}
