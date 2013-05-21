/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.library.internal.ui.wizards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IBuildpathContainer;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.core.UserLibraryManager;
import org.eclipse.dltk.internal.ui.wizards.buildpath.BPListElement;
import org.eclipse.dltk.internal.ui.wizards.buildpath.BPListLabelProvider;
import org.eclipse.dltk.internal.ui.wizards.buildpath.BPUserLibraryElement;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.CheckedListDialogField;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.IListAdapter;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.ListDialogField;
import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.dltk.ui.dialogs.StatusInfo;
import org.eclipse.dltk.ui.preferences.UserLibraryPreferencePage;
import org.eclipse.dltk.ui.wizards.NewElementWizardPage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;
import org.eclipse.php.internal.core.PHPLanguageToolkit;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.zend.php.library.internal.ui.LibraryUI;
import org.zend.php.library.internal.ui.Messages;
import org.zend.php.zendserver.deployment.core.descriptor.DescriptorContainerManager;

import com.ibm.icu.text.Collator;

/**
 * @author Wojciech Galanciak, 2013
 * 
 */
public class UserLibraryWizardPage extends NewElementWizardPage {

	private class LibraryListAdapter implements IListAdapter,
			IDialogFieldListener {

		public LibraryListAdapter() {
		}

		public void dialogFieldChanged(DialogField field) {
			doDialogFieldChanged(field);
		}

		public void customButtonPressed(ListDialogField field, int index) {
			doButtonPressed(index);
		}

		public void selectionChanged(ListDialogField field) {
		}

		public void doubleClicked(ListDialogField field) {
			doDoubleClicked(field);
		}
	}

	private CheckedListDialogField fLibrarySelector;
	private BPUserLibraryElement fEditResult;
	private Set<IPath> fUsedPaths;
	private boolean fIsEditMode;
	private IScriptProject fProject;
	private boolean fIsExported;
	private IDLTKLanguageToolkit languageToolkit;
	private Text pathText;
	private Button pathButton;
	private Button addSourceButton;
	private Button addDependecyButton;

