package org.zend.php.common;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.equinox.internal.p2.discovery.Catalog;
import org.eclipse.equinox.internal.p2.discovery.DiscoveryCore;
import org.eclipse.equinox.internal.p2.discovery.compatibility.RemoteBundleDiscoveryStrategy;
import org.eclipse.equinox.internal.p2.discovery.compatibility.SiteVerifier;
import org.eclipse.equinox.internal.p2.discovery.model.CatalogCategory;
import org.eclipse.equinox.internal.p2.discovery.model.CatalogItem;
import org.eclipse.equinox.internal.p2.discovery.model.Tag;
import org.eclipse.equinox.internal.p2.discovery.util.CatalogCategoryComparator;
import org.eclipse.equinox.internal.p2.discovery.util.CatalogItemComparator;
import org.eclipse.equinox.internal.p2.ui.ProvUI;
import org.eclipse.equinox.internal.p2.ui.discovery.util.FilteredViewer;
import org.eclipse.equinox.internal.p2.ui.discovery.util.PatternFilter;
import org.eclipse.equinox.internal.p2.ui.discovery.util.TextSearchControl;
import org.eclipse.equinox.internal.p2.ui.discovery.wizards.CatalogConfiguration;
import org.eclipse.equinox.internal.p2.ui.discovery.wizards.CatalogFilter;
import org.eclipse.equinox.internal.p2.ui.discovery.wizards.DiscoveryResources;
import org.eclipse.equinox.p2.engine.IProfile;
import org.eclipse.equinox.p2.engine.IProfileRegistry;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.ui.Policy;
import org.eclipse.equinox.p2.ui.ProvisioningUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.progress.WorkbenchJob;
import org.eclipse.ui.statushandlers.StatusManager;

@SuppressWarnings("restriction")
public class ZendCatalogViewer extends FilteredViewer {

	private static String LOADING = "Loading..."; //$NON-NLS-N$

	private Composite header;

	private ZendCatalogContentProvider contentProvider;

	DiscoveryResources resources;

	public DiscoveryResources getResources() {
		return resources;
	}

	Set<String> installedFeatures;

	protected final IRunnableContext context;

	final Catalog catalog;

	private final CatalogConfiguration configuration;

	boolean ignoreUpdates;

	CatalogItem[] installedConectors;

	protected final IShellProvider shellProvider;

	boolean showInstalled = true;

	Button showInstalledCheckbox;

	Set<Tag> visibleTags;

	WorkbenchJob refreshJob;

	private long refreshJobDelay = 200L;

	private PatternFilter searchFilter;

	private Label clearFilterTextControl;

	private Label loadingLabel;

	TextSearchControl filterText;

	private boolean automaticFind = true;

	String previousFilterText = ""; //$NON-NLS-1$

	Object[] prevState;

	protected CheckboxTreeViewer viewer;

	boolean performDiscovery = true;

	Button applyChangesButton;

	Button restoreButton;

	/**
	 * Whether to show the header on top of the search bar of the catalog
	 */
	private boolean showHeader = false;

	private String operationName;

	protected ProfileModificationHelper pm;
	
	public ZendCatalogViewer(IShellProvider shellProvider,
			IRunnableContext context) {
		
		this.pm = new ProfileModificationHelper();
		this.catalog = new Catalog();
		catalog.setEnvironment(DiscoveryCore.createEnvironment());
		catalog.setVerifyUpdateSiteAvailability(false);

		// look for descriptors from installed bundles
		// catalog.getDiscoveryStrategies().add(new BundleDiscoveryStrategy());
		
		this.configuration = new CatalogConfiguration();
		configuration.setShowTagFilter(false);
		configuration.setShowInstalled(true);

		this.shellProvider = shellProvider;
		this.context = context;
		this.showInstalled = configuration.isShowInstalled();
		if (configuration.getSelectedTags() != null) {
			this.visibleTags = new HashSet<Tag>(configuration.getSelectedTags());
		} else {
			this.visibleTags = new HashSet<Tag>();
		}
	}
	
