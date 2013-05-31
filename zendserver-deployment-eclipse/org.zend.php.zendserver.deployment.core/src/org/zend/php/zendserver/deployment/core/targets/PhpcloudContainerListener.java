/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.core.targets;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.zend.php.zendserver.deployment.core.DeploymentCore;
import org.zend.sdklib.SdkException;
import org.zend.sdklib.internal.target.ZendDevCloud;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.request.IRequest;
import org.zend.webapi.core.service.IRequestListener;

/**
 * Phpcloud container listener which allows to detect if container is in
 * sleeping mode and if it is, it wakes it up.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class PhpcloudContainerListener implements IRequestListener {

	// 3h 30 minutes
	private static final int UPTIME = 12600000;

	private static final String ID = DeploymentCore.PLUGIN_ID
			+ ".phpCloudContainer"; //$NON-NLS-1$
	
	private static Map<String, Long> timestamps = new HashMap<String, Long>();
	
	static {
		timestamps = Collections
				.synchronizedMap(new HashMap<String, Long>());
	}

	private class PasswordProvider implements Runnable {

		private String password;
		private IZendTarget target;

		public PasswordProvider(IZendTarget target) {
			super();
			this.target = target;
		}

		public void run() {
			PhpcloudPasswordDialog dialog = new PhpcloudPasswordDialog(Display
					.getDefault().getActiveShell(), target);
			if (dialog.open() == Window.OK) {
				this.password = dialog.getPassword();
			}
		}

		public String getPassword() {
			return password;
		}

	}

	public boolean perform(IRequest request) throws WebApiException {
		URL url = null;
		try {
			url = new URL(request.getHost());
		} catch (MalformedURLException e) {
			return false;
		}
		String host = url.getHost();
		if (TargetsManager.isPhpcloud(host)) {
			IZendTarget target = getTarget(host);
			Long timestamp = timestamps.get(target.getId());
			if (timestamp != null) {
				Long currentTime = System.currentTimeMillis();
				if (currentTime - timestamp < UPTIME) {
					addTimestamp(target.getId(), currentTime);
					return true;
				}
			}
			ZendDevCloud devcloud = new ZendDevCloud();
			String container = target
					.getProperty(ZendDevCloud.TARGET_CONTAINER);
			String username = target.getProperty(ZendDevCloud.TARGET_USERNAME);
			String password = target.getProperty(ZendDevCloud.TARGET_PASSWORD);
			if (password == null) {
				password = TargetsManagerService.INSTANCE
						.getPhpcloudPassword(target);
			}
			if (password == null) {
				password = getPassword(target);
			}
			try {
				if (password != null && container != null && username != null
						&& devcloud.wakeUp(container, username, password)) {
					addTimestamp(target.getId(), System.currentTimeMillis());
					return true;
				}
			} catch (SdkException e) {
				new ContainerAwakeningException();
			}
		}
		return false;
	}

	public String getId() {
		return ID;
	}

	private IZendTarget getTarget(String host) {
		IZendTarget[] targets = TargetsManagerService.INSTANCE
				.getTargetManager().getTargets();
		for (IZendTarget target : targets) {
			if (host.equals(target.getHost().getHost())) {
				return target;
			}
		}
		return null;
	}

	private String getPassword(IZendTarget target) {
		PasswordProvider provider = new PasswordProvider(target);
		Display.getDefault().syncExec(provider);
		return provider.getPassword();
	}
	
	private static synchronized void addTimestamp(String targetId,
			Long timestamp) {
		timestamps.put(targetId, timestamp);
	}

}
