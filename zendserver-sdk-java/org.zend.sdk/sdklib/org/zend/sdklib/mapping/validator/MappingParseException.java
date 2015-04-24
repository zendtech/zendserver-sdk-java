/*******************************************************************************
 * Copyright (c) Jun 28, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.mapping.validator;

import java.util.List;

/**
 * Exception which is thrown if any error occurs during resource mapping
 * properites parsing.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class MappingParseException extends Exception {

	/**
	 * Generated serial no.
	 */
	private static final long serialVersionUID = 4065791409030750636L;

	private List<MappingParseStatus> errors;

	public MappingParseException(List<MappingParseStatus> errors) {
		this.errors = errors;
	}

	/**
	 * @return list of {@link MappingParseStatus} instances. Each of them is
	 *         related to the one error which occured during resource mapping
	 *         properties parsing.
	 */
	public List<MappingParseStatus> getErrors() {
		return errors;
	}

}
