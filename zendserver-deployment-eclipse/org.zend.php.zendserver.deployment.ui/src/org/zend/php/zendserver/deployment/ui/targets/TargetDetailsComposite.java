package org.zend.php.zendserver.deployment.ui.targets;

import java.beans.PropertyChangeListener;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.zend.php.zendserver.deployment.ui.Messages;

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
	
	public TargetDetailsComposite() {
		targetComposites = new AbstractTargetDetailsComposite[] {
			new ZendTargetDetailsComposite(),
			new DevCloudDetailsComposite()
		};
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
			composites[i].setVisible(currentComposite == i);
			((GridData)composites[i].getLayoutData()).exclude = !(currentComposite == i);
		}
		
		Button validateButton = new Button(clientArea, SWT.NONE);
		validateButton.setText(Messages.TargetDialog_validate);
		validateButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				targetComposites[currentComposite].validate();
			}
		});
		
		return clientArea;
	}
	
	/**
	 * Specify composite type to show up, e.g. ZendTargetDetailsComposite.getClass().getName() or some other class name.
	 * @param name
	 */
	public void setType(String name) {
		int idx = -1;
		for (int i = 0; idx == -1 && i < targetComposites.length; i++) {
			if (name.equals(targetComposites[i].getClass().getName())) {
				idx = i;
			}
		}
		
		composites[currentComposite].setVisible(false);
		((GridData)composites[currentComposite].getLayoutData()).exclude = true;
		currentComposite = idx;
		
		composites[currentComposite].setVisible(true);
		((GridData)composites[currentComposite].getLayoutData()).exclude = false;
		
		clientArea.layout();
	}

	/**
	 * Validate data entered by user.
	 * Returns job (already scheduled), that performs validation. One may want to listen on the job change
	 * to update the GUI accordingly.
	 * 
	 * @return Job that performs validation.
	 */
	public Job validate() {
		return targetComposites[currentComposite].validate();
	}
	
}
