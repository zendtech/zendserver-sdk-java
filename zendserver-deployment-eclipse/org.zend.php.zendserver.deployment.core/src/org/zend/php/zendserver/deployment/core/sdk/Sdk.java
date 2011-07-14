package org.zend.php.zendserver.deployment.core.sdk;

import java.io.File;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;
import org.zend.php.zendserver.deployment.core.DeploymentCore;


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
		new SdkBundle("org.restlet", "/lib/org.restlet.jar"),
		new SdkBundle("org.restlet.ext.xml", "/lib/org.restlet.ext.xml.jar"),
		new SdkBundle("org.apache.commons.cli", "/lib/commons-cli-1.2.jar"),
		new SdkBundle("org.zend.webapi.core", "/lib/zend-webapi.jar"),
		new SdkBundle("org.zend.sdk", "/lib/org.zend.sdk.jar"),
	};
	
	private String location;

	public Sdk(String location) {
		this.location = location;
	}
	
	String getLocation() {
		return location;
	}
	
	public void install() throws BundleException {
		for (int i = 0; i < BUNDLES.length; i++) {
			String bundlePath = "file://" + location + BUNDLES[i].sdkLocation; //$NON-NLS-1$
			DeploymentCore.log(new Status(IStatus.INFO, DeploymentCore.PLUGIN_ID, "Installing "+bundlePath));
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
		Bundle bundle = ctx.getBundle(bundlePath);
		return (bundle != null);
	}

	public void uninstall() throws BundleException {
		for (int i = 0; i < BUNDLES.length; i++) {
			Bundle bundle = DeploymentCore.getContext().getBundle(location + BUNDLES[i]);
			if (bundle != null) {
				bundle.uninstall();
			}
		}
	}
	
	public String validate() {
		for (int i = 0; i < BUNDLES.length; i++) {
			if (!new File(location, BUNDLES[i].sdkLocation).exists()) {
				return "SDK Location is not valid. Missing file " + BUNDLES[i];
			}
		}
		
		return null;
	}
	
}
