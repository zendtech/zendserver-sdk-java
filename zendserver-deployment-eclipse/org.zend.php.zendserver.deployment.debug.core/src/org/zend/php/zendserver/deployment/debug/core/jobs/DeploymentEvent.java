package org.zend.php.zendserver.deployment.debug.core.jobs;

public class DeploymentEvent {

	private String basePath;

	public DeploymentEvent(String basePath) {
		this.basePath = basePath;
	}

	public String getBasePath() {
		return basePath;
	}
}
