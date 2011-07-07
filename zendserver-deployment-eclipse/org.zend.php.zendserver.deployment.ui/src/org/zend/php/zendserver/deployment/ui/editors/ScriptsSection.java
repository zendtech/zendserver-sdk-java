package org.zend.php.zendserver.deployment.ui.editors;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
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
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.part.FileEditorInput;
import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorPackage;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.Messages;
import org.zend.php.zendserver.deployment.ui.editors.ScriptsContentProvider.Script;
import org.zend.sdklib.application.ZendProject;
import org.zend.sdklib.mapping.IMappingModel;

public class ScriptsSection {

	private DeploymentDescriptorEditor editor;
	private TextField scriptsDir;
	private TreeViewer scriptsTree;
	
	public ScriptsSection(DeploymentDescriptorEditor editor) {
		this.editor = editor;
		IDeploymentDescriptor descr = editor.getModel();
		
		scriptsDir = new TextField(descr,
				DeploymentDescriptorPackage.SCRIPTSDIR,
				Messages.OverviewPage_Scriptsdir);
		
	}
	
	private void openScript(final String name) {
		Job job = new Job(Messages.OverviewPage_CreatingDeploymentScript) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					IFile file = getScript(name);
					if (!file.exists()) {
						createScript(name, monitor);
						refreshScriptsTree();
					}
					openEditor(file);
				} catch (CoreException e) {
					return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
							e.getMessage(), e);
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

	private void createScript(String scriptName, IProgressMonitor monitor)
			throws CoreException {
		File projLocation = editor.getProject().getLocation().toFile();
		ZendProject zp = new ZendProject(projLocation);
		zp.update(scriptName);
		editor.getProject().refreshLocal(IProject.DEPTH_INFINITE, monitor);

	}

	protected void openEditor(final IFile file) throws PartInitException {
		editor.getSite().getShell().getDisplay().asyncExec(new Runnable() {

			public void run() {
				IWorkbenchPage page = editor.getSite().getPage();
				try {
					IEditorDescriptor desc = PlatformUI.getWorkbench()
							.getEditorRegistry()
							.getDefaultEditor(file.getName());
					page.openEditor(new FileEditorInput(file), desc.getId());
				} catch (PartInitException e) {
					// TODO Log exception
					e.printStackTrace();
				}
			}

		});

	}
	
	public void refresh() {
		scriptsDir.refresh();
	}
	
	public void createDeploymentScriptsSection(IManagedForm managedForm, Composite parent) {
		ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();

		Section section = toolkit.createSection(parent,
				Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE);
		section.marginWidth = 5;
		section.marginHeight = 5;
		section.setText(Messages.OverviewPage_DeploymentScripts);
		section.setDescription(Messages.OverviewPage_ScriptsDescription);
		Composite sectionClient = toolkit.createComposite(section);
		section.setClient(sectionClient);
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.BOTTOM));
		sectionClient.setLayout(new GridLayout(3, false));
		
		scriptsDir.create(sectionClient, toolkit);

		Label label = toolkit.createLabel(sectionClient,
				Messages.OverviewPage_Doubleclick);
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.horizontalSpan = 3;
		label.setLayoutData(gd);

		scriptsTree = new TreeViewer(sectionClient, SWT.BORDER);
		Tree tree = scriptsTree.getTree();
		gd = new GridData(SWT.FILL, SWT.DEFAULT, true, true);
		gd.horizontalSpan = 3;
		tree.setLayoutData(gd);

		ScriptsContentProvider cp = new ScriptsContentProvider();
		scriptsTree.setContentProvider(cp);
		scriptsTree.setLabelProvider(new ScriptsLabelProvider(this));
		scriptsTree.setInput(cp.model);
		scriptsTree.expandAll();
		scriptsTree.addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {
				Object element = ((IStructuredSelection) event.getSelection())
						.getFirstElement();
				if (element instanceof ScriptsContentProvider.Script) {
					ScriptsContentProvider.Script script = (Script) element;
					IFile file = getScript(script.name);
					if (!file.exists()) {
						boolean canCreate = MessageDialog.openQuestion(
								editor.getSite().getShell(), Messages.OverviewPage_OpenScript,
								Messages.OverviewPage_SelectedScriptDoesntExist);
						if (!canCreate) {
							return;
						}
					}
					openScript(script.name);
				}
			}
		});
	}

	protected IFile getScript(String scriptName) {
		IMappingModel mapping = editor.getDescriptorContainer().getMappingModel();
		if (mapping == null) {
			return editor.getProject().getFile(scriptName);
		}
		
		try {
			String folder = mapping.getPath(scriptName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}
