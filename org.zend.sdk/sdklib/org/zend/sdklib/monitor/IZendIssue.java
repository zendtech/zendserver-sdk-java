/*******************************************************************************
 * Copyright (c) Feb 20, 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.monitor;

import java.io.File;
import java.util.List;

import org.zend.webapi.core.connection.data.EventsGroupDetails;
import org.zend.webapi.core.connection.data.Issue;
import org.zend.webapi.core.connection.data.IssueDetails;
import org.zend.webapi.core.connection.data.values.IssueStatus;

/**
 * Wrapper for {@link Issue} class. Provides some additional method to retrieve
 * issue details.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public interface IZendIssue {
	
	/**
	 * Returns {@link Issue} instance.
	 * 
	 * @return issue
	 */
	Issue getIssue();
	
	/**
	 * Provides issue details.
	 * 
	 * @return issue details
	 */
	IssueDetails getDetails();

	/**
	 * Changes issue status.
	 * 
	 * @param status
	 *            new status value
	 * @return <code>true</code> if status was changed successfully; otherwise
	 *         return <code>false</code>
	 */
	boolean changeStatus(IssueStatus status);
	
	/**
	 * Provides list of details for each events group connected with this issue
	 * (based on {@link IssueDetails#getEventsGroups()}.
	 * 
	 * @return group details
	 */
	List<EventsGroupDetails> getGroupDetails();
	
	/**
	 * Exports list of issue files.
	 * 
	 * @return list of exported issues
	 */
	List<File> export();
	
	/**
	 * Exports list of issue files.
	 * 
	 * @param destination
	 *            location where exported issues should be stored
	 * @return list of exported issues
	 */
	List<File> export(File destination);
	
}
