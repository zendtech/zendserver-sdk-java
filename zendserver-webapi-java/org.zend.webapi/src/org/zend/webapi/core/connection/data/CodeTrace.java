/*******************************************************************************
 * Copyright (c) Feb 12, 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;

/**
 * A single Code trace file's set of properties.
 * 
 * @author Wojciech Galanciak, 2012
 * @since 1.2
 */
public class CodeTrace extends AbstractResponseData {

	private static final String CODE_TRACE = "/codeTrace";
	
	private String id;
	private long date;
	private String url;
	private String createdBy;
	private int filesize;
	private int applicationId;

	protected CodeTrace() {
		super(ResponseType.CODE_TRACE, BASE_PATH + CODE_TRACE, CODE_TRACE);
	}

	protected CodeTrace(String prefix, int occurrance) {
		super(ResponseType.CODE_TRACE, prefix, CODE_TRACE, occurrance);
	}

	public boolean accept(IResponseDataVisitor visitor) {
		if (visitor.preVisit(this)) {
			return visitor.visit(this);
		}
		return false;
	}

	/**
	 * @return Sequential numbering of the backtrace steps
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return Creation timestamp
	 */
	public long getDate() {
		return date;
	}

	/**
	 * @return URL string that created the trace
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @return Method of creation (Code Request, Manual Request, Monitor Event)
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * @return File size in bytes
	 */
	public int getFilesize() {
		return filesize;
	}

	/**
	 * @return Application context for the trace-file
	 */
	public int getApplicationId() {
		return applicationId;
	}

	protected void setId(String id) {
		this.id = id;
	}

	protected void setDate(long date) {
		this.date = date;
	}

	protected void setUrl(String url) {
		this.url = url;
	}

	protected void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	protected void setFilesize(int filesize) {
		this.filesize = filesize;
	}

	protected void setApplicationId(int applicationId) {
		this.applicationId = applicationId;
	}

}
