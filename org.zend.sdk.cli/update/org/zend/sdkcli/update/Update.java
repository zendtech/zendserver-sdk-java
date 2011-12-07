/*******************************************************************************
 * Copyright (c) Dec 7, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.update;

import org.zend.sdkcli.update.manager.UpdateManager;

public class Update {

	/**
	 * Main class which is responsible for handling update command:
	 * <code>zend update</code>.
	 * 
	 * @author Wojciech Galanciak, 2011
	 * 
	 */
	public static void main(String[] args) {
		try {
			System.out.println("Updating Zend SDK...");
			UpdateManager manager = new UpdateManager(args[0]);
			UpdateStatus status = UpdateStatus.FAIL;
			status = manager.performUpdate();
			switch (status) {
			case SUCCESS:
				System.out.println("Zend SDK was updated successfully");
				break;
			case UP_TO_DATE:
				System.out.println("Zend SDK is up-to-date");
				break;
			case FAIL:
				System.out.println("Zend SDK was not updated successfully");
				break;
			default:
				break;
			}
		} catch (Exception e) {
			System.out.println("Zend SDK was not updated successfully");
			e.printStackTrace();
		}
	}

}
