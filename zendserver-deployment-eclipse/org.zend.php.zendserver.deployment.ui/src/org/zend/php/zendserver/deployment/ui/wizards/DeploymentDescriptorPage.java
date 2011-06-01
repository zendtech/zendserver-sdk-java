package org.zend.php.zendserver.deployment.ui.wizards;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.zend.php.zendserver.deployment.core.descriptor.DescriptorContainerManager;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.php.zendserver.deployment.core.utils.DeploymentUtils;
import org.zend.php.zendserver.deployment.ui.editors.DeploymentDescriptorEditor;
import org.zend.php.zendserver.deployment.ui.editors.OpenFileDialog;


public class DeploymentDescriptorPage extends WizardPage {

	private IDescriptorContainer model;

	private Text name;
	private Text folder;
	private Link editorLink;
	private Button browseButton;

	protected DeploymentDescriptorPage(IDescriptorContainer model) {
		super(Messages.descriptorPage_Title);
		this.model = model;
		setDescription(Messages.deployWizardPage_Description);
		setTitle(Messages.descriptorPage_Title);
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(3, false);
		container.setLayout(layout);

		Label descLabel = new Label(container, SWT.NULL);
		descLabel.setText(Messages.descriptorPage_Details);
		GridData gd = new GridData();
		gd.horizontalSpan = 3;
		descLabel.setLayoutData(gd);

		name = createLabelWithText(Messages.descriptorPage_AppName, container);
		GridData nameGd = new GridData(GridData.FILL_HORIZONTAL);
		nameGd.horizontalSpan = 2;
		name.setLayoutData(nameGd);
		folder = createLabelWithText(Messages.descriptorPage_Folder, container);
		GridData folderGd = new GridData(GridData.FILL_HORIZONTAL);
		folderGd.horizontalSpan = 1;
		folder.setLayoutData(folderGd);
		browseButton = new Button(container, SWT.PUSH);
		browseButton.setText(Messages.descriptorPage_FolderBrowse);
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Shell shell = e.widget.getDisplay().getActiveShell();
				IProject project = model.getFile().getProject();
				String sel = OpenFileDialog.openFolder(shell, project,
						Messages.descriptorPage_FolderBrowse_Title,
						Messages.descriptorPage_FolderBrowse_Desc,
						folder.getText());
				if (sel != null) {
					folder.setText(sel);
				}
				validatePage();
			}
		});
		createLink(container);
		setControl(container);
		setPageComplete(false);
	}

	public String getApplciationName() {
		return name.getText();
	}

	public String getDocumentRoot() {
		return folder.getText();
	}

	private void createLink(Composite container) {
		editorLink = new Link(container, SWT.NONE);
		String text = "<a>" + Messages.descriptorPage_Link + "</a>";
		editorLink.setText(text);
		editorLink.setEnabled(false);
		editorLink.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				IProject project = model.getFile().getProject();
				DeploymentUtils.createDescriptor(project, name.getText(),
						folder.getText(), new NullProgressMonitor());
				((WizardDialog) getWizard().getContainer()).close();
				try {
					openDescriptorInEditor(project);
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	private void openDescriptorInEditor(IProject project) throws CoreException,
			PartInitException {
		project.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
		IFile file = project
				.getFile((DescriptorContainerManager.DESCRIPTOR_PATH));
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		if (window != null) {
			IWorkbenchPage page = window.getActivePage();
			if (page != null) {
				page.openEditor(new FileEditorInput(file),
						DeploymentDescriptorEditor.ID);
			}
		}
	}

	private Text createLabelWithText(String labelText, Composite container) {
		Label label = new Label(container, SWT.NULL);
		label.setText(labelText);
		Text text = new Text(container, SWT.BORDER | SWT.SINGLE);
		text.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				validatePage();
			}
		});
		return text;
	}

	private void validatePage() {
		if (!name.getText().isEmpty() && !folder.getText().isEmpty()) {
			editorLink.setEnabled(true);
			setPageComplete(true);
		} else {
			editorLink.setEnabled(false);
			setPageComplete(false);
		}
	}

}
