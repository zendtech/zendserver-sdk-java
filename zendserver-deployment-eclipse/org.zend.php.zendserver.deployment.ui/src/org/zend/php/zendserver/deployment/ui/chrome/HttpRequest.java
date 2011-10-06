package org.zend.php.zendserver.deployment.ui.chrome;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

	private String method;
	
	private String request;
	
	private String path;
	
	private String query;
	
	private Map<String, String> headers = new HashMap<String, String>();
	private Map<String, String> paramsMap = new HashMap<String, String>();

	private String body;

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) throws URISyntaxException {
		try {
			request = URLDecoder.decode(request, "UTF-8"); //$NON-NLS-1$
		} catch (UnsupportedEncodingException e) {
			// ignore
		}
		
		this.request = request;
		
		URI uri = new URI(request);
		this.path = uri.getPath();
		this.query = uri.getQuery();
		
		parseQuery();
	}

	private void parseQuery() {
		paramsMap.clear();
		if (query == null) {
			return;
		}
		String[] entries = query.split("&"); //$NON-NLS-1$
		
		for (String entry : entries) {
			int idx = entry.indexOf('=');
			String key = (idx == -1) ? entry : entry.substring(0, idx);
			String value = (idx == -1) ? "" : entry.substring(idx + 1); //$NON-NLS-1$
			paramsMap.put(key,  value);
		}
	}

	public String getPath() {
		return path;
	}

	public String getQuery() {
		return query;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public Map getParameterMap() {
		return paramsMap;
	}

	public void setBody(String body) {
		this.body = body;
	}
	
	public String getBody() {
		return body;
	}
	
	
}
