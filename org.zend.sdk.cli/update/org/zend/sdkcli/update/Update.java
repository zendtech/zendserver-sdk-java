/*******************************************************************************
 * Copyright (c) Dec 7, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.update;

import java.text.MessageFormat;

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
				System.out.println(MessageFormat.format("{0} -> {1}", manager
						.getSdkVersion().getStringValue(), manager
						.getNewSdkVersion().getStringValue()));
				break;
			case UP_TO_DATE:
				System.out.println(MessageFormat.format(
						"Zend SDK is up-to-date (version {0})", manager
								.getSdkVersion().getStringValue()));
				;
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
