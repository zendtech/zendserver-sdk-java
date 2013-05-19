/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies.
 * All rights reserved. This program and the accompanying materials
 * are the copyright of Zend Technologies and is protected under
 * copyright laws of the United States.
 * You must not copy, adapt or redistribute this document for 
 * any use.
 *******************************************************************************/
package org.zend.php.library.internal.ui.preferences;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.php.internal.core.PHPLanguageToolkit;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.zend.php.library.core.LibraryManager;
import org.zend.php.library.core.LibraryVersion;
import org.zend.php.library.core.LibraryVersion.Suffix;
import org.zend.php.library.core.LibraryVersionRange;
import org.zend.php.library.core.composer.ComposerService;
import org.zend.php.library.internal.core.PackageVersion;
import org.zend.php.library.internal.core.RepositoryPackage;

/**
 * @author Wojciech Galanciak, 2013
 *
 */
public class LibraryDetailsPage extends WizardPage {

	private Label descriptionText;
	private Label nameValLabel;
	private Link homePageLink;
	private Combo versionCombo;
	private Table table;

	Map<String, LibraryVersion> availableLibraries;
	private RepositoryPackage repositoryPackage;
	private List<String> requires;
	private List<String> resolvedRequires;
	private String version;

	public String getVersion() {
		return version;
	}

	public List<String> getRequires() {
		return requires;
	}

	/**
	 * Create the wizard.
	 * 
	 * @param libraries
	 * 
	 * @param searchPage
	 */
	public LibraryDetailsPage(Map<String, LibraryVersion> libraries) {
		super("wizardPage");
		setTitle("Library Details");
		setDescription("Choose version and resolve dependences of selected library.");
		this.requires = new ArrayList<String>();
		this.resolvedRequires = new ArrayList<String>();
		this.availableLibraries = libraries;
	}

