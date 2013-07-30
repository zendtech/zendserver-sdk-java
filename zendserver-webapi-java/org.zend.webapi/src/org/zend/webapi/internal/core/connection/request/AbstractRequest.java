/*******************************************************************************
 * Copyright (c) Jan 27, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.internal.core.connection.request;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.restlet.Request;
import org.restlet.data.Method;
import org.restlet.representation.StringRepresentation;
import org.zend.webapi.core.connection.data.values.ServerType;
import org.zend.webapi.core.connection.data.values.WebApiVersion;
import org.zend.webapi.core.connection.request.IRequest;
import org.zend.webapi.core.connection.request.RequestParameter;
import org.zend.webapi.core.connection.response.ResponseCode;
import org.zend.webapi.core.progress.IChangeNotifier;
import org.zend.webapi.internal.core.Utils;
import org.zend.webapi.internal.core.connection.auth.signature.Signature;
import org.zend.webapi.internal.core.connection.auth.signature.SignatureException;

public abstract class AbstractRequest implements IRequest {

	public static final int DEFAULT_TIMEOUT = 40000;
	
	private final WebApiVersion version;
	private final Date date;
	private final String userAgent;
	private final String host;
	private final String secretKey;
	private final String keyName;
	private final ServerType type;
	private List<RequestParameter<?>> parameters;
	protected IChangeNotifier notifier;
	
	public AbstractRequest(WebApiVersion version, Date date, String keyName,
			String userAgent, String host, String secretKey) {
		this(version, date, keyName, userAgent, host, secretKey,
				ServerType.ZEND_SERVER);
	}

	public AbstractRequest(WebApiVersion version, Date date, String keyName,
			String userAgent, String host, String secretKey, ServerType type) {
		super();
		this.version = version;
		this.date = date;
		this.keyName = keyName;
		this.userAgent = userAgent;
		try {
			new URL(host);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("error parsing host name");
		}
		this.host = host;
		this.secretKey = secretKey;
		this.type = type;
		this.parameters = null;
	}

	public void setNotifier(IChangeNotifier notifier) {
		this.notifier = notifier;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.webapi.core.connection.IRequest#getVersion()
	 */
	public WebApiVersion getVersion() {
		return version;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.webapi.core.connection.IRequest#getDate()
	 */
	public Date getDate() {
		return date;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.webapi.core.connection.request.IRequest#getKeyName()
	 */
	public String getKeyName() {
		return keyName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.webapi.core.connection.IRequest#getUserAgent()
	 */
	public String getUserAgent() {
		return userAgent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.webapi.core.connection.IRequest#getHost()
	 */
	public String getHost() {
		return host;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.webapi.core.connection.IRequest#getSignature()
	 */
	public String getSignature() throws SignatureException {
		final Signature signature = new Signature(getHost(), getUserAgent(),
				getSecretKey());
		return signature.encode(getUri(), Utils.getFormattedDate(getDate()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.webapi.core.connection.IRequest#getSecretKey()
	 */
	public String getSecretKey() {
		return secretKey;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.webapi.core.connection.request.IRequest#getContentType()
	 */
	public String getContentType() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.webapi.core.connection.request.IRequest#getParameters()
	 */
	public List<RequestParameter<?>> getParameters() {
		return parameters;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.webapi.core.connection.request.IRequest#getServerType()
	 */
	public ServerType getServerType() {
		return type;
	}
	
	public long getTimeout() {
		return DEFAULT_TIMEOUT;
	}
	
	
	public final String getUri() {
		return "/" + getServerType().getName() + "/Api/" + getRequestName();
	}
	
	/**
	 * @return particular request name
	 */
	protected abstract String getRequestName();

	/**
	 * @return an unmodifiable list of the given response code
	 */
	protected boolean isCodeInList(int element, ResponseCode... var) {
		for (int i = 0; i < var.length; i++) {
			ResponseCode j = var[i];
			if (element == j.getCode()) {
				return true;
			}
		}
		return false;
	}

	protected <T> void addParameter(String key, T value) {
		if (key == null || value == null) {
			throw new IllegalArgumentException("key or value must be assigned");
		}
		RequestParameter<T> param = new RequestParameter<T>(key,
				value);
		if (this.getParameters() == null) {
			this.parameters = new LinkedList<RequestParameter<?>>();
		}
		getParameters().add(param);
	}

	/**
	 * Returns true if the given return code is supported
	 */
	public boolean isExpectedResponseCode(int code) {
		return isCodeInList(code, getValidResponseCode());
	}

	/**
	 * Internal method to ease the response code verification
	 * 
	 * @return the list of expected response codes
	 */
	protected abstract ResponseCode[] getValidResponseCode();

	
	/* (non-Javadoc)
	 * @see org.zend.webapi.core.connection.request.IRequest#applyParameters(org.restlet.Request)
	 */
	public void applyParameters(Request request) {
		final String query = getParametersAsString();
		final Method method = getMethod();
		if (method == Method.POST) {
			request.setEntity(new StringRepresentation(query));
		} else if (method == Method.GET) {
			request.getResourceRef().setQuery(query);
		}
	}
	
	/**
	 * @param parameters
	 * @return a string representation of the 
	 */
	public String getParametersAsString() {
		StringBuilder sb = new StringBuilder();
		boolean isFirst = true;
		for (RequestParameter<?> requestParameter : parameters) {
			if (!isFirst) {
				sb.append("&");
			}
			isFirst = false;
			sb.append(requestParameter.toString());
		}
		return sb.toString();
	}
	
	
}
