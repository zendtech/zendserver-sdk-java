package org.zend.php.zendserver.deployment.core.tunnel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.zend.php.zendserver.deployment.core.tunnel.AbstractSSHTunnel.State;
import org.zend.sdklib.internal.target.OpenShiftTarget;
import org.zend.sdklib.internal.target.ZendDevCloud;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;

import com.jcraft.jsch.JSchException;

public class SSHTunnelManager {

	private static SSHTunnelManager manager;

	private Map<IZendTarget, AbstractSSHTunnel> targets;

	private SSHTunnelManager() {
		this.targets = new HashMap<IZendTarget, AbstractSSHTunnel>();
	}

	public static SSHTunnelManager getManager() {
		if (manager == null) {
			manager = new SSHTunnelManager();
		}
		return manager;
	}

	public State connect(IZendTarget target) throws TunnelException, JSchException {
		if (!isTargetSupported(target)) {
			return null;
		}
		AbstractSSHTunnel tunnel = getTunnel(target);
		boolean init = false;
		if (tunnel == null) {
			if (TargetsManager.isPhpcloud(target)) {
				String user = getUsername(target);
				String privateKey = getSSHPrivateKey(target);
				if (user == null || user.length() == 0 || privateKey == null
						|| privateKey.length() == 0) {
					return State.NOT_SUPPORTED;
				}
				tunnel = new ZendDevCloudTunnel(user, privateKey);
				targets.put(target, tunnel);
				init = true;
			} else if (TargetsManager.isOpenShift(target)) {
				String user = getUuid(target);
				String privateKey = getSSHPrivateKey(target);
				if (user == null || user.length() == 0 || privateKey == null
						|| privateKey.length() == 0) {
					return State.NOT_SUPPORTED;
				}
				String internalHost = target.getProperty(OpenShiftTarget.TARGET_INTERNAL_HOST);
				String host = target.getDefaultServerURL().getHost();
				tunnel = new OpenShiftTunnel(user, host, internalHost, privateKey);
				targets.put(target, tunnel);
				init = true;
			}
		}
		return connect(tunnel, target, init);
	}

	public void disconnect(IZendTarget target) {
		AbstractSSHTunnel tunnel = targets.get(target);
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
		AbstractSSHTunnel tunnel = targets.get(target);
		if (tunnel != null) {
			return true;
		}
		return false;
	}

	public int getDatabasePort(IZendTarget target) {
		if (isTargetSupported(target)) {
			AbstractSSHTunnel tunnel = targets.get(target);
			if (tunnel != null) {
				return tunnel.getDatabasePort();
			}
		}
		return -1;
	}

	private AbstractSSHTunnel getTunnel(IZendTarget target) {
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
		if (TargetsManager.isOpenShift(target)) {
			return target.getProperty(OpenShiftTarget.SSH_PRIVATE_KEY_PATH);
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

	private String getUuid(IZendTarget target) {
		if (TargetsManager.isOpenShift(target)) {
			return target.getProperty(OpenShiftTarget.TARGET_UUID);
		}
		return null;
	}

	private State connect(AbstractSSHTunnel tunnel, IZendTarget target,
			boolean init) throws TunnelException, JSchException {
		if (init) {
			try {
				return tunnel.connect();
			} catch (TunnelException e) {
				tunnel.disconnect();
				targets.remove(target);
				throw e;
			}
		} else {
			if (!tunnel.isConnected()) {
				try {
					return tunnel.connect();
				} catch (Exception e) {
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

	private boolean isTargetSupported(IZendTarget target) {
		if (TargetsManager.isPhpcloud(target)
				|| TargetsManager.isOpenShift(target)) {
			return true;
		}
		return false;
	}

}
