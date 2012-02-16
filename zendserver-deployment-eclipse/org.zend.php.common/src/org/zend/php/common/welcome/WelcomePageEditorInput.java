package org.zend.php.common.welcome;

import java.net.URL;

import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.internal.browser.WebBrowserEditorInput;

public class WelcomePageEditorInput extends WebBrowserEditorInput {

	public WelcomePageEditorInput() {
		super();
		// TODO Auto-generated constructor stub
	}

	public WelcomePageEditorInput(URL url, boolean b) {
		super(url, b);
		// TODO Auto-generated constructor stub
	}

	public WelcomePageEditorInput(URL url, int style, String browserId) {
		super(url, style, browserId);
		// TODO Auto-generated constructor stub
	}

	public WelcomePageEditorInput(URL url) {
		super(url);
		// TODO Auto-generated constructor stub
	}

	public WelcomePageEditorInput(URL url, int style) {
		super(url, style);
	}

	public String getFactoryId() {
		return ELEMENT_FACTORY_ID;
	}

	public String getName() {
		return "Welcome";
	}

	@Override
	public String getToolTipText() {
		return getName();
	}

	public boolean exists() {
		return true;
	}

	public IPersistableElement getPersistable() {
		return this;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof WebBrowserEditorInput) {
			if (this.getURL().equals(((WebBrowserEditorInput) obj).getURL()))
				return true;
		}
		return false;
	}

	public static final String ELEMENT_FACTORY_ID = WelcomePageEditorInput.class
			.getName();

	public String getDiscoveryDirFileName() {
		return "/pdt_directory.xml";
	}

}
