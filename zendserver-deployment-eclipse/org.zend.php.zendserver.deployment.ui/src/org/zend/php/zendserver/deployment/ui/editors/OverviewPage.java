package org.zend.php.zendserver.deployment.ui.editors;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.IManagedForm;
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
import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorPackage;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.Messages;
import org.zend.php.zendserver.deployment.ui.actions.DeployAppInCloudAction;
import org.zend.php.zendserver.deployment.ui.actions.ExportApplicationAction;
import org.zend.php.zendserver.deployment.ui.actions.RunApplicationAction;

public class OverviewPage extends DescriptorEditorPage {

	private static final String LINK_DEBUG_PHP = "debugPHP"; //$NON-NLS-1$
	private static final String LINK_RUN_PHP = "runPHP"; //$NON-NLS-1$
	private static final String LINK_RUN_TEST = "testPHP"; //$NON-NLS-1$
	private static final String LINK_EXPORT = "export"; //$NON-NLS-1$

	private TextField name;
	private TextField summary;
	private TextField description;
	private TextField releaseVersion;
	private TextField apiVersion;
	private TextField license;
	private TextField icon;
	private TextField docRoot;
	private TextField appDir;

	private ImageHyperlink runApplicationLink;
	private ImageHyperlink debugApplicationLink;
	private ImageHyperlink runTestsLink;

	private ResourceListSection persistent;

	public OverviewPage(DeploymentDescriptorEditor editor) {
		super(editor, "overview", Messages.OverviewPage_Overview); //$NON-NLS-1$
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
		left.setLayout(FormLayoutFactory.createFormPaneTableWrapLayout(false, 1));
		left.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		
		createGeneralInformationSection(managedForm, left);
		createPersistentResourcesSection(managedForm, left);

		Composite right = toolkit.createComposite(body);
		right.setLayout(FormLayoutFactory.createFormPaneTableWrapLayout(false, 1));
		right.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		
		createTestingSection(managedForm, right);
		createExportingSection(managedForm, right);

		createActions();

		form.reflow(true);
		
		showMarkers();
	}

	private void createPersistentResourcesSection(IManagedForm managedForm, Composite left) {
		persistent = new ResourceListSection(editor, managedForm,
				Messages.OverviewPage_PersistentResources,
				Messages.OverviewPage_PersistentResourcesDescription, left) {

			@Override
			public Object[] getElements(Object input) {
				List<String> list = editor.getModel().getPersistentResources();
				return editor.getResourceMapper().getResources(
						list.toArray(new String[list.size()]));
			}

			@Override
			protected void addPath() {
				Shell shell = editor.getSite().getShell();
				IProject root = editor.getProject();
				String[] newPaths = OpenFileDialog.openMany(shell, root,
						Messages.OverviewPage_AddPath,
						Messages.OverviewPage_SelectPath, null);
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
			}
		};

	}

