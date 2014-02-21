package org.zend.php.zendserver.deployment.core.sdk;

import java.io.File;
import java.text.MessageFormat;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;
import org.zend.php.zendserver.deployment.core.DeploymentCore;
import org.zend.php.zendserver.deployment.core.Messages;


public class Sdk {

	public static class SdkBundle {
		public String name;
		public String sdkLocation;
		public SdkBundle(String name, String sdkLocation) {
			this.name = name;
			this.sdkLocation = sdkLocation;
		}
	}
	
	private static final SdkBundle[] BUNDLES = {
		new SdkBundle("org.zend.sdk", "/lib/org.zend.sdk.jar"), //$NON-NLS-1$ //$NON-NLS-2$
	};
	
	private String location;

	public Sdk(String location) {
		this.location = location;
	}
	
	String getLocation() {
		return location;
	}
	
	public void install() throws BundleException {
		if (location != null) {
			for (int i = 0; i < BUNDLES.length; i++) {
				String bundlePath = "file://" + location + BUNDLES[i].sdkLocation; //$NON-NLS-1$
				//DeploymentCore.log(new Status(IStatus.INFO, DeploymentCore.PLUGIN_ID, "Installing "+bundlePath));
				try {
					if (!bundleIsInstalled(bundlePath) && !bundleByNameIsInstalled(BUNDLES[i].name)) {
						Bundle bundle = DeploymentCore.getContext().installBundle(bundlePath);
						bundle.start();
					}
				} catch (BundleException e) {
					System.err.println("Error loading bundle "+bundlePath); //$NON-NLS-1$
					e.printStackTrace();
					throw e;
				}
			}
		}
		
		new LoggerInitializer().initialize();
	}
	
	private boolean bundleByNameIsInstalled(String name) {
		BundleContext ctx = DeploymentCore.getContext();
	    ServiceReference ref = ctx.getServiceReference(
	        org.osgi.service.packageadmin.PackageAdmin.class.getName());
	    PackageAdmin pa = (ref == null) ? null : 
	        (PackageAdmin) ctx.getService(ref);
	    Bundle[] bundles = pa.getBundles(name, null);
		return bundles != null && bundles.length > 0;
	}

	private boolean bundleIsInstalled(String bundlePath) {
		BundleContext ctx = DeploymentCore.getContext();
		//Bundle bundle = ctx.getBundle(bundlePath); // commented-out for comatibility with eclipse 3.6
		//return (bundle != null);
		return true;
	}

	public void uninstall() throws BundleException {
		//for (int i = 0; i < BUNDLES.length; i++) {
		//	Bundle bundle = DeploymentCore.getContext().getBundle(location + BUNDLES[i]); // commented-out for compatibility with eclipse 3.6
		//	if (bundle != null) {
		//		bundle.uninstall();
		//	}
		//}
	}
	
	public String validate() {
		for (int i = 0; i < BUNDLES.length; i++) {
			if (!new File(location, BUNDLES[i].sdkLocation).exists()) {
				return MessageFormat.format(Messages.Sdk_InvalidLocation,
						BUNDLES[i]);
			}
		}

		return null;
	}
	
}
