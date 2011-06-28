package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorPackage;
import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;


public class VersionControl {

	public static final int EQUALS = 0;
	public static final int CONFLICTS = 1;
	public static final int RANGE = 2;
	public static final int EXCLUDE = 4;
	private String[] modeOperators = {"=", "!=", "range"};
	
	
	private IModelObject input;
	
	private boolean isRefresh;

	private Label minLabel;
	private Text minText;

	private Label maxLabel;
	private Text maxText;

	private Label equalsLabel;
	private Text equalsText;

	private Label conflictsLabel;
	private Text conflictsText;

	private Label excludeLabel;
	private Text excludeText;

	private Combo choice;
	private DeploymentDescriptorEditor editor;
	private int modes;
	
	public VersionControl(int modes) {
		this.modes = modes;
	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void setFocus() {
		
	}

	public void refresh() {
		isRefresh = true;
		try {
			int versionChoice = EQUALS;
			if (input.get(DeploymentDescriptorPackage.DEPENDENCY_EQUALS) != null) {
				versionChoice = CONFLICTS;
			} else if (input.get(DeploymentDescriptorPackage.DEPENDENCY_MAX) != null || input.get(DeploymentDescriptorPackage.DEPENDENCY_MIN) != null) {
				versionChoice = RANGE;
			}
			
			choice.select(versionChoice);
			updateFieldsVisibility();			
			// TODO
		} finally {
			isRefresh = false;
		}
	}

	private void updateFieldsVisibility() {
		boolean showEqualsField = !modeOperators[RANGE].equals(choice.getText());
		equalsText.setVisible(showEqualsField);
		minText.setVisible(!showEqualsField);
		minLabel.setVisible(!showEqualsField);
		maxText.setVisible(!showEqualsField);
		maxLabel.setVisible(!showEqualsField);
		
		if ((modes & EXCLUDE) == EXCLUDE) {
			excludeText.setVisible(!showEqualsField);
			excludeLabel.setVisible(!showEqualsField);
		}
		
		switch (choice.getSelectionIndex()) {
		case EQUALS:
			String str = input.get(DeploymentDescriptorPackage.DEPENDENCY_EQUALS);
			equalsText.setText(str == null ? "" : str);
			break;
		case CONFLICTS:
			str = input.get(DeploymentDescriptorPackage.DEPENDENCY_CONFLICTS);
			equalsText.setText(str == null ? "" : str);
			break;
		case RANGE:
			str = input.get(DeploymentDescriptorPackage.DEPENDENCY_MIN);
			minText.setText(str == null ? "" : str);
			str = input.get(DeploymentDescriptorPackage.DEPENDENCY_MAX);
			maxText.setText(str == null ? "" : str);
			break;
		}
	}
	
	public void setInput(IModelObject input) {
		this.input = input;
	}
	
	public void setEditor(DeploymentDescriptorEditor editor) {
		this.editor = editor;
	}

	public void createContents(Composite client, FormToolkit toolkit) {
		
		equalsLabel = toolkit.createLabel(client, "Version");
		choice = new Combo(client, SWT.READ_ONLY);
		if ((modes & EQUALS) == EQUALS)
			choice.add(modeOperators[EQUALS]);
		if ((modes & CONFLICTS) == CONFLICTS)
			choice.add(modeOperators[CONFLICTS]);
		if ((modes & RANGE) == RANGE)
			choice.add(modeOperators[RANGE]);
		choice.select(0);
		choice.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateFieldsVisibility();
			}
		});
		
		equalsText = toolkit.createText(client, "");
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		equalsText.setLayoutData(gd);
		equalsText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (isRefresh) return;
				String txt = ((Text)e.widget).getText();
				equalsChange("".equals(txt) ? null : txt);
			}
		});

		toolkit.createLabel(client, "");
		
		minLabel = toolkit.createLabel(client, "Minimum");
		minText = toolkit.createText(client, "");
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		minText.setLayoutData(gd);
		minText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (isRefresh) return;
				String txt = ((Text)e.widget).getText();
				minChange("".equals(txt) ? null : txt);
			}
		});
		
		toolkit.createLabel(client, "");
		
		maxLabel = toolkit.createLabel(client, "Maximum");
		maxText = toolkit.createText(client, "");
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		maxText.setLayoutData(gd);
		maxText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (isRefresh) return;
				String txt = ((Text)e.widget).getText();
				maxChange("".equals(txt) ? null : txt);
			}
		});
		
		if ((modes & EXCLUDE) == EXCLUDE) {
			toolkit.createComposite(client);
			
			excludeLabel = toolkit.createLabel(client, "Exclude");
			excludeLabel.setLayoutData(new GridData());
			excludeText = toolkit.createText(client, "", SWT.MULTI|SWT.WRAP|SWT.V_SCROLL);
			gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
			gd.heightHint = 100;
			gd.widthHint = 100;
			excludeText.setLayoutData(gd);
			excludeText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					if (isRefresh) return;
					String txt = ((Text)e.widget).getText();
					excludeChange("".equals(txt) ? null : txt);
				}
			});
		}
	}

	protected void excludeChange(String text) {
		// TODO Auto-generated method stub
		
	}
	
	protected void conflictsChange(String text) {
		input.set(DeploymentDescriptorPackage.DEPENDENCY_CONFLICTS, text);
	}

	protected void equalsChange(String text) {
		input.set(DeploymentDescriptorPackage.DEPENDENCY_EQUALS, text);
	}

	protected void maxChange(String text) {
		input.set(DeploymentDescriptorPackage.DEPENDENCY_MAX, text);
	}

	protected void minChange(String text) {
		input.set(DeploymentDescriptorPackage.DEPENDENCY_MIN, text);
	}

	protected void nameChange(String text) {
		input.set(DeploymentDescriptorPackage.DEPENDENCY_NAME, text);
	}
}
