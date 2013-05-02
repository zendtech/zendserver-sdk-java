package org.zend.php.zendserver.deployment.ui.targets;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
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
	CreateTargetWizard wizard;
	private String type;
	
	public TargetDetailsPage(Contribution[] elements, CreateTargetWizard createTargetWizard) {
		super(Messages.TargetDetailsPage_TargetDetails);
		setTitle(Messages.TargetDetailsPage_AddTarget);
		setDescription(Messages.TargetDetailsPage_SpecifyTargetDetails);
//		setImageDescriptor(Activator.getImageDescriptor(Activator.IMAGE_WIZBAN_DEP));
		
		composite = new TargetDetailsComposite(elements);
		this.wizard = createTargetWizard;
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
		
		PropertyChangeListener messageListener = new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent evt) {
				AbstractTargetDetailsComposite src = (AbstractTargetDetailsComposite) evt
						.getSource();
				targets = src.getTarget();
				final Object newVal = evt.getNewValue();
				Display.getDefault().syncExec(new Runnable() {

					public void run() {
						setErrorMessage(null);
						if (newVal != null) {
							setMessage((String) newVal);
						}
						setPageComplete(true);
					}
				});
			}
		};
		composite.addPropertyChangeListener(AbstractTargetDetailsComposite.PROP_MESSAGE, messageListener);
		
		PropertyChangeListener modifyListener = new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent evt) {
				targets = null;
				setPageComplete((Boolean) evt.getNewValue());
			}
			
		};
		composite.addPropertyChangeListener(AbstractTargetDetailsComposite.PROP_MODIFY, modifyListener);
		setPageComplete(false);
		if (defaultTargetSettings != null) {
			composite.setDefaultTargetSettings(defaultTargetSettings);
		}
		setControl(newControl);
	}

	public void setType(String name) {
		composite.setType(name);
		setErrorMessage(null);
		targets = null;
		type = name;
	//	setPageComplete(!composite.hasPage(name));
	}
	
	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			if (ZendTargetDetailsComposite.class.getName().equals(type)) {
				wizard.setWindowTitle(Messages.TargetDetailsPage_1);
				setTitle(Messages.TargetDetailsPage_AddTarget);
				setDescription(Messages.TargetDetailsPage_SpecifyTargetDetails);
			} else if (DevCloudDetailsComposite.class.getName().equals(type)) {
				wizard.setWindowTitle(Messages.TargetDetailsPage_3);
				setTitle(Messages.TargetDetailsPage_4);
			} else if (OpenshiftDetailsComposite.class.getName().equals(type)) {
				wizard.setWindowTitle(Messages.TargetDetailsPage_5);
				setTitle(Messages.TargetDetailsPage_6);
			} else if (DetectLocal.class.getName().equals(type)) {
				wizard.setWindowTitle(Messages.TargetDetailsPage_7);
				setTitle(Messages.TargetDetailsPage_8);
			}
		} else {
			wizard.setWindowTitle(Messages.AddTargetAction_AddTarget);
		}
		super.setVisible(visible);
	}
	
	public IZendTarget[] getTarget() {
		return targets;
	}
	
	public IStatus validate() throws InvocationTargetException,
			InterruptedException {
		targets = null;
		IStatus status = composite.validate();
		if (status.getSeverity() == IStatus.CANCEL) {
			setPageComplete(true);
		} else {
			setPageComplete(targets != null && targets.length > 0);
		}
		return status;
	}

	public void setDefaultTargetSettings(IZendTarget target) {
		defaultTargetSettings = target;
	}

	public boolean hasPage(String type) {
		return composite.hasPage(type);
	}
	
	public String getType() {
		return type;
	}
	
}
