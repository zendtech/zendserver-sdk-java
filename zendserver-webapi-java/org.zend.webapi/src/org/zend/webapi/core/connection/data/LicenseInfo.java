/*******************************************************************************
 * Copyright (c) Jan 28, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;

import java.util.Date;

import org.zend.webapi.core.connection.data.values.LicenseInfoStatus;

/**
 * Information about a Zend Server or Zend Server Cluster Manager license
 * @author Roy, 2011
 */
public class LicenseInfo extends AbstractResponseData {

	private static final String LICENSE_INFO = "/licenseInfo";
	
	private LicenseInfoStatus status;
	private String orderNumber;
	private Date validUntil;
	private int limit;
	

	protected LicenseInfo() {
		super(ResponseType.LICENSE_INFO, AbstractResponseData.BASE_PATH
				+ LICENSE_INFO, LICENSE_INFO);
	}

	protected LicenseInfo(String prefix) {
		super(ResponseType.LICENSE_INFO, prefix, LICENSE_INFO);
	}

	/**
	 * @return Licensing status.
	 */
	public LicenseInfoStatus getStatus() {
		return status;
	}

	/**
	 * @return License order number. Empty if no license.
	 */
	public String getOrderNumber() {
		return orderNumber;
	}

	/**
	 * @return License expiration date, Empty if no license.
	 */
	public Date getValidUntil() {
		return validUntil;
	}

	/**
	 * @return serverLimit Integer 1 If this is a ZSCM license, number of
	 *         servers allowed by the license. If not a ZSCM license, value is
	 *         always 0
	 */
	public int getServerLimit() {
		return limit;
	}

	public boolean accept(IResponseDataVisitor visitor) {
		if (visitor.preVisit(this)) {
			return visitor.visit(this);
		}
		return false;
	}

	/**
	 * @param limit the limit to set
	 */
	protected void setLimit(int limit) {
		this.limit = limit;
	}

	/**
	 * @param status the status to set
	 */
	protected void setStatus(LicenseInfoStatus status) {
		this.status = status;
	}

	/**
	 * @param orderNumber the orderNumber to set
	 */
	protected void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	/**
	 * @param validUntil the validUntil to set
	 */
	protected void setValidUntil(Date validUntil) {
		this.validUntil = validUntil;
	}
}
