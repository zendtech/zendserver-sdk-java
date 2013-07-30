package org.zend.webapi.test.server.response;

import org.w3c.dom.Document;

public class ServiceResponse extends ServerResponse {

	private Document data;

	public ServiceResponse(int code, Document data) {
		super(code);
		this.data = data;
	}

	public Document getData() {
		return data;
	}

}
