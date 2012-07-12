package org.zend.php.zendserver.deployment.ui.targets;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.zend.php.zendserver.deployment.ui.HelpContextIds;
import org.zend.php.zendserver.deployment.ui.Messages;
import org.zend.sdklib.target.IZendTarget;

public class SelectTargetTypePage extends WizardPage {

	private SelectTargetType targetType;
	
	protected IZendTarget result;
	
	protected SelectTargetTypePage(Contribution[] elements) {
		super(Messages.SelectTargetTypePage_SelectTargetType);
		setTitle(Messages.SelectTargetTypePage_AddTarget);
		setDescription(Messages.SelectTargetTypePage_SelectTargetType);
//		setImageDescriptor(Activator.getImageDescriptor(Activator.IMAGE_WIZBAN_DEP));
		
		targetType = new SelectTargetType(elements);
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(1, false));
		
		targetType.create(composite);
		
		setControl(composite);
		targetType.addPropertyChangeListener(SelectTargetType.PROP_TYPE, new PropertyChangeListener() {
			
			public void propertyChange(PropertyChangeEvent event) {
				setPageComplete(event.getNewValue() != null);
			}
		});
		setPageComplete(targetType.getSelectionCount() > 0);
		PlatformUI.getWorkbench().getHelpSystem()
				.setHelp(getControl(), HelpContextIds.MANAGING_TARGETS);
	}
	
	public SelectTargetType getSelectTargetType() {
		return targetType;
	}
}
