/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;

import java.util.List;

/**
 * A list of library versions.
 * 
 * @author Wojciech Galanciak, 2013
 * 
 */
public class LibraryVersions extends AbstractResponseData {

	private static final String LIBRARY_VERSIONS = "/libraryVersions";
	
	private List<LibraryVersion> versions;

	protected LibraryVersions() {
		super(ResponseType.LIBRARY_VERSIONS, BASE_PATH + LIBRARY_VERSIONS,
				LIBRARY_VERSIONS);
	}

	protected LibraryVersions(String prefix, int occurrance) {
		super(ResponseType.LIBRARY_VERSIONS, prefix, LIBRARY_VERSIONS,
				occurrance);
	}

	public List<LibraryVersion> getVersions() {
		return versions;
	}

	public boolean accept(IResponseDataVisitor visitor) {
		boolean visit = visitor.preVisit(this);
		if (visit) {
			if (getVersions() != null) {
				for (LibraryVersion version : getVersions()) {
					version.accept(visitor);
				}
			}
			return visitor.visit(this);
		}
		return false;
	}

	protected void setVersions(List<LibraryVersion> versions) {
		this.versions = versions;
	}

}
