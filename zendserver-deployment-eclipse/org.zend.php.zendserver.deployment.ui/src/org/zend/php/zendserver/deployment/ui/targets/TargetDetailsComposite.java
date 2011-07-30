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
	
	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		for (int i = 0; i < targetComposites.length; i++) {
			targetComposites[i].addPropertyChangeListener(propertyName, listener);
		}
	}
	
	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		for (int i = 0; i < targetComposites.length; i++) {
			targetComposites[i].removePropertyChangeListener(propertyName, listener);
		}
	}
	
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

	public Job validate() {
		return targetComposites[currentComposite].validate();
	}
	
}
