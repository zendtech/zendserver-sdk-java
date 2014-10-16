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
import org.zend.sdklib.internal.target.OpenShiftTarget;
import org.zend.sdklib.target.IZendTarget;

/**
 * @author Wojciech Galanciak, 2012
 * 
 */
public class OpenShiftDatabase extends TargetDatabase {

	private static final String DEFAULT_USERNAME = "admin"; //$NON-NLS-1$

	public OpenShiftDatabase(IZendTarget target, TargetsDatabaseManager manager) {
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
		String username = target.getProperty(OpenShiftTarget.MYSQL_USERNAME);
		if (username == null) {
			username = DEFAULT_USERNAME;
		}
		return username;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.php.zendserver.deployment.core.internal.database.TargetDatabase
	 * #getDatabaseName()
	 */
	protected String getDatabaseName() {
		return target.getProperty(OpenShiftTarget.TARGET_CONTAINER);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.php.zendserver.deployment.core.internal.database.TargetDatabase
	 * #getProfilePrefix()
	 */
	protected String getProfilePrefix() {
		return "OpenShift server"; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.php.zendserver.deployment.core.internal.database.TargetDatabase
	 * #getPassword()
	 */
	protected String getPassword() {
		return target.getProperty(OpenShiftTarget.MYSQL_PASSWORD);
	}

}
