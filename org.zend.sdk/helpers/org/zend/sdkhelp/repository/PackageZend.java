/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkhelp.repository;

import java.io.File;

import org.zend.sdkcli.internal.logger.CliLogger;
import org.zend.sdklib.application.PackageBuilder;
import org.zend.sdklib.logger.Log;

/**
 * @author roy
 * 
 */
public class PackageZend {

	public static void main(String[] args) {
		Log.getInstance().registerLogger(new CliLogger());
		System.out.println(args[0] + " " + args[1]);
		
		PackageBuilder b = new PackageBuilder(new File(args[0]));
		b.createDeploymentPackage(args[1]);

	}

}
