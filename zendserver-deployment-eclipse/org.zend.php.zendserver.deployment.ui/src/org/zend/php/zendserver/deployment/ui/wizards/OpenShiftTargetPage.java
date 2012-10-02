/*******************************************************************************
 * Copyright (c) 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.ui.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.zend.sdklib.internal.target.OpenShiftTarget;

/**
 * Target attributes page.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class OpenShiftTargetPage extends WizardPage {

	private Text targetNameText;
	// private TableViewer platformsViewer;
	private Combo gearProfileCombo;
	private OpenShiftTarget target;
	private Button mySqlButton;
	
	private OpenShiftTargetData data;
	

	protected OpenShiftTargetPage(OpenShiftTargetWizard wizard, OpenShiftTargetData data) {
		super(Messages.OpenShiftTargetPage_PageTitle);
		setDescription(Messages.OpenShiftTargetPage_EnterNameMessage);
		setTitle(Messages.OpenShiftTargetPage_PageDescription);
		this.target = new OpenShiftTarget(wizard.getUsername(),
				wizard.getPassword());
		this.data = data;
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 0;
		container.setLayout(layout);
		targetNameText = createLabelWithText(
				Messages.OpenShiftTargetPage_TargetNameLabel, container);
		gearProfileCombo = createLabelWithCombo(
				Messages.OpenShiftTargetPage_GearProfileLabel, container);
		mySqlButton = createMySqlSection(container);
		// platformsViewer = createPlatformsSection(container);
		setControl(container);
		initializeValues();
		setPageComplete(validatePage());
	}

	public void initializeValues() {
		if (data.getGearProfiles().size() > 0) {
			Display.getDefault().asyncExec(new Runnable() {

				public void run() {
					for (String profile : data.getGearProfiles()) {
						gearProfileCombo.add(profile);
					}
					gearProfileCombo.select(0);
				}
			});
		}
	}
	
	public void updateData(){
		if (targetNameText != null) {
			data.setName(targetNameText.getText());
		}
		if (gearProfileCombo != null) {
			data.setGearProfile(gearProfileCombo.getText());
		}
		if (mySqlButton != null) {
			data.setMySQLSupport(mySqlButton.getSelection());
		}
		data.setTarget(target);
	}

	private boolean validatePage() {
		setErrorMessage(null);
		if (targetNameText == null || targetNameText.getText().isEmpty()) {
			setMessage(Messages.OpenShiftTargetPage_EnterNameMessage);
			return false;
		}
		String name = targetNameText.getText();
		for (String existingTarget : data.getZendTargets()) {
			if (existingTarget.equalsIgnoreCase(name)) {
				setErrorMessage(Messages.OpenShiftTargetPage_SameTargetErrorMessage);
				return false;
			}
		}
		setErrorMessage(null);
		setMessage(Messages.OpenShiftTargetPage_PageDescription);
		return true;
	}

	private Text createLabelWithText(String labelText, Composite container) {
		Composite parent = new Composite(container, SWT.NONE);
		parent.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		parent.setLayout(layout);
		Label label = new Label(parent, SWT.NONE);
		label.setText(labelText);
		GridData gd = new GridData(SWT.LEFT, SWT.CENTER, false, true);
		gd.widthHint = 100;
		label.setLayoutData(gd);
		Text text = new Text(parent, SWT.BORDER | SWT.SINGLE);
		text.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				setPageComplete(validatePage());
			}
		});
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return text;
	}

	private Button createMySqlSection(Composite container) {
		Label label = new Label(container, SWT.NONE);
		GridData gd = new GridData(SWT.FILL, SWT.LEFT, false, false);
		gd.widthHint = 100;
		label.setLayoutData(gd);
		Button mySqlButton = new Button(container, SWT.CHECK);
		mySqlButton.setText(Messages.OpenShiftTargetPage_AddMySQLLabel);
		mySqlButton
				.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		mySqlButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				setPageComplete(validatePage());
			}
		});
		return mySqlButton;
	}

	private Combo createLabelWithCombo(String labelText, Composite container) {
		Composite parent = new Composite(container, SWT.NONE);
		parent.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		parent.setLayout(layout);
		Label label = new Label(parent, SWT.NONE);
		label.setText(labelText);
		GridData gd = new GridData(SWT.LEFT, SWT.CENTER, false, true);
		gd.widthHint = 100;
		label.setLayoutData(gd);
		Combo combo = new Combo(parent, SWT.BORDER | SWT.SINGLE);
		combo.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				setPageComplete(validatePage());
			}
		});
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return combo;
	}

	/*
	 * private TableViewer createPlatformsSection(Composite container) {
	 * Composite parent = new Composite(container, SWT.NONE);
	 * parent.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
	 * GridLayout layout = new GridLayout(2, false); layout.marginWidth = 0;
	 * layout.marginHeight = 0; parent.setLayout(layout); Label label = new
	 * Label(parent, SWT.NONE); label.setText("Cartridges:"); GridData gd = new
	 * GridData(SWT.LEFT, SWT.CENTER, false, true); gd.widthHint = 100;
	 * label.setLayoutData(gd); CheckboxTableViewer viewer =
	 * CheckboxTableViewer.newCheckList(parent, SWT.CHECK | SWT.BORDER |
	 * SWT.V_SCROLL | SWT.SINGLE); viewer.setContentProvider(new
	 * IStructuredContentProvider() {
	 * 
	 * public void dispose() { }
	 * 
	 * public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	 * { }
	 * 
	 * public Object[] getElements(Object inputElement) { try { return
	 * target.getCartridges(); } catch (SdkException e) { Activator.log(e); }
	 * return new Object[0]; } }); viewer.setLabelProvider(new
	 * ColumnLabelProvider() {
	 * 
	 * public String getText(Object element) { return
	 * target.getCartridgeLabel(element); } }); final Table table =
	 * viewer.getTable(); table.setHeaderVisible(false);
	 * table.setLinesVisible(true); table.setLayoutData(new
	 * GridData(GridData.FILL_HORIZONTAL));
	 * viewer.addSelectionChangedListener(new ISelectionChangedListener() {
	 * 
	 * public void selectionChanged(SelectionChangedEvent event) {
	 * 
	 * } }); return viewer; }
	 */

}
