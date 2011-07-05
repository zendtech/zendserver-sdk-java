package org.zend.php.zendserver.deployment.ui.editors;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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
	
	public String[] choices = {"=", "!=", "Range"};
	public int[] choiceTypes = {EQUALS, CONFLICTS, RANGE};
	
	private IModelObject input;
	
	private TextField equals;
	private TextField min;
	private TextField max;
	private TextField conflicts;
	private ListField exclude;
	
	private Combo choice;
	private DeploymentDescriptorEditor editor;
	private int modes;
	private Composite client;
	private Composite inputsComposite;
	
	public VersionControl(int modes) {
		this.modes = modes;
		
		if ((modes & EQUALS) == EQUALS) {
			equals = new TextField(null, DeploymentDescriptorPackage.DEPENDENCY_EQUALS, null);
		}
		
		if ((modes & CONFLICTS) == CONFLICTS) {
			conflicts = new TextField(null, DeploymentDescriptorPackage.DEPENDENCY_CONFLICTS, null);
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
		
		int sel = 0;
		for (int i = 0; i < choiceTypes.length; i++) {
			if (choiceTypes[i] == versionChoice) {
				sel = i;
			}
		}
		
		sel = Arrays.asList(choice.getItems()).indexOf(choices[sel]);
		
		choice.select(sel);
		
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
		int selection = Arrays.asList(choices).indexOf(choice.getText());
		
		if ((modes & EQUALS) == EQUALS) {
			equals.setVisible(selection == 0);
		}
		
		if ((modes & CONFLICTS) == CONFLICTS) {
			conflicts.setVisible(selection == 1);
		}
		
		if ((modes & EXCLUDE) == EXCLUDE) {
			exclude.setVisible(selection == 2);
		}

		if ((modes & RANGE) == RANGE) {
			min.setVisible(selection == 2);
			max.setVisible(selection == 2);
		}
		
		// re-layout and make sure all widgets are visible
		Composite cmp = client.getParent();
		cmp.layout();
		Point size = cmp.getSize();
		Point newsize = cmp.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		cmp.setSize(size.x, newsize.y);
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
		this.client = client;
		
		choice = new Combo(client, SWT.READ_ONLY);
		if ((modes & EQUALS) == EQUALS)
			choice.add(choices[0]);
		if ((modes & CONFLICTS) == CONFLICTS)
			choice.add(choices[1]);
		if ((modes & RANGE) == RANGE)
			choice.add(choices[2]);
		choice.select(0);
		choice.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateFieldsVisibility();
			}
		});
		
		GridData gd = new GridData(SWT.LEFT, SWT.TOP, false, false);
		choice.setLayoutData(gd);
		
		inputsComposite = new Composite(client, SWT.NONE);
		GridLayout gl = new GridLayout(3, false);
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		inputsComposite.setLayout(gl);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.horizontalSpan = 2;
		inputsComposite.setLayoutData(gd);
		
		if ((modes & EQUALS) == EQUALS) {
			equals.create(inputsComposite, toolkit);
		}
		
		if ((modes & CONFLICTS) == CONFLICTS) {
			conflicts.create(inputsComposite, toolkit);
		}
		
		if ((modes & RANGE) == RANGE) {
			min.create(inputsComposite, toolkit);
			max.create(inputsComposite, toolkit);
		}
		
		if ((modes & EXCLUDE) == EXCLUDE) {
			exclude.create(inputsComposite, toolkit);
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
}
