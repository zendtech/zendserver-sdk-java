/*******************************************************************************
 * Copyright (c) Feb 12, 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;


/**
 * List of parameter elements grouped by source get, post, cookie, session and
 * server.
 * 
 * @author Wojciech Galanciak, 2012
 * @since 1.2
 */
public class SuperGlobals extends AbstractResponseData {

	private static final String SUPER_GLOBALS = "/superGlobals";
	
	private ParameterList get;
	private ParameterList post;
	private ParameterList cookie;
	private ParameterList session;
	private ParameterList server;

	protected SuperGlobals() {
		super(ResponseType.SUPER_GLOBALS, BASE_PATH + SUPER_GLOBALS,
				SUPER_GLOBALS);
	}

	protected SuperGlobals(String prefix, int occurrance) {
		super(ResponseType.SUPER_GLOBALS, prefix, SUPER_GLOBALS, occurrance);
	}

	public boolean accept(IResponseDataVisitor visitor) {
		if (visitor.preVisit(this)) {
			if (this.getGet() != null) {
				this.getGet().accept(visitor);
			}
			if (this.getPost() != null) {
				this.getPost().accept(visitor);
			}
			if (this.getCookie() != null) {
				this.getCookie().accept(visitor);
			}
			if (this.getSession() != null) {
				this.getSession().accept(visitor);
			}
			if (this.getServer() != null) {
				this.getServer().accept(visitor);
			}
			return visitor.visit(this);
		}
		return false;
	}

	/**
	 * @return Available GET parameters
	 */
	public ParameterList getGet() {
		return get;
	}

	/**
	 * @return Available POST parameters
	 */
	public ParameterList getPost() {
		return post;
	}

	/**
	 * @return Available COOKIE values
	 */
	public ParameterList getCookie() {
		return cookie;
	}

	/**
	 * @return Available SESSION values
	 */
	public ParameterList getSession() {
		return session;
	}

	/**
	 * @return Available SERVER environment parameters
	 */
	public ParameterList getServer() {
		return server;
	}

	protected void setGet(ParameterList get) {
		this.get = get;
	}

	protected void setPost(ParameterList post) {
		this.post = post;
	}

	protected void setCookie(ParameterList cookie) {
		this.cookie = cookie;
	}

	protected void setSession(ParameterList session) {
		this.session = session;
	}

	protected void setServer(ParameterList server) {
		this.server = server;
	}

}
