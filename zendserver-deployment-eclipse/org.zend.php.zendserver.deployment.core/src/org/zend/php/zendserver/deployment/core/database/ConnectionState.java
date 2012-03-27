/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.core.database;

import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.zend.php.zendserver.deployment.core.Messages;

/**
 * Represents possible connection state for phpcloud database.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public enum ConnectionState {

	CONNECTED(IConnectionProfile.CONNECTED_STATE,
			Messages.ConnectionState_Connected),

	DISCONNECTED(IConnectionProfile.DISCONNECTED_STATE,
			Messages.ConnectionState_Disconnected),

	WORKING_OFFLINE(IConnectionProfile.WORKING_OFFLINE_STATE,
			Messages.ConnectionState_WorkingOffline),

	UNAVAILABLE(-1, Messages.ConnectionState_Unavailable),

	UNKNOWN(-1, ""); //$NON-NLS-1$

	private final int state;
	private final String label;

	private ConnectionState(int state, String label) {
		this.state = state;
		this.label = label;
	}

	public static ConnectionState byState(int state) {
		if (state == -1) {
			return UNKNOWN;
		}
		ConnectionState[] values = values();
		for (ConnectionState connectionState : values) {
			if (connectionState.state == state) {
				return connectionState;
			}
		}
		return UNKNOWN;
	}

	public String getLabel() {
		return label;
	}

}