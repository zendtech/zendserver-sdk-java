package org.zend.php.zendserver.deployment.ui.targets;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.zend.php.zendserver.deployment.ui.Messages;
import org.zend.sdklib.target.IZendTarget;

public class CreateTargetWizard extends Wizard {

	private SelectTargetTypePage typePage;
	private TargetDetailsPage detailsPage;
	private NewTargetWizardDialog dialog;
	
	private static class NewTargetWizardDialog extends WizardDialog {

		public NewTargetWizardDialog(Shell parentShell, IWizard newWizard) {
			super(parentShell, newWizard);
		}
		
		/* 
		 * make this method public, so that we can close wizard when user double-clicks target that doesn't require
		 * configuration.
		 */
		@Override
		public void finishPressed() {
			super.finishPressed();
		}
	}
	
	public CreateTargetWizard() {
		setWindowTitle(Messages.AddTargetAction_AddTarget);
		
		Contribution[] elements = NewTargetContributionsFactory.getElements();
		
		typePage = new SelectTargetTypePage(elements);
		detailsPage = new TargetDetailsPage(elements);
		
		typePage.getSelectTargetType().addPropertyChangeListener(SelectTargetType.PROP_TYPE, new PropertyChangeListener() {
			
			public void propertyChange(PropertyChangeEvent evt) {
				detailsPage.setType(typePage.getSelectTargetType().getType());
				dialog.updateButtons();
			}
		});
		typePage.getSelectTargetType().addPropertyChangeListener(SelectTargetType.PROP_DOUBLECLICK, new PropertyChangeListener() {
			
			public void propertyChange(PropertyChangeEvent evt) {
				if (detailsPage.isPageComplete()) {
					dialog.finishPressed();
				} else {
					dialog.showPage(detailsPage);
				}
			}
		});
		addPage(typePage);
		addPage(detailsPage);
	}
	
	@Override
	public boolean performFinish() {
		IZendTarget target = detailsPage.getTarget();
		if(target != null) {
			return true;
		}
		
		try {
			getContainer().run(true, true, new IRunnableWithProgress() {
				
				public void run(IProgressMonitor monitor) throws InvocationTargetException,
						InterruptedException {
					Job job = detailsPage.validate();
					monitor.beginTask(job.getName(), IProgressMonitor.UNKNOWN);
					while (job.getResult() == null) {
						Thread.sleep(500);
						if (monitor.isCanceled()) {
							job.getThread().interrupt();
						}
					}
				}
			});
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return detailsPage.getTarget() != null;
	}
	
	public WizardDialog createDialog(Shell parentShell) {
		dialog = new NewTargetWizardDialog(parentShell, this);
		dialog.setTitle(Messages.AddTargetAction_AddTarget);
		
		return dialog;
	}

	public IZendTarget getTarget() {
		return detailsPage.getTarget();
	}

}
