package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.actions.DeployAppInCloudAction;
import org.zend.php.zendserver.deployment.ui.actions.ExportApplicationAction;
import org.zend.php.zendserver.deployment.ui.actions.RunApplicationAction;


public class OverviewPage extends DescriptorEditorPage {

	private TextField name;
	private TextField summary;
	private TextField description;
	private TextField releaseVersion;
	private TextField apiVersion;
	private TextField license;
	private TextField icon;
	private TextField docRoot;
	private TextField appDir;
	private TextField scriptsDir;
	
	private ImageHyperlink runApplicationLink;
	private ImageHyperlink runInZendCloudLink;
	private ImageHyperlink exportPackageLink;

	public OverviewPage(DeploymentDescriptorEditor editor) {
		super(editor, "overview", "Overview");
		IDeploymentDescriptor descr = editor.getModel();
		
		name = addField(new TextField(descr, IDeploymentDescriptor.NAME, "Name"));
		summary = addField(new TextField(descr, IDeploymentDescriptor.SUMMARY, "Summary"));
		description = addField(new TextField(descr, IDeploymentDescriptor.DESCRIPTION, "Description"));
		releaseVersion = addField(new TextField(descr, IDeploymentDescriptor.VERSION_RELEASE, "Release Version"));
		apiVersion = addField(new TextField(descr, IDeploymentDescriptor.VERSION_API, "API Version"));
		 
		license = addField(new FileField(descr, IDeploymentDescriptor.EULA, "License", editor.getProject()));
		icon = addField(new FileField(descr, IDeploymentDescriptor.ICON, "Icon", editor.getProject()));
		docRoot = addField(new FolderField(descr, IDeploymentDescriptor.DOCROOT, "Document root", editor.getProject()));
		scriptsDir = addField(new FolderField(descr, IDeploymentDescriptor.SCRIPTSDIR, "Scripts directory", editor.getProject()));
		appDir = addField(new FolderField(descr, IDeploymentDescriptor.APPDIR, "Application dir", editor.getProject()));
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		form.setText(getTitle());
		IToolBarManager mgr = form.getToolBarManager();
		mgr.add(new RunApplicationAction());
		mgr.add(new DeployAppInCloudAction());
		mgr.add(new ExportApplicationAction());
		mgr.update(true);

		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		form.getBody().setLayout(layout);

		createGeneralInformationSection(managedForm);
		createDeploymentScriptsSection(managedForm);
		createTestingSection(managedForm);
		createActions();
		
		form.reflow(true);
	}
	
	private void createDeploymentScriptsSection(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		
		Section section = toolkit.createSection(form.getBody(), Section.DESCRIPTION | Section.TITLE_BAR | Section.EXPANDED);
		section.setText("Deployment Scripts");
		section.setDescription("Scripts to invoke during various phases of deployment.");
		Composite sectionClient = toolkit.createComposite(section);
		section.setClient(sectionClient);
		sectionClient.setLayout(new GridLayout(3, false));
		
		TableWrapData td = new TableWrapData();
		td.grabHorizontal = true;
		td.grabVertical = true;
		td.rowspan = 2;
		td.heightHint = 250;
		section.setLayoutData(td);
		
		scriptsDir.create(sectionClient, toolkit);
		
		Label label = toolkit.createLabel(sectionClient, "Double-click on deployment phase to edit script.");
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.horizontalSpan = 3;
		label.setLayoutData(gd);
		
		Tree tree = toolkit.createTree(sectionClient, SWT.NONE);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 3;
		tree.setLayoutData(gd);
		TreeItem tm = new TreeItem(tree, SWT.NONE);
		tm.setText("Staging");
		tm.setExpanded(true);
		TreeItem tm2 = new TreeItem(tm, SWT.NONE);
		tm2.setText("Pre-Staging");
		tm2 = new TreeItem(tm, SWT.NONE);
		tm2.setText("Post-Staging");
		
		tm = new TreeItem(tree, SWT.NONE);
		tm.setText("Activation");
		tm.setExpanded(true);
		tm2 = new TreeItem(tm, SWT.NONE);
		tm2.setText("Pre-Staging");
		tm2 = new TreeItem(tm, SWT.NONE);
		tm2.setText("Post-Staging");
		
		tm = new TreeItem(tree, SWT.NONE);
		tm.setText("Removal");
		tm.setExpanded(true);
		tm2 = new TreeItem(tm, SWT.NONE);
		tm2.setText("Pre-Staging");
		tm2.setExpanded(true);
		tm2 = new TreeItem(tm, SWT.NONE);
		tm2.setText("Post-Staging");
		tm.setExpanded(true);
	}

	private void createTestingSection(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		
		Section section = toolkit.createSection(form.getBody(), Section.DESCRIPTION | Section.TITLE_BAR | Section.EXPANDED);
		section.setText("Testing");
		Composite sectionClient = toolkit.createComposite(section);
		section.setClient(sectionClient);
		sectionClient.setLayout(new GridLayout(1, false));
		
		runApplicationLink = toolkit.createImageHyperlink(sectionClient, SWT.NONE);
		runApplicationLink.setText("Run application.");
		runApplicationLink.setImage(Activator.getImageDescriptor(Activator.IMAGE_RUN_APPLICATION).createImage());
		
		runInZendCloudLink = toolkit.createImageHyperlink(sectionClient, SWT.NONE);
		runInZendCloudLink.setText("Deploy application to Zend Cloud.");
		runInZendCloudLink.setImage(Activator.getImageDescriptor(Activator.IMAGE_ZENDCLOUD_APPLICATION).createImage());
		
		exportPackageLink = toolkit.createImageHyperlink(sectionClient, SWT.NONE);
		exportPackageLink.setText("Export application package to file system.");
		exportPackageLink.setImage(Activator.getImageDescriptor(Activator.IMAGE_EXPORT_APPLICATION).createImage());
	}

	private void createGeneralInformationSection(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		
		Section section = toolkit.createSection(form.getBody(), Section.DESCRIPTION | Section.TITLE_BAR | Section.EXPANDED);
		section.setText("General Information");
		section.setDescription("Information about the application package.");
		Composite sectionClient = toolkit.createComposite(section);
		section.setClient(sectionClient);
		sectionClient.setLayout(new GridLayout(3, false));
		
		TableWrapData td = new TableWrapData();
		td.grabHorizontal = true;
		section.setLayoutData(td);
		
		name.create(sectionClient, toolkit);
		summary.create(sectionClient, toolkit);
		description.create(sectionClient, toolkit);
		apiVersion.create(sectionClient, toolkit);
		releaseVersion.create(sectionClient, toolkit);
		
		license.create(sectionClient, toolkit);
		
		icon.create(sectionClient, toolkit);
		
		docRoot.create(sectionClient, toolkit);
		appDir.create(sectionClient, toolkit);
	}
	
	private void createActions() {
		runApplicationLink.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				new RunApplicationAction().run();
			}
		});
		
		runInZendCloudLink.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				new DeployAppInCloudAction().run();
			}
		});
		
		exportPackageLink.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				new ExportApplicationAction().run();
			}
		});
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
		scriptsDir.refresh();
	}
}
