/*******************************************************************************
 * Copyright (c) 2015 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;

/**
 * Server's configuration directive info.
 * 
 * @author Bartlomiej Laczkowski
 */
public class DirectiveInfo extends AbstractResponseData {

	private static final String DIRECTIVE = "/directive"; //$NON-NLS-1$

	private String name;
	private String section;
	private String type;
	private String fileValue;
	private String defaultValue;
	private String previousValue;
	private String units;
	private ListValues listValues;

	protected DirectiveInfo(String prefix, int occurrence) {
		super(ResponseType.DIRECTIVE_INFO, prefix, DIRECTIVE, occurrence);
	}

	protected DirectiveInfo() {
		this(BASE_PATH + DIRECTIVE, 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.webapi.core.connection.data.IResponseData#accept(org.zend.webapi
	 * .core.connection.data.IResponseDataVisitor)
	 */
	@Override
	public boolean accept(IResponseDataVisitor visitor) {
		boolean visit = visitor.preVisit(this);
		if (visit) {
			if (getListValues() != null) {
				getListValues().accept(visitor);
			}
			return visitor.visit(this);
		}
		return false;
	}

	/**
	 * Returns directive name.
	 * 
	 * @return directive name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns directive section.
	 * 
	 * @return directive section
	 */
	public String getSection() {
		return section;
	}

	/**
	 * Returns directive type.
	 * 
	 * @return directive type
	 */
	public String getDirectiveType() {
		return type;
	}

	/**
	 * Returns directive file value.
	 * 
	 * @return directive file value
	 */
	public String getFileValue() {
		return fileValue;
	}

	/**
	 * Returns directive default value.
	 * 
	 * @return directive default value
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * Returns directive previous value.
	 * 
	 * @return directive previous value
	 */
	public String getPreviousValue() {
		return previousValue;
	}

	/**
	 * Returns directive units.
	 * 
	 * @return directive units
	 */
	public String getUnits() {
		return units;
	}

	/**
	 * Returns directive list values.
	 * 
	 * @return directive list values
	 */
	public ListValues getListValues() {
		return listValues;
	}

	protected void setName(String name) {
		this.name = name;
	}

	protected void setSection(String section) {
		this.section = section;
	}

	protected void setDirectiveType(String type) {
		this.type = type;
	}

	protected void setFileValue(String fileValue) {
		this.fileValue = fileValue;
	}

	protected void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	protected void setPreviousValue(String previousValue) {
		this.previousValue = previousValue;
	}

	protected void setUnits(String units) {
		this.units = units;
	}

	protected void setListValues(ListValues listValues) {
		this.listValues = listValues;
	}

}
