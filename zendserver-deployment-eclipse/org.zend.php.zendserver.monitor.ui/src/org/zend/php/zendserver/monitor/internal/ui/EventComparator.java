/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.monitor.internal.ui;

import org.zend.core.notifications.ui.IComparator;
import org.zend.sdklib.monitor.IZendIssue;

/**
 * Implementation of {@link IComparator} for event notifications.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class EventComparator implements IComparator {

	private IZendIssue issue;

	public EventComparator(IZendIssue issue) {
		super();
		this.issue = issue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.core.notifications.ui.IComparator#equals(org.zend.core.notifications
	 * .ui.IComparator)
	 */
	public boolean equals(IComparator comparator) {
		if (comparator instanceof EventComparator) {
			EventComparator c = (EventComparator) comparator;
			return issue.equals(c.issue);
		}
		return false;
	}

}
