/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.core.targets;

import java.net.MalformedURLException;

import org.zend.php.zendserver.deployment.core.DeploymentCore;
import org.zend.sdklib.internal.target.SSLContextInitializer;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;
import org.zend.webapi.core.WebApiClient;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.auth.BasicCredentials;
import org.zend.webapi.core.connection.auth.WebApiCredentials;
import org.zend.webapi.core.connection.data.values.ServerType;
import org.zend.webapi.core.connection.data.values.WebApiVersion;
import org.zend.webapi.core.connection.data.values.ZendServerVersion;
import org.zend.webapi.core.service.IRequestListener;
import org.zend.webapi.internal.core.connection.exception.InvalidResponseException;

/**
 * Phpcloud container listener which allows to detect if container is in
 * sleeping mode and if it is, it wakes it up.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class PhpcloudContainerListener implements IRequestListener {

	private static final String ID = DeploymentCore.PLUGIN_ID
			+ ".phpCloudContainer"; //$NON-NLS-1$

	private IZendTarget target;

	public PhpcloudContainerListener(String targetId) {
		this.target = TargetsManagerService.INSTANCE.getTargetManager()
				.getTargetById(targetId);
	}

	public PhpcloudContainerListener(IZendTarget target) {
		super();
		this.target = target;
	}

	public boolean perform() {
		if (TargetsManager.isPhpcloud(target)) {
			try {
				WebApiClient client = getClient(target);

				while (true) {
					try {
						client.getSystemInfo();
						return true;
					} catch (InvalidResponseException e) {
						// container is sleeping or it is zs6
						boolean isZS6 = testZendServer6(client);
						if (isZS6) {
							return true;
						}
						continue;
					} catch (WebApiException e) {
						DeploymentCore.log(e);
						break;
					}
				}
			} catch (MalformedURLException e) {
				DeploymentCore.log(e);
			}
		}
		return true;
	}
	
	private boolean testZendServer6(WebApiClient client) {
		client.setCustomVersion(WebApiVersion.V1_3);
		client.setServerType(ServerType.ZEND_SERVER);
		try {
			client.getSystemInfo();
			return true;
		} catch (InvalidResponseException e) {
		} catch (WebApiException e) {
			DeploymentCore.log(e);
			return true;
		}
		client.setCustomVersion(null);
		client.setServerType(ServerType.ZEND_SERVER_MANAGER);
		return false;
	}

	public WebApiClient getClient(IZendTarget target)
			throws MalformedURLException {
		WebApiCredentials credentials = new BasicCredentials(target.getKey(),
				target.getSecretKey());
		String hostname = target.getHost().toString();
		WebApiClient client = new WebApiClient(credentials, hostname,
				SSLContextInitializer.instance.getRestletContext());
		client.disableListeners();
		if (ZendServerVersion
				.byName(target.getProperty(IZendTarget.SERVER_VERSION))
				.getName().startsWith("6")) { //$NON-NLS-1$
			client.setCustomVersion(WebApiVersion.V1_3);
			client.setServerType(ServerType.ZEND_SERVER);
		}
		if (TargetsManager.isOpenShift(target)) {
			client.setServerType(ServerType.ZEND_SERVER);
		}
		return client;
	}

	public String getId() {
		return ID + '.' + target.getId();
	}

}
