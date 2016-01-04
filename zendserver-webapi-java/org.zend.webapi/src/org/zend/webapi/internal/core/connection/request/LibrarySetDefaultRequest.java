package org.zend.webapi.internal.core.connection.request;

import java.util.Date;

import org.restlet.data.Method;
import org.zend.webapi.core.connection.data.IResponseData.ResponseType;
import org.zend.webapi.core.connection.data.values.ServerType;
import org.zend.webapi.core.connection.data.values.WebApiVersion;
import org.zend.webapi.core.connection.response.ResponseCode;

/**
 * Change library version to be the default version for the library. </br>
 * Request Parameters:</br>
 * <table border="1">
 * <tr>
 * <th>Parameter</th>
 * <th>Type</th>
 * <th>Required</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>libraryVersionId</td>
 * <td>Integer</td>
 * <td>Yes</td>
 * <td>A library version ID.</td>
 * </tr>
 * </table>
 * 
 */
public class LibrarySetDefaultRequest extends AbstractRequest {

	private static final ResponseCode[] RESPONSE_CODES = new ResponseCode[] { ResponseCode.ACCEPTED };

	public LibrarySetDefaultRequest(WebApiVersion version, Date date, String keyName, String userAgent, String host,
			String secretKey, ServerType type) {
		super(version, date, keyName, userAgent, host, secretKey, type);
	}

	@Override
	public Method getMethod() {
		return Method.POST;
	}

	@Override
	public ResponseType getExpectedResponseDataType() {
		return ResponseType.LIBRARY_LIST;
	}

	/**
	 * Library version identifier.
	 * 
	 * @param libraryVersion
	 */
	public void setLibraryVersionId(int id) {
		addParameter("libraryVersionId", id);
	}

	@Override
	protected String getRequestName() {
		return "librarySetDefault";
	}

	@Override
	protected ResponseCode[] getValidResponseCode() {
		return RESPONSE_CODES;
	}

}
