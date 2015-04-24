/*******************************************************************************
 * Copyright (c) Jun 28, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.mapping.validator;

/**
 * Represents messages for all possible resource mapping parsing errors.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public enum MappingParseMessage {

	INVALID_KEY("Invalid key syntax"),

	INVALID_LINE("Invalid property syntax"),

	INVALID_FOLDER("Invalid folder name"),

	INVALID_SUFFIX("Invalid suffix"),

	NOT_EXIST("File does not exist"),

	EMPTY_MAPPING_FILE("Empty mapping file entry"),

	EMPTY_MAPPING("Empty mapping value"),

	EMPTY_FILE("Empty properties file"),

	NO_APPDIR("No appdir include declaration"),

	CANNOT_READ("Cannot read the properties file"),

	INVALID_LIBRARY("Invalid library mapping value");

	private String message;

	private MappingParseMessage(String message) {
		this.message = message;
	}

	/**
	 * @return error message
	 */
	public String getMessage() {
		return message;
	}

}
