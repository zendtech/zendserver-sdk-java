/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.monitor.core;

import org.zend.sdklib.monitor.IZendIssue;

/**
 * Implementors should provide user interface part for notifying user about
 * server events.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public interface INotificationProvider {

	/**
	 * Displays notification for event retirieved from a server.
	 * 
	 * @param issue
	 *            Zend Server issue
	 * @param targetId
	 *            target id
	 * @param eventSource
	 *            source of the event
	 */
	void showNonification(IZendIssue issue, String targetId,
			EventSource eventSource);

}
