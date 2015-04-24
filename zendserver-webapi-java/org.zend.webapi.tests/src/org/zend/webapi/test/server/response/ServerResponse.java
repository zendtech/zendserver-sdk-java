package org.zend.webapi.test.server.response;

import org.restlet.data.Status;

public class ServerResponse {

	private Status status;

	public ServerResponse(int code) {
		super();
		this.status = new Status(code);
	}

	public Status getStatus() {
		return status;
	}

}
