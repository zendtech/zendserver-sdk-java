package org.zend.php.zendserver.deployment.debug.core.jobs;

import java.util.ArrayList;
import java.util.List;

public class DeploymentEventsService {

	private List<DeploymentEventListener> listeners = new ArrayList<DeploymentEventListener>();
	
	private static DeploymentEventsService instance;
	
	public static DeploymentEventsService getInstance() {
		if (instance == null) {
			instance = new DeploymentEventsService();
		}
		return instance;
	}
	
	public void addDeploymentEventListener(DeploymentEventListener listener) {
		listeners.add(listener);
	}
	
	public void removeDeploymentEventListener(DeploymentEventListener listener) {
		listeners.remove(listener);
	}
	
	void fireEvent(DeploymentEvent event) {
		for (DeploymentEventListener listener : listeners) {
			listener.onEvent(event);
		}
	}
}
