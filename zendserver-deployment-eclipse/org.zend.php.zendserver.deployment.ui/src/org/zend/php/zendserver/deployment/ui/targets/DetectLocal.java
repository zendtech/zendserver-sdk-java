package org.zend.php.zendserver.deployment.ui.targets;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.zend.php.zendserver.deployment.ui.HelpContextIds;
import org.zend.php.zendserver.deployment.ui.actions.DetectTargetAction;
import org.zend.sdklib.SdkException;
import org.zend.sdklib.target.IZendTarget;

public class DetectLocal extends AbstractTargetDetailsComposite {

	@Override
	public Composite create(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(1, false));
		Label descLabel = new Label(composite, SWT.NONE);
		descLabel.setText(Messages.DetectLocal_0);
		descLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, true));
		return composite;
	}

	@Override
	public void setDefaultTargetSettings(IZendTarget defaultTarget) {
		// empty
	}

	@Override
	protected String[] getData() {
		return null;
	}

	@Override
	protected IZendTarget[] createTarget(String[] data, IProgressMonitor monitor)
			throws SdkException, IOException, CancelCreationException {

		DetectTargetAction detectTargetAction = new DetectTargetAction();
		detectTargetAction.run();
		IStatus status = detectTargetAction.getStatus();
		if (status != null) {
			if (status.getSeverity() == IStatus.CANCEL) {
				throw new CancelCreationException(status.getMessage());
			} else if (status.getSeverity() == IStatus.ERROR) {
				throw new SdkException(status.getMessage());
			}
		}
		return new IZendTarget[] { detectTargetAction.getDetectedTarget() };
	}

	@Override
	public boolean hasPage() {
		return true;
	}

	@Override
	protected String getHelpResource() {
		return HelpContextIds.CREATING_A_LOCAL_ZEND_SERVER_TARGET;
	}
	
	@Override
	protected boolean validatePage() {
		return true;
	}

}
