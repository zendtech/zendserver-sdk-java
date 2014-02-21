package org.zend.php.zendserver.deployment.core.targets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Status;
import org.eclipse.jsch.internal.core.IConstants;
import org.eclipse.jsch.internal.core.JSchCorePlugin;
import org.zend.php.zendserver.deployment.core.DeploymentCore;
import org.zend.php.zendserver.deployment.core.Messages;
import org.zend.sdklib.internal.target.ZendDevCloud;
import org.zend.sdklib.internal.target.ZendTarget;
import org.zend.sdklib.target.IZendTarget;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPairRSA;

public class EclipseSSH2Settings {

	private static final String PEM = ".pem"; //$NON-NLS-1$
	private static final String KEY_NAME_SEPARATOR = ","; //$NON-NLS-1$

	public static boolean registerDevCloudTarget(IZendTarget target, boolean overwrite) {
		String keyPath = target.getProperty(ZendDevCloud.SSH_PRIVATE_KEY_PATH);
		
		if (keyPath == null) {
			return false;
		}
		
		try {
			String newPath = copySSHKey(keyPath, target.getId(), overwrite);
			if (!keyPath.equals(newPath)) {
				ZendTarget zsTarget = (ZendTarget) target;
				zsTarget.addProperty(ZendDevCloud.SSH_PRIVATE_KEY_PATH, newPath);
				return true;
			}
		} catch (IOException e) {
			// TODO handle error adding key
		}
		return false;
	}
	
	public static void unregisterDevCloudTarget(IZendTarget target) {
		String keyPath = target.getProperty(ZendDevCloud.SSH_PRIVATE_KEY_PATH);
		if (keyPath == null) {
			return;
		}
		Preferences preferences = JSchCorePlugin.getPlugin()
				.getPluginPreferences();
		String ssh2Home = preferences.getString(IConstants.KEY_SSH2HOME);
		String existingPrivateKeys = preferences
				.getString(IConstants.KEY_PRIVATEKEY);
		List<String> existingKeys = new ArrayList<String>(Arrays.asList(existingPrivateKeys
				.split(KEY_NAME_SEPARATOR)));

		File keyFile = new File(keyPath);
		String keyName = keyFile.getName();
		String parent = keyFile.getParent();

		if (parent != null && parent.equals(ssh2Home)) {
			if (existingKeys.contains(keyName)) {
				existingKeys.remove(keyName);
				StringBuilder updatedKey = new StringBuilder();
				for (String key : existingKeys) {
					updatedKey.append(key);
					updatedKey.append(KEY_NAME_SEPARATOR);
				}
				if (updatedKey.toString().endsWith(KEY_NAME_SEPARATOR)) {
					existingPrivateKeys = updatedKey.substring(0,
							updatedKey.length() - 1);
				} else {
					existingPrivateKeys = updatedKey.toString();
				}
				preferences.setValue(IConstants.KEY_PRIVATEKEY,
						existingPrivateKeys);

				JSchCorePlugin.getPlugin().setNeedToLoadKnownHosts(true);
				JSchCorePlugin.getPlugin().setNeedToLoadKeys(true);
				JSchCorePlugin.getPlugin().savePluginPreferences();
			}
		}
	}
	
	public static File getPrivateKey(String type) {
		Preferences preferences = JSchCorePlugin.getPlugin()
				.getPluginPreferences();
		String ssh2Home = preferences.getString(IConstants.KEY_SSH2HOME);
		return new File(ssh2Home, "id_rsa"); //$NON-NLS-1$
	}
	
