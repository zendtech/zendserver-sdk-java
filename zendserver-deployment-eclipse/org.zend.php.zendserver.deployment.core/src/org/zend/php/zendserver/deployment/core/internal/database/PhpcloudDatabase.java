/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.core.internal.database;

import org.zend.php.zendserver.deployment.core.database.TargetsDatabaseManager;
import org.zend.php.zendserver.deployment.core.tunnel.SSHTunnelManager;
import org.zend.sdklib.target.IZendTarget;

/**
 * @author Wojciech Galanciak, 2012
 * 
 */
public class PhpcloudDatabase extends TargetDatabase {

	public PhpcloudDatabase(IZendTarget target, TargetsDatabaseManager manager) {
		super(target, manager);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.php.zendserver.deployment.core.internal.database.TargetDatabase
	 * #getUrl()
	 */
	protected String getUrl() {
		int port = SSHTunnelManager.getManager().getDatabasePort(
				target.getHost().getHost());
		return PROTOCOL + DEFAULT_HOST + ':' + port + '/' + getDatabaseName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.php.zendserver.deployment.core.internal.database.TargetDatabase
	 * #getUsername()
	 */
	protected String getUsername() {
		return getContainerName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.php.zendserver.deployment.core.internal.database.TargetDatabase
	 * #getDatabaseName()
	 */
	protected String getDatabaseName() {
		return getContainerName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.php.zendserver.deployment.core.internal.database.TargetDatabase
	 * #getProfilePrefix()
	 */
	protected String getProfilePrefix() {
		return "phpcloud container"; //$NON-NLS-1$
	}

	private String getContainerName() {
		String host = target.getHost().getHost();
		if (host != null && host.length() > 0) {
			int index = host.indexOf('.');
			return index != -1 ? host.substring(0, index) : null;
		}
		return null;
	}

}
