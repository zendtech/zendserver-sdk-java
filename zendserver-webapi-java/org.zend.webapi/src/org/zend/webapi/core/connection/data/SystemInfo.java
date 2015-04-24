/*******************************************************************************
 * Copyright (c) Jan 27, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;

import java.util.List;

import org.zend.webapi.core.connection.data.values.SystemEdition;
import org.zend.webapi.core.connection.data.values.SystemStatus;
import org.zend.webapi.core.connection.data.values.WebApiVersion;
import org.zend.webapi.core.connection.data.values.ZendServerVersion;

/**
 * systemInfo Generic information about the system being accessed
 * 
 * @author Roy, 2011
 */
public class SystemInfo extends AbstractResponseData {

	private static final String SYSTEM_INFO = "/systemInfo";
	
	private SystemStatus status;
	private SystemEdition edition;
	private ZendServerVersion version;
	private List<WebApiVersion> supportedApiVersions;
	private String phpVersion;
	private String operatingSystem;
	private LicenseInfo licenseInfo;
	private LicenseInfo managerLicenseInfo;
	private MessageList messageList;

	protected SystemInfo() {
		super(ResponseType.SYSTEM_INFO, AbstractResponseData.BASE_PATH
				+ SYSTEM_INFO, SYSTEM_INFO);
	}

	/**
	 * @return Global status information
	 */
	public SystemStatus getStatus() {
		return status;
	}

	/**
	 * @return Zend Server Edition
	 */
	public SystemEdition getEdition() {
		return edition;
	}

	/**
	 * @return Full version of Zend Server
	 */
	public ZendServerVersion getVersion() {
		return version;
	}

	/**
	 * @return Comma-separated list of supported content types / versions of the
	 *         Zend Server Web API
	 */
	public List<WebApiVersion> getSupportedApiVersions() {
		return supportedApiVersions;
	}

	/**
	 * @return Full PHP version 
	 */
	public String getPhpVersion() {
		return phpVersion;
	}

	/**
	 * @return A string identifying the operating system
	 */
	public String getOperatingSystem() {
		return operatingSystem;
	}

	/**
	 * @return Information about the Zend Server license. If running in cluster,
	 *         will contain the node license information
	 */
	public LicenseInfo getLicenseInfo() {
		return licenseInfo;
	}

	/**
	 * @return Information about the Zend Server Cluster Manager license
	 */
	public LicenseInfo getManagerLicenseInfo() {
		return managerLicenseInfo;
	}

	/**
	 * @return List of messages reported by this server. Can be empty if there
	 *         are no messages to show.
	 */
	public MessageList getMessageList() {
		return messageList;
	}
	
	public boolean accept(IResponseDataVisitor visitor) {
		final boolean preVisit = visitor.preVisit(this);
		if (preVisit) {
			if (this.licenseInfo != null) {
				this.licenseInfo.accept(visitor);
			}
			if (this.managerLicenseInfo != null) {
				this.managerLicenseInfo.accept(visitor);
			}
			if (this.messageList != null) {
				this.messageList.accept(visitor) ;
			}
			return visitor.visit(this);
		}
		
		return false;
	}

	/**
	 * @param status the status to set
	 */
	protected void setStatus(SystemStatus status) {
		this.status = status;
	}

	/**
	 * @param edition the edition to set
	 */
	protected void setEdition(SystemEdition edition) {
		this.edition = edition;
	}

	/**
	 * @param version the version to set
	 */
	protected void setVersion(String version) {
		this.version = ZendServerVersion.byName(version);
	}

	/**
	 * @param supportedApiVersions the supportedApiVersions to set
	 */
	protected void setSupportedApiVersions(List<WebApiVersion> supportedApiVersions) {
		this.supportedApiVersions = supportedApiVersions;
	}

	/**
	 * @param phpVersion the phpVersion to set
	 */
	protected void setPhpVersion(String phpVersion) {
		this.phpVersion = phpVersion;
	}

	/**
	 * @param operatingSystem the operatingSystem to set
	 */
	protected void setOperatingSystem(String operatingSystem) {
		this.operatingSystem = operatingSystem;
	}

	/**
	 * @param licenseInfo the licenseInfo to set
	 */
	protected void setLicenseInfo(LicenseInfo licenseInfo) {
		this.licenseInfo = licenseInfo;
	}

	/**
	 * @param managerLicenseInfo the managerLicenseInfo to set
	 */
	protected void setManagerLicenseInfo(LicenseInfo managerLicenseInfo) {
		this.managerLicenseInfo = managerLicenseInfo;
	}

	/**
	 * @param messageList the messageList to set
	 */
	protected void setMessageList(MessageList messageList) {
		this.messageList = messageList;
	}
}