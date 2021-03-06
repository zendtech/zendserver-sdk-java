/*******************************************************************************
 * Copyright (c) Feb 20, 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.internal.commands;

import org.zend.sdkcli.internal.options.Option;
import org.zend.sdklib.internal.target.UserBasedTargetLoader;
import org.zend.sdklib.monitor.ZendMonitor;

/**
 * Abstract command class for all monitoring related commands.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public abstract class AbstractMonitorCommand extends AbstractCommand {

	private static final String TARGET = "t";

	@Option(opt = TARGET, required = true, description = "Target ID", argName = "target id")
	public String getTarget() {
		return getValue(TARGET);
	}

	protected ZendMonitor getMonitor() {
		String targetId = getTarget();
		return new ZendMonitor(targetId, new UserBasedTargetLoader());
	}

}
