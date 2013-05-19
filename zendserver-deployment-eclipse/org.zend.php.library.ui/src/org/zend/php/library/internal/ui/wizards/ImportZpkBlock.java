/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.library.internal.ui.wizards;

import java.io.File;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
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
import org.zend.php.library.core.LibraryUtils;
import org.zend.php.library.internal.ui.LibraryUI;
import org.zend.php.library.internal.ui.Messages;
import org.zend.php.zendserver.deployment.core.descriptor.ProjectType;
import org.zend.php.zendserver.deployment.debug.ui.listeners.IStatusChangeListener;

/**
 * @author Wojciech Galanciak, 2013
 * 
 */
public class ImportZpkBlock extends AbstractBlock {

	private String description;

	private Label libraryName;
	private Label libraryVersion;

	private Text zpkPathText;

	public ImportZpkBlock(IStatusChangeListener listener) {
		super(listener);
		this.description = Messages.ImportZpkBlock_Description;
	}

	public Composite createContents(final Composite parent, boolean resizeShell) {
		Composite container = super.createContents(parent, resizeShell);
		createPathSection(container);
		libraryName = createLabelWithLabel(
				Messages.LibraryConfigurationBlock_Name, null, container);
		libraryVersion = createLabelWithLabel(
				Messages.LibraryConfigurationBlock_Version, null, container);
		return container;
	}

	private void createPathSection(Composite container) {
		Composite section = new Composite(container, SWT.NULL);
		GridLayout layout = new GridLayout(3, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		section.setLayout(layout);
		section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2,
				1));
		zpkPathText = createLabelWithText(Messages.ImportZpkBlock_ZpkFIleLabel,
				null, section, false, 0);
		zpkPathText.setEditable(false);
		Button zpkSelectionButton = new Button(section, SWT.PUSH);
		zpkSelectionButton.setText(Messages.ImportZpkBlock_BrowseLabel);
		zpkSelectionButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
				false, false));
		zpkSelectionButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(e.display.getActiveShell(),
						SWT.SINGLE);
				dialog.setText(Messages.ImportZpkBlock_DialogTitle);
				dialog.setFilterExtensions(new String[] { "*.zpk" }); //$NON-NLS-1$
				final String res = dialog.open();
				if (res == null) {
					return;
				}
				zpkPathText.setText(res);
				File deploymentFile = LibraryUtils.unzipDescriptor(new File(
						res));
				Document doc = LibraryUtils
						.getDeploymentDescriptor(deploymentFile);
				if (LibraryUtils.getProjectType(doc) == ProjectType.LIBRARY) {
					String name = LibraryUtils.getLibraryName(doc);
					String version = LibraryUtils.getLibraryVersion(doc);
					libraryName.setText(name);
					libraryVersion.setText(version);
					listener.statusChanged(validatePage());
				} else {
					listener.statusChanged(new Status(IStatus.ERROR,
							LibraryUI.PLUGIN_ID,
							Messages.ImportZpkBlock_NotLibaryError));
				}
				deploymentFile.deleteOnExit();
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.php.library.internal.ui.wizards.AbstractBlock#validatePage()
	 */
	public IStatus validatePage() {
		if (!zpkPathText.getText().isEmpty()) {
			if (libraryName.getText().isEmpty()) {
				return new Status(IStatus.ERROR, LibraryUI.PLUGIN_ID,
						Messages.ImportZpkBlock_CannotParseNameError);
			}
			if (libraryVersion.getText().isEmpty()) {
				return new Status(IStatus.ERROR, LibraryUI.PLUGIN_ID,
						Messages.ImportZpkBlock_CannotParseVersionError);
			}
		}
		return new Status(IStatus.OK, LibraryUI.PLUGIN_ID, description);
	}

	public ImportZpkData getData() {
		ImportZpkData data = new ImportZpkData();
		data.setName(libraryName.getText());
		data.setVersion(libraryVersion.getText());
		data.setPath(zpkPathText.getText());
		return data;
	}
}
