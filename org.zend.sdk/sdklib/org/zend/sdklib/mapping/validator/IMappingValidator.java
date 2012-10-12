/*******************************************************************************
 * Copyright (c) Jun 28, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.mapping.validator;

import java.io.InputStream;

import org.zend.sdklib.mapping.IVariableResolver;

/**
 * Interface for parsing and validating resource mapping properites file.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public interface IMappingValidator {

	/**
	 * Parses provided properties input stream. All errors are collected and if
	 * their number is greater than 0 then {@link MappingParseException} is
	 * thrown.
	 * 
	 * @param stream
	 * @return
	 * @throws MappingParseException
	 */
	boolean parse(InputStream stream) throws MappingParseException;

	public void setVariableResolver(IVariableResolver variableResolver);

}
