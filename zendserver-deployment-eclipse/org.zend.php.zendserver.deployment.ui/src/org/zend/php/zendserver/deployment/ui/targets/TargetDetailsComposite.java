package org.zend.php.zendserver.deployment.ui.targets;

import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.HelpEvent;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.internal.help.WorkbenchHelpSystem;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.sdklib.target.IZendTarget;

/**
 * Single area that contains all different composites for editing target details.
 * Shows one composite at a time. It can be switched using setType().
 * Call validate() to validate data provided by user.
 * 
 * Listen on AbstractTargetDetailsComposite.PROP_ERROR_MESSAGE for validation results. 
 *
 */
public class TargetDetailsComposite {

	private AbstractTargetDetailsComposite[] targetComposites;
	
	private Composite[] composites;
	
	private Composite clientArea;
	
	private int currentComposite;

	private IRunnableContext runnableContext;
	
	public TargetDetailsComposite(Contribution[] elements) {
		AbstractTargetDetailsComposite[] composites = new AbstractTargetDetailsComposite[elements.length];
		for (int i = 0; i < elements.length; i++) {
			try {
				composites[i] = (AbstractTargetDetailsComposite) elements[i].control.newInstance();
			} catch (InstantiationException e) {
				Activator.log(e);
			} catch (IllegalAccessException e) {
				Activator.log(e);
			}
		}
		
		targetComposites = composites;
	}
	
	/**
	 * Add property change listener to composites.
	 * 
	 * @param propertyName property name
	 * @param listener change listener
	 */
	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		for (int i = 0; i < targetComposites.length; i++) {
			targetComposites[i].addPropertyChangeListener(propertyName, listener);
		}
	}
	
	/**
	 * Remove property change listener to composites.
	 * 
	 * @param propertyName property name
	 * @param listener change listener
	 */
	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		for (int i = 0; i < targetComposites.length; i++) {
			targetComposites[i].removePropertyChangeListener(propertyName, listener);
		}
	}
	
	/**
	 * Create composite area.
	 * 
	 * @param parent Parent composite to lay widgets on top of.
	 * 
	 * @return created control.
	 */
	public Composite create(Composite parent) {
		clientArea = new Composite(parent, SWT.NONE);
		clientArea.setLayoutData(new GridData(GridData.FILL_BOTH));
		clientArea.setLayout(new GridLayout(1, true));
		
		composites = new Composite[targetComposites.length];
		for (int i = 0; i < targetComposites.length; i++) {
			composites[i] = targetComposites[i].create(clientArea);
			if (composites[i] != null) {
				composites[i].setVisible(currentComposite == i);
				((GridData)composites[i].getLayoutData()).exclude = !(currentComposite == i);
			}
		}	
		return clientArea;
	}
	
	/**
	 * Specify composite type to show up, e.g. ZendTargetDetailsComposite.getClass().getName() or some other class name.
	 * @param name
	 */
	public void setType(String name) {
		int idx = -1;
		if (name != null) {
			for (int i = 0; idx == -1 && i < targetComposites.length; i++) {
				if (name.equals(targetComposites[i].getClass().getName())) {
					idx = i;
				}
			}
		}
		
		if ((currentComposite != -1) && (composites != null)) {
			if ((composites[currentComposite] != null) && (!composites[currentComposite].isDisposed())) {
				composites[currentComposite].setVisible(false);
				((GridData)composites[currentComposite].getLayoutData()).exclude = true;
			}
		}
		currentComposite = idx;
		
		if ((currentComposite != -1) && (composites != null)) {
			if ((composites[currentComposite] != null) && (!composites[currentComposite].isDisposed())) {
				composites[currentComposite].setVisible(true);
				((GridData)composites[currentComposite].getLayoutData()).exclude = false;
			}
		}
		if (clientArea != null && currentComposite != -1) {
			final String help = targetComposites[currentComposite].getHelpResource();
			
			clientArea.setData(WorkbenchHelpSystem.HELP_KEY, help);
			clientArea.addHelpListener(new HelpListener() {
				public void helpRequested(HelpEvent arg0) {
					org.eclipse.swt.program.Program.launch(help);
				}
			});
			
			clientArea.setData(WorkbenchHelpSystem.HELP_KEY, help);
			clientArea.addHelpListener(new HelpListener() {
				public void helpRequested(HelpEvent arg0) {
					org.eclipse.swt.program.Program.launch(help);
				}
			});
			
			
		}
		if ((clientArea != null) && (! clientArea.isDisposed())) {
			clientArea.layout();
		}
	}
	
	public boolean hasPage(String name) {
		int idx = -1;
		for (int i = 0; idx == -1 && i < targetComposites.length; i++) {
			if (name.equals(targetComposites[i].getClass().getName())) {
				idx = i;
			}
		}
		
		return (idx != -1) && targetComposites[idx].hasPage();
	}

	private IStatus result;
	
	/**
	 * Validate data entered by user.
	 * Returns job (already scheduled), that performs validation. One may want to listen on the job change
	 * to update the GUI accordingly.
	 * 
	 * @return Job that performs validation.
	 * @throws InterruptedException 
	 * @throws InvocationTargetException 
	 */
	public IStatus validate() throws InvocationTargetException, InterruptedException {
		IRunnableWithProgress runnable = new IRunnableWithProgress() {
				
			public void run(IProgressMonitor monitor) throws InvocationTargetException,
					InterruptedException {
				result = targetComposites[currentComposite].validate(monitor);
			}
		};
		runnableContext.run(true, true, runnable);
		MessageTranslator messageTranslator = new MessageTranslator();
		switch (result.getSeverity()) {
		case IStatus.OK:
			targetComposites[currentComposite]
					.setErrorMessage(null);
			break;
		case IStatus.WARNING:
			targetComposites[currentComposite]
					.setWarningMessage(messageTranslator.translate(result
							.getMessage()));
			break;
		case IStatus.ERROR:
			targetComposites[currentComposite]
					.setErrorMessage(messageTranslator.translate(result
							.getMessage()));
			break;
		}
		return result;
	}

	public void setDefaultTargetSettings(IZendTarget defaultTarget) {
		targetComposites[currentComposite].setDefaultTargetSettings(defaultTarget);
	}

	public IZendTarget[] getTarget() {
		return targetComposites[currentComposite].getTarget();
	}

	public void setRunnableContext(IRunnableContext context) {
		this.runnableContext = context;
		for (AbstractTargetDetailsComposite composite : targetComposites) {
			composite.setRunnableContext(context);
		}
	}
	
}
