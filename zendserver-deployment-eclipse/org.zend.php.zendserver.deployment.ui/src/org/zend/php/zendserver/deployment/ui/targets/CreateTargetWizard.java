package org.zend.php.zendserver.deployment.ui.targets;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.zend.php.zendserver.deployment.ui.Messages;
import org.zend.sdklib.target.IZendTarget;

public class CreateTargetWizard extends Wizard {

	private SelectTargetTypePage typePage;
	private TargetDetailsPage detailsPage;
	private WizardDialog dialog;
	
	public CreateTargetWizard() {
		typePage = new SelectTargetTypePage();
		detailsPage = new TargetDetailsPage();
		typePage.addPropertyChangeListener(SelectTargetTypePage.PROP_TYPE, new PropertyChangeListener() {
			
			public void propertyChange(PropertyChangeEvent evt) {
				detailsPage.setType(typePage.getType());
			}
		});
		typePage.addPropertyChangeListener(SelectTargetTypePage.PROP_DOUBLECLICK, new PropertyChangeListener() {
			
			public void propertyChange(PropertyChangeEvent evt) {
				dialog.showPage(detailsPage);
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
		
		// otherwise, run validate
		Job job = detailsPage.validate();
		
		Display display = Display.getDefault();
		while (job.getResult() == null) {
			if (! display.readAndDispatch()) {
				display.sleep();
			}
		}
		
		
		return detailsPage.getTarget() != null;
	}
	
	public WizardDialog createDialog(Shell parentShell) {
		dialog = new WizardDialog(parentShell, this);
		dialog.setTitle(Messages.AddTargetAction_AddTarget);
		
		return dialog;
	}

	public IZendTarget getTarget() {
		return detailsPage.getTarget();
	}

}
