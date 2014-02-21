package org.zend.php.zendserver.deployment.core.tests;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static Activator instance;
	private BundleContext context;
	
	public void start(BundleContext context) throws Exception {
		instance = this;
		this.context = context;
	}

	public void stop(BundleContext context) throws Exception {
		instance = null;
	}
	
	public static Activator getInstance() {
		return instance;
	}
	
	public BundleContext getBundleContext() {
		return context;
	}

}
