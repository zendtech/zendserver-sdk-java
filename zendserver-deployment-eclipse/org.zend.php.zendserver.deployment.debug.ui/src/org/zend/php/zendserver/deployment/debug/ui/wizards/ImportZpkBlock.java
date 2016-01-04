/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.debug.ui.wizards;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.zend.php.zendserver.deployment.core.descriptor.ProjectType;
import org.zend.php.zendserver.deployment.core.utils.LibraryUtils;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.Messages;
import org.zend.php.zendserver.deployment.debug.ui.listeners.IStatusChangeListener;

/**
 * @author Wojciech Galanciak, 2013
 * 
 */
public class ImportZpkBlock extends AbstractLibraryBlock {

	private static final String ZPK_EXTENSION = "zpk"; //$NON-NLS-1$

	private Text libraryNameText;
	private Text libraryVersionText;
	private Text zpkPathText;

	public ImportZpkBlock(IStatusChangeListener listener) {
		super(listener, null);
	}

	public Composite createContents(final Composite parent, boolean resizeShell) {
		Composite container = super.createContents(parent, resizeShell);
		GridLayout layout = new GridLayout(3, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		container.setLayout(layout);
		Label label = new Label(container, SWT.NONE);
		label.setText(Messages.ImportZpkBlock_ZpkFIleLabel);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1));
		new Label(container, SWT.NONE);
		zpkPathText = new Text(container, SWT.BORDER | SWT.SINGLE);
		zpkPathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		zpkPathText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				IStatus status = validatePage();
				listener.statusChanged(status);
				updateLibraryProperties(status);
			}
		});
		Button zpkSelectionButton = new Button(container, SWT.PUSH);
		zpkSelectionButton.setText(Messages.ImportZpkBlock_BrowseLabel);
		zpkSelectionButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		zpkSelectionButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(e.display.getActiveShell(), SWT.SINGLE);
				dialog.setText(Messages.ImportZpkBlock_DialogTitle);
				dialog.setFilterExtensions(new String[] { "*." + ZPK_EXTENSION }); //$NON-NLS-1$
				final String res = dialog.open();
				if (res == null) {
					return;
				}
				zpkPathText.setText(res);
			}
		});
		Label lblName = new Label(container, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		lblName.setText(Messages.LibraryConfigurationBlock_Name);
		libraryNameText = new Text(container, SWT.BORDER);
		libraryNameText.setEnabled(false);
		libraryNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		new Label(container, SWT.NONE);
		Label lblVersion = new Label(container, SWT.NONE);
		lblVersion.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		lblVersion.setText(Messages.LibraryConfigurationBlock_Version);
		libraryVersionText = new Text(container, SWT.BORDER);
		libraryVersionText.setEnabled(false);
		libraryVersionText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		new Label(container, SWT.NONE);
		return container;
	}

	protected void updateLibraryProperties(IStatus validationStatus) {
		libraryNameText.setText(""); //$NON-NLS-1$
		libraryVersionText.setText(""); //$NON-NLS-1$

		if (validationStatus.getSeverity() != IStatus.OK) {
			return;
		}

		try {
			File location = new File(zpkPathText.getText());
			Document doc = LibraryUtils.getDeploymentDescriptor(location);
			String name = LibraryUtils.getLibraryName(doc);
			String version = LibraryUtils.getLibraryVersion(doc);
			libraryNameText.setText(name);
			libraryVersionText.setText(version);
		} catch (IOException | ParserConfigurationException | SAXException e) {
			// should not occur; if it does it means the validation does not
			// work well; log it
			Activator.log(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.php.library.internal.ui.wizards.AbstractBlock#validatePage()
	 */
	public IStatus validatePage() {
		IPath path = new Path(zpkPathText.getText());
		if (path.isEmpty()) {
			return new Status(IStatus.INFO, Activator.PLUGIN_ID, Messages.ImportZpkBlock_InvalidZpkPathError);
		}

		File file = path.toFile();
		if (!file.exists()) {
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.DeployTargetBlock_ZpkDoesNotExistError);
		}

		if (!file.isFile()) {
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.ImportZpkBlock_PathIsNotFileError);
		}

		if (!path.getFileExtension().equalsIgnoreCase(ZPK_EXTENSION)) {
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.DeployTargetBlock_InvalidZpkError);
		}

		try {
			Document descriptorDocument = LibraryUtils.getDeploymentDescriptor(file);

			if (LibraryUtils.getProjectType(descriptorDocument) != ProjectType.LIBRARY) {
				return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.ImportZpkBlock_NotLibaryError);
			}

			String name = LibraryUtils.getLibraryName(descriptorDocument);
			if (name == null || name.isEmpty()) {
				return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.ImportZpkBlock_CannotParseNameError);
			}

			String version = LibraryUtils.getLibraryVersion(descriptorDocument);
			if (version == null || version.isEmpty()) {
				return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.ImportZpkBlock_CannotParseVersionError);
			}

		} catch (IOException | ParserConfigurationException | SAXException e) {
			String message = MessageFormat.format(Messages.DeployTargetBlock_CouldNotReadZpk_Error,
					e.getLocalizedMessage());
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, message);
		}

		return new Status(IStatus.OK, Activator.PLUGIN_ID, Messages.ImportZpkBlock_Description);
	}

	public ImportZpkData getData() {
		ImportZpkData data = new ImportZpkData();
		data.setName(libraryNameText.getText());
		data.setVersion(libraryVersionText.getText());
		data.setPath(zpkPathText.getText());
		return data;
	}
}
