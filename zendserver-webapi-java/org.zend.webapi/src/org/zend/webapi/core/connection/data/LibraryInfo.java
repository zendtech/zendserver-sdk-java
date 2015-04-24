/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;

/**
 * Information about a specific deployed library.
 * 
 * @author Wojciech Galanciak, 2013
 * 
 */
public class LibraryInfo extends AbstractResponseData {

	private static final String LIBRARY_INFO = "/libraryInfo";
	
	private int libraryId;
	private String libraryName;
	private String status;
	private LibraryVersions libraryVersions;

	protected LibraryInfo(String prefix, int occurrence) {
		super(ResponseType.LIBRARY_INFO, prefix, LIBRARY_INFO, occurrence);
	}

	protected LibraryInfo() {
		this(BASE_PATH + LIBRARY_INFO, 0);
	}

	public int getLibraryId() {
		return libraryId;
	}

	public String getLibraryName() {
		return libraryName;
	}

	public String getStatus() {
		return status;
	}

	public LibraryVersions getLibraryVersions() {
		return libraryVersions;
	}

	public boolean accept(IResponseDataVisitor visitor) {
		boolean visit = visitor.preVisit(this);
		if (visit) {
			if (this.getLibraryVersions() != null) {
				this.getLibraryVersions().accept(visitor);
			}
			return visitor.visit(this);
		}
		return false;
	}

	protected void setLibraryId(int libraryId) {
		this.libraryId = libraryId;
	}

	protected void setLibraryName(String libraryName) {
		this.libraryName = libraryName;
	}

	protected void setStatus(String status) {
		this.status = status;
	}

	protected void setLibraryVersions(LibraryVersions libraryVersions) {
		this.libraryVersions = libraryVersions;
	}

}
