package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorPackage;
import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;


public class VersionControl {

	public static final int EQUALS = 1;
	public static final int CONFLICTS = 2;
	public static final int RANGE = 4;
	public static final int EXCLUDE = 8;
	
	private IModelObject input;
	
	private TextField min;
	private TextField max;
	private TextField equals;
	private TextField conflicts;
	private ListField exclude;
	
	private Combo choice;
	private DeploymentDescriptorEditor editor;
	private int modes;
	
	public VersionControl(int modes) {
		this.modes = modes;
		
		if ((modes & EQUALS) == EQUALS) {
			equals = new TextField(null, DeploymentDescriptorPackage.DEPENDENCY_EQUALS, "Equals");
		}
		
		if ((modes & CONFLICTS) == CONFLICTS) {
			conflicts = new TextField(null, DeploymentDescriptorPackage.DEPENDENCY_CONFLICTS, "Conflicts");
		}
		
		if ((modes & RANGE) == RANGE) {
			min = new TextField(null, DeploymentDescriptorPackage.DEPENDENCY_MIN, "Minimum");
			max = new TextField(null, DeploymentDescriptorPackage.DEPENDENCY_MAX, "Maximum");
		}
		
		if ((modes & EXCLUDE) == EXCLUDE) {
			exclude = new ListField(null, DeploymentDescriptorPackage.DEPENDENCY_EXCLUDE, "Exclude");
		}
	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void setFocus() {
		
	}

	public void refresh() {
		int versionChoice = EQUALS;
		if (input.get(DeploymentDescriptorPackage.DEPENDENCY_CONFLICTS) != null) {
			versionChoice = CONFLICTS;
		} else if (input.get(DeploymentDescriptorPackage.DEPENDENCY_MAX) != null || input.get(DeploymentDescriptorPackage.DEPENDENCY_MIN) != null) {
			versionChoice = RANGE;
		}
		
		choice.setText(getOper(versionChoice));
		
		if (equals != null) {
			equals.refresh();
		}
		
		if (min != null) {
			min.refresh();
		}
		
		if (max != null) {
			max.refresh();
		}
		
		if (exclude != null) {
			exclude.refresh();
		}
		
		if (conflicts != null) {
			conflicts.refresh();
		}
		
		updateFieldsVisibility();
	}

	private void updateFieldsVisibility() {
		boolean showEqualsField = !getOper(RANGE).equals(choice.getText());
		if ((modes & EQUALS) == EQUALS) {
			equals.setVisible(showEqualsField);
		}
		
		if ((modes & RANGE) == RANGE) {
			min.setVisible(!showEqualsField);
			max.setVisible(!showEqualsField);
		}
		
		if ((modes & EXCLUDE) == EXCLUDE) {
			exclude.setVisible(!showEqualsField);
		}
		
		switch (choice.getSelectionIndex()) {
		case EQUALS:
			equals.refresh();
			break;
		case CONFLICTS:
			conflicts.refresh();
			break;
		case RANGE:
			min.refresh();
			max.refresh();
			break;
		}
	}
	
	public void setInput(IModelObject input) {
		this.input = input;
		
		if ((modes & EQUALS) == EQUALS) {
			equals.setInput(input);
		}
		
		if ((modes & RANGE) == RANGE) {
			min.setInput(input);
			max.setInput(input);
		}
		
		if ((modes & EXCLUDE) == EXCLUDE) {
			exclude.setInput(input);
		}
		
		if ((modes & CONFLICTS) == CONFLICTS) {
			conflicts.setInput(input);
		}
	}
	
	public void setEditor(DeploymentDescriptorEditor editor) {
		this.editor = editor;
	}

	public void createContents(Composite client, FormToolkit toolkit) {
		
		choice = new Combo(client, SWT.READ_ONLY);
		if ((modes & EQUALS) == EQUALS)
			choice.add(getOper(EQUALS));
		if ((modes & CONFLICTS) == CONFLICTS)
			choice.add(getOper(CONFLICTS));
		if ((modes & RANGE) == RANGE)
			choice.add(getOper(RANGE));
		choice.select(0);
		choice.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateFieldsVisibility();
			}
		});
		
		GridData gd = new GridData();
		gd.horizontalSpan = 3;
		choice.setLayoutData(gd);
		
		if ((modes & EQUALS) == EQUALS) {
			equals.create(client, toolkit);
		}
		
		if ((modes & RANGE) == RANGE) {
			min.create(client, toolkit);
			max.create(client, toolkit);
		}
		
		if ((modes & CONFLICTS) == CONFLICTS) {
			conflicts.create(client, toolkit);
		}
		
		if ((modes & EXCLUDE) == EXCLUDE) {
			exclude.create(client, toolkit);
		}
			
			/* TODO add MultiLineField
			excludeText = toolkit.createText(client, "", SWT.MULTI|SWT.WRAP|SWT.V_SCROLL);
			gd.heightHint = 100;
			gd.widthHint = 100;
			excludeText.setLayoutData(gd);
			excludeText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					if (isRefresh) return;
					String txt = ((Text)e.widget).getText();
					excludeChange("".equals(txt) ? null : txt);
				}
			});*/
	}
	
	private String getOper(int flag) {
		switch (flag) {
		case EQUALS: return "=";
		case CONFLICTS: return "!=";
		case RANGE : return "range";
		}
		
		return null;
	}
}
