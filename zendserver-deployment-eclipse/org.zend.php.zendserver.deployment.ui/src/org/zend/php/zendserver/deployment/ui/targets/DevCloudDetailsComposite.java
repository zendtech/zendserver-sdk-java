package org.zend.php.zendserver.deployment.ui.targets;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.ui.Messages;
import org.zend.sdklib.SdkException;
import org.zend.sdklib.internal.target.ZendDevCloud;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;

/**
 * DevCloud details editing composite: username and password.
 */
public class DevCloudDetailsComposite extends AbstractTargetDetailsComposite {

	private static final String HREF_RESTORE_PASSWORD = "restorePassword"; //$NON-NLS-1$
	private static final String HREF_CREATE_ACCOUNT = "createAccount"; //$NON-NLS-1$
	
	private static final String RESTORE_PASSWORD_URL = "http://www.zend.com/user/lost"; //$NON-NLS-1$
	private static final String CREATE_ACCOUNT_URL = "http://www.zend.com/user/register"; //$NON-NLS-1$
	
	private Text usernameText;
	private Text passwordText;
	private Text privateKeyText;

	public Composite create(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(4, false));

		Label label = new Label(composite, SWT.NONE);
		label.setText(Messages.DevCloudDetailsComposite_Username);
		usernameText = new Text(composite, SWT.BORDER);
		GridData layoutData = new GridData(SWT.FILL, SWT.DEFAULT, true, false);
		layoutData.horizontalSpan = 3;
		usernameText.setLayoutData(layoutData);
		usernameText
				.setToolTipText(Messages.DevCloudDetailsComposite_UsernameTooltip);

		label = new Label(composite, SWT.NONE);
		label.setText(Messages.DevCloudDetailsComposite_Password);
		passwordText = new Text(composite, SWT.BORDER | SWT.PASSWORD);
		passwordText.setLayoutData(layoutData);
		passwordText
				.setToolTipText(Messages.DevCloudDetailsComposite_PasswordTooltip);

		Composite hyperlinks = new Composite(composite, SWT.NONE);
		GridData gd = new GridData(SWT.RIGHT, SWT.TOP, true, false, 4, 1);
		hyperlinks.setLayoutData(gd);
		hyperlinks.setLayout(new GridLayout(2, false));
		
		Hyperlink createAccount = new Hyperlink(hyperlinks, SWT.NONE);
		createAccount.setUnderlined(true);
		createAccount.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLUE));
		createAccount.setText(Messages.DevCloudDetailsComposite_CreatePHPCloudAccount);
		createAccount.setHref(HREF_CREATE_ACCOUNT);
		
		Hyperlink forgotPassword = new Hyperlink(hyperlinks, SWT.NONE);
		forgotPassword.setUnderlined(true);
		forgotPassword.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLUE));
		forgotPassword.setText(Messages.DevCloudDetailsComposite_RestorePassword);
		forgotPassword.setHref(HREF_RESTORE_PASSWORD);
				
		IHyperlinkListener hrefListener = new HyperlinkAdapter() {
			
			public void linkActivated(HyperlinkEvent e) {
				handleHyperlink(e.getHref());
			}
		};
		
		createAccount.addHyperlinkListener(hrefListener);
		createAccount.addHyperlinkListener(hrefListener);
		
		label = new Label(composite, SWT.NONE);
		label.setText(Messages.DevCloudDetailsComposite_0);
		privateKeyText = new Text(composite, SWT.BORDER);
		privateKeyText.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true,
				false));
		privateKeyText
				.setToolTipText(Messages.DevCloudDetailsComposite_1);
		Button btnBrowse = new Button(composite, SWT.PUSH);
		btnBrowse.setText(Messages.DevCloudDetailsComposite_2);
		Button btnGenerate = new Button(composite, SWT.PUSH);
		btnGenerate.setText(Messages.DevCloudDetailsComposite_3);
		btnBrowse.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				final FileDialog d = new FileDialog(e.display.getActiveShell(),
						SWT.OPEN);
				final String file = d.open();
				if (file != null) {
					privateKeyText.setText(file);
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		label = new Label(composite, SWT.WRAP);
		label = new Label(composite, SWT.WRAP);
		label.setText(Messages.DevCloudDetailsComposite_4
				+ Messages.DevCloudDetailsComposite_5 +
				Messages.DevCloudDetailsComposite_6);
		layoutData = new GridData(SWT.LEFT, SWT.TOP, true, false);
		layoutData.horizontalSpan = 3;
		layoutData.verticalSpan = 2;
		label.setLayoutData(layoutData);
				
		return composite;
	}

	protected void handleHyperlink(Object href) {
		if (HREF_CREATE_ACCOUNT.equals(href)) {
			Program.launch(CREATE_ACCOUNT_URL);
		} else if (HREF_RESTORE_PASSWORD.equals(href)) {
			Program.launch(RESTORE_PASSWORD_URL);
		}
	}

	public void setDefaultTargetSettings(IZendTarget defaultTarget) {
		// empty, can't restore DevCloud account details from IZendTarget
	}

	public String[] getData() {
		return new String[] { usernameText.getText(), passwordText.getText(), privateKeyText.getText() };
	}

	public IZendTarget createTarget(String[] data) throws SdkException,
			IOException {
		ZendDevCloud detect = new ZendDevCloud();
		String username = data[0];
		String password = data[1];
		String sshkeyfile = data[2];

		IZendTarget[] target = detect.detectTarget(username, password);
		if (target == null || target.length == 0) {
			return null;
		}

		TargetsManager tm = TargetsManagerService.INSTANCE.getTargetManager();

		String uniqueId = tm.createUniqueId(null);
		
		Properties p = new Properties();
		if (sshkeyfile != null && sshkeyfile.length() > 0) {
			p.put(ZendDevCloud.SSH_PRIVATE_KEY, fullStream(sshkeyfile));
		}
		
		final IZendTarget t = tm.createTarget(uniqueId, target[0].getHost().toString(),
				target[0].getKey(), target[0].getSecretKey(), p);
				
		return t;
	}

	/*
	 * @param fname The filename
	 * 
	 * @return The filled InputStream
	 * 
	 * @exception IOException, if the Streams couldn't be created.
	 */
	private static String fullStream(String fname) throws IOException {
		FileInputStream fis = new FileInputStream(fname);
		DataInputStream dis = new DataInputStream(fis);
		byte[] bytes = new byte[dis.available()];
		dis.readFully(bytes);
		dis.close();
		return new String(bytes);
	}

	@Override
	public boolean hasPage() {
		return true;
	}

}
