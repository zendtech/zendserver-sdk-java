package org.zend.php.zendserver.deployment.ui.targets;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.Messages;
import org.zend.sdklib.target.IZendTarget;

/**
 * Wizard page for editing IZendTarget details. 
 * Allows for TargetDetails/DevCloud account data editing via TargetDetailsComposite.
 *
 */
public class TargetDetailsPage extends WizardPage {

	private TargetDetailsComposite composite;
	
	private IZendTarget target;
	
	protected TargetDetailsPage(AbstractTargetDetailsComposite[] pages) {
		super(Messages.TargetDetailsPage_TargetDetails);
		setTitle(Messages.TargetDetailsPage_AddTarget);
		setDescription(Messages.TargetDetailsPage_SpecifyTargetDetails);
		setImageDescriptor(Activator.getImageDescriptor(Activator.IMAGE_WIZBAN_DEP));
		
		composite = new TargetDetailsComposite(pages);
	}

	public void createControl(Composite parent) {
		Composite newControl = composite.create(parent);
		
		PropertyChangeListener listener = new PropertyChangeListener() {
			
			public void propertyChange(PropertyChangeEvent evt) {
				AbstractTargetDetailsComposite src = (AbstractTargetDetailsComposite) evt.getSource();
				target = src.getTarget();
				final String errorMessage = (String) evt.getNewValue();
				
				Display.getDefault().syncExec(new Runnable() {
					
					public void run() {
						setErrorMessage(errorMessage);
						setPageComplete(target != null);
					}
				});
			}
		};
		composite.addPropertyChangeListener(AbstractTargetDetailsComposite.PROP_ERROR_MESSAGE, listener );
		setPageComplete(true);
		setControl(newControl);
	}

	public void setType(String name) {
		composite.setType(name);
		setErrorMessage(null);
		target = null;
		setPageComplete(!composite.hasPage(name));
	}
	
	public IZendTarget getTarget() {
		return target;
	}
	
	public Job validate() {
		return composite.validate();
	}

}
