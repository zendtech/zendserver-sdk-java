/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;

/**
 * A zpk archive file containing library code and package configuration.
 * 
 * @author Wojciech Galanciak, 2013
 */
public class LibraryFile implements IResponseData {

	private String fileName;

	private long fileSize;

	private byte[] content;

	public boolean accept(IResponseDataVisitor visitor) {
		if (visitor.preVisit(this)) {
			return visitor.visit(this);
		}
		return false;
	}

	public ResponseType getType() {
		return ResponseType.LIBRARY_FILE;
	}

	/**
	 * @return a suggested file name for the library file
	 */
	public String getFilename() {
		return fileName;
	}

	/**
	 * @return size of content
	 */
	public long getFileSize() {
		return fileSize;
	}

	/**
	 * @return content of issue file
	 */
	public byte[] getFileContent() {
		return content;
	}

	protected void setFilename(String filename) {
		this.fileName = filename;
	}

	protected void setFileSize(long size) {
		this.fileSize = size;
	}

	protected void setFileContent(byte[] content) {
		this.content = content;
	}

}