	public UserLibraryWizardPage() {
		super("UserLibraryWizardPage"); //$NON-NLS-1$
		this.languageToolkit = PHPLanguageToolkit.getDefault();
		setTitle(Messages.UserLibraryWizardPage_title);
		setImageDescriptor(DLTKPluginImages.DESC_WIZBAN_ADD_LIBRARY);
		updateDescription(null);
		fUsedPaths = new HashSet<IPath>();
		fProject = createPlaceholderProject();

		LibraryListAdapter adapter = new LibraryListAdapter();
		String[] buttonLabels = new String[] { Messages.UserLibraryWizardPage_Manage };
		fLibrarySelector = new CheckedListDialogField(adapter, buttonLabels,
				new BPListLabelProvider());
		fLibrarySelector.setDialogFieldListener(adapter);
		fLibrarySelector
				.setLabelText(Messages.UserLibraryWizardPage_ListDescription);
		fEditResult = null;
		updateStatus(validateSettings(Collections.EMPTY_LIST));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());
		LayoutUtil.doDefaultLayout(composite,
				new DialogField[] { fLibrarySelector }, true, SWT.DEFAULT,
				SWT.DEFAULT);
		LayoutUtil.setHorizontalGrabbing(fLibrarySelector.getListControl(null));
		Dialog.applyDialogFont(composite);
		Composite bottomSection = new Composite(composite, SWT.NONE);
		bottomSection.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true,
				false, 2, 1));
		GridLayout layout = new GridLayout(3, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		bottomSection.setLayout(layout);
		if (fProject.getProject().findMember(
				DescriptorContainerManager.DESCRIPTOR_PATH) != null) {
			addDependecyButton = new Button(bottomSection, SWT.CHECK);
			addDependecyButton
					.setText(Messages.UserLibraryWizardPage_AddDependency);
			addDependecyButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
					true, false, 3, 1));
		}
		addSourceButton = new Button(bottomSection, SWT.CHECK);
		addSourceButton.setText(Messages.UserLibraryWizardPage_AddToSource);
		addSourceButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 3, 1));
		addSourceButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				boolean enabled = addSourceButton.getSelection();
				pathButton.setEnabled(enabled);
				pathText.setEnabled(enabled);
			}
		});
		Label pathLabel = new Label(bottomSection, SWT.NONE);
		pathLabel.setText(Messages.UserLibraryWizardPage_SourceFolder);
		pathText = new Text(bottomSection, SWT.BORDER);
		pathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		pathText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				String path = pathText.getText();
				if (fProject.getProject().findMember(path) == null) {
					updateStatus(new StatusInfo(IStatus.ERROR,
							Messages.UserLibraryWizardPage_SourceFolderNotExist));
				} else {
					updateStatus(new StatusInfo());
				}
			}
		});
		pathText.setEnabled(false);
		pathButton = new Button(bottomSection, SWT.PUSH);
		pathButton.setText(Messages.UserLibraryWizardPage_Browse);
		pathButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false));
		pathButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String path = selectFolder(e.display.getActiveShell());
				if (path != null) {
					pathText.setText(path);
				}
			}
		});
		pathButton.setEnabled(false);
		setControl(composite);
	}

	protected void initialize(IScriptProject project,
			IBuildpathEntry[] currentEntries) {
		fProject = project;
		for (int i = 0; i < currentEntries.length; i++) {
			IBuildpathEntry curr = currentEntries[i];
			if (curr.getEntryKind() == IBuildpathEntry.BPE_CONTAINER) {
				fUsedPaths.add(curr.getPath());
			}
		}
		List<BPUserLibraryElement> newEntries = updateLibraryList();
		if (newEntries.size() > 0) {
			if (fIsEditMode) {
				fLibrarySelector.setChecked(newEntries.get(0), true);
			} else {
				fLibrarySelector.setCheckedElements(newEntries);
			}
		}
	}

	protected IBuildpathEntry getSelection() {
		if (fEditResult != null) {
			return DLTKCore.newContainerEntry(fEditResult.getPath(),
					fIsExported);
		}
		return null;
	}

	protected IBuildpathEntry[] getNewContainers() {
		List<?> selected = fLibrarySelector.getCheckedElements();
		IBuildpathEntry[] res = new IBuildpathEntry[selected.size()];
		for (int i = 0; i < res.length; i++) {
			BPUserLibraryElement curr = (BPUserLibraryElement) selected.get(i);
			res[i] = DLTKCore.newContainerEntry(curr.getPath(), fIsExported);
		}
		return res;
	}

	protected boolean getAddDependency() {
		if (addDependecyButton != null) {
			return addDependecyButton.getSelection();
		}
		return false;
	}

	protected IPath getAddSource() {
		if (addSourceButton.getSelection()) {
			return new Path(pathText.getText());
		}
		return null;
	}

	protected void setSelection(IBuildpathEntry containerEntry) {
		fIsExported = containerEntry != null && containerEntry.isExported();

		updateDescription(containerEntry);
		fIsEditMode = (containerEntry != null);
		if (containerEntry != null) {
			fUsedPaths.remove(containerEntry.getPath());
		}

		String selected = null;
		if (containerEntry != null
				&& containerEntry.getPath().segmentCount() > 1) {
			selected = containerEntry.getPath().segment(1);
			int pos = selected.indexOf("#"); //$NON-NLS-1$
			if (pos != -1) {
				selected = selected.substring(pos + 1);
			}
		} else {
			// get from dialog store
		}
		updateLibraryList();
		if (selected != null) {
			List<?> elements = fLibrarySelector.getElements();
			for (int i = 0; i < elements.size(); i++) {
				BPUserLibraryElement curr = (BPUserLibraryElement) elements
						.get(i);
				if (curr.getName().equals(selected)) {
					fLibrarySelector.setChecked(curr, true);
					return;
				}
			}
		}
	}

	private static IScriptProject createPlaceholderProject() {
		String name = "####internal"; //$NON-NLS-1$
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		while (true) {
			IProject project = root.getProject(name);
			if (!project.exists()) {
				return DLTKCore.create(project);
			}
			name += '1';
		}
	}

	private void updateDescription(IBuildpathEntry containerEntry) {
		if (containerEntry == null
				|| containerEntry.getPath().segmentCount() != 2) {
			setDescription(Messages.UserLibraryWizardPage_NewDescription);
		} else {
			setDescription(Messages.UserLibraryWizardPage_EditDescription);
		}
	}

	private List<BPUserLibraryElement> updateLibraryList() {
		HashSet<String> oldNames = new HashSet<String>();
		HashSet<String> oldCheckedNames = new HashSet<String>();
		List<?> oldElements = fLibrarySelector.getElements();
		for (int i = 0; i < oldElements.size(); i++) {
			BPUserLibraryElement curr = (BPUserLibraryElement) oldElements
					.get(i);
			oldNames.add(curr.getName());
			if (fLibrarySelector.isChecked(curr)) {
				oldCheckedNames.add(curr.getName());
			}
		}

		ArrayList<BPUserLibraryElement> entriesToCheck = new ArrayList<BPUserLibraryElement>();

		IDLTKLanguageToolkit toolkit = null;
		toolkit = DLTKLanguageManager.getLanguageToolkit(fProject);
		if (toolkit == null) {
			toolkit = this.languageToolkit;
		}
		String[] names = DLTKCore.getUserLibraryNames(toolkit);
		Arrays.sort(names, Collator.getInstance());

		List<String> usedNames = new ArrayList<String>();
		for (IPath name : fUsedPaths) {
			String usedName = name.segment(1);
			int pos = usedName.indexOf("#"); //$NON-NLS-1$
			if (pos != -1) {
				usedName = usedName.substring(pos + 1);
			}
			usedNames.add(usedName);
		}
		ArrayList<BPUserLibraryElement> elements = new ArrayList<BPUserLibraryElement>(
				names.length);
		for (int i = 0; i < names.length; i++) {
			String curr = names[i];
			IPath path = new Path(DLTKCore.USER_LIBRARY_CONTAINER_ID)
					.append(UserLibraryManager.makeLibraryName(curr, toolkit));
			try {
				IBuildpathContainer container = DLTKCore.getBuildpathContainer(
						path, fProject);
				String version = DLTKCore.getUserLibraryVersion(curr,
						PHPLanguageToolkit.getDefault());
				if (version != null) {
					path.append(version);
				}
				BPUserLibraryElement elem = new BPUserLibraryElement(curr,
						container, fProject);
				if (!usedNames.contains(curr)) {
					elements.add(elem);
					if (!oldCheckedNames.isEmpty()) {
						if (oldCheckedNames.contains(curr)) {
							entriesToCheck.add(elem);
						}
					} else {
						if (!oldNames.contains(curr)) {
							entriesToCheck.add(elem);
						}
					}
				}
			} catch (ModelException e) {
				LibraryUI.log(e);
				// ignore
			}
		}
		fLibrarySelector.setElements(elements);
		return entriesToCheck;
	}

	private void doDialogFieldChanged(DialogField field) {
		if (field == fLibrarySelector) {
			List<?> list = fLibrarySelector.getCheckedElements();
			if (fIsEditMode) {
				if (list.size() > 1) {
					if (fEditResult != null && list.remove(fEditResult)) {
						fLibrarySelector.setCheckedWithoutUpdate(fEditResult,
								false);
					}
					fEditResult = (BPUserLibraryElement) list.get(0); // take
					// the
					// first
					for (int i = 1; i < list.size(); i++) { // uncheck the rest
						fLibrarySelector.setCheckedWithoutUpdate(list.get(i),
								false);
					}
				} else if (list.size() == 1) {
					fEditResult = (BPUserLibraryElement) list.get(0);
				}
			}
			updateStatus(validateSettings(list));
		}
	}

	private IStatus validateSettings(List selected) {
		int nSelected = selected.size();
		if (nSelected == 0) {
			return new StatusInfo(IStatus.ERROR,
					Messages.UserLibraryWizardPage_NoSelectionError);
		} else if (fIsEditMode && nSelected > 1) {
			return new StatusInfo(IStatus.ERROR,
					Messages.UserLibraryWizardPage_TooManyError);
		}
		for (int i = 0; i < selected.size(); i++) {
			BPUserLibraryElement curr = (BPUserLibraryElement) selected.get(i);
			BPListElement[] children = curr.getChildren();
			if (fUsedPaths.contains(curr.getPath())) {
				return new StatusInfo(IStatus.ERROR,
						Messages.UserLibraryWizardPage_error_alreadyoncp);
			}
		}
		return new StatusInfo();
	}

	private void doButtonPressed(int index) {
		if (index == 0) {
			HashMap data = new HashMap(3);
			if (fEditResult != null) {
				data.put(UserLibraryPreferencePage.DATA_LIBRARY_TO_SELECT,
						fEditResult.getName());
			}
			String id = UserLibraryPreferencePage
					.getPreferencePageId(languageToolkit);
			PreferencesUtil.createPreferenceDialogOn(getShell(), id,
					new String[] { id }, data).open();

			List<BPUserLibraryElement> newEntries = updateLibraryList();
			if (newEntries.size() > 0) {
				if (fIsEditMode) {
					fLibrarySelector.setChecked(newEntries.get(0), true);
				} else {
					fLibrarySelector.setCheckedElements(newEntries);
				}
			}
		} else {
			fLibrarySelector.setCheckedElements(fLibrarySelector
					.getSelectedElements());
		}
	}

	private void doDoubleClicked(ListDialogField field) {
		if (field == fLibrarySelector) {
			List list = fLibrarySelector.getSelectedElements();
			if (list.size() == 1) {
				Object elem = list.get(0);
				boolean state = fLibrarySelector.isChecked(elem);
				if (!state || !fIsEditMode) {
					fLibrarySelector.setChecked(elem, !state);
				}
			}
		}
	}

	private String selectFolder(Shell shell) {
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
				shell, new WorkbenchLabelProvider(),
				new BaseWorkbenchContentProvider() {
					public Object[] getChildren(Object element) {
						IWorkbenchAdapter adapter = getAdapter(element);
						if (adapter != null) {
							Object[] children = adapter.getChildren(element);
							List result = new ArrayList();
							for (Object child : children) {
								if (child instanceof IContainer
										&& !((IContainer) child).getName()
												.startsWith(".")) { //$NON-NLS-1$
									result.add(child);
								}
							}
							return result.toArray(new Object[result.size()]);
						}
						return new Object[0];
					}
				});
		dialog.setTitle(Messages.UserLibraryWizardPage_DialogTitle);
		dialog.setMessage(Messages.UserLibraryWizardPage_DialogDescription);
		dialog.setInput(fProject.getResource());
		dialog.setAllowMultiple(false);
		if (dialog.open() == Window.OK && dialog.getResult().length > 0) {
			return ((IContainer) dialog.getResult()[0]).getName();
		}
		return null;
	}

}