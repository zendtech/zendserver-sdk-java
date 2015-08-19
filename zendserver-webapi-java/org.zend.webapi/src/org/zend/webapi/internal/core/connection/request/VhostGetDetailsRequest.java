package org.zend.webapi.internal.core.connection.request;

import java.util.Date;

import org.restlet.data.Method;
import org.zend.webapi.core.connection.data.IResponseData.ResponseType;
import org.zend.webapi.core.connection.data.values.ServerType;
import org.zend.webapi.core.connection.data.values.WebApiVersion;
import org.zend.webapi.core.connection.response.ResponseCode;

/**
 * @since 1.6
 */
public class VhostGetDetailsRequest extends AbstractRequest {

	private static final ResponseCode[] RESPONSE_CODES = new ResponseCode[] { ResponseCode.OK };
	
	public VhostGetDetailsRequest(WebApiVersion version, Date date, String keyName, String userAgent, String host,
			String secretKey, ServerType type) {
		super(version, date, keyName, userAgent, host, secretKey, type);
	}

	public void setId(int id) {
		addParameter("vhost", id); //$NON-NLS-1$
	}

	@Override
	public Method getMethod() {
		return Method.GET;
	}

	@Override
	public ResponseType getExpectedResponseDataType() {
		return ResponseType.VHOST_DETAILS;
	}

	@Override
	protected String getRequestName() {
		return "vhostGetDetails"; //$NON-NLS-1$
	}

	@Override
	protected ResponseCode[] getValidResponseCode() {
		return RESPONSE_CODES;
	}

}
