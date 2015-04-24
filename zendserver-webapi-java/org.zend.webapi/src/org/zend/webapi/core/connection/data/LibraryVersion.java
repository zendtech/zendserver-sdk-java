/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;

/**
 * Information about a specific library version.
 * 
 * @author Wojciech Galanciak, 2013
 * 
 */
public class LibraryVersion extends AbstractResponseData {

	private static final String LIBRARY_VERSION = "/libraryVersion";
	
	private int libraryVersionId;
	private String version;
	private String status;
	private String installedLocation;
	private boolean isDefinedLibrary;
	private String creationTime;
	private String creationTimeTimestamp;
	private String lastUsed;
	private String lastUsedTimestamp;
	private LibraryServers servers;

	protected LibraryVersion(String prefix, int occurrence) {
		super(ResponseType.LIBRARY_VERSION, prefix, LIBRARY_VERSION, occurrence);
	}

	protected LibraryVersion() {
		this(BASE_PATH + LIBRARY_VERSION, 0);
	}

	public int getLibraryVersionId() {
		return libraryVersionId;
	}

	public String getVersion() {
		return version;
	}

	public String getStatus() {
		return status;
	}

	public String getInstalledLocation() {
		return installedLocation;
	}

	public boolean isDefinedLibrary() {
		return isDefinedLibrary;
	}

	public String getCreationTime() {
		return creationTime;
	}

	public String getLastUsed() {
		return lastUsed;
	}

	public String getCreationTimeTimestamp() {
		return creationTimeTimestamp;
	}

	public String getLastUsedTimestamp() {
		return lastUsedTimestamp;
	}

	public LibraryServers getServers() {
		return servers;
	}

	public boolean accept(IResponseDataVisitor visitor) {
		if (visitor.preVisit(this)) {
			if (getServers() != null) {
				getServers().accept(visitor);
			}
			return visitor.visit(this);
		}
		return false;
	}

	protected void setLibraryVersionId(int libraryVersionId) {
		this.libraryVersionId = libraryVersionId;
	}

	protected void setVersion(String version) {
		this.version = version;
	}

	protected void setStatus(String status) {
		this.status = status;
	}

	protected void setInstalledLocation(String installedLocation) {
		this.installedLocation = installedLocation;
	}

	protected void setDefinedLibrary(boolean isDefinedLibrary) {
		this.isDefinedLibrary = isDefinedLibrary;
	}

	protected void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}

	protected void setLastUsed(String lastUsed) {
		this.lastUsed = lastUsed;
	}

	protected void setCreationTimeTimestamp(String creationTimeTimestamp) {
		this.creationTimeTimestamp = creationTimeTimestamp;
	}

	protected void setLastUsedTimestamp(String lastUsedTimestamp) {
		this.lastUsedTimestamp = lastUsedTimestamp;
	}

	protected void setServers(LibraryServers servers) {
		this.servers = servers;
	}

}
