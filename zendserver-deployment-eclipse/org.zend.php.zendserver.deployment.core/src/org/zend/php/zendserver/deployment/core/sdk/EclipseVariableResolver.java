/*******************************************************************************
 * Copyright (c) May 26, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.core.sdk;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.zend.sdklib.SdkException;
import org.zend.sdklib.mapping.IVariableResolver;

/**
 * Implemenation of {@link IVariableResolver} which use Eclipse variables mechanism.
 * 
 * @author Wojciech Galanciak, 2012
 *
 */
public class EclipseVariableResolver implements IVariableResolver {
	
	private IStringVariableManager manager;
	
	public EclipseVariableResolver() {
		this.manager = VariablesPlugin.getDefault().getStringVariableManager();
	}

	public String resolve(String path) throws SdkException {
		try {
			return manager.performStringSubstitution(path);
		} catch (CoreException e) {
			throw new SdkException(e);
		}
	}

}
