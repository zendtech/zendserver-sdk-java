package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IToolBarManager;
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
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptorModifier;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.actions.DeployAppInCloudAction;
import org.zend.php.zendserver.deployment.ui.actions.ExportApplicationAction;
import org.zend.php.zendserver.deployment.ui.actions.RunApplicationAction;


public class OverviewPage extends DescriptorEditorPage {

	private Text nameText;
	private Text summaryText;
	private Text descriptionText;
	private Text releaseVersionText;
	private Text apiVersionText;
	private Text licenseText;
	private Text iconText;
	private Text docrootText;
	/*private Text healthText;*/
	private Text scriptsDirText;
	private Button licenseBrowseButton;
	private Button iconBrowseButton;
	private Button docrootBrowseButton;
	private boolean isRefresh;
	private ImageHyperlink runApplicationLink;
	private ImageHyperlink runInZendCloudLink;
	private ImageHyperlink exportPackageLink;
	private Button scriptsDirBrowseButton;

	public OverviewPage(DeploymentDescriptorEditor editor) {
		super(editor, "overview", "Overview");
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
		
		toolkit.createLabel(sectionClient, "Scripts directory");
		scriptsDirText = toolkit.createText(sectionClient, "");
		scriptsDirBrowseButton = toolkit.createButton(sectionClient, "Browse...", SWT.PUSH);
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		scriptsDirText.setLayoutData(gd);
		
		Label label = toolkit.createLabel(sectionClient, "Double-click on deployment phase to edit script.");
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
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
		tm2.setExpanded(true);
		
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
		
		toolkit.createLabel(sectionClient, "Name");
		nameText = toolkit.createText(sectionClient, "");
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.horizontalSpan = 2;
		nameText.setLayoutData(gd);
		
		toolkit.createLabel(sectionClient, "Summary");
		summaryText = toolkit.createText(sectionClient, "");
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.horizontalSpan = 2;
		summaryText.setLayoutData(gd);
		
		toolkit.createLabel(sectionClient, "Description");
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.horizontalSpan = 2;
		descriptionText = toolkit.createText(sectionClient, "");
		descriptionText.setLayoutData(gd);
		
		toolkit.createLabel(sectionClient, "Release Version");
		releaseVersionText = toolkit.createText(sectionClient, "");
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.horizontalSpan = 2;
		releaseVersionText.setLayoutData(gd);
		
		toolkit.createLabel(sectionClient, "API Version");
		apiVersionText = toolkit.createText(sectionClient, "");
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.horizontalSpan = 2;
		apiVersionText.setLayoutData(gd);
		
		toolkit.createLabel(sectionClient, "License");
		licenseText = toolkit.createText(sectionClient, "");
		licenseBrowseButton = toolkit.createButton(sectionClient, "Browse...", SWT.PUSH);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		licenseText.setLayoutData(gd);
		
		toolkit.createLabel(sectionClient, "Icon");
		iconText = toolkit.createText(sectionClient, "");
		iconBrowseButton = toolkit.createButton(sectionClient, "Browse...", SWT.PUSH);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		iconText.setLayoutData(gd);
		
		toolkit.createLabel(sectionClient, "Document Root");
		docrootText = toolkit.createText(sectionClient, "");
		docrootBrowseButton = toolkit.createButton(sectionClient, "Browse...", SWT.PUSH);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		docrootText.setLayoutData(gd);
		
		/** Health-check is not in specification now
		toolkit.createLabel(sectionClient, "Health-check");
		healthText = toolkit.createText(sectionClient, "");
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.horizontalSpan = 2;
		healthText.setLayoutData(gd);
		*/
	}
	
