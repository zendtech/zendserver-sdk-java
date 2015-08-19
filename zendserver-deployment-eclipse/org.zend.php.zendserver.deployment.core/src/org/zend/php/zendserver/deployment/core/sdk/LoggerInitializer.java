package org.zend.php.zendserver.deployment.core.sdk;

import org.zend.php.zendserver.deployment.core.DeploymentCore;
import org.zend.sdklib.logger.ILogger;
import org.zend.sdklib.logger.Log;

/**
 * Initializes ZendSDK Logger
 * 
 * This initialization code needs not be referenced directly e.g. in bundle activator because
 * imported interfaces may not be available yet.
 */
public class LoggerInitializer {

	public void initialize() {
		Log.getInstance().registerLogger(new ILogger() {
			
			public void warning(Object message) {
				DeploymentCore.logError(message.toString());
			}
			
			public void info(Object message) {
				DeploymentCore.logError(message.toString());
			}
			
			public ILogger getLogger(String creatorName, boolean verbose) {
				return this;
			}
			
			public void error(Object message) {
				DeploymentCore.logError(message.toString());
			}
			
			public void debug(Object message) {
				DeploymentCore.logError(message.toString());
			}
		});
		
	}

}
