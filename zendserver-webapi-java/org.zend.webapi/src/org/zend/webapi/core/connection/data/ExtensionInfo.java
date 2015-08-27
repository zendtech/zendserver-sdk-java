/*******************************************************************************
 * Copyright (c) 2015 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;

/**
 * Server extension info.
 * 
 * @author Bartlomiej Laczkowski
 */
public class ExtensionInfo extends AbstractResponseData {

	private static final String EXTENSION = "/extension"; //$NON-NLS-1$

	private String name;
	private String version;
	private String type;
	private String status;
	private String shortDescription;
	private String longDescription;
	private MessageList messageList;
	private boolean loaded;
	private boolean installed;
	private boolean builtIn;
	private boolean dummy;
	private boolean restartRequired;

	protected ExtensionInfo(String prefix, int occurrence) {
		super(ResponseType.EXTENSION_INFO, prefix, EXTENSION, occurrence);
	}

	protected ExtensionInfo() {
		this(BASE_PATH + EXTENSION, 0);
	}

	@Override
	public boolean accept(IResponseDataVisitor visitor) {
		boolean visit = visitor.preVisit(this);
		if (visit) {
			return visitor.visit(this);
		}
		return false;
	}

	/**
	 * Returns extension name
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns extension version
	 * 
	 * @return version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Returns extension type
	 * 
	 * @return type
	 */
	public String getExtensionType() {
		return type;
	}

	/**
	 * Returns extension status
	 * 
	 * @return status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Returns extension short description
	 * 
	 * @return short description
	 */
	public String getShortDescription() {
		return shortDescription;
	}

	/**
	 * Returns extension long description
	 * 
	 * @return long description
	 */
	public String getLongDescription() {
		return longDescription;
	}

	/**
	 * Returns extension messages list
	 * 
	 * @return messages list
	 */
	public MessageList getMessageList() {
		return messageList;
	}

	/**
	 * Returns extension "loaded" state
	 * 
	 * @return <code>true</code> if extension is loaded, <code>false</code>
	 *         otherwise
	 */
	public boolean isLoaded() {
		return loaded;
	}

	/**
	 * Returns extension "installed" state
	 * 
	 * @return <code>true</code> if extension is installed, <code>false</code>
	 *         otherwise
	 */
	public boolean isInstalled() {
		return installed;
	}

	/**
	 * Returns extension "builtIn" state
	 * 
	 * @return <code>true</code> if extension is built-in, <code>false</code>
	 *         otherwise
	 */
	public boolean isBuiltIn() {
		return builtIn;
	}

	/**
	 * Returns extension "dummy" state
	 * 
	 * @return <code>true</code> if extension is dummy, <code>false</code>
	 *         otherwise
	 */
	public boolean isDummy() {
		return dummy;
	}

	/**
	 * Returns extension "restart required" state
	 * 
	 * @return <code>true</code> if restart is required, <code>false</code>
	 *         otherwise
	 */
	public boolean isRestartRequired() {
		return restartRequired;
	}

	protected void setName(String name) {
		this.name = name;
	}

	protected void setVersion(String version) {
		this.version = version;
	}

	protected void setExtensionType(String type) {
		this.type = type;
	}

	protected void setStatus(String status) {
		this.status = status;
	}

	protected void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	protected void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}

	protected void setMessageList(MessageList messageList) {
		this.messageList = messageList;
	}

	protected void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	protected void setInstalled(boolean installed) {
		this.installed = installed;
	}

	protected void setBuiltIn(boolean builtIn) {
		this.builtIn = builtIn;
	}

	protected void setDummy(boolean dummy) {
		this.dummy = dummy;
	}

	protected void setRestartRequired(boolean restartRequired) {
		this.restartRequired = restartRequired;
	}

}