	/**
	 * Create contents of the wizard.
	 * 
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(2, false));

		Label lblName = new Label(container, SWT.NONE);
		lblName.setText("Name:");

		nameValLabel = new Label(container, SWT.NONE);
		nameValLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		Label lblDescription = new Label(container, SWT.NONE);
		lblDescription.setText("Description:");

		descriptionText = new Label(container, SWT.WRAP);
		descriptionText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		Label lblHomepage = new Label(container, SWT.NONE);
		lblHomepage.setText("Homepage:");

		homePageLink = new Link(container, SWT.NONE);
		homePageLink.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true,
				false, 1, 1));
		homePageLink.setText("<a></a>");

		Label lblVersions = new Label(container, SWT.NONE);
		lblVersions.setText("Version(s):");

		versionCombo = new Combo(container, SWT.READ_ONLY);
		versionCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		versionCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				version = versionCombo.getText();
				refreshRequires();
				setPageComplete(validatePage());
			}
		});

		Label lblRequires = new Label(container, SWT.NONE);
		lblRequires.setText("Requires:");

		Composite requiresComposite = new Composite(container, SWT.NONE);
		requiresComposite.setLayout(new GridLayout(2, false));
		requiresComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 1, 1));

		Label requiresDesc = new Label(requiresComposite, SWT.NONE);
		requiresDesc.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,
				2, 1));
		requiresDesc.setText("Import library dependences selected below:");

		table = new Table(requiresComposite, SWT.BORDER | SWT.CHECK
				| SWT.FULL_SELECTION);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				TableItem selectedItem = (TableItem) e.item;
				if (selectedItem.getChecked()) {
					String name = selectedItem.getText(1);
					if (resolvedRequires.contains(name)) {
						selectedItem.setChecked(false);
					} else if (!"php".equalsIgnoreCase(name)) {
						requires.add(name);
					}
				} else {
					requires.remove(selectedItem.getText(1));
				}
				setPageComplete(validatePage());
			}
		});

		TableColumn checkColumn = new TableColumn(table, SWT.NONE);
		checkColumn.setText("");

		TableColumn tblclmnName = new TableColumn(table, SWT.NONE);
		tblclmnName.setWidth(100);
		tblclmnName.setText("Name");

		TableColumn tblclmnVersion = new TableColumn(table, SWT.NONE);
		tblclmnVersion.setWidth(100);
		tblclmnVersion.setText("Version");

		TableColumn tblclmnStatus = new TableColumn(table, SWT.NONE);
		tblclmnStatus.setWidth(100);
		tblclmnStatus.setText("Status");

		Composite tableButtonsComposite = new Composite(requiresComposite,
				SWT.NONE);
		tableButtonsComposite.setLayout(new FillLayout(SWT.VERTICAL));
		tableButtonsComposite.setLayoutData(new GridData(SWT.CENTER, SWT.TOP,
				false, false, 1, 1));

		Button selectAllButton = new Button(tableButtonsComposite, SWT.NONE);
		selectAllButton.setText("Select All");
		selectAllButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TableItem[] items = table.getItems();
				for (TableItem tableItem : items) {
					String name = tableItem.getText(1);
					if (resolvedRequires.contains(name)) {
						continue;
					}
					tableItem.setChecked(true);
					if (!"php".equalsIgnoreCase(name)) {
						requires.add(name);
					}
				}
				setPageComplete(validatePage());
			}
		});

		Button deselectAllButton = new Button(tableButtonsComposite, SWT.NONE);
		deselectAllButton.setText("Deselect All");
		deselectAllButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TableItem[] items = table.getItems();
				for (TableItem tableItem : items) {
					String name = tableItem.getText(1);
					if (resolvedRequires.contains(name)) {
						continue;
					}
					tableItem.setChecked(false);
					requires.remove(name);
				}
				setPageComplete(validatePage());
			}
		});
		setPageComplete(validatePage());
	}

	private boolean validatePage() {
		if (repositoryPackage == null) {
			return false;
		}
		String libName = LibraryManager.createLibraryName(repositoryPackage
				.getName());
		String libVersion = DLTKCore.getUserLibraryVersion(libName,
				PHPLanguageToolkit.getDefault());
		LibraryVersion existingVersion = LibraryVersion.byName(libVersion);
		List<PackageVersion> versions = repositoryPackage.getVersions();
		for (PackageVersion version : versions) {
			if (version.getVersion().equals(getVersion())) {
				if (LibraryVersion.byName(version.getVersionNormalized())
						.compareTo(existingVersion) == 0) {
					setErrorMessage(MessageFormat.format(
							"{0} version of {1} library already exists.",
							getVersion(), repositoryPackage.getName()));
					return false;
				}
				break;
			}
		}
		setErrorMessage(null);
		return true;
	}

	private void refreshRequires() {
		resolvedRequires.clear();
		int index = versionCombo.getSelectionIndex();
		PackageVersion version = repositoryPackage.getVersions().get(index);
		table.removeAll();
		Map<String, String> requires = version.getRequires();
		Set<String> keys = requires.keySet();
		for (String key : keys) {
			if (!"php".equalsIgnoreCase(key)) {
				TableItem item = new TableItem(table, SWT.NONE);
				item.setText(1, key);
				item.setText(2, requires.get(key));
				String[] libraryNames = DLTKCore
						.getUserLibraryNames(PHPLanguageToolkit.getDefault());
				for (String name : libraryNames) {
					String libName = ComposerService.parseName(name);
					if (libName != null) {
						libName = libName.replaceAll("-", "/");
						if (key.equals(libName)) {
							LibraryVersion libVersion = ComposerService
									.parseVersion(name);
							LibraryVersionRange range = LibraryVersionRange
									.getRange(requires.get(key));
							if (range.isInRange(libVersion)) {
								resolvedRequires.add(libName);
								item.setText(3, "available");
								item.setForeground(Display.getDefault()
										.getSystemColor(SWT.COLOR_GRAY));
							}
						}
					}
				}
			}
		}
	}

	public void initializeFields(RepositoryPackage pkg) {
		this.repositoryPackage = pkg;
		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				if (repositoryPackage == null) {
					nameValLabel.setText("");
					descriptionText.setText("");
					homePageLink.setText("");
					versionCombo.removeAll();
					table.removeAll();
				} else {
					nameValLabel.setText(repositoryPackage.getName());
					descriptionText.setText(repositoryPackage.getDescription());
					if (repositoryPackage.getUrl() != null) {
						homePageLink.setText("<a>" + repositoryPackage.getUrl()
								+ "</a>");
					}
					List<PackageVersion> versions = repositoryPackage
							.getVersions();
					LibraryVersion[] libraryVersions = new LibraryVersion[versions
							.size()];
					for (int i = 0; i < libraryVersions.length; i++) {
						libraryVersions[i] = LibraryVersion.byName(versions
								.get(i).getVersion());
					}
					Arrays.sort(libraryVersions);
					for (LibraryVersion v : libraryVersions) {
						versionCombo.add(v.toString());
					}
					if (libraryVersions.length > 0) {
						for (int i = libraryVersions.length - 1; i >= 0; i--) {
							if (libraryVersions[i].getSuffix() == Suffix.NONE) {
								versionCombo.select(i);
								version = versionCombo.getText();
								break;
							}
						}
					}
					refreshRequires();
				}
				setPageComplete(validatePage());
			}
		});
	}

}
