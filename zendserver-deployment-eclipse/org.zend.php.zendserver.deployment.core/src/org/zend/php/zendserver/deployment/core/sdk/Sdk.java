package org.zend.php.zendserver.deployment.core.sdk;

import java.io.File;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.zend.php.zendserver.deployment.core.DeploymentCore;


public class Sdk {

	private static final String[] BUNDLES = {
		"/lib/org.restlet.jar",
		"/lib/org.restlet.ext.xml.jar",
		"/lib/log4j-1.2.16.jar",
		"/lib/commons-cli-1.2.jar",
		"/lib/zend-webapi.jar",
		"/lib/zend-sdk.jar",
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
			DeploymentCore.getContext().installBundle(location + BUNDLES[i]);
		}
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
			if (!new File(location, BUNDLES[i]).exists()) {
				return "SDK Location is not valid. Missing file " + BUNDLES[i];
			}
		}
		
		return null;
	}
	
}
