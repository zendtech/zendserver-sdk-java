/*******************************************************************************
 * Copyright (c) Feb 13, 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.test.server.response;


public class FileResponse extends ServerResponse {

	private String fileName;
	private long fileSize;
	private byte[] content;

	public FileResponse(int code, String fileName, long fileSize,
			byte[] content) {
		super(code);
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.content = content;
	}

	public String getFileName() {
		return fileName;
	}

	public long getFileSize() {
		return fileSize;
	}

	public byte[] getContent() {
		return content;
	}

}
