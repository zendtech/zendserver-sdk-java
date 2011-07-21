package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorPackage;
import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;
import org.zend.php.zendserver.deployment.ui.Messages;

/**
 * Represents the version control composite in the dependency blocks
 */
public class VersionControl {

	public int[] choiceTypes = { EQUALS, CONFLICTS, RANGE };

	public static final int EQUALS = 1;
	public static final int CONFLICTS = 2;
	public static final int RANGE = 4;
	public static final int EXCLUDE = 8;

	private int modes;

	private Button btnEquals;
	private Button btnConflict;
	private Button btnMatches;

	private IModelObject input;

	private TextField equalsField;
	private TextField conflictsField;
	private TextField minField;
	private TextField maxField;
	private ListField excludeField;
	private Composite versions;
	private Composite equals;
	private Composite conflicts;
	private Composite range;

	public VersionControl(int modes) {
		this.modes = modes;
	}

	public void dispose() {
	}

	public void setFocus() {
	}

	public void refresh() {
		
		if (equalsField != null) {
			btnEquals
					.setSelection(input
							.get(DeploymentDescriptorPackage.DEPENDENCY_EQUALS) != null);
			equalsField.refresh();
		}

		if (conflictsField != null) {
			btnConflict
					.setSelection(input
							.get(DeploymentDescriptorPackage.DEPENDENCY_CONFLICTS) != null);
			conflictsField.refresh();
		}

		if (minField != null) {
			btnMatches
					.setSelection(input
							.get(DeploymentDescriptorPackage.DEPENDENCY_MAX) != null
							|| input.get(DeploymentDescriptorPackage.DEPENDENCY_MIN) != null);
			minField.refresh();
		}

		if (maxField != null) {
			maxField.refresh();
		}

		if (excludeField != null) {
			excludeField.refresh();
		}
		
		updateFieldsVisibility();
	}

	private void updateFieldsVisibility() {
		int selection = getSelected();
		
		if ((modes & RANGE) == RANGE) {
			compositeState(range, selection == RANGE);
		} 
		
		if ((modes & EQUALS) == EQUALS) {
			compositeState(equals, selection == EQUALS);
		} 

		if ((modes & CONFLICTS) == CONFLICTS) {
			compositeState(conflicts, selection == CONFLICTS);
		} 
		// re-layout and make sure all widgets are visible
		versions.layout(false);
	}

	protected void compositeState(Composite composite, boolean state) {
		composite.setVisible(state);
		final GridData ld = (GridData) composite.getLayoutData();
		ld.exclude = !state;
	}

	public void setInput(IModelObject input) {
		this.input = input;

		if ((modes & EQUALS) == EQUALS) {
			equalsField.setInput(input);
		}

		if ((modes & CONFLICTS) == CONFLICTS) {
			conflictsField.setInput(input);
		}

		if ((modes & RANGE) == RANGE) {
			minField.setInput(input);
			maxField.setInput(input);
		}

		if ((modes & EXCLUDE) == EXCLUDE) {
			excludeField.setInput(input);
		}
	}

	public void createContents(Composite client, FormToolkit toolkit) {
		client.setLayout(new GridLayout(1, false));
		client.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Composite options = new Composite(client, SWT.NULL);
		final GridLayout layout = new GridLayout(numOfModes(modes), false);
		options.setLayout(layout);
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		options.setLayoutData(gd);

		// radio buttons
		addRadioButtons(options);

		versions = toolkit.createComposite(client);
		versions.setLayout(new GridLayout(1, false));
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		versions.setLayoutData(gd);
		addVersionFields(toolkit, versions);
	}

	protected void addVersionFields(FormToolkit toolkit, Composite versions) {

		range = null;
		if ((modes & RANGE) == RANGE) {
			range = toolkit.createComposite(versions);
			range.setLayout(new GridLayout(3, false));
			GridData data = new GridData(SWT.FILL, SWT.TOP, true, true);
			range.setLayoutData(data);
			
			minField = new TextField(null,
					DeploymentDescriptorPackage.DEPENDENCY_MIN,
					Messages.VersionControl_Minimum);
			maxField = new TextField(null,
					DeploymentDescriptorPackage.DEPENDENCY_MAX,
					Messages.VersionControl_Maximum);
			minField.create(range, toolkit);
			maxField.create(range, toolkit);
		}

		if ((modes & EXCLUDE) == EXCLUDE) {
			excludeField = new ListField(null,
					DeploymentDescriptorPackage.DEPENDENCY_EXCLUDE,
					Messages.VersionControl_Exclude);
			excludeField.create(range, toolkit);
		}

		if ((modes & CONFLICTS) == CONFLICTS) {
			conflicts = toolkit.createComposite(versions);
			conflicts.setLayout(new GridLayout(3, false));
			GridData data = new GridData(SWT.FILL, SWT.TOP, true, true);
			conflicts.setLayoutData(data);

			conflictsField = new TextField(null,
					DeploymentDescriptorPackage.DEPENDENCY_CONFLICTS,
					Messages.VersionControl_0);
			conflictsField.create(conflicts, toolkit);
		}

		if ((modes & EQUALS) == EQUALS) {
			equals = toolkit.createComposite(versions);
			equals.setLayout(new GridLayout(3, false));
			GridData data = new GridData(SWT.FILL, SWT.TOP, true, true);
			equals.setLayoutData(data);
			equalsField = new TextField(null,
					DeploymentDescriptorPackage.DEPENDENCY_EQUALS,
					Messages.VersionControl_0);
			equalsField.create(equals, toolkit);
		}
	}

	protected void addRadioButtons(Composite options) {
		if ((modes & EQUALS) == EQUALS) {
			btnEquals = new Button(options, SWT.RADIO);
			btnEquals.setText(Messages.VersionControl_2);
			btnEquals.setSelection(true);
			btnEquals.addSelectionListener(new SelectionChanged());
			btnEquals.setSelection(true);
		}

		if ((modes & CONFLICTS) == CONFLICTS) {
			btnConflict = new Button(options, SWT.RADIO);
			btnConflict.setText(Messages.VersionControl_3);
			btnConflict.addSelectionListener(new SelectionChanged());
		}

		if ((modes & RANGE) == RANGE) {
			btnMatches = new Button(options, SWT.RADIO);
			btnMatches.setText(Messages.VersionControl_4);
			btnMatches.addSelectionListener(new SelectionChanged());
		}
	}

	private int getSelected() {
		if (btnEquals != null && btnEquals.getSelection()) {
			return EQUALS;
		}
		if (btnConflict != null && btnConflict.getSelection()) {
			return CONFLICTS;
		} else {
			return RANGE;
		}

	}

	class SelectionChanged implements SelectionListener {
		public void widgetSelected(SelectionEvent e) {
			 updateFieldsVisibility();
		}

		public void widgetDefaultSelected(SelectionEvent e) {
			 updateFieldsVisibility();
		}
	}

	private int numOfModes(int modes2) {
		int r = 0;
		if ((modes & EQUALS) == EQUALS) {
			r++;
		}
		if ((modes & CONFLICTS) == CONFLICTS) {
			r++;
		}
		if ((modes & RANGE) == RANGE) {
			r++;
		}
		return r;
	}

	public void setEditor(DeploymentDescriptorEditor editor) {
		// TODO: can be removed?
	}
}
