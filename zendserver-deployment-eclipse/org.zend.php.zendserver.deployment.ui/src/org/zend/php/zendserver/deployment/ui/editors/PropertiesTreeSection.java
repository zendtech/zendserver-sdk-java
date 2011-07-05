package org.zend.php.zendserver.deployment.ui.editors;

import java.io.IOException;
import java.util.ArrayList;
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
import org.eclipse.core.runtime.Path;
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

		public Object[] getElements(Object parent) {
			if (parent instanceof IContainer) {
				try {
					return ((IContainer) parent).members();
				} catch (CoreException e) {
					// TODO log
				}
			}
			return new Object[0];
		}

		/**
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
		 */
		public Object[] getChildren(Object parent) {
			try {
				if (parent instanceof IFolder)
					return ((IFolder) parent).members();
			} catch (CoreException e) {
				// TODO log
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
							if (element instanceof IFile) {
								IFile file = (IFile) event.getElement();
								handleFileChecked(file, event.getChecked());
							} else if (element instanceof IFolder) {
								IContainer folder = (IContainer) event
										.getElement();
								handleFolderChecked(folder, event.getChecked());
							}
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

	private void handleFolderChecked(IContainer container, boolean isChecked)
			throws CoreException {
		if (isChecked) {
			List<IResource> checkedMembers = getMembers(container, true);
			for (IResource member : checkedMembers) {
				String name = getName(member);
				removeIncludeMapping(name);
				removeExcludeMapping(name);
			}
			addIncludeMapping(getName(container), false, false);
		} else {
			if (!removeIncludeMapping(getName(container))) {
				handleFileChecked(container.getParent(), false);
			}
		}
	}

	private void handleFileChecked(IResource resource, boolean isChecked)
			throws CoreException {
		IContainer parent = resource.getParent();
		boolean isParentChecked = fTreeViewer.getChecked(parent);
		boolean isParentGrayed = fTreeViewer.getGrayed(parent);
		if (isChecked) {
			doHandleFileChecked(resource, parent, isParentChecked);
		} else {
			doHandleFileUnchecked(resource, parent, isParentChecked, isParentGrayed);
		}
	}

	private void doHandleFileUnchecked(IResource resource, IContainer parent,
			boolean isParentChecked, boolean isParentGrayed) throws CoreException {
		if (isParentChecked || isParentGrayed) {
			if (parent.getName().equals(getContainer().getName())) {
				removeIncludeMapping(getName(resource));
			} else if (parent.members().length <= 1) {
				handleFolderChecked(parent, false);
			} else {
				List<IResource> checkedMembers = getMembers(parent, true);
				if (checkedMembers.size() * 2 < parent.members().length) {
					for (IResource member : parent.members()) {
						String name = getName(member);
						removeIncludeMapping(name);
						removeExcludeMapping(name);
					}
					// handleFolderChecked(parent, false);
					if (removeIncludeMapping(getName(parent))) {
						doHandleFileUnchecked(parent, parent.getParent(),
								fTreeViewer.getChecked(parent.getParent()),
								fTreeViewer.getGrayed(parent.getParent()));
					}
					for (IResource checked : checkedMembers) {
						addIncludeMapping(getName(checked), false, false);
					}
				} else {
					addExcludeMapping(getName(resource), false, false);
				}
			}
		} else {
			removeIncludeMapping(getName(resource));
		}
	}

	private void doHandleFileChecked(IResource resource, IContainer parent,
			boolean isParentChecked) throws CoreException {
		if (isParentChecked) {
			removeExcludeMapping(getName(resource));
		} else {
			List<IResource> uncheckedMembers = getMembers(parent, false);

			if (parent.members().length > 1
					&& uncheckedMembers.size() * 2 < parent.members().length) {
				for (IResource member : parent.members()) {
					String name = getName(member);
					removeIncludeMapping(name);
					removeExcludeMapping(name);
				}
				addIncludeMapping(getName(parent), false, true);
				for (IResource unchecked : uncheckedMembers) {
					addExcludeMapping(getName(unchecked), false, false);
				}
			} else {
				String name = getName(resource);
				removeExcludeMapping(name);
				addIncludeMapping(name, false, false);
			}
		}
	}

	private boolean addIncludeMapping(String name, boolean isGlobal, boolean isContent) {
		return model.getMappingModel().addMapping(getFolder(), Type.INCLUDE, name, isGlobal,
				isContent);
	}

	private boolean addExcludeMapping(String name, boolean isGlobal, boolean isContent) {
		return model.getMappingModel().addMapping(getFolder(), Type.EXCLUDE, name, isGlobal,
				isContent);
	}

	private boolean removeIncludeMapping(String name) {
		return model.getMappingModel().removeMapping(getFolder(), Type.INCLUDE, name);
	}

	private boolean removeExcludeMapping(String name) {
		return model.getMappingModel().removeMapping(getFolder(), Type.EXCLUDE, name);
	}

	private List<IResource> getMembers(IContainer container, boolean isChecked)
			throws CoreException {
		List<IResource> result = new ArrayList<IResource>();
		IResource[] members = container.members();
		for (IResource member : members) {
			if (fTreeViewer.getChecked(member) == isChecked) {
				result.add(member);
			}
		}
		return result;
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
						if (includes.size() == 0) {
							return;
						}
						for (IMapping include : includes) {
							if (!include.isGlobal()) {
								checkInclude(include);
							} else {
								iterateOverMembers(getContainer(),
										include.getPath(), true);
							}
						}
						for (IMapping exclude : excludes) {
							if (!exclude.isGlobal()) {
								IResource resource = getContainer().findMember(
										new Path(exclude.getPath()));
								if (resource != null) {
									if (resource instanceof IFolder) {
										fTreeViewer.setSubtreeChecked(resource,
												false);
										fTreeViewer.collapseToLevel(resource, 1);
									} else {
										fTreeViewer.setChecked(resource, false);
									}
								}
							} else {
								iterateOverMembers(getContainer(),
										exclude.getPath(), false);
							}
						}
					}

					private void iterateOverMembers(IResource resource,
							String path, boolean state) {
						if (resource instanceof IContainer) {
							try {
								if (resource.getName().equals(path)) {
									fTreeViewer.setSubtreeChecked(resource,
											state);
								} else {
									IResource[] members = ((IContainer) resource)
											.members();
									for (IResource member : members) {
										iterateOverMembers(member, path, state);
									}
								}
							} catch (CoreException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} else {
							if (resource.getName().equals(path)) {
								fTreeViewer.setChecked(resource, state);
							}
						}
					}
				});
			}
		});
	}

	private void checkInclude(IMapping include) {
		IResource resource = getContainer().findMember(
				new Path(include.getPath()));
		if (resource != null) {
			if (resource instanceof IFolder) {
				if (include.isContent()) {
					fTreeViewer.setSubtreeChecked(resource, true);
					// fTreeViewer.setGrayChecked(resource, true);
					fTreeViewer.setChecked(resource, false);
					fTreeViewer.expandToLevel(resource, 1);
					expandParents(resource);
				} else {
					fTreeViewer.setSubtreeChecked(resource, true);
				}
			} else {
				fTreeViewer.setChecked(resource, true);
			}
		}
	}

	private void expandParents(IResource resource) {
		if (!resource.getName().equals(getContainer().getName())) {
			fTreeViewer.expandToLevel(resource, 1);
			expandParents(resource.getParent());
		}
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
		refresh();
	}

}
