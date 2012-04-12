package org.zend.php.zendserver.deployment.core.tunnel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.zend.php.zendserver.deployment.core.tunnel.ZendDevCloudTunnel.State;
import org.zend.sdklib.internal.target.ZendDevCloud;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;

public class ZendDevCloudTunnelManager {

	private static ZendDevCloudTunnelManager manager;

	private Map<IZendTarget, ZendDevCloudTunnel> targets;

	private ZendDevCloudTunnelManager() {
		this.targets = new HashMap<IZendTarget, ZendDevCloudTunnel>();
	}

	public static ZendDevCloudTunnelManager getManager() {
		if (manager == null) {
			manager = new ZendDevCloudTunnelManager();
		}
		return manager;
	}

	public State connect(IZendTarget target) throws IOException {
		ZendDevCloudTunnel tunnel = getTunnel(target);
		String user = getUsername(target);
		String privateKey = getSSHPrivateKey(target);
		if (user == null || user.length() == 0 || privateKey == null || privateKey.length() == 0) {
			return State.NOT_SUPPORTED;
		}
		boolean init = false;
		if (tunnel == null) {
			tunnel = new ZendDevCloudTunnel(user, privateKey);
			targets.put(target, tunnel);
			init = true;
		}
		return connect(tunnel, target, init);
	}

	public void disconnect(IZendTarget target) {
		ZendDevCloudTunnel tunnel = targets.get(target);
		if (tunnel != null) {
			tunnel.disconnect();
		}
	}

	public void disconnectAll() {
		Set<IZendTarget> targetsSet = targets.keySet();
		for (IZendTarget target : targetsSet) {
			disconnect(target);
		}
	}
	
	public boolean isAvailable(IZendTarget target) {
		ZendDevCloudTunnel tunnel = targets.get(target);
		if (tunnel != null) {
			return true;
		}
		return false;
	}

	public int getDatabasePort(IZendTarget target) {
		ZendDevCloudTunnel tunnel = targets.get(target);
		if (tunnel != null) {
			return tunnel.getDatabasePort();
		}
		return -1;
	}

	private ZendDevCloudTunnel getTunnel(IZendTarget target) {
		Set<IZendTarget> targetsSet = targets.keySet();
		for (IZendTarget t : targetsSet) {
			if (target.getHost().getHost().equals(t.getHost().getHost())) {
				return targets.get(t);
			}
		}
		return null;
	}

	private String getSSHPrivateKey(IZendTarget target) {
		if (TargetsManager.isPhpcloud(target)) {
			return target.getProperty(ZendDevCloud.SSH_PRIVATE_KEY_PATH);
		}
		return null;
	}

	private String getUsername(IZendTarget target) {
		if (TargetsManager.isPhpcloud(target)) {
			String host = target.getHost().getHost();
			return host.substring(0, host.indexOf('.'));
		}
		return null;
	}

	private State connect(ZendDevCloudTunnel tunnel, IZendTarget target,
			boolean init) throws IOException {
		if (init) {
			return tunnel.connect();
		} else {
			if (!tunnel.isConnected()) {
				try {
					return tunnel.connect();
				} catch (IOException e) {
					// If tunnel exists but it is disconnected and failed to
					// connect it again
					// try to remove old tunnel and create a new one for it
					targets.remove(target);
					return connect(target);
				}
			}
		}
		return State.CONNECTED;
	}

}
