package org.zend.php.zendserver.deployment.ui.editors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.sdklib.mapping.IMapping;
import org.zend.sdklib.mapping.IMappingChangeEvent;
import org.zend.sdklib.mapping.IMappingChangeEvent.Kind;
import org.zend.sdklib.mapping.IMappingChangeListener;
import org.zend.sdklib.mapping.IMappingEntry;
import org.zend.sdklib.mapping.IMappingEntry.Type;

public abstract class PropertiesTreeSection implements IResourceChangeListener,
		IResourceDeltaVisitor, IMappingChangeListener {

	private Section section;
	private FormToolkit toolkit;
	protected CheckboxTreeViewer fTreeViewer;
	protected IResource fOriginalResource;
	protected IResource fParentResource;
	protected boolean isChecked;
	private boolean fDoRefresh = false;

	protected IDescriptorContainer model;

	protected FormEditor editor;

	public PropertiesTreeSection(FormEditor editor, Composite parent,
			FormToolkit toolkit, IDescriptorContainer model) {
		super();
		this.section = toolkit.createSection(parent, Section.DESCRIPTION
				| Section.TITLE_BAR | Section.EXPANDED);
		this.model = model;
		this.toolkit = toolkit;
		this.editor = editor;
		initializeSection();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	public class TreeContentProvider implements ITreeContentProvider {
		
		private final List<String> excluded = Arrays.asList(new String[] {
			".buildpath", ".project", ".settings", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		});

		public Object[] getElements(Object parent) {
			try {
				if (parent instanceof IContainer) {
						return getFilteredMembers(parent);
				}
			} catch (CoreException e) {
			}
			return new Object[0];
		}

		private Object[] getFilteredMembers(Object parent) throws CoreException {
			List<IResource> filteredMembers = new ArrayList<IResource>();
			IResource[] members = ((IContainer) parent).members();
			for (IResource res : members) {
				if (!excluded.contains(res.getName()) && !res.isLinked()) {
					filteredMembers.add(res);
				}
			}
			return filteredMembers.toArray(new IResource[0]);
		}

		/**
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
		 */
		public Object[] getChildren(Object parent) {
			try {
				if (parent instanceof IFolder) {
					return getFilteredMembers(parent);
				} 
			} catch (CoreException e) {
			}
			return new Object[0];
		}

		public Object[] getFolderChildren(Object parent) {
			IResource[] members = null;
			try {
				if (!(parent instanceof IFolder))
					return new Object[0];
				members = ((IFolder) parent).members();
				ArrayList<IResource> results = new ArrayList<IResource>();
				for (int i = 0; i < members.length; i++) {
					if ((members[i].getType() == IResource.FOLDER)) {
						results.add(members[i]);
					}
				}
				return results.toArray();
			} catch (CoreException e) {
				// TODO log
			}
			return new Object[0];
		}

		/**
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
		 */
		public Object getParent(Object element) {
			if (element != null && element instanceof IResource) {
				return ((IResource) element).getParent();
			}
			return null;
		}

		/**
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
		 */
		public boolean hasChildren(Object element) {
			if (element instanceof IFolder)
				return getChildren(element).length > 0;
			return false;
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	private void initializeSection() {
		Composite sectionClient = toolkit.createComposite(section);
		section.setClient(createClient());
		sectionClient.setLayout(new GridLayout(3, false));
		section.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	private Control createClient() {
		Composite container = toolkit.createComposite(section);
		container.setLayout(new GridLayout(1, true));
		fTreeViewer = new CheckboxTreeViewer(toolkit.createTree(container,
				SWT.CHECK));
		fTreeViewer.setContentProvider(new TreeContentProvider());
		fTreeViewer.setLabelProvider(new WorkbenchLabelProvider());
		fTreeViewer.setAutoExpandLevel(0);
		fTreeViewer.addCheckStateListener(new ICheckStateListener() {

			public void checkStateChanged(final CheckStateChangedEvent event) {
				final Object element = event.getElement();
				BusyIndicator.showWhile(section.getDisplay(), new Runnable() {

					public void run() {
						try {
							if (event.getChecked()) {
								if (element instanceof IFile) {
									IFile file = (IFile) element;
									if (file.getParent() == getContainer()) {
										handleRootFileChecked(file);
									} else {
										handleFolderFileChecked(file);
									}
								} else if (element instanceof IFolder) {
									IContainer folder = (IContainer) element;
									if (folder.getParent() == getContainer()) {
										handleRootFolderChecked(folder);
									} else {
										handleFolderFolderChecked(folder);
									}
								}
							} else {
								if (element instanceof IFile) {
									IFile file = (IFile) element;
									if (file.getParent() == getContainer()) {
										handleRootFileUnchecked(file);
									} else {
										handleFolderFileUnchecked(file);
									}
								} else if (element instanceof IFolder) {
									IContainer folder = (IContainer) element;
									if (folder.getParent() == getContainer()) {
										handleRootFolderUnchecked(folder);
									} else {
										handleFolderFolderUnchecked(folder);
									}
								}
							}
							refresh();
						} catch (CoreException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}
		});
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		fTreeViewer.getTree().setLayoutData(gd);

		initialize();
		toolkit.paintBordersFor(container);
		return container;
	}

	private void handleRootFileUnchecked(IResource file) {
		removeIncludeMapping(getName(file));
	}

	private void handleRootFolderUnchecked(IContainer folder) throws CoreException {
		IResource[] members = folder.members();
		for (IResource member : members) {
			String name = getName(member);
			removeIncludeMapping(name);
			removeExcludeMapping(name);
		}
		removeIncludeMapping(getName(folder));
	}

	private void handleFolderFileUnchecked(IResource file) throws CoreException {
		handleRootFileUnchecked(file);
		handleFileInFolderUnchecked(file);
	}

	private void handleFolderFolderUnchecked(IContainer folder) throws CoreException {
		handleRootFolderUnchecked(folder);
		handleFileInFolderUnchecked(folder);
	}

	private void handleFileInFolderUnchecked(IResource file) throws CoreException {
		IContainer parent = file.getParent();
		try {
			String[] parentsIncluded = model.getMappingModel().getFolders(parent.getLocation().toString());
			if (parentsIncluded != null) {
				for (String parentName : parentsIncluded) {
					if (parentName.equals(getFolder())) {
						addExcludeMapping(getName(file), false);
					}
				}
			}
		} catch (IOException e) {
			// should not occur if we are there
		}
	}

	private void handleRootFileChecked(IResource file) {
		removeExcludeMapping(getName(file));
		addIncludeMapping(getName(file), false);
	}

	private void handleRootFolderChecked(IContainer folder)
			throws CoreException {
		IResource[] members = folder.members();
		for (IResource member : members) {
			String name = getName(member);
			if (member instanceof IFolder) {
				handleRootFolderChecked((IFolder) member);
			}
			removeIncludeMapping(name);
			removeExcludeMapping(name);
		}
		removeExcludeMapping(getName(folder));
		addIncludeMapping(getName(folder), false);
	}

	private void handleFolderFileChecked(IResource file) throws CoreException {
		handleRootFileChecked(file);
		removeIfParentIncluded(file);
	}

	private void handleFolderFolderChecked(IContainer folder)
			throws CoreException {
		handleRootFolderChecked(folder);
		removeIfParentIncluded(folder);
	}

	private void removeIfParentIncluded(IResource resource) {
		IContainer parent = resource.getParent();
		if (parent == null || parent.equals(getContainer())) {
			return;
		}
		try {
			String[] parentsIncluded = model.getMappingModel().getFolders(parent.getLocation().toString());
			if (parentsIncluded != null) {
				for (String parentName : parentsIncluded) {
					if (parentName.equals(getFolder())) {
						if (!isExcludedAnyParents(resource)) {
							removeIncludeMapping(getName(resource));
							return;
						}
					}
				}
			}
			removeIfParentIncluded(parent);
		} catch (IOException e) {
			//
		}
	}

	private boolean isExcludedAnyParents(IResource resource) {
		IContainer parent = resource.getParent();
		if (parent != null) {
			try {
				if (model.getMappingModel().isExcluded(getFolder(), parent.getLocation().toString())) {
					return true;
				} else {
					return isExcludedAnyParents(parent);
				}
			} catch (IOException e) {
				return false;
			}
		}
		return false;
	}

	private boolean addIncludeMapping(String name, boolean isGlobal) {
		return model.getMappingModel().addMapping(getFolder(), Type.INCLUDE, name, isGlobal);
	}

	private boolean addExcludeMapping(String name, boolean isGlobal) {
		return model.getMappingModel().addMapping(getFolder(), Type.EXCLUDE, name, isGlobal);
	}

	private boolean removeIncludeMapping(String name) {
		return model.getMappingModel().removeMapping(getFolder(), Type.INCLUDE, name);
	}

	private boolean removeExcludeMapping(String name) {
		return model.getMappingModel().removeMapping(getFolder(), Type.EXCLUDE, name);
	}

	private String getName(IResource resource) {
		return resource.getProjectRelativePath()
				.makeRelativeTo(getContainer().getProjectRelativePath()).toPortableString();
	}

	private void initialize() {
		if (fTreeViewer.getInput() == null) {
			fTreeViewer.setUseHashlookup(true);
			fTreeViewer.setInput(getContainer());
		}
		initializeCheckState();
		model.getMappingModel().addMappingChangeListener(this);
	}

	private void asyncRefresh() {
		Control control = fTreeViewer.getControl();
		if (!control.isDisposed()) {
			control.getDisplay().asyncExec(new Runnable() {

				public void run() {
					if (!fTreeViewer.getControl().isDisposed()) {
						fTreeViewer.refresh(true);
						initializeCheckState();
					}
				}
			});
		}
	}

	protected IResource handleAllUnselected(IResource resource, String name) {
		IResource parent = resource.getParent();
		if (parent.equals(getContainer())) {
			return resource;
		}
		try {
			boolean uncheck = true;
			IResource[] members = ((IFolder) parent).members();
			for (int i = 0; i < members.length; i++) {
				if (fTreeViewer.getChecked(members[i])
						&& !members[i].getName().equals(name))
					uncheck = false;
			}
			if (uncheck) {
				return handleAllUnselected(parent, parent.getName());
			}
			return resource;
		} catch (CoreException e) {
			// TODO log
			return null;
		}
	}

	protected void initializeCheckState() {
		uncheckAll();
		List<IMapping> includes = new ArrayList<IMapping>();
		IMappingEntry entry = model.getMappingModel().getEntry(getFolder(), Type.INCLUDE);
		if (entry != null) {
			includes.addAll(entry.getMappings());
		}
		List<IMapping> excludes = new ArrayList<IMapping>();
		entry = model.getMappingModel().getEntry(getFolder(), Type.EXCLUDE);
		if (entry != null) {
			excludes.addAll(entry.getMappings());
		}
		excludes.addAll(model.getMappingModel().getDefaultExclusion());
		initializeCheckState(includes, excludes);
	}

	protected IContainer getContainer() {
		return model.getFile().getParent();
	}

	protected abstract String getFolder();

	public void setText(String label) {
		section.setText(label);
	}

	public void setDescription(String label) {
		section.setDescription(label);
	}

	public void uncheckAll() {
		fTreeViewer.setCheckedElements(new Object[0]);
		fTreeViewer.setGrayedElements(new Object[0]);
	}

	public void resourceChanged(IResourceChangeEvent event) {
		if (fTreeViewer.getControl().isDisposed())
			return;
		fDoRefresh = false;
		IResourceDelta delta = event.getDelta();
		try {
			if (delta != null)
				delta.accept(this);
			if (fDoRefresh) {
				asyncRefresh();
				fDoRefresh = false;
			}
		} catch (CoreException e) {
		}
	}

	public boolean visit(IResourceDelta delta) throws CoreException {
		IResource resource = delta.getResource();
		IProject project = model.getFile().getProject();

		if ((resource instanceof IFile || resource instanceof IFolder)
				&& resource.getProject().equals(project)) {
			if (delta.getKind() == IResourceDelta.ADDED
					|| delta.getKind() == IResourceDelta.REMOVED) {
				fDoRefresh = true;
				return false;
			}
		} else if (resource instanceof IProject
				&& ((IProject) resource).equals(project)) {
			return delta.getKind() != IResourceDelta.REMOVED;
		}
		return true;
	}

	public void initializeCheckState(final List<IMapping> includes,
			final List<IMapping> excludes) {
		fTreeViewer.getTree().getDisplay().asyncExec(new Runnable() {

			public void run() {
				BusyIndicator.showWhile(Display.getCurrent(), new Runnable() {

					public void run() {
						if (fTreeViewer.getTree().isDisposed()) {
							return;
						}
						List<IMappingEntry> entries = model.getMappingModel().getEnties();
						for (IMappingEntry entry : entries) {
							if (entry.getType() == Type.INCLUDE
									&& entry.getFolder().equals(getFolder())) {
								List<IMapping> mappings = entry.getMappings();
								for (IMapping mapping : mappings) {
									IResource res = getContainer().findMember(mapping.getPath());
									if (res != null) {
										try {
											checkTreeElements(res, mapping);
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} catch (CoreException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
								}
							}
						}
					}
					
					private void checkTreeElements(IResource resource, IMapping mapping)
							throws IOException, CoreException {
						if (!model.getMappingModel().isExcluded(getFolder(),
								resource.getLocation().toOSString())) {
							if (mapping != null) {
								if (resource instanceof IFolder) {
									fTreeViewer.setChecked(resource, true);
									checkAllChildren(resource);
								} else {
									fTreeViewer.setChecked(resource, true);
								}
							} else {
								fTreeViewer.setChecked(resource, true);
								if (resource instanceof IFolder) {
									checkAllChildren(resource);
								}
							}
						}
					}

					private void checkAllChildren(IResource resource) throws CoreException,
							IOException {
						IResource[] members = ((IContainer) resource).members();
						for (IResource member : members) {
							checkTreeElements(member, null);
						}
					}
				});
			}
		});
	}

	public void refresh() {
		initializeCheckState();
	}

	public void mappingChanged(IMappingChangeEvent event) {
		if (event.getChangeKind() != Kind.STORE) {
			try {
				model.getMappingModel().store();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
