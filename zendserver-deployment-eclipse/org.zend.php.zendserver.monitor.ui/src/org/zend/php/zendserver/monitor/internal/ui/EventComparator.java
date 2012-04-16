/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.monitor.internal.ui;

import org.zend.core.notifications.ui.IComparator;

/**
 * Implementation of {@link IComparator} for event notifications.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class EventComparator implements IComparator {

	private String rule;
	private String source;

	public EventComparator(String rule, String source) {
		super();
		this.rule = rule;
		this.source = source;
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
			return rule.equals(c.rule) && source.equals(c.source);
		}
		return false;
	}

}
