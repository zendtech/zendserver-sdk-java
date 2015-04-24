/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.ui.actions;

import org.eclipse.jface.action.Action;
import org.zend.php.zendserver.deployment.ui.Activator;

public class HelpAction extends Action {

	private final String helpContextID;

	public HelpAction(String helpContextID) {
		this.helpContextID = helpContextID;
		setText(Messages.HelpAction_0);
		setToolTipText(Messages.HelpAction_0);
		setImageDescriptor(Activator
				.getImageDescriptor(Activator.IMAGE_DESCRIPTOR_HELP));
	}

	public void run() {
		org.eclipse.swt.program.Program.launch(helpContextID);
	}
}
