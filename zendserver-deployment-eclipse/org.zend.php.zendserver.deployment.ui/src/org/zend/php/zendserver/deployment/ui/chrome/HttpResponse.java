package org.zend.php.zendserver.deployment.ui.chrome;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

	private static final String HEADER_CONTENT_TYPE = "Content-Type"; //$NON-NLS-1$
	private static final String HEADER_CONTENT_LENGTH = "Content-Length"; //$NON-NLS-1$
	
	public static final int OK = 200;
	public static int ERROR = 500;

	private static final String RESPONSE_OK = "OK"; //$NON-NLS-1$
	private static final String RESPONSE_INTERNAL_ERROR = "Internal Error"; //$NON-NLS-1$
	
	private static final String EOL = "\r\n"; //$NON-NLS-1$
	
	private PrintWriter out;
	private Map<String, String> headers = new HashMap<String, String>();
	private int status;
	
	public HttpResponse(PrintWriter out) {
		this.out = out;
		setContentType("text/html"); // default content-type //$NON-NLS-1$
	}

	public void setContentType(String string) {
		headers.put(HEADER_CONTENT_TYPE, string);
	}

	private void setLength(int length) {
		headers.put(HEADER_CONTENT_LENGTH, Integer.toString(length));
	}
	
	public void setStatus(int code) {
		this.status = code;
	}

	public void send(String body) {
		StringBuilder out = new StringBuilder();
		out.append("HTTP/1.1 "); //$NON-NLS-1$
		out.append(Integer.toString(status));
		out.append(" "); //$NON-NLS-1$
		out.append(getStatusMessage());
		out.append(EOL);
		
		if (body != null) {
			int bodyLength = body.length();
			setLength(bodyLength);
		}
		
		for (Map.Entry<String, String> header : headers.entrySet()) {
			out.append(header.getKey());
			out.append(": "); //$NON-NLS-1$
			out.append(header.getValue());
			out.append(EOL);
		}
		
		out.append(EOL);
		
		if (body != null) {
			out.append(body);
			out.append(EOL);
		}
		
		this.out.write(out.toString());
		this.out.flush();
	}

	private String getStatusMessage() {
		if (status == OK) {
			return RESPONSE_OK;
		} else if (status == ERROR) {
			return RESPONSE_INTERNAL_ERROR;
		}
		
		throw new IllegalArgumentException("Unknown status "+status); //$NON-NLS-1$
	}

}
