/*******************************************************************************
 * Copyright (c) Jun 28, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.mapping.validator;

/**
 * Represents error which occurred during resource mapping properties parsing.
 * It consists information about line where error occurred, offest in it and the
 * error message ({@link MappingParseMessage}).
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class MappingParseStatus {

	private int line;
	private int start;
	private int end;
	private MappingParseMessage message;

	public MappingParseStatus(int line, int start, int end, MappingParseMessage message) {
		super();
		this.line = line;
		this.start = start;
		this.end = end;
		this.message = message;
	}

	/**
	 * @return line number where error occurred
	 */
	public int getLine() {
		return line;
	}

	/**
	 * @return char index where error starts
	 */
	public int getStart() {
		return start;
	}

	/**
	 * @return char index where error ends
	 */
	public int getEnd() {
		return end;
	}

	/**
	 * @return message for the error
	 */
	public String getMessage() {
		return message.getMessage();
	}

}