	public void setDiscoveryDirFileName(String directoryFileName) {
		// look for remote descriptor
		RemoteBundleDiscoveryStrategy remoteDiscoveryStrategy = new RemoteBundleDiscoveryStrategy();
		catalog.getDiscoveryStrategies().add(remoteDiscoveryStrategy);

		URI repo = null;
		try {
			repo = ProfileModificationHelper.getExtraRepository();
		} catch (IllegalArgumentException ex) {
			performDiscovery = false;
			handleError(Messages.ConnectorDiscovery_InvalidUrlDefined_Title,
					NLS.bind(
							Messages.ConnectorDiscovery_InvalidUrlDefined_Msg,
							ex.getMessage()
									+ (ex.getCause() != null ? ex.getCause()
											: "")), IStatus.ERROR);
			return;
		}
		if (repo != null) {
			remoteDiscoveryStrategy.setDirectoryUrl(repo.toString().concat(
					directoryFileName));
		} else {
			performDiscovery = false;
			handleError(Messages.ConnectorDiscovery_NoUrlDefined_Title,
					Messages.ConnectorDiscovery_NoUrlDefined_Msg,
					IStatus.WARNING);
		}
	}

	private void handleError(final String title, final String msg,
			final int status) {
		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				if (status == IStatus.ERROR) {
					MessageDialog.openError(shellProvider.getShell(), title,
							msg);
				} else if (status == IStatus.WARNING) {
					MessageDialog.openWarning(shellProvider.getShell(), title,
							msg);
				}

			}
		});
	}

	protected PatternFilter doCreateFilter() {
		return new FindFilter();
	}

	public void refresh() {
		installedFeatures = getInstalledFeatures(null);
		postDiscovery();
		catalogUpdated();
	}

	protected void doFind(String text) {
		searchFilter.setPattern(text);
		if (clearFilterTextControl != null) {
			clearFilterTextControl.setVisible(text != null
					&& text.length() != 0);
		}
		viewer.refresh(false);
		showInstalled();
	}

	protected void catalogUpdated() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (!viewer.getControl().isDisposed()) {
					viewer.setInput(catalog);
					installedConectors = contentProvider.getInstalledElements();
					showInstalled();
				}
			}
		});
	}

	protected CheckboxTreeViewer doCreateViewer(Composite container) {
		Composite viewerComposite = new Composite(container, SWT.FILL);
		Color c = new Color(Display.getDefault(), 255, 255, 255);
		GridLayout gdl = new GridLayout(1, true);
		gdl.marginHeight = 0;
		gdl.marginWidth = 0;
		viewerComposite.setLayout(gdl);
		GridData hgd = new GridData(GridData.FILL_BOTH);
		viewerComposite.setLayoutData(hgd);
		viewerComposite.setBackgroundMode(SWT.INHERIT_FORCE);
		viewerComposite.setBackground(c);
		if (performDiscovery) {
			loadingLabel = new Label(viewerComposite, SWT.TRANSPARENT);
			loadingLabel.setText(LOADING);
			GridData ldata = new GridData();
			ldata.horizontalIndent = 10;
			loadingLabel.setLayoutData(ldata);
		}
		viewer = new CheckboxTreeViewer(viewerComposite, SWT.BORDER | SWT.FILL);
		GridData data = new GridData();
		data.exclude = true;
		viewer.getControl().setLayoutData(data);
		viewer.getControl().setVisible(false);
		viewerComposite.layout();
		viewerComposite.pack();
		contentProvider = new ZendCatalogContentProvider(viewerComposite, this);
		contentProvider.setHasCategories(true);
		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(new ZendCheckboxTreeLabelProvider());
		viewer.setSorter(new ViewerSorter() {
			CatalogCategoryComparator categoryComparator = new CatalogCategoryComparator();
			CatalogItemComparator itemComparator = new CatalogItemComparator();

			public int compare(Viewer viewer, Object o1, Object o2) {
				CatalogCategory cat1 = getCategory(o1);
				CatalogCategory cat2 = getCategory(o2);

				// FIXME filter uncategorized items?
				if (cat1 == null) {
					return (cat2 != null) ? 1 : 0;
				} else if (cat2 == null) {
					return 1;
				}

				int i = categoryComparator.compare(cat1, cat2);
				if (i == 0) {
					if (o1 instanceof CatalogCategory) {
						return -1;
					}
					if (o2 instanceof CatalogCategory) {
						return 1;
					}
					if (cat1 == cat2 && o1 instanceof CatalogItem
							&& o2 instanceof CatalogItem) {
						return itemComparator.compare((CatalogItem) o1,
								(CatalogItem) o2);
					}
					return super.compare(viewer, o1, o2);
				}
				return i;
			}

			private CatalogCategory getCategory(Object o) {
				if (o instanceof CatalogCategory) {
					return (CatalogCategory) o;
				}
				if (o instanceof CatalogItem) {
					return ((CatalogItem) o).getCategory();
				}
				return null;
			}
		});

		resources = new DiscoveryResources(viewerComposite.getDisplay());
		viewer.getControl().addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				resources.dispose();
				if (contentProvider.getCatalog() != null)
					contentProvider.getCatalog().dispose();
			}
		});
		viewer.addFilter(new Filter());
		final ToolTipHandler tooltip = new ToolTipHandler(
				viewerComposite.getShell());
		tooltip.activateHoverHelp(viewer.getControl());

		return viewer;
	}

	protected boolean doFilter(CatalogItem item) {
		if (!showInstalled && item.isInstalled()) {
			return false;
		}

		if (!isTagVisible(item)) {
			return false;
		}

		for (CatalogFilter filter : configuration.getFilters()) {
			if (!filter.select(item)) {
				return false;
			}
		}

		return true;
	}

	private boolean isTagVisible(CatalogItem item) {
		if (!configuration.isShowTagFilter()) {
			return true;
		}
		for (Tag selectedTag : visibleTags) {
			for (Tag tag : item.getTags()) {
				if (tag.equals(selectedTag)) {
					return true;
				}
			}
		}
		return false;
	}

	protected Set<String> getInstalledFeatures(IProgressMonitor monitor) {
		Set<String> features = new HashSet<String>();
		IProfileRegistry profileReg = ProvUI.getProfileRegistry(
				ProvisioningUI.getDefaultUI().getSession());
		if (profileReg != null) {
			IProfile profile = profileReg.getProfile(ProvisioningUI.getDefaultUI().getProfileId());
			if (profile != null) {
				IQueryResult<IInstallableUnit> result = profile.available(
						QueryUtil.createIUGroupQuery(), monitor);
				for (Iterator<IInstallableUnit> it = result.iterator(); it
						.hasNext();) {
					IInstallableUnit unit = it.next();
					features.add(unit.getId());
				}
			}
		}
		return features;
	}

	protected void postDiscovery() {
		for (CatalogItem connector : catalog.getItems()) {
			connector.setInstalled(installedFeatures != null
					&& installedFeatures.containsAll(connector
							.getInstallableUnits()));
		}
	}

	public void updateCatalog() {
		enableHeaderControls(performDiscovery);
		if (performDiscovery) {
			Job job = new Job(Messages.UpdateCatalogJobName) {

				protected IStatus run(IProgressMonitor monitor) {

					IStatus result = catalog.performDiscovery(monitor);
					if (monitor.isCanceled()) {
						return Status.CANCEL_STATUS;
					}

					if (result != null && !result.isOK()) {
						MultiStatus stat = new MultiStatus(Activator.PLUGIN_ID,
								0, Messages.ConnectorDiscoveryFailed, null);
						stat.addAll(result);
						StatusManager.getManager().handle(
								stat,
								StatusManager.SHOW | StatusManager.BLOCK
										| StatusManager.LOG);
					}

					if (installedFeatures == null) {
						installedFeatures = getInstalledFeatures(monitor);
					}
					postDiscovery();

					if (catalog != null) {
						catalogUpdated();
						verifyUpdateSiteAvailability();
					}
					// help UI tests
					viewer.setData("discoveryComplete", "true"); //$NON-NLS-1$//$NON-NLS-2$
					return Status.OK_STATUS;
				}
			};
			job.setPriority(Job.INTERACTIVE);
			job.addJobChangeListener(new JobChangeAdapter() {
				public void done(final IJobChangeEvent event) {
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							if (performDiscovery)
								loadingLabel.dispose();
							GridData data = new GridData();
							data.exclude = false;
							viewer.getControl().setLayoutData(data);
							viewer.getControl().setVisible(true);
							GridDataFactory.fillDefaults().grab(true, true)
									.align(SWT.FILL, SWT.FILL)
									.applyTo(viewer.getControl());
							viewer.getControl().getParent().layout();
						}
					});

				}
			});
			job.schedule();
		}

	}

	private void enableHeaderControls(boolean enable) {
		applyChangesButton.setEnabled(enable);
		restoreButton.setEnabled(enable);
		filterText.setEnabled(enable);
	}

	protected void verifyUpdateSiteAvailability() {
		if (configuration.isVerifyUpdateSiteAvailability()
				&& !catalog.getItems().isEmpty()) {
			Job job = new Job("Verifying update site availability") {

				protected IStatus run(IProgressMonitor monitor) {
					SiteVerifier verifier = new SiteVerifier(catalog);
					verifier.verifySiteAvailability(monitor);
					if (monitor.isCanceled()) {
						return Status.CANCEL_STATUS;
					}
					return Status.OK_STATUS;
				}
			};
			job.setPriority(Job.INTERACTIVE);
			job.schedule();
		}
	}

	private class Filter extends ViewerFilter {

		public boolean select(Viewer filteredViewer, Object parentElement,
				Object element) {
			if (element instanceof CatalogItem) {
				return doFilter((CatalogItem) element);
			} else if (element instanceof CatalogCategory) {
				// only show categories if at least one child is visible
				CatalogCategory category = (CatalogCategory) element;
				for (CatalogItem item : category.getItems()) {
					if (doFilter(item)) {
						return true;
					}
				}
				return false;
			}
			return true;
		}
	}

	protected void doCreateHeaderControls(Composite parent) {
		header = new Composite(parent, SWT.TRANSPARENT);
		GridLayout gdl = new GridLayout(3, false);
		gdl.marginHeight = 0;
		gdl.marginWidth = 0;
		gdl.marginBottom = 5;
		header.setLayout(gdl);
		GridData hgd = new GridData(SWT.FILL, SWT.FILL, true, false);
		header.setLayoutData(hgd);
		header.setBackgroundMode(SWT.INHERIT_FORCE);

		if (showHeader) {
			Label txt = new Label(header, SWT.WRAP | SWT.TRANSPARENT);
			txt.setText(Messages.CustomizationComponent_Description);
			final Display display = parent.getShell().getDisplay();
			txt.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
			GridData labelGD = new GridData(SWT.FILL, SWT.FILL, true, false);
			labelGD.horizontalSpan = 3;
			txt.setLayoutData(labelGD);
			Color c = new Color(Display.getDefault(), 46, 100, 20);
			txt.setBackground(c);
		}

		filterText = new TextSearchControl(header, automaticFind);
		if (automaticFind) {
			filterText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					filterTextChanged();
				}
			});
		} else {
			filterText.getTextControl().addTraverseListener(
					new TraverseListener() {
						public void keyTraversed(TraverseEvent e) {
							if (e.detail == SWT.TRAVERSE_RETURN) {
								e.doit = false;
								filterTextChanged();
							}
						}
					});
		}
		filterText.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				if (e.detail == SWT.ICON_CANCEL) {
					clearFilterText();
				} else {
					// search icon and enter
					filterTextChanged();
				}
			}
		});
		filterText.setBackgroundMode(SWT.INHERIT_FORCE);
		filterText.setBackground(new Color(Display.getDefault(), 95, 166, 48));
		GridDataFactory.fillDefaults().grab(true, false)
				.align(SWT.FILL, SWT.CENTER).applyTo(filterText);

		applyChangesButton = new Button(header, SWT.PUSH | SWT.TRANSPARENT);
		applyChangesButton.setText(Messages.Button_ApplyChanges);
		applyChangesButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				applyChanges();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		restoreButton = new Button(header, SWT.PUSH);
		restoreButton.setText(Messages.Button_Restore);
		restoreButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				refresh();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				refresh();
			}
		});

		enableHeaderControls(false);
	}

	void clearFilterText() {
		filterText.getTextControl().setText(""); //$NON-NLS-1$
		filterTextChanged();
	}

	public void createControl(Composite parent) {
		if (parent.isDisposed()) {
			return;
		}
		parent.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				if (refreshJob != null) {
					refreshJob.cancel();
				}
			}
		});

		doCreateHeaderControls(parent);

		viewer = doCreateViewer(parent);
		searchFilter = doCreateFilter();
		viewer.addFilter(searchFilter);

		GridDataFactory.fillDefaults().grab(true, true)
				.align(SWT.FILL, SWT.FILL).applyTo(viewer.getControl());
	}

	// overridden to weed out non-IU elements, such as repositories or empty
	// explanations
	public Object[] getCatalogItems() {
		Object[] selection = viewer.getCheckedElements();
		ArrayList<Object> list = new ArrayList<Object>(selection.length);
		for (Object obj : selection)
			if (obj instanceof CatalogCategory) {
				CatalogCategory category = (CatalogCategory) obj;
				if (!viewer.getGrayed(category)) {
					list.addAll(category.getItems());
				}
			} else {
				list.add(obj);
			}
		return list.toArray();
	}

	private void applyChanges() {
		Object[] selection = getCatalogItems();
		
		List<CatalogItem> toAddItems = new ArrayList<CatalogItem>();
		List<CatalogItem> toRemoveItems = new ArrayList<CatalogItem>();
		findFeatureChanges(selection, toAddItems, toRemoveItems);
		doApplyChanges(toAddItems, toRemoveItems);
	}
	
	protected void doApplyChanges(final List<CatalogItem> toAddItems, final List<CatalogItem> toRemoveItems) {
		if (!toAddItems.isEmpty() || !toRemoveItems.isEmpty()) {
			Job job = new Job(Messages.ApplyChanges_JobName) {
				protected IStatus run(IProgressMonitor monitor) {
					return pm.modify(monitor,
							toAddItems, toRemoveItems,
							Policy.RESTART_POLICY_PROMPT_RESTART_OR_APPLY,
							operationName);
				}
			};

			job.addJobChangeListener(new JobChangeAdapter() {
				public void done(final IJobChangeEvent event) {
					refresh();
				}

			});
			job.setPriority(Job.INTERACTIVE);
			job.setUser(true);
			job.schedule();
		}
	}
	

	private void findFeatureChanges(Object[] selection, List<CatalogItem> toAddItems, List<CatalogItem> toRemoveItems) {
		List<Object> selectedConnectors = Arrays.asList(selection);
		for (Object connector : selectedConnectors) {
			if (connector instanceof CatalogItem) {
				toAddItems.add((CatalogItem) connector);
			}
		}
		for (CatalogItem connector : installedConectors) {
			if (!selectedConnectors.contains(connector)) {
				toRemoveItems.add(connector);
			} else {
				toAddItems.remove(connector);
			}
		}
	}

	private void showInstalled() {
		viewer.setCheckedElements(installedConectors);
	}

	/**
	 * Invoked whenever the filter text is changed or the user otherwise causes
	 * the filter text to change.
	 */
	protected void filterTextChanged() {
		if (refreshJob == null) {
			refreshJob = doCreateRefreshJob();
		} else {
			refreshJob.cancel();
		}
		refreshJob.schedule(refreshJobDelay);
	}

	protected WorkbenchJob doCreateRefreshJob() {
		return new WorkbenchJob("filter") { //$NON-NLS-1$
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				if (filterText.isDisposed()) {
					return Status.CANCEL_STATUS;
				}
				String text = filterText.getTextControl().getText();
				text = text.trim();

				if (!previousFilterText.equals(text)) {
					previousFilterText = text;
					doFind(text);
				}
				return Status.OK_STATUS;
			}
		};
	}

	public void setOperationName(String operName) {
		this.operationName = operName;
	}

	public void setShowCategories(boolean doShowCategories) {
		contentProvider.setHasCategories(doShowCategories);
	}
	
}
