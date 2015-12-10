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
import org.zend.php.zendserver.deployment.core.DeploymentCore;
import org.zend.php.zendserver.deployment.core.utils.DeploymentUtils;
import org.zend.sdklib.internal.target.OpenShiftTarget;
import org.zend.sdklib.internal.target.ZendTarget;
import org.zend.sdklib.manager.TargetException;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;
import org.zend.sdklib.target.LicenseExpiredException;

public class EclipseTargetsManager extends TargetsManager {

	private static final String LISTENERS_ELEMENT = "listener"; //$NON-NLS-1$
	private static final String LISTENERS_EXTENSION = DeploymentCore.PLUGIN_ID + ".targetsManagerListener"; //$NON-NLS-1$
	
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
	public IZendTarget add(IZendTarget target,
			boolean suppressConnect) throws TargetException,
			LicenseExpiredException {
		// Create PHP Server associated with this target
		Server server = DeploymentUtils.findExistingServer(target);
		if (server != null) {
			String baseUrl = server.getBaseURL();
			ZendTarget t = (ZendTarget) target;
			try {
				t.setDefaultServerURL(new URL(baseUrl));
				if (target.getServerName() == null) {
					t.setServerName(server.getName());
				}
			} catch (MalformedURLException e) {
				// should not occur
			}
		}
		IZendTarget result = super.add(target, suppressConnect);
		for (ITargetsManagerListener listener : listeners) {
			listener.targetAdded(target);
		}

		return result;
	}

	@Override
	public IZendTarget remove(IZendTarget target) {
		for (ITargetsManagerListener listener : listeners) {
			listener.targetRemoved(target);
		}
		return super.remove(target);
	}

	@Override
	public IZendTarget updateTarget(String targetId, String host,
			String defaultServer, String key, String secretKey)
			throws LicenseExpiredException {
		return super
				.updateTarget(targetId, host, defaultServer, key, secretKey);
		// TODO update SSH key?
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
