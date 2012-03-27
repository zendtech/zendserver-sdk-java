package org.zend.php.common.welcome;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.util.StatusHandler;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.internal.browser.WebBrowserEditorInput;
import org.eclipse.ui.internal.browser.WebBrowserUIPlugin;

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
	
	public IAdaptable createElement(IMemento memento) {
		int style = 0;
		Integer integer = memento.getInteger("style");
		if (integer != null) {
			style = integer.intValue();
		}

		URL url = null;
		String str = memento.getString("url");
		if (str != null) {
			try {
				url = new URL(str);
			}
			catch (MalformedURLException e) {
				String msg = "Malformed URL while initializing browser editor"; //$NON-NLS-1$
				WebBrowserUIPlugin.logError(msg, e);
			}
		}

		String id = memento.getString("id");
		String name = memento.getString("name");
		String tooltip = memento.getString("tooltip");
		
		WebBrowserEditorInput input = new WelcomePageEditorInput(url, style, id);
		input.setName(name);
		input.setToolTipText(tooltip);
		return input;
	}

	public StatusHandler getStatusHandler() {
		return null;
	}

}
