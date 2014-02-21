package org.zend.php.zendserver.deployment.debug.core.jobs;

public class DeploymentEvent {

	private String basePath;
	private String projectName;

	public DeploymentEvent(String projectName, String baseUrl) {
		this.projectName = projectName;
		this.basePath = baseUrl;
	}

	public String getBaseUrl() {
		return basePath;
	}
	
	public String getProjectName() {
		return projectName;
	}
}
