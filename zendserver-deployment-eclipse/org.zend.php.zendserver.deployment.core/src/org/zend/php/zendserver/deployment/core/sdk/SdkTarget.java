/*******************************************************************************
 * Copyright (c) 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/

package org.zend.php.zendserver.deployment.core.sdk;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.zend.sdklib.target.IZendTarget;
import org.zend.webapi.core.WebApiException;

public class SdkTarget implements IZendTarget {

	private IZendTarget target;

	public SdkTarget(IZendTarget target) {
		super();
		this.target = target;
	}

	public String getSecretKey() {
		return target.getSecretKey();
	}

	public String getKey() {
		return target.getKey();
	}

	public URL getHost() {
		return target.getHost();
	}

	public String getId() {
		return target.getId();
	}

	public boolean connect() throws WebApiException {
		return target.connect();
	}

	public String getProperty(String key) {
		return target.getProperty(key);
	}

	public void load(InputStream stream) throws IOException {
		target.load(stream);
	}

	public void store(OutputStream stream) throws IOException {
		target.store(stream);
	}

}