	private void createExportingSection(IManagedForm managedForm, Composite body) {
		ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		Section section = createStaticSection(toolkit, body,
				Messages.OverviewPage_Exporting);
		Composite container = createStaticSectionClient(toolkit, section);
		createClient(container, Messages.OverviewPage_PackageAndExport,
				toolkit, new HyperlinkAdapter() {
					public void linkActivated(HyperlinkEvent e) {
						handleLinkClick(e.data);
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

	private void createTestingSection(IManagedForm managedForm, Composite body) {
		ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();

		Section section = toolkit.createSection(body,
				Section.DESCRIPTION | Section.TITLE_BAR | Section.EXPANDED);
		section.setText(Messages.OverviewPage_Testing);
		section.setDescription(Messages.OverviewPage_TestingDescr);
		Composite sectionClient = toolkit.createComposite(section);
		section.setClient(sectionClient);
		sectionClient.setLayout(new GridLayout(1, false));

		runApplicationLink = toolkit.createImageHyperlink(sectionClient,
				SWT.NONE);
		runApplicationLink.setText(Messages.OverviewPage_LaunchingPHPApp);
		runApplicationLink.setImage(Activator.getImageDescriptor(
				Activator.IMAGE_RUN_APPLICATION).createImage());
		runApplicationLink.setHref(LINK_RUN_PHP);

		debugApplicationLink = toolkit.createImageHyperlink(sectionClient,
				SWT.NONE);
		debugApplicationLink
				.setText(Messages.OverviewPage_LaunchingAndDebuggingPHPApp);
		debugApplicationLink.setImage(Activator.getImageDescriptor(
				Activator.IMAGE_DEBUG_APPLICATION).createImage());
		debugApplicationLink.setHref(LINK_DEBUG_PHP);

		runTestsLink = toolkit.createImageHyperlink(sectionClient, SWT.NONE);
		runTestsLink.setText(Messages.OverviewPage_LaunchingPHPTest);
		runTestsLink.setImage(Activator.getImageDescriptor(
				Activator.IMAGE_RUN_TEST).createImage());
		runTestsLink.setHref(LINK_RUN_TEST);

	}

	private void createGeneralInformationSection(IManagedForm managedForm, Composite body) {
		IDeploymentDescriptor descr = editor.getModel();

		name = addField(new TextField(descr,
				DeploymentDescriptorPackage.PKG_NAME,
				Messages.OverviewPage_Name));
		summary = addField(new TextField(descr,
				DeploymentDescriptorPackage.SUMMARY,
				Messages.OverviewPage_Summary));
		description = addField(new TextField(descr,
				DeploymentDescriptorPackage.PKG_DESCRIPTION,
				Messages.OverviewPage_Description));
		releaseVersion = addField(new TextField(descr,
				DeploymentDescriptorPackage.VERSION_RELEASE,
				Messages.OverviewPage_0));
		apiVersion = addField(new TextField(descr,
				DeploymentDescriptorPackage.VERSION_API,
				Messages.OverviewPage_1));

		license = addField(new FileField(descr,
				DeploymentDescriptorPackage.EULA,
				"License", editor.getProject())); //$NON-NLS-1$
		icon = addField(new FileField(descr, DeploymentDescriptorPackage.ICON,
				Messages.OverviewPage_Icon, editor.getProject()));
		docRoot = addField(new FolderField(descr,
				DeploymentDescriptorPackage.DOCROOT,
				Messages.OverviewPage_Docroot, editor.getProject()));
		appDir = addField(new TextField(descr,
				DeploymentDescriptorPackage.APPDIR,
				Messages.OverviewPage_Appdir));

		ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		Section section = toolkit.createSection(body,
				Section.DESCRIPTION | Section.TITLE_BAR | Section.EXPANDED);
		section.setText(Messages.OverviewPage_GeneralInfo);
		section.setDescription(Messages.OverviewPage_GeneralInfoDescr);
		Composite sectionClient = toolkit.createComposite(section);
		section.setClient(sectionClient);
		sectionClient.setLayout(new GridLayout(3, false));

		TableWrapData td = new TableWrapData();
		td.grabHorizontal = true;
		td.rowspan = 2;
		section.setLayoutData(td);

		name.create(sectionClient, toolkit);
		summary.create(sectionClient, toolkit);
		description.create(sectionClient, toolkit);
		releaseVersion.create(sectionClient, toolkit);
		apiVersion.create(sectionClient, toolkit);

		license.create(sectionClient, toolkit);

		icon.create(sectionClient, toolkit);

		docRoot.create(sectionClient, toolkit);
		appDir.create(sectionClient, toolkit);
	}

	private void createActions() {
		runApplicationLink.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				handleLinkClick(e.getHref());
			}
		});

		debugApplicationLink.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				handleLinkClick(e.getHref());
			}
		});
	}

	protected void handleLinkClick(Object href) {
		if (LINK_DEBUG_PHP.equals(href)) {
			new DeployAppInCloudAction().run();
		} else if (LINK_RUN_PHP.equals(href)) {
			new RunApplicationAction().run();
		} else if (LINK_EXPORT.equals(href)) {
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
		summary.refresh();
		description.refresh();
		releaseVersion.refresh();
		apiVersion.refresh();
		license.refresh();
		icon.refresh();
		docRoot.refresh();
		appDir.refresh();
		persistent.refresh();
	}
}
