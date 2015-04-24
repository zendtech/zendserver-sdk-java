/*******************************************************************************
 * Copyright (c) Feb 17, 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.internal.application;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Random;

import org.zend.sdklib.application.PackageBuilder;
import org.zend.sdklib.internal.library.AbstractChangeNotifier;
import org.zend.sdklib.internal.target.SSLContextInitializer;
import org.zend.sdklib.internal.target.UserBasedTargetLoader;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.mapping.IMappingLoader;
import org.zend.sdklib.mapping.IVariableResolver;
import org.zend.sdklib.target.ITargetLoader;
import org.zend.sdklib.target.IZendTarget;
import org.zend.webapi.core.WebApiClient;
import org.zend.webapi.core.connection.auth.BasicCredentials;
import org.zend.webapi.core.connection.auth.WebApiCredentials;

/**
 * Abstract class which provides interface to perform WebAPI methods.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public abstract class ZendConnection extends AbstractChangeNotifier {

	private final TargetsManager manager;
	private IMappingLoader mappingLoader;

	public ZendConnection() {
		super();
		manager = new TargetsManager(new UserBasedTargetLoader());
	}

	public ZendConnection(IMappingLoader mappingLoader) {
		this();
		this.mappingLoader = mappingLoader;
	}

	public ZendConnection(ITargetLoader loader) {
		super();
		manager = new TargetsManager(loader);
	}

	public ZendConnection(ITargetLoader loader, IMappingLoader mappingLoader) {
		this(loader);
		this.mappingLoader = mappingLoader;
	}

	/**
	 * @param targetId
	 *            - target id
	 * @return instance of a WebAPI client for specified target id. If target
	 *         does not exist, it returns <code>null</code>
	 * @throws MalformedURLException
	 */
	public WebApiClient getClient(String targetId) throws MalformedURLException {
		IZendTarget target = getTargetById(targetId);
		if (target == null) {
			final String er = "Target with id '" + targetId
					+ "' does not exist.";
			log.error(er);
			throw new IllegalArgumentException(er);
		}
		WebApiCredentials credentials = new BasicCredentials(target.getKey(),
				target.getSecretKey());
		String hostname = target.getHost().toString();
		WebApiClient client = new WebApiClient(credentials, hostname,
				SSLContextInitializer.instance.getRestletContext(), notifier);
		client.setCustomVersion(target.getWebApiVersion());
		client.setServerType(target.getServerType());
		return client;
	}

	protected IZendTarget getTargetById(String targetId) {
		return manager.getTargetById(targetId);
	}

	protected PackageBuilder getPackageBuilder(String path,
			IVariableResolver variableResolver) {
		PackageBuilder builder = null;
		if (mappingLoader == null) {
			builder = new PackageBuilder(new File(path));
		} else {
			builder = new PackageBuilder(new File(path), mappingLoader, this);
		}
		if (variableResolver != null) {
			builder.setVariableResolver(variableResolver);
		}
		return builder;
	}

	protected PackageBuilder getPackageBuilder(String path,
			String configLocation, IVariableResolver variableResolver) {
		PackageBuilder builder = null;
		if (mappingLoader == null) {
			builder = new PackageBuilder(new File(path), new File(
					configLocation));
		} else {
			builder = new PackageBuilder(new File(path), new File(
					configLocation), mappingLoader, this);
		}
		if (variableResolver != null) {
			builder.setVariableResolver(variableResolver);
		}
		return builder;
	}

	protected void closeStream(Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				log.error(e);
			}
		}
	}

	protected File getTempFolder() {
		String tempDir = System.getProperty("java.io.tmpdir");
		File tempFile = new File(tempDir + File.separator
				+ new Random().nextLong());
		if (!tempFile.exists()) {
			tempFile.mkdir();
		}
		return tempFile;
	}

}
