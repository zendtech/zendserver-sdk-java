package org.zend.php.zendserver.deployment.core.targets;

import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Arrays;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Status;
import org.eclipse.jsch.internal.core.IConstants;
import org.eclipse.jsch.internal.core.JSchCorePlugin;
import org.zend.php.zendserver.deployment.core.DeploymentCore;
import org.zend.sdklib.internal.target.ZendDevCloud;
import org.zend.sdklib.target.IZendTarget;

public class EclipseSSH2Settings {

	public static void registerDevCloudTarget(IZendTarget target) {
		String keyPath = target.getProperty(ZendDevCloud.SSH_PRIVATE_KEY_PATH);
		String targetId = target.getId();
		
		if (keyPath == null) {
			return;
		}
		
		try {
			addSSHKey(targetId, keyPath);
		} catch (IOException e) {
			// TODO handle error adding key
		}
	}
	
	public static File getPrivateKey(String type) {
		Preferences preferences = JSchCorePlugin.getPlugin()
				.getPluginPreferences();
		String ssh2Home = preferences.getString(IConstants.KEY_SSH2HOME);
		return new File(ssh2Home, "id_rsa");
	}
	
	public static byte[] createPrivateKey(String type) throws CoreException {
		KeyPairGenerator gen;
		try {
			gen = KeyPairGenerator.getInstance(type);
		} catch (NoSuchAlgorithmException e) {
			throw new CoreException(new Status(IStatus.ERROR, DeploymentCore.PLUGIN_ID, e.getMessage(), e));
		}
		KeyPair pair = gen.generateKeyPair();
		PublicKey pk = pair.getPublic();
		return pk.getEncoded();
	}

	private static void addSSHKey(String keyName, String keyPath) throws IOException {
		Preferences preferences = JSchCorePlugin.getPlugin()
				.getPluginPreferences();
		String ssh2Home = preferences.getString(IConstants.KEY_SSH2HOME);
		String privateKey = preferences.getString(IConstants.KEY_PRIVATEKEY);
		
		// TODO What to do? Move the key to ssh2home or what?
		String[] keys = privateKey.split(","); //$NON-NLS-1$
		if (Arrays.asList(keys).contains(keyName)) {
			return;
		}
		
		privateKey = privateKey + "," + keyName;
		
		preferences.setValue(IConstants.KEY_PRIVATEKEY, privateKey);

		JSchCorePlugin.getPlugin().setNeedToLoadKnownHosts(true);
		JSchCorePlugin.getPlugin().setNeedToLoadKeys(true);
		JSchCorePlugin.getPlugin().savePluginPreferences();
	}

}
