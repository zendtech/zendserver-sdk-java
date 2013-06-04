/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies.
 * All rights reserved. This program and the accompanying materials
 * are the copyright of Zend Technologies and is protected under
 * copyright laws of the United States.
 * You must not copy, adapt or redistribute this document for 
 * any use.
 *******************************************************************************/
package org.zend.php.library.internal.ui.preferences;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

/**
 * @author Wojciech Galanciak, 2013
 * 
 */
public class SearchLibraryDialog extends TitleAreaDialog {

	private Text searchText;
	private Table table;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public SearchLibraryDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle(Messages.SearchLibraryDialog_0);
		setMessage(Messages.SearchLibraryDialog_1);
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		searchText = new Text(container, SWT.BORDER);
		searchText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		Button searchButton = new Button(container, SWT.NONE);
		searchButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		searchButton.setText(Messages.SearchLibraryDialog_2);

		table = new Table(container, SWT.BORDER | SWT.FULL_SELECTION);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableColumn tblclmnName = new TableColumn(table, SWT.NONE);
		tblclmnName.setWidth(100);
		tblclmnName.setText(Messages.SearchLibraryDialog_3);

		TableColumn versionColumn = new TableColumn(table, SWT.NONE);
		versionColumn.setWidth(100);
		versionColumn.setText(Messages.SearchLibraryDialog_4);

		TableColumn descriptionColumn = new TableColumn(table, SWT.LEFT);
		descriptionColumn.setWidth(100);
		descriptionColumn.setText(Messages.SearchLibraryDialog_5);

		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.VERTICAL));
		composite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false,
				1, 1));

		Button detailsButton = new Button(composite, SWT.NONE);
		detailsButton.setText(Messages.SearchLibraryDialog_6);

		Button newButton = new Button(composite, SWT.NONE);
		newButton.setText(Messages.SearchLibraryDialog_7);

		return area;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

}
