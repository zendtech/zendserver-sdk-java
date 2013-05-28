package org.zend.php.zendserver.deployment.ui.editors;

import java.io.IOException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.Messages;
import org.zend.sdklib.mapping.IMappingEntry.Type;
import org.zend.sdklib.mapping.IMappingModel;

public class FileField extends TextField {

	private Button licenseBrowseButton;
	private IContainer root;
	private IDescriptorContainer fModel;
	
	public FileField(IDeploymentDescriptor descriptor, Feature key, String label, IContainer root) {
		super(descriptor, key, label);
		this.root = root;
	}
	
	public FileField(IDescriptorContainer model, Feature key, String label, IContainer root) {
		this(model.getDescriptorModel(), key, label, root);
		this.fModel = model;
	}

	@Override
	protected void createTextControl(Composite parent, FormToolkit toolkit) {
		text = toolkit.createText(parent, ""); //$NON-NLS-1$
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		text.setLayoutData(gd);
		text.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		gd.horizontalSpan = labelTxt != null ? 1 : 2;
		licenseBrowseButton = toolkit.createButton(parent, Messages.FileField_Browse, SWT.PUSH);
	}
	
	@Override
	protected void createActions() {
		super.createActions();
		licenseBrowseButton.addSelectionListener(new SelectionAdapter() {
			private static final String SEPARATOR = "/"; //$NON-NLS-1$
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				Shell shell = e.widget.getDisplay().getActiveShell();
				String msg = Messages.bind(Messages.FileField_SelectFile, labelTxt);
				OpenFileDialog dialog = new OpenFileDialog(shell, root, labelTxt, msg, text.getText());
				String newSelection = openDialog(dialog);
				if (newSelection != null) {
					if (fModel != null) {
						try {
							IMappingModel mappingModel = fModel.getMappingModel();
							String mappedPath = mappingModel.getPackagePath(IMappingModel.APPDIR,
									newSelection);
							if (mappedPath == null) {
								mappingModel.addMapping(IMappingModel.APPDIR, Type.INCLUDE,
										newSelection, false);
								mappedPath = mappingModel.getPackagePath(IMappingModel.APPDIR,
										newSelection);
								mappingModel.store();
							}
							String appdir = fModel.getDescriptorModel().getApplicationDir();
							text.setText(appdir + SEPARATOR + getUnifiedPath(mappedPath));
						} catch (IOException e1) {
							Activator.log(e1);
						}
					} else {
						text.setText(newSelection);
					}
					
				}
			}
			
			private String getUnifiedPath(String path) {
				String result = path.replaceAll("\\\\", SEPARATOR); //$NON-NLS-1$
				return result.substring(result.indexOf(SEPARATOR) + 1);
			}
		});
	}
	
	protected String openDialog(OpenFileDialog dialog) {
		return dialog.openFile();
	}
	
	@Override
	public void setEnabled(boolean enable) {
		super.setEnabled(enable);
		licenseBrowseButton.setEnabled(enable);
	}
	
}
