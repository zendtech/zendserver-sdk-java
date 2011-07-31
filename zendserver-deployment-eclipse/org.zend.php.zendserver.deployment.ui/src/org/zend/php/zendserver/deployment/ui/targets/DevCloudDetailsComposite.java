package org.zend.php.zendserver.deployment.ui.targets;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.zend.php.zendserver.deployment.ui.Messages;
import org.zend.sdklib.SdkException;
import org.zend.sdklib.internal.target.ZendDevPaasDetect;
import org.zend.sdklib.target.IZendTarget;

/**
 * DevCloud details editing composite: username and password.
 */
public class DevCloudDetailsComposite extends AbstractTargetDetailsComposite {

	private Text usernameText;
	private Text passwordText;

	public Composite create(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(2, false));

		Label label = new Label(composite, SWT.NONE);
		label.setText(Messages.DevCloudDetailsComposite_Username);
		usernameText = new Text(composite, SWT.BORDER);
		usernameText.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true,
				false));
		usernameText
				.setToolTipText(Messages.DevCloudDetailsComposite_UsernameTooltip);

		label = new Label(composite, SWT.NONE);
		label.setText(Messages.DevCloudDetailsComposite_Password);
		passwordText = new Text(composite, SWT.BORDER | SWT.PASSWORD);
		passwordText.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true,
				false));
		passwordText
				.setToolTipText(Messages.DevCloudDetailsComposite_PasswordTooltip);

		return composite;
	}

	public void setDefaultTargetSettings(IZendTarget defaultTarget) {
		// empty, can't restore DevCloud account details from IZendTarget
	}

	public String[] getData() {
		return new String[] { usernameText.getText(), passwordText.getText() };
	}

	public IZendTarget createTarget(String[] data) throws SdkException,
			IOException {
		ZendDevPaasDetect detect = new ZendDevPaasDetect();
		String username = data[0];
		String password = data[1];

		IZendTarget[] target = detect.detectTarget(username, password);
		if (target == null || target.length == 0) {
			return null;
		}

		final IZendTarget first = target[0];
		return createTarget(first.getHost(), first.getKey(),
				first.getSecretKey());
	}
}
