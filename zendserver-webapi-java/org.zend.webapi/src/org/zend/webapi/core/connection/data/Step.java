/*******************************************************************************
 * Copyright (c) Feb 12, 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;

/**
 * List of backtrace entry properties. Backtrace elements show up in a list of
 * backtraces in which order is important.
 * 
 * @author Wojciech Galanciak, 2012
 * @since 1.2
 */
public class Step extends AbstractResponseData {

	private static final String STEP = "/step";
	
	private int number;
	private String objectId;
	private String classId;
	private String function;
	private String file;
	private int line;

	protected Step() {
		super(ResponseType.STEP, BASE_PATH + STEP, STEP);
	}

	protected Step(String prefix, int occurrance) {
		super(ResponseType.STEP, prefix, STEP, occurrance);
	}

	public boolean accept(IResponseDataVisitor visitor) {
		if (visitor.preVisit(this)) {
			return visitor.visit(this);
		}
		return false;
	}

	/**
	 * @return Sequential numbering of the backtrace steps
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * @return Object identifier
	 */
	public String getObjectId() {
		return objectId;
	}

	/**
	 * @return Object class identifier
	 */
	public String getClassId() {
		return classId;
	}

	/**
	 * @return Function or method name
	 */
	public String getFunction() {
		return function;
	}

	/**
	 * @return file path
	 */
	public String getFile() {
		return file;
	}

	/**
	 * @return Line number in the file
	 */
	public int getLine() {
		return line;
	}

	protected void setNumber(int number) {
		this.number = number;
	}

	protected void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	protected void setClassId(String classId) {
		this.classId = classId;
	}

	protected void setFunction(String function) {
		this.function = function;
	}

	protected void setFile(String file) {
		this.file = file;
	}

	protected void setLine(int line) {
		this.line = line;
	}

}
