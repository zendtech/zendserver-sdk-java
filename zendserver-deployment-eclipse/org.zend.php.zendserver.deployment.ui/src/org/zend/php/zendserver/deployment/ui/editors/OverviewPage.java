package org.zend.php.zendserver.deployment.ui.editors;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.FileEditorInput;
import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorPackage;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.actions.DeployAppInCloudAction;
import org.zend.php.zendserver.deployment.ui.actions.ExportApplicationAction;
import org.zend.php.zendserver.deployment.ui.actions.RunApplicationAction;
import org.zend.php.zendserver.deployment.ui.editors.ScriptsContentProvider.Script;


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
	private TreeViewer scriptsTree;

	public OverviewPage(DeploymentDescriptorEditor editor) {
		super(editor, "overview", "Overview");
		IDeploymentDescriptor descr = editor.getModel();
		
		name = addField(new TextField(descr, DeploymentDescriptorPackage.PKG_NAME, "Name"));
		summary = addField(new TextField(descr, DeploymentDescriptorPackage.SUMMARY, "Summary"));
		description = addField(new TextField(descr, DeploymentDescriptorPackage.PKG_DESCRIPTION, "Description"));
		releaseVersion = addField(new TextField(descr, DeploymentDescriptorPackage.VERSION_RELEASE, "Release Version"));
		apiVersion = addField(new TextField(descr, DeploymentDescriptorPackage.VERSION_API, "API Version"));
		 
		license = addField(new FileField(descr, DeploymentDescriptorPackage.EULA, "License", editor.getProject()));
		icon = addField(new FileField(descr, DeploymentDescriptorPackage.ICON, "Icon", editor.getProject()));
		docRoot = addField(new FolderField(descr, DeploymentDescriptorPackage.DOCROOT, "Document root", editor.getProject()));
		scriptsDir = addField(new TextField(descr, DeploymentDescriptorPackage.SCRIPTSDIR, "Scripts directory"));
		appDir = addField(new TextField(descr, DeploymentDescriptorPackage.APPDIR, "Application dir"));
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		
		ScrolledForm form = managedForm.getForm();
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
		td.heightHint = 350;
		section.setLayoutData(td);
		
		scriptsDir.create(sectionClient, toolkit);
		
		Label label = toolkit.createLabel(sectionClient, "Double-click on deployment phase to edit script.");
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.horizontalSpan = 3;
		label.setLayoutData(gd);
		
		scriptsTree = new TreeViewer(sectionClient, SWT.BORDER);
		Tree tree = scriptsTree.getTree();
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 3;
		tree.setLayoutData(gd);
		
		ScriptsContentProvider cp = new ScriptsContentProvider();
		scriptsTree.setContentProvider(cp);
		scriptsTree.setLabelProvider(new ScriptsLabelProvider(this));
		scriptsTree.setInput(cp.model);
		scriptsTree.expandAll();
		scriptsTree.addDoubleClickListener(new IDoubleClickListener() {
			
			public void doubleClick(DoubleClickEvent event) {
				Object element = ((IStructuredSelection)event.getSelection()).getFirstElement();
				if (element instanceof ScriptsContentProvider.Script) {
					ScriptsContentProvider.Script script = (Script) element;
					IFile file = getScript(script.name);
					if (! file.exists()) {
						boolean canCreate = MessageDialog.openQuestion(getSite().getShell(), "Open script", "Selected script doesn't exist. Would you like to create it?");
						if (!canCreate) {
							return;
						}
					}
					openScript(file);
				}
			}
		});
	}

	private void openScript(final IFile file) {
		Job job = new Job("Creating Deployment Script") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					if (! file.exists()) {
						createScript(file, monitor);
						refreshScriptsTree();
					}
					openEditor(file);
				} catch (CoreException e) {
					return new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
				}
				return Status.OK_STATUS;
			}
			
		};
		job.setUser(true);
		job.schedule();
	}
	
	protected void refreshScriptsTree() {
		scriptsTree.getControl().getDisplay().asyncExec(new Runnable() {
			
			public void run() {
				scriptsTree.refresh(); // update created script icon
			}
		});
	}

	private void createScript(IFile file, IProgressMonitor monitor) throws CoreException {
		// TODO use SDK to create script file?
		file.create(new ByteArrayInputStream(new byte[0]), true, monitor);
		
	}
	
	protected void openEditor(final IFile file) throws PartInitException {
		final IWorkbenchPage page = getSite().getPage();
		getSite().getShell().getDisplay().asyncExec(new Runnable() {

			public void run() {
				try {
					IEditorDescriptor desc = PlatformUI.getWorkbench().
					        getEditorRegistry().getDefaultEditor(file.getName());
					page.openEditor(new FileEditorInput(file), desc.getId());
				} catch (PartInitException e) {
					// TODO Log exception
					e.printStackTrace();
				}
			}
			
		});
		
	}

	protected IFile getScript(String scriptName) {
		// TODO use mapping.getScriptPath(scriptName)
		return editor.getProject().getFile(scriptName);
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