	private void createActions() {
		nameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (isRefresh) {
					return;
				}
				nameChange(((Text)e.widget).getText());
			}
		});
		
		summaryText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (isRefresh) {
					return;
				}
				summaryChange(((Text)e.widget).getText());
			}
		});
		
		descriptionText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (isRefresh) {
					return;
				}
				descriptionChange(((Text)e.widget).getText());
			}
		});
		
		releaseVersionText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (isRefresh) {
					return;
				}
				releaseVersionChange(((Text)e.widget).getText());
			}
		});
		
		apiVersionText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (isRefresh) {
					return;
				}
				apiVersionChange(((Text)e.widget).getText());
			}
		});
		
		licenseText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (isRefresh) {
					return;
				}
				eulaChange(((Text)e.widget).getText());
			}
		});
		
		licenseBrowseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Shell shell = getSite().getShell();
				IProject proj = getDeploymentEditor().getProject();
				String newSelection = OpenFileDialog.openFile(shell, proj, "License", "Select file with License:", licenseText.getText());
				if (newSelection != null) {
					licenseText.setText(newSelection);
				}
			}
		});
		
		iconText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (isRefresh) {
					return;
				}
				iconChange(((Text)e.widget).getText());
			}
		});
		
		iconBrowseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Shell shell = getSite().getShell();
				IProject proj = getDeploymentEditor().getProject();
				String newSelection = OpenFileDialog.openFile(shell, proj, "License", "Select application icon:", iconText.getText());
				if (newSelection != null) {
					iconText.setText(newSelection);
				}
			}
		});
		
		docrootText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (isRefresh) {
					return;
				}
				docrootChange(((Text)e.widget).getText());
			}
		});
		
		docrootBrowseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Shell shell = getSite().getShell();
				IProject proj = getDeploymentEditor().getProject();
				String newSelection = OpenFileDialog.openFolder(shell, proj, "Document root", "Select application document root:", docrootText.getText());
				if (newSelection != null) {
					docrootText.setText(newSelection);
				}
			}
		});
		
		/*
			public void modifyText(ModifyEvent e) {
				if (isRefresh) {
					return;
				}
				healthChange(((Text)e.widget).getText());
			}
		});*/
		
		scriptsDirText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (isRefresh) {
					return;
				}
				scriptsDirChange(((Text)e.widget).getText());
			}
		});
		
		scriptsDirBrowseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Shell shell = getSite().getShell();
				IProject proj = getDeploymentEditor().getProject();
				String newSelection = OpenFileDialog.openFolder(shell, proj, "Scripts directory", "Select directory with deployment scripts:", scriptsDirText.getText());
				if (newSelection != null) {
					scriptsDirText.setText(newSelection);
				}
			}
		});
		
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
		nameText.setFocus();
	}
	
	protected DeploymentDescriptorEditor getDeploymentEditor() {
		return (DeploymentDescriptorEditor) getEditor();
	}
	
	protected IDeploymentDescriptorModifier getModel() {
		return getDeploymentEditor().getModel();
	}

	public void refresh() {
		IDeploymentDescriptor model = getModel().getDescriptor();
		
		isRefresh = true;
		try {
			nameText.setText(model.getName());
			summaryText.setText(model.getSummary());
			descriptionText.setText(model.getDescription());
			releaseVersionText.setText(model.getReleaseVersion());
			licenseText.setText(model.getEulaLocation());
			iconText.setText(model.getIconLocation());
			docrootText.setText(model.getDocumentRoot());
			/*healthText.setText(model.getHealthcheck());*/
			scriptsDirText.setText(model.getScriptsRoot());
		} finally {
			isRefresh = false;
		}
	}

	private void nameChange(String text) {
		try {
			getModel().setName(text);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void healthChange(String text) {
		try {
			getModel().setHealthcheck(text);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void docrootChange(String text) {
		try {
			getModel().setDocumentRoot(text);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void scriptsDirChange(String text) {
		try {
			getModel().setScriptsRoot(text);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void iconChange(String text) {
		try {
			getModel().setIconLocation(text);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void eulaChange(String text) {
		try {
			getModel().setEulaLocation(text);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void releaseVersionChange(String text) {
		try {
			getModel().setReleaseVersion(text);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void apiVersionChange(String text) {
		try {
			getModel().setApiVersion(text);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void descriptionChange(String text) {
		try {
			getModel().setDescription(text);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void summaryChange(String text) {
		try {
			getModel().setSummary(text);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
