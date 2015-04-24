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
 * A list of 0 or more libraries.
 * 
 * @author Wojciech Galanciak, 2013
 * 
 */
public class LibraryList extends AbstractResponseData {

	private static final String LIBRARY_LIST = "/libraryList";
	
	private List<LibraryInfo> librariesInfo;

	protected LibraryList() {
		super(ResponseType.LIBRARY_LIST, BASE_PATH + LIBRARY_LIST, LIBRARY_LIST);
	}

	protected LibraryList(String prefix) {
		super(ResponseType.LIBRARY_LIST, LIBRARY_LIST, prefix);
	}

	public List<LibraryInfo> getLibrariesInfo() {
		return librariesInfo;
	}

	public boolean accept(IResponseDataVisitor visitor) {
		boolean visit = visitor.preVisit(this);
		if (visit) {
			if (getLibrariesInfo() != null) {
				for (LibraryInfo info : getLibrariesInfo()) {
					info.accept(visitor);
				}
			}
			return visitor.visit(this);
		}
		return false;
	}

	protected void setLibrariesInfo(List<LibraryInfo> librariesInfo) {
		this.librariesInfo = librariesInfo;
	}

}
