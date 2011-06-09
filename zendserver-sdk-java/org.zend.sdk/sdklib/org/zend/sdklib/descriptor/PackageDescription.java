/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.descriptor;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBException;

import org.zend.sdklib.descriptor.pkg.Package;
import org.zend.sdklib.internal.utils.JaxbHelper;

/**
 * Represents the general information for packaging description
 * 
 * @author Roy, 2011
 */
public class PackageDescription {

	private final Package pkg;

	public PackageDescription(InputStream packageStream) throws IOException,
			JAXBException {
		if (packageStream == null) {
			throw new IllegalArgumentException("packageStream must not be null");
		}

		pkg = JaxbHelper.unmarshalPackage(packageStream);
	}

	/**
	 * @return the deployment descriptor
	 */
	public Package getPackage() {
		return pkg;

	}

	/**
	 * @return the deployment descriptor
	 */
	public Object getPackageProperties() {
		throw new UnsupportedOperationException("implement me!");
	}

}
