/*******************************************************************************
 * Copyright (c) Jan 27, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;

/**
 * Configuration file of a server
 * 
 * @author Roy, 2011
 */
public class ServerConfig implements IResponseData {
	/**
	 * specifying a suggested file name for the configuration snapshot file.
	 */
	private String fileName;

	/**
	 * configuration file size
	 */
	private long fileSize;

	/**
	 * Configuration file content
	 */
	private byte[] content;

	public boolean accept(IResponseDataVisitor visitor) {
		if (visitor.preVisit(this)) {
			return visitor.visit(this);
		}
		return false;
	}

	public ResponseType getType() {
		return ResponseType.SERVER_CONFIG;
	}

	/**
	 * @param filename
	 */
	protected void setFilename(String filename) {
		this.fileName = filename;
	}

	/**
	 * @return a suggested file name for the configuration snapshot file
	 */
	public String getFilename() {
		return fileName;
	}

	/**
	 * @param size
	 */
	protected void setFileSize(long size) {
		this.fileSize = size;
	}

	/**
	 * @return sixe of content
	 */
	public long getFileSize() {
		return fileSize;
	}

	/**
	 * @param content
	 */
	protected void setFileContent(byte[] content) {
		this.content = content;
	}

	/**
	 * @return content of configuration file
	 */
	public byte[] getFileContent() {
		return content;
	}
}