	public static void createPrivateKey(String type, String path) throws CoreException {
		Assert.isTrue(IConstants.RSA.equals(type));
		
		JSch jsch = JSchCorePlugin.getPlugin().getJSch();
		KeyPairRSA pair = new KeyPairRSA(jsch);
		com.jcraft.jsch.KeyPair pk;
		try {
			pk = pair.genKeyPair(jsch, com.jcraft.jsch.KeyPair.RSA);
		} catch (JSchException e) {
			throw new CoreException(new Status(IStatus.ERROR, DeploymentCore.PLUGIN_ID, e.getMessage(), e));
		}
		
		File dir = new File(path).getParentFile();
		if (!dir.exists()) {
			boolean success = dir.mkdirs();
			if (!success) {
				throw new CoreException(new Status(IStatus.ERROR,
						DeploymentCore.PLUGIN_ID, Messages.bind(
								Messages.EclipseSSH2Settings_CreateFileError,
								new Object[] { path, dir })));
			}
		}
		
		try {
			pk.writePrivateKey(new FileOutputStream(path));
		} catch (FileNotFoundException e) {
			throw new CoreException(new Status(IStatus.ERROR, DeploymentCore.PLUGIN_ID, e.getMessage(), e));
		}
	}

	/**
	 * Copies SSH key file to Eclipse SSH home
	 * 
	 * @param keyPath existing key
	 * @param newNameHint hint to use when inventing new key name
	 * @param overwrite 
	 * @return copied key full path
	 * 
	 * @throws IOException
	 */
	private static String copySSHKey(String keyPath, String newNameHint, boolean overwrite) throws IOException {
		Preferences preferences = JSchCorePlugin.getPlugin()
				.getPluginPreferences();
		String ssh2Home = preferences.getString(IConstants.KEY_SSH2HOME);
		String existingPrivateKeys = preferences.getString(IConstants.KEY_PRIVATEKEY);
		List<String> existingKeys = Arrays.asList(existingPrivateKeys.split(KEY_NAME_SEPARATOR));
		
		File keyFile;
		
		keyFile = new File(keyPath);
		String keyName = keyFile.getName();
		String parent = keyFile.getParent();

		if (parent != null && parent.equals(ssh2Home)) {
			if (existingKeys.contains(keyName)) {
				return keyPath; // key already exists in ssh2Home and is on private keys list
			} else {
				// key is already in ssh2Home, so we'll only add it to the list (later below)
			}
		} else {
			if (existingKeys.contains(keyName) && !overwrite) {
				keyName = newNameHint;
				if (existingKeys.contains(keyName)) {
					int i = 1;
					do {
						keyName = newNameHint + i++;
					} while (existingKeys.contains(keyName));
				}
			}
			// key is in external directory. Let's copy it to ssh2Home
			File newKeyFile = new File(ssh2Home, keyName);
			copyFile(keyFile, newKeyFile);
			keyFile = newKeyFile;
			// if key is already on the list do not add it again
			if (existingKeys.contains(keyName) && overwrite) {
				return newKeyFile.toString();
			}
		}
		
		existingPrivateKeys = existingPrivateKeys + KEY_NAME_SEPARATOR + keyFile.getName();
		preferences.setValue(IConstants.KEY_PRIVATEKEY, existingPrivateKeys);

		JSchCorePlugin.getPlugin().setNeedToLoadKnownHosts(true);
		JSchCorePlugin.getPlugin().setNeedToLoadKeys(true);
		JSchCorePlugin.getPlugin().savePluginPreferences();
		
		return keyFile.toString();
	}

	private static void copyFile(File srcFile, File destFile)
			throws IOException {
		File dir = destFile.getParentFile();
		if (!dir.exists()) {
			boolean success = dir.mkdirs();
			if (!success) {
				throw new IOException(Messages.bind(
						Messages.EclipseSSH2Settings_CopyFileError,
						new Object[] { srcFile, destFile, dir }));
			}
		}

		FileOutputStream fos = new FileOutputStream(destFile);
		FileInputStream fis = new FileInputStream(srcFile);
		
		byte[] buf = new byte[4096];
		int len;
		try {
			while ((len = fis.read(buf)) != -1) {
				fos.write(buf, 0, len);
			}
		} finally {
			fos.close();
			fis.close();
		}
		
	}

	public static String getSSHHome() {
		Preferences preferences = JSchCorePlugin.getPlugin()
				.getPluginPreferences();
		return preferences.getString(IConstants.KEY_SSH2HOME);
	}

}
