package org.zend.php.zendserver.deployment.core.targets;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.server.core.manager.ServersManager;
import org.zend.php.zendserver.deployment.core.DeploymentCore;
import org.zend.sdklib.SdkException;
import org.zend.sdklib.internal.target.OpenShiftTarget;
import org.zend.sdklib.internal.target.ZendDevCloud;
import org.zend.sdklib.internal.target.ZendTarget;
import org.zend.sdklib.manager.TargetException;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;

public class EclipseTargetsManager extends TargetsManager {

	private static final String LISTENERS_ELEMENT = "listener"; //$NON-NLS-1$
	private static final String LISTENERS_EXTENSION = DeploymentCore.PLUGIN_ID + ".targetsManagerListener"; //$NON-NLS-1$
	private static final String ZENDSERVER_PORT_KEY = "zendserver_default_port"; //$NON-NLS-1$

	private ITargetsManagerListener[] listeners;
	
	public EclipseTargetsManager() {
		super();
		IEclipsePreferences prefs = InstanceScope.INSTANCE
				.getNode(DeploymentCore.PLUGIN_ID);
		OpenShiftTarget.iniLibraServer(prefs.get(
				OpenShiftTarget.LIBRA_SERVER_PROP,
				OpenShiftTarget.getDefaultLibraServer()));
		OpenShiftTarget.iniLibraDomain(prefs.get(
				OpenShiftTarget.LIBRA_DOMAIN_PROP,
				OpenShiftTarget.getDefaultLibraDomain()));
		this.listeners = getManagerListeners();
		// TODO on startup check if there are targets added from command-line
	}
	
	@Override
	protected void load() {
		super.load();
		IZendTarget[] targets = getTargets();
		for (IZendTarget target : targets) {
			if (isPhpcloud(target)) {
				if (EclipseSSH2Settings.registerDevCloudTarget(target, false)) {
					updateTarget(target, true);
				}
			}
		}
	}

	@Override
	public synchronized IZendTarget add(IZendTarget target,
			boolean suppressConnect) throws TargetException {
		// Create PHP Server associated with this target
		Server server = findExistingServer(target);
		if (server != null) {
			String baseUrl = server.getBaseURL();
			ZendTarget t = (ZendTarget) target;
			try {
				t.setDefaultServerURL(new URL(baseUrl));
			} catch (MalformedURLException e) {
				// should not occur
			}
		}

		IZendTarget result = super.add(target, suppressConnect);

		if (result != null
				&& EclipseSSH2Settings.registerDevCloudTarget(result, false)) {
			updateTarget(result, true);
		}

		ZendDevCloud cloud = new ZendDevCloud();
		if (cloud.isCloudTarget(target)) {
			cloud.setPublicKeyBuilder(new JSCHPubKeyDecryptor());
			try {
				cloud.uploadPublicKey(target);
			} catch (SdkException e) {
				throw new TargetException(e);
			}
		}
		
		for (ITargetsManagerListener listener : listeners) {
			listener.targetAdded(target);
		}

		return result;
	}

	@Override
	public synchronized IZendTarget remove(IZendTarget target) {
		for (ITargetsManagerListener listener : listeners) {
			listener.targetRemoved(target);
		}
		EclipseSSH2Settings.unregisterDevCloudTarget(target);
		return super.remove(target);
	}

	@Override
	public IZendTarget updateTarget(String targetId, String host,
			String defaultServer, String key, String secretKey) {
		return super
				.updateTarget(targetId, host, defaultServer, key, secretKey);
		// TODO update SSH key?
	}
	
	@SuppressWarnings("restriction")
	public static Server findExistingServer(IZendTarget target) {
		try {
			URL baseURL = target.getDefaultServerURL();
			Server[] servers = ServersManager.getServers();
			for (Server server : servers) {
				URL serverBaseURL = new URL(server.getBaseURL());
				if (serverBaseURL.getHost().equals(baseURL.getHost())) {
					if ((serverBaseURL.getPort() == baseURL.getPort())
							|| (isDefaultPort(serverBaseURL) && isDefaultPort(baseURL))) {
						return server;
					} else {
						String zsPort = server.getAttribute(
								ZENDSERVER_PORT_KEY, "-1"); //$NON-NLS-1$
						if (Integer.valueOf(zsPort) == baseURL.getPort()) {
							return server;
						}
					}
				}
			}
			return createPHPServer(target.getDefaultServerURL(), target.getId());
		} catch (MalformedURLException e) {
			DeploymentCore.log(e);
			// do nothing and return null
		}
		return null;
	}
	
	protected static boolean isDefaultPort(URL url) {
		int port = url.getPort();
		if (port == -1 || port == 80) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("restriction")
	protected static Server createPHPServer(URL baseURL, String targetId) {
		try {
			URL url = new URL(baseURL.getProtocol(), baseURL.getHost(),
					baseURL.getPort(), ""); //$NON-NLS-1$
			String urlString = url.toString();
			Server server = new Server(
					"Zend Target (id: " + targetId + " host: " + url.getHost() //$NON-NLS-1$ //$NON-NLS-2$
							+ ")", urlString, urlString, ""); //$NON-NLS-1$ //$NON-NLS-2$
			ServersManager.addServer(server);
			ServersManager.save();
			return server;
		} catch (MalformedURLException e) {
			DeploymentCore.log(e);
			// ignore, verified earlier
		}
		return null;
	}
	
	private static ITargetsManagerListener[] getManagerListeners() {
		IConfigurationElement[] elements = Platform
				.getExtensionRegistry()
				.getConfigurationElementsFor(
						LISTENERS_EXTENSION);
		List<ITargetsManagerListener> result = new ArrayList<ITargetsManagerListener>();
		for (IConfigurationElement element : elements) {
			if (LISTENERS_ELEMENT.equals(element.getName())) {
				try {
					Object listener = element
							.createExecutableExtension("class"); //$NON-NLS-1$
					if (listener instanceof ITargetsManagerListener) {
						result.add((ITargetsManagerListener) listener);
						break;
					}
				} catch (CoreException e) {
					DeploymentCore.log(e);
				}
			}
		}
		return result.toArray(new ITargetsManagerListener[result.size()]);
	}

}
