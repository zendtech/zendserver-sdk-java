package org.zend.php.zendserver.deployment.ui.editors;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.zend.php.zendserver.deployment.core.descriptor.ChangeEvent;
import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorPackage;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorChangeListener;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.php.zendserver.deployment.core.descriptor.ProjectType;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.HelpContextIds;
import org.zend.php.zendserver.deployment.ui.Messages;
import org.zend.php.zendserver.deployment.ui.actions.ExportApplicationAction;
import org.zend.php.zendserver.deployment.ui.contributions.ITestingSectionContribution;

public class OverviewPage extends DescriptorEditorPage {

	private static final String VIEW = "view:"; //$NON-NLS-1$

	private static final String LINK_EXPORT = "export"; //$NON-NLS-1$

	private TextField name;
	private ComboField type;
	private TextField summary;
	private TextField description;
	private TextField releaseVersion;
	private TextField apiVersion;
	private TextField healthcheck;
	private TextField license;
	private TextField icon;
	private TextField docRoot;
	private TextField appDir;
	private TextField libDir;
	private TextField updateUrl;

	private ResourceListSection persistent;

	private IDescriptorContainer fModel;

	public OverviewPage(DeploymentDescriptorEditor editor,
			IDescriptorContainer model) {
		super(editor, "overview", Messages.OverviewPage_Overview); //$NON-NLS-1$
		this.fModel = model;
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);

		ScrolledForm form = managedForm.getForm();
		form.getBody().setLayout(
				FormLayoutFactory.createFormTableWrapLayout(true, 2));

		final FormToolkit toolkit = managedForm.getToolkit();
		final Composite body = managedForm.getForm().getBody();

		Composite left = toolkit.createComposite(body);
		left.setLayout(FormLayoutFactory
				.createFormPaneTableWrapLayout(false, 1));
		left.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

		createGeneralInformationSection(managedForm, left);
		createPersistentResourcesSection(managedForm, left);

		Composite right = toolkit.createComposite(body);
		right.setLayout(FormLayoutFactory.createFormPaneTableWrapLayout(false,
				1));
		right.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

		createTestingSection(managedForm, right);
		createExportingSection(managedForm, right);

		form.reflow(true);

