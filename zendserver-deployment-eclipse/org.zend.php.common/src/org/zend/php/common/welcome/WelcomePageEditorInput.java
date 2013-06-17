package org.zend.php.common.welcome;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.util.StatusHandler;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.internal.browser.WebBrowserEditorInput;
import org.zend.php.common.Activator;
import org.zend.php.common.ZendCatalogViewer;

public class WelcomePageEditorInput extends WebBrowserEditorInput {

	private String discoveryFile;
	private boolean showCategories;
	private boolean doFlattenTopLevelCategories;
	private boolean autoExpandCategories;
	private String progressDialogMsg;

	public WelcomePageEditorInput() {
		super();
		// TODO Auto-generated constructor stub
	}

	public WelcomePageEditorInput(URL url, boolean b) {
		super(url, b);
		// TODO Auto-generated constructor stub
	}

	public WelcomePageEditorInput(URL url, int style, String browserId,
			String discoveryFile, boolean showCategories,
			String progressDialogMsg, boolean flatTopLevel,
			boolean autoExpandCategories) {
		super(url, style, browserId);
		this.discoveryFile = discoveryFile;
		this.showCategories = showCategories;
		this.progressDialogMsg = progressDialogMsg;
		this.doFlattenTopLevelCategories = flatTopLevel;
		this.autoExpandCategories = autoExpandCategories;
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
		return discoveryFile;
	}

	public void saveState(IMemento memento) {
		super.saveState(memento);
		if (discoveryFile != null) {
			memento.putString("discoveryFile", discoveryFile);
		}
		memento.putBoolean("categories", showCategories);
		memento.putString("progressDialogMsg", progressDialogMsg);
	}

	public IAdaptable createElement(IMemento memento) {
		int style = 0;
		Integer integer = memento.getInteger("style");
		if (integer != null) {
			style = integer.intValue();
		}

		URL url = null;

		if (Platform.getProduct().getId().equals("org.zend.php.product")) {
			url = FileLocator.find(Activator.getDefault().getBundle(),
					new Path("/resources/welcome/PDT-welcome-page.html"), null);
		} else {
			// TODO we should not have com.zend.php.ui reference here...
			url = FileLocator
					.find(Platform.getBundle("com.zend.php.ui"),
							new Path(
									"/resources/welcome/index-zend-studio-10-welcome-page.html"), //$NON-NLS-1$
							null);
		}
		String id = memento.getString("id");
		String name = memento.getString("name");
		String tooltip = memento.getString("tooltip");
		String discoveryFileName = memento.getString("discoveryFile");
		Boolean showCategories = memento.getBoolean("categories");
		if (showCategories == null) {
			showCategories = false;
		}
		Boolean flatTopLevel = memento.getBoolean("flatTopLevel");
		if (flatTopLevel == null) {
			flatTopLevel = false;
		}
		Boolean autoExpandCategories = memento
				.getBoolean("autoExpandCategories");
		if (autoExpandCategories == null) {
			autoExpandCategories = false;
		}
		String progressDialogMsg = memento.getString("progressDialogMsg");
		WebBrowserEditorInput input = null;
		try {
			if (url.toString().toLowerCase().contains("pdt")) {

				input = new PdtWelcomePageEditorInput(
						FileLocator.toFileURL(url),
						IWorkbenchBrowserSupport.PERSISTENT, "welcomeBrowser");
			} else {
				input = new WelcomePageEditorInput(FileLocator.toFileURL(url),
						style, id, discoveryFileName, showCategories,
						progressDialogMsg, flatTopLevel, autoExpandCategories);
			}
		} catch (IOException e) {
			Activator.log(e);
		}
		input.setName(name);
		input.setToolTipText(tooltip);
		return input;
	}

	public StatusHandler getStatusHandler() {
		return null;
	}

	public void initFeaturesViewer(ZendCatalogViewer viewer) {
		viewer.setDiscoveryDirFileName(getDiscoveryDirFileName());
		viewer.setShowCategories(showCategories);
		viewer.setFlattenTopLevelCategories(doFlattenTopLevelCategories);
		viewer.setAutoExpandCategories(autoExpandCategories);
		viewer.setOperationName(progressDialogMsg);
	}

	Boolean URLExists(URL fileURL) {
		File f;
		try {
			f = new File(FileLocator.toFileURL(fileURL).getPath());
			return f.exists();
		} catch (IOException e) {
		}
		return false;
	}

}
