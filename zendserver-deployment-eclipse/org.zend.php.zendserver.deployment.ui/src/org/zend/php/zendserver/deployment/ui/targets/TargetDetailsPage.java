package org.zend.php.zendserver.deployment.ui.targets;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.dialogs.IMessageProvider;
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
	
	private IZendTarget[] targets;

	private IZendTarget defaultTargetSettings;
	
	public TargetDetailsPage(Contribution[] elements) {
		super(Messages.TargetDetailsPage_TargetDetails);
		setTitle(Messages.TargetDetailsPage_AddTarget);
		setDescription(Messages.TargetDetailsPage_SpecifyTargetDetails);
		setImageDescriptor(Activator.getImageDescriptor(Activator.IMAGE_WIZBAN_DEP));
		
		composite = new TargetDetailsComposite(elements);
	}

	public void createControl(Composite parent) {
		Composite newControl = composite.create(parent);
		composite.setRunnableContext(getContainer());
		
		PropertyChangeListener errorListener = new PropertyChangeListener() {
			
			public void propertyChange(PropertyChangeEvent evt) {
				AbstractTargetDetailsComposite src = (AbstractTargetDetailsComposite) evt.getSource();
				targets = src.getTarget();
				final String errorMessage = (String) evt.getNewValue();
				setPageComplete(errorMessage == null);
				setMessage(null);
				
				Display.getDefault().syncExec(new Runnable() {
					
					public void run() {
						setErrorMessage(errorMessage);
					}
				});
			}
		};
		composite.addPropertyChangeListener(AbstractTargetDetailsComposite.PROP_ERROR_MESSAGE, errorListener);
		
		PropertyChangeListener warningListener = new PropertyChangeListener() {
			
			public void propertyChange(PropertyChangeEvent evt) {
				AbstractTargetDetailsComposite src = (AbstractTargetDetailsComposite) evt.getSource();
				targets = src.getTarget();
				final String warningMessage = (String) evt.getNewValue();
				setPageComplete(true);
				setErrorMessage(null);
				Display.getDefault().syncExec(new Runnable() {
					
					public void run() {
						setMessage(warningMessage, IMessageProvider.WARNING);
					}
				});
			}
		};
		composite.addPropertyChangeListener(AbstractTargetDetailsComposite.PROP_WARNING_MESSAGE, warningListener);
		
		PropertyChangeListener modifyListener = new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent evt) {
				targets = null;
			}
			
		};
		composite.addPropertyChangeListener(AbstractTargetDetailsComposite.PROP_MODIFY, modifyListener);
		
		if (defaultTargetSettings != null) {
			composite.setDefaultTargetSettings(defaultTargetSettings);
		}
		setPageComplete(false);
		setControl(newControl);
	}

	public void setType(String name) {
		composite.setType(name);
		setErrorMessage(null);
		targets = null;
	//	setPageComplete(!composite.hasPage(name));
	}
	
	public IZendTarget[] getTarget() {
		return targets;
	}
	
	public void validate() throws InvocationTargetException, InterruptedException {
		targets = null;
		composite.validate();
		setPageComplete(targets != null);
	}

	public void setDefaultTargetSettings(IZendTarget target) {
		defaultTargetSettings = target;
	}

	public boolean hasPage(String type) {
		return composite.hasPage(type);
	}
}
