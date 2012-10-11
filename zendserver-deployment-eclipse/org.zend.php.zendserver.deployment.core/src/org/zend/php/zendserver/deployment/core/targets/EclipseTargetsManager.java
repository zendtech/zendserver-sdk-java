package org.zend.php.zendserver.deployment.core.targets;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.zend.php.zendserver.deployment.core.DeploymentCore;
import org.zend.sdklib.SdkException;
import org.zend.sdklib.internal.target.OpenShiftTarget;
import org.zend.sdklib.internal.target.ZendDevCloud;
import org.zend.sdklib.manager.TargetException;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;

public class EclipseTargetsManager extends TargetsManager {

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
		// TODO on startup check if there are targets added from command-line

	}

	@Override
	public synchronized IZendTarget add(IZendTarget target,
			boolean suppressConnect) throws TargetException {

		IZendTarget result = super.add(target, suppressConnect);

		if (result != null) {
			EclipseSSH2Settings.registerDevCloudTarget(result, false);
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

		return result;
	}

	@Override
	public synchronized IZendTarget remove(IZendTarget target) {
		return super.remove(target);
		// TODO remove SSH keys for removed targets
	}

	@Override
	public IZendTarget updateTarget(String targetId, String host,
			String defaultServer, String key, String secretKey) {
		return super
				.updateTarget(targetId, host, defaultServer, key, secretKey);
		// TODO update SSH key?
	}
}