		showMarkers();
	}

	private void createPersistentResourcesSection(IManagedForm managedForm,
			Composite left) {
		persistent = new ResourceListSection(editor, managedForm,
				Messages.OverviewPage_PersistentResources,
				Messages.OverviewPage_PersistentResourcesDescription, left) {

			@Override
			public Object[] getElements(Object input) {
				List<String> list = editor.getModel().getPersistentResources();
				return list.toArray();
			}

			@Override
			protected void addPath() {
				Shell shell = editor.getSite().getShell();
				IProject root = editor.getProject();
				String[] newPaths = OpenFileDialog.openAny(shell, root,
						Messages.OverviewPage_AddPath,
						Messages.OverviewPage_SelectPath,
						Messages.OverviewPage_2, null);
				if (newPaths == null) {
					return;
				}

				for (int i = 0; i < newPaths.length; i++) {
					editor.getModel().getPersistentResources().add(newPaths[i]);
				}
			}

			@Override
			protected void editPath(Object element) {
				String currPath = (String) element;

				Shell shell = editor.getSite().getShell();
				IProject root = editor.getProject();
				String newPath = OpenFileDialog.open(shell, root,
						Messages.OverviewPage_ChangePath,
						Messages.OverviewPage_SelectPath, currPath.toString());
				if (newPath == null) {
					return;
				}
				editor.getModel().getPersistentResources().remove(currPath);
				editor.getModel().getPersistentResources().add(newPath);
			}

			@Override
			protected void removePath(Object element) {
				String path = (String) element;
				editor.getModel().getPersistentResources().remove(path);
				upadateEnablement();
			}
		};

	}

	private void createExportingSection(final IManagedForm managedForm, Composite body) {
		FormToolkit toolkit = managedForm.getToolkit();
		Section section = createStaticSection(toolkit, body,
				Messages.OverviewPage_Exporting);
		Composite container = createStaticSectionClient(toolkit, section);
		String content = Messages.OverviewPage_PackageAndExport;
		if (fModel.getDescriptorModel().getType() == ProjectType.LIBRARY) {
			content = Messages.OverviewPage_PackageAndExportLibrary;
		}
		final FormText text = createClient(container, content, toolkit,
				new HyperlinkAdapter() {
					public void linkActivated(HyperlinkEvent e) {
						handleLinkClick(e.data);
					}
				});
		fModel.getDescriptorModel().addListener(
				new IDescriptorChangeListener() {

					public void descriptorChanged(final ChangeEvent event) {
						if (text == null || text.isDisposed()) {
							return;
						}
						Display.getDefault().asyncExec(new Runnable() {

							public void run() {
								switch (fModel.getDescriptorModel().getType()) {
								case LIBRARY:
									text.setText(
											Messages.OverviewPage_PackageAndExportLibrary,
											true, false);
									break;
								default:
									text.setText(
											Messages.OverviewPage_PackageAndExport,
											true, false);
									break;
								}
								managedForm.reflow(true);
							}
						});
					}
				});
		section.setClient(container);
	}

	/**
	 * @param toolkit
	 * @param parent
	 * @return
	 */
	protected Composite createStaticSectionClient(FormToolkit toolkit,
			Composite parent) {
		Composite container = toolkit.createComposite(parent, SWT.NONE);
		container.setLayout(FormLayoutFactory
				.createSectionClientTableWrapLayout(false, 1));
		TableWrapData data = new TableWrapData(TableWrapData.FILL_GRAB);
		container.setLayoutData(data);
		return container;
	}

	protected final Section createStaticSection(FormToolkit toolkit,
			Composite parent, String text) {
		Section section = toolkit.createSection(parent,
				ExpandableComposite.TITLE_BAR);
		section.clientVerticalSpacing = FormLayoutFactory.SECTION_HEADER_VERTICAL_SPACING;
		section.setText(text);
		section.setLayout(FormLayoutFactory
				.createClearTableWrapLayout(false, 1));
		TableWrapData data = new TableWrapData(TableWrapData.FILL_GRAB);
		section.setLayoutData(data);
		return section;
	}

	protected final FormText createClient(Composite section, String content,
			FormToolkit toolkit, IHyperlinkListener hyperLink) {
		FormText text = toolkit.createFormText(section, true);
		try {
			text.setText(content, true, false);
		} catch (SWTException e) {
			text.setText(e.getMessage(), false, false);
		}
		text.addHyperlinkListener(hyperLink);
		return text;
	}

	private void createTestingSection(final IManagedForm managedForm, Composite body) {
		FormToolkit toolkit = managedForm.getToolkit();

		Section section = toolkit.createSection(body, Section.TITLE_BAR
				| Section.EXPANDED);
		section.setText(Messages.OverviewPage_Testing);
		TableWrapData data = new TableWrapData(TableWrapData.FILL_GRAB);
		section.setLayoutData(data);

		final Composite sectionClient = toolkit.createComposite(section);

		FormText formText = toolkit.createFormText(sectionClient, true);
		formText.setText(Messages.OverviewPage_TestingDescr, true, true);
		formText.setLayoutData(new GridData(GridData.FILL_BOTH));
		formText.addHyperlinkListener(new HyperlinkAdapter() {

			public void linkActivated(HyperlinkEvent e) {
				Object obj = e.getHref();
				String href = (String) obj;
				if (href.startsWith(VIEW)) {
					String viewId = href.substring(VIEW.length());
					try {
						getSite().getPage().showView(viewId);
					} catch (PartInitException ex) {
						Activator.log(ex);
					}
				}
			}
		});

		section.setClient(sectionClient);
		sectionClient.setLayout(new GridLayout(1, false));

		ProjectType type = fModel.getDescriptorModel().getType();
		resolveContributions(sectionClient, type);
		fModel.getDescriptorModel().addListener(
				new IDescriptorChangeListener() {

					public void descriptorChanged(final ChangeEvent event) {
						if (sectionClient == null || sectionClient.isDisposed()) {
							return;
						}
						Display.getDefault().asyncExec(new Runnable() {

							public void run() {
								Control[] children = sectionClient
										.getChildren();
								if (event.feature
										.equals(DeploymentDescriptorPackage.PKG_TYPE)) {
									if (event.newValue != null
											&& !event.newValue
													.equals(event.oldValue)) {
										ProjectType currentType = ProjectType
												.byName((String) event.newValue);
										for (Control child : children) {
											if (child instanceof ImageHyperlink) {
												ProjectType linkType = (ProjectType) child.getData();
												setControlVisibility(child, linkType == currentType);
											}
										}
										managedForm.reflow(true);
									}
								}
							}
						});
					}
				});
	}

	protected void updateTestingActions(Composite parent,
			IContributionItem[] items) {
		for (IContributionItem item : items) {
			item.fill(parent);
		}
	}

	private void createGeneralInformationSection(IManagedForm managedForm,
			Composite body) {
		IDeploymentDescriptor descr = editor.getModel();
		IMessageManager mmng = managedForm.getMessageManager();

		name = addField(new TextField(descr,
				DeploymentDescriptorPackage.PKG_NAME,
				Messages.OverviewPage_Name, mmng));
		type = addField(new ComboField(descr,
				DeploymentDescriptorPackage.PKG_TYPE,
				Messages.OverviewPage_ProjectType, SWT.READ_ONLY,
				ProjectType.APPLICATION.getName()));
		type.setItems(new String[] { ProjectType.APPLICATION.getName(),
				ProjectType.LIBRARY.getName() });
		summary = addField(new TextField(descr,
				DeploymentDescriptorPackage.SUMMARY,
				Messages.OverviewPage_Summary, mmng));
		description = addField(new TextField(descr,
				DeploymentDescriptorPackage.PKG_DESCRIPTION,
				Messages.OverviewPage_Description, SWT.MULTI | SWT.WRAP
						| SWT.V_SCROLL | SWT.RESIZE, false, mmng));
		releaseVersion = addField(new TextField(descr,
				DeploymentDescriptorPackage.VERSION_RELEASE,
				Messages.OverviewPage_0, mmng));
		apiVersion = addField(new TextField(descr,
				DeploymentDescriptorPackage.VERSION_API,
				Messages.OverviewPage_1, mmng));
		healthcheck = addField(new TextField(descr,
				DeploymentDescriptorPackage.HEALTHCHECK, Messages.OverviewPage_3, mmng));
		license = addField(new FileField(descr,
				DeploymentDescriptorPackage.EULA,
				Messages.OverviewPage_4, editor.getProject(), mmng));
		icon = addField(new FileField(descr, DeploymentDescriptorPackage.ICON,
				Messages.OverviewPage_Icon, editor.getProject(), mmng));
		docRoot = addField(new FolderField(fModel,
				DeploymentDescriptorPackage.DOCROOT,
				Messages.OverviewPage_Docroot, editor.getProject(), mmng));
		libDir = addField(new TextField(descr,
				DeploymentDescriptorPackage.LIBDIR,
				Messages.OverviewPage_Libdir, mmng));
		updateUrl = addField(new TextField(descr,
				DeploymentDescriptorPackage.UPDATE_URL,
				Messages.OverviewPage_UpdateUrl, mmng));
		appDir = addField(new TextField(descr,
				DeploymentDescriptorPackage.APPDIR,
				Messages.OverviewPage_Appdir, mmng));

		FormToolkit toolkit = managedForm.getToolkit();
		final Section section = toolkit.createSection(body, Section.DESCRIPTION
				| Section.TITLE_BAR | Section.EXPANDED);
		section.setText(Messages.OverviewPage_GeneralInfo);
		fModel.getDescriptorModel().addListener(
				new IDescriptorChangeListener() {

					public void descriptorChanged(final ChangeEvent event) {
						if (section == null || section.isDisposed()) {
							return;
						}
						Display.getDefault().asyncExec(new Runnable() {

							public void run() {
								boolean libEnabled = false;
								switch (fModel.getDescriptorModel().getType()) {
								case LIBRARY:
									section.setDescription(Messages.OverviewPage_GeneralInfoDescrLibrary);
									libEnabled = true;
									break;
								default:
									section.setDescription(Messages.OverviewPage_GeneralInfoDescr);
									break;
								}
								appDir.setEnabled(!libEnabled);
								docRoot.setEnabled(!libEnabled);
								healthcheck.setEnabled(!libEnabled);
								libDir.setEnabled(libEnabled);
								updateUrl.setEnabled(libEnabled);
							}
						});
					}
				});
		Composite sectionClient = toolkit.createComposite(section);
		section.setClient(sectionClient);
		sectionClient.setLayout(new GridLayout(3, false));

		TableWrapData td = new TableWrapData();
		td.grabHorizontal = true;
		td.rowspan = 2;
		section.setLayoutData(td);

		name.create(sectionClient, toolkit);
		type.create(sectionClient, toolkit);
		summary.create(sectionClient, toolkit);
		description.create(sectionClient, toolkit);
		((GridData) description.getText().getLayoutData()).heightHint = 50;
		((GridData) description.getText().getLayoutData()).widthHint = 50;

		releaseVersion.create(sectionClient, toolkit);
		apiVersion.create(sectionClient, toolkit);
		healthcheck.create(sectionClient, toolkit);

		libDir.create(sectionClient, toolkit);
		updateUrl.create(sectionClient, toolkit);
		appDir.create(sectionClient, toolkit);
		docRoot.create(sectionClient, toolkit);

		license.create(sectionClient, toolkit);

		icon.create(sectionClient, toolkit);

		switch (fModel.getDescriptorModel().getType()) {
		case LIBRARY:
			appDir.setEnabled(false);
			docRoot.setEnabled(false);
			healthcheck.setEnabled(false);
			section.setDescription(Messages.OverviewPage_GeneralInfoDescrLibrary);
			break;
		default:
			libDir.setEnabled(false);
			updateUrl.setEnabled(false);
			section.setDescription(Messages.OverviewPage_GeneralInfoDescr);
			break;
		}
		
		toolkit.paintBordersFor(sectionClient);
	}

	protected void handleLinkClick(Object href) {
		if (LINK_EXPORT.equals(href)) {
			new ExportApplicationAction(editor.getProject()).run();
		} else if (href instanceof String) {
			editor.setActivePage((String) href);
		}
	}

	@Override
	public void setActive(boolean active) {
		super.setActive(active);
		if (active) {
			refresh();
		}
	}

	@Override
	public void setFocus() {
		name.setFocus();
	}

	public void refresh() {
		name.refresh();
		type.refresh();
		summary.refresh();
		description.refresh();
		releaseVersion.refresh();
		apiVersion.refresh();
		healthcheck.refresh();
		license.refresh();
		icon.refresh();
		docRoot.refresh();
		appDir.refresh();
		persistent.refresh();
	}

	@Override
	protected String getHelpResource() {
		return HelpContextIds.OVERVIEW_TAB;
	}

	private void resolveContributions(final Composite sectionClient,
			ProjectType currentType) {
		if (currentType == ProjectType.UNKNOWN) {
			currentType = ProjectType.APPLICATION;
		}
		List<ITestingSectionContribution> contributions = getTestingContributions();
		for (ITestingSectionContribution c : contributions) {
			ContributionControl control = new ContributionControl(
					c.getCommand(), c.getMode(), c.getLabel(), c.getIcon(), c.getType());
			Control link = control.createControl(sectionClient);
			setControlVisibility(link, c.getType() == currentType);
		}
	}
	
	private void setControlVisibility(Control control, boolean show) {
		control.setVisible(show);
		GridData data = (GridData) control.getLayoutData();
		if (data == null) {
			data = new GridData();
			control.setLayoutData(data);
		}
		data.exclude = !show;
	}

}
