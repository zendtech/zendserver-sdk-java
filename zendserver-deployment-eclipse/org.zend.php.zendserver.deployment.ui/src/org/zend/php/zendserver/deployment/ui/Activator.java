package org.zend.php.zendserver.deployment.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.zend.php.zendserver.deployment.ui"; //$NON-NLS-1$

	public static final String IMAGE_RUN_APPLICATION = "icons/obj16/run_exc.gif"; //$NON-NLS-1$
	public static final String IMAGE_DEBUG_APPLICATION = "icons/obj16/debug_exc.gif"; //$NON-NLS-1$
	public static final String IMAGE_RUN_TEST = "icons/obj16/test_application.gif"; //$NON-NLS-1$
	public static final String IMAGE_EXPORT_APPLICATION = "icons/obj16/bundle_obj.gif"; //$NON-NLS-1$

	public static final String IMAGE_DESCRIPTOR_OVERVIEW = "icons/obj16/overview_obj.gif"; //$NON-NLS-1$
	public static final String IMAGE_DESCRIPTOR_PARAMETERS = "icons/sample.gif"; //$NON-NLS-1$
	public static final String IMAGE_DESCRIPTOR_PREREQS = "icons/sample.gif"; //$NON-NLS-1$
	public static final String IMAGE_DESCRIPTOR_REMOVAL = "icons/sample.gif"; //$NON-NLS-1$
	public static final String IMAGE_DESCRIPTOR_VARIABLES = "icons/sample.gif"; //$NON-NLS-1$

	public static final String IMAGE_PHP = "icons/obj16/dependency_obj.gif"; //$NON-NLS-1$
	public static final String IMAGE_PHP_EXTENSION = "icons/obj16/dependency_obj.gif"; //$NON-NLS-1$
	public static final String IMAGE_PHP_DIRECTIVE = "icons/obj16/dependency_obj.gif"; //$NON-NLS-1$
	public static final String IMAGE_ZENDSERVER = "icons/obj16/dependency_obj.gif"; //$NON-NLS-1$
	public static final String IMAGE_ZENDFRAMEWORK = "icons/obj16/dependency_obj.gif"; //$NON-NLS-1$
	public static final String IMAGE_ZENDSERVERCOMPONENT = "icons/obj16/dependency_obj.gif"; //$NON-NLS-1$

	public static final String IMAGE_PARAMTYPE_PASSWORD = "icons/obj16/category_obj.gif"; //$NON-NLS-1$
	public static final String IMAGE_PARAMTYPE_STRING = "icons/obj16/category_obj.gif"; //$NON-NLS-1$
	public static final String IMAGE_PARAMTYPE_NUMBER = "icons/obj16/category_obj.gif"; //$NON-NLS-1$
	public static final String IMAGE_PARAMTYPE_CHOICE = "icons/obj16/category_obj.gif"; //$NON-NLS-1$
	public static final String IMAGE_PARAMTYPE_CHECKBOX = "icons/obj16/category_obj.gif"; //$NON-NLS-1$
	public static final String IMAGE_PARAMTYPE_HOSTNAME = "icons/obj16/category_obj.gif"; //$NON-NLS-1$
	public static final String IMAGE_PARAMTYPE_EMAIL = "icons/obj16/category_obj.gif"; //$NON-NLS-1$
	public static final String IMAGE_PARAMTYPE_UNKNOWN = "icons/obj16/category_obj.gif"; //$NON-NLS-1$

	public static final String IMAGE_DEPLOY_WIZARD = "icons/wizban/deploy_wiz.png"; //$NON-NLS-1$
	public static final String IMAGE_EXPORT_WIZARD = "icons/wizban/export_wiz.png"; //$NON-NLS-1$

	public static final String IMAGE_VARIABLE = "icons/obj16/category_obj.gif";; //$NON-NLS-1$

	public static final String IMAGE_SCRIPT = "icons/obj16/script_obj.gif"; //$NON-NLS-1$
	public static final String IMAGE_SCRIPT_NOTEXISTS = "icons/obj16/script_dis_obj.gif"; //$NON-NLS-1$
	public static final String IMAGE_SCRIPT_TYPE = "icons/obj16/req_obj.gif"; //$NON-NLS-1$

	public static final String IMAGE_DESCRIPTOR_HELP = "icons/obj16/help.gif"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	private FormColors formColors;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public FormColors getFormColors(Display display) {
		if (formColors == null) {
			formColors = new FormColors(display);
			formColors.markShared();
		}
		return formColors;
	}

	public Image getImage(String path) {
		Image image = getImageRegistry().get(path);
		if (image == null) {
			getImageRegistry().put(path, getImageDescriptor(path));
			image = getImageRegistry().get(path);
		}

		return image;
	}

	public Image createWorkspaceImage(String path, int height) {
		Image image = null;
		ImageDescriptor id = ImageDescriptor.createFromFile(null, path);
		int origH = id.getImageData().height;
		int origW = id.getImageData().width;
		int width = origW * height / origH;
		if (id != null) {
			Image tmpImage = id.createImage();
			image = resize(tmpImage, width, height);
		}

		return image;
	}

	private Image resize(Image image, int width, int height) {
		Image scaled = new Image(Display.getDefault(), width, height);
		GC gc = new GC(scaled);
		gc.setAntialias(SWT.ON);
		gc.setInterpolation(SWT.HIGH);
		gc.drawImage(image, 0, 0, image.getBounds().width,
				image.getBounds().height, 0, 0, width, height);
		gc.dispose();
		image.dispose(); // don't forget about me!
		return scaled;
	}
}
