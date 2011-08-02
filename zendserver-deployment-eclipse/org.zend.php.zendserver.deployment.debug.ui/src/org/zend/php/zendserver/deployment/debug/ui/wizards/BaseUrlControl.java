package org.zend.php.zendserver.deployment.debug.ui.wizards;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class BaseUrlControl {

	public static final String DEFAULT_HOST = "<default-server>";

	private Label protocol;
	private Label pathSeparator;

	private Text host;
	private Text path;
	private Composite parent;

	public void createControl(Composite composite) {
		parent = new Composite(composite, SWT.NONE);
		parent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout layout = new GridLayout(6, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		parent.setLayout(layout);

		protocol = new Label(parent, SWT.NULL);
		protocol.setText("http://");

		host = new Text(parent, SWT.SINGLE | SWT.BORDER);
		host.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		pathSeparator = new Label(parent, SWT.NULL);
		pathSeparator.setText("/");

		path = new Text(parent, SWT.SINGLE | SWT.BORDER);
		path.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	public void setLayoutData(Object layoutData) {
		parent.setLayoutData(layoutData);
	}

	public void setDefaultServer(boolean value) {
		if (value) {
			host.setEnabled(false);
			host.setText(DEFAULT_HOST);
		} else {
			host.setEnabled(true);
			host.setText("");
		}
	}

	public URL getURL() {
		URL result = null;
		String realHost = DEFAULT_HOST.equals(host.getText()) ? "default" : host.getText();
		try {
			result = new URL(protocol.getText() + realHost + "/" + path.getText());
		} catch (MalformedURLException e) {
			// ignore and return null
		}
		return result;
	}

	public void setURL(String vHost, String basePath) {
		host.setText(vHost);
		path.setText(basePath);
	}

	public boolean isValid() {
		if (getURL() != null && !host.getText().isEmpty()) {
			return true;
		}
		return false;
	}

	public void setEnabled(boolean value) {
		host.setEnabled(value);
		path.setEnabled(value);
	}

	public void addKeyListener(KeyListener listener) {
		host.addKeyListener(listener);
		path.addKeyListener(listener);
	}

}