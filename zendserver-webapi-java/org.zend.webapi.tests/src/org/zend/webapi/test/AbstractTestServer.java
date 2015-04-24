package org.zend.webapi.test;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.After;
import org.junit.Before;
import org.mockito.Mockito;
import org.restlet.data.Protocol;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.response.ResponseCode;
import org.zend.webapi.test.server.RequestHandler;
import org.zend.webapi.test.server.ZendSystem;
import org.zend.webapi.test.server.response.ServerResponse;
import org.zend.webapi.test.server.utils.ResponseFactory;
import org.zend.webapi.test.server.utils.ServerType;

public abstract class AbstractTestServer {

	protected RequestHandler handler = Mockito.mock(RequestHandler.class);

	@Before
	public void startupServer() throws MalformedURLException, WebApiException {
		// init client
		Configuration.getClient();

		// init embedded server
		if (Configuration.getType() == ServerType.EMBEDDED) {
			try {
				URI uri = new URI(Configuration.getHost());
				ZendSystem server = ZendSystem.initializeServer(
						Configuration.getEdition(),
						getProtocol(uri.getScheme()), uri.getPort(), handler);
				server.startServer();
			} catch (URISyntaxException e) {
				fail("Incorrect host address: " + Configuration.getHost());
			} catch (Exception e) {
				fail("Cannot start embedded server on "
						+ Configuration.getHost());
			}
		}
	}

	@After
	public void shutdownServer() {
		if (Configuration.getType() == ServerType.EMBEDDED) {
			try {
				ZendSystem.getInstance().stopServer();
			} catch (Exception e) {
				fail("Cannot stop embedded server on "
						+ Configuration.getHost());
			}
		}
		Configuration.clean();
	}

	protected void initMock(ServerResponse toMock, String operation,
			ResponseCode code) {
		if (Configuration.getType() == ServerType.EMBEDDED) {
			try {
				when(toMock).thenReturn(
						ResponseFactory.createResponse(operation, code));
			} catch (IOException e) {
				fail(e.getMessage());
			}
		}
	}

	protected void initFileMock(ServerResponse toMock, String operation,
			ResponseCode code, String folder, String name) {
		if (Configuration.getType() == ServerType.EMBEDDED) {
			try {
				when(toMock).thenReturn(
						ResponseFactory.createFileResponse(operation, code,
								folder, name));
			} catch (IOException e) {
				fail(e.getMessage());
			}
		}
	}

	protected void initErrorMock(ServerResponse toMock, String operation,
			ResponseCode code) {
		if (Configuration.getType() == ServerType.EMBEDDED) {
			try {
				when(toMock).thenReturn(
						ResponseFactory.createErrorResponse(operation, code));
			} catch (IOException e) {
				fail(e.getMessage());
			}
		}
	}

	private Protocol getProtocol(String host) {
		if (host.startsWith("https")) {
			return Protocol.HTTPS;
		}
		return Protocol.HTTP;
	}

}
