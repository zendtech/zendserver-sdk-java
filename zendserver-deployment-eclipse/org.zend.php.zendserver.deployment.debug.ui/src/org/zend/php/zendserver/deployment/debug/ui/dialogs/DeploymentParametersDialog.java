package org.zend.php.zendserver.deployment.debug.ui.dialogs;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.SharedScrolledComposite;
import org.zend.php.zendserver.deployment.core.debugger.IDeploymentHelper;
import org.zend.php.zendserver.deployment.debug.ui.Messages;
import org.zend.php.zendserver.deployment.debug.ui.listeners.IStatusChangeListener;
import org.zend.php.zendserver.deployment.debug.ui.wizards.ParametersBlock;

public class DeploymentParametersDialog extends TitleAreaDialog implements IStatusChangeListener {

	private IProject project;
	private IDeploymentHelper helper;
	private ParametersBlock block;

	private Map<String, String> parameters;

	public DeploymentParametersDialog(Shell parentShell, IProject project, IDeploymentHelper helper) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
		this.project = project;
		this.helper = helper;
		this.block = new ParametersBlock(this);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		final SharedScrolledComposite scrolledComposite = new SharedScrolledComposite(parent,
				SWT.V_SCROLL) {
		};
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		scrolledComposite.setLayoutData(data);
		scrolledComposite.setLayout(new FillLayout());
		Composite container = new Composite(scrolledComposite, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setContent(container);
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);
		block.createContents(container);
		block.createParametersGroup(project);
		if (helper != null) {
			block.initializeFields(helper);
		}
		statusChanged(block.validatePage());
		setTitle(Messages.DeploymentParameters_Title);
		getShell().setText(Messages.DeploymentParameters_Title);
		return container;
	}

	@Override
	protected void initializeBounds() {
		super.initializeBounds();
		getShell().setMinimumSize(550, 600);
		getShell().setSize(getShell().getMinimumSize());
		Rectangle monitorArea = getShell().getDisplay().getPrimaryMonitor().getBounds();
		Rectangle shellArea = getShell().getBounds();
		int x = monitorArea.x + (monitorArea.width - shellArea.width) / 2;
		int y = monitorArea.y + (monitorArea.height - shellArea.height) / 3;
		getShell().setLocation(x, y);
	}

	@Override
	protected void okPressed() {
		this.parameters = block.getHelper().getUserParams();
		super.okPressed();
	}
	
	public Map<String, String> getParameters() {
		return parameters;
	}

	public void statusChanged(IStatus status) {
		if (status.getSeverity() == IStatus.ERROR) {
			setErrorMessage(status.getMessage());
		} else {
			setErrorMessage(null);
			setMessage(Messages.parametersPage_Description);
		}
	}

}
