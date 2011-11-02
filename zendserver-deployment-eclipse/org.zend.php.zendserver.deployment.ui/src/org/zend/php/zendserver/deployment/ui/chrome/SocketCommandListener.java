package org.zend.php.zendserver.deployment.ui.chrome;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;

import org.zend.php.zendserver.deployment.ui.Activator;

/**
 * Listens for requests on network socket and executes Eclipse commands. Request
 * to execute a command should contain command id in the request path last
 * segment. Parameters passed in request query will be provided in command
 * execution event. For example:
 * 
 * http://127.0.0.1/org.zend.php.deployment.debug.OpenSshTunnel?container=
 * myContainer
 * 
 * invokes org.zend.php.deploymentdebug.OpenSshTunnel with single param
 * "container".
 * 
 */
public class SocketCommandListener {
	
	private Thread listeningThread;
	private static final int port = 28029;
	
	private CommandHandler handler = new CommandHandler();

	public void start() {
		listeningThread = new Thread(new Runnable() {

			public void run() {
				ServerSocket serverSocket = null;
				try {
					serverSocket = openServerSocket(port);
				} catch (IOException ex) {
					Activator.log(ex);
					return;
				}

				try {
					serverLoop(serverSocket);
				} finally {
					try {
						serverSocket.close();
					} catch (IOException ex) {
						Activator.log(ex);
					}
				}
			}

			
		});
		listeningThread.start();
	}

	protected ServerSocket openServerSocket(int port2) throws IOException {
		int tries = 3;
		
		IOException ex = null;
		
		while (tries > 0) {
			tries--;
			try {
				return new ServerSocket(port);
			} catch (IOException e) {
				ex = e;
				if (tries > 0) {
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e1) {
						// ignore
					}
				}
			}
		}
		
		throw ex;
	}

	private void serverLoop(ServerSocket serverSocket) {
		while (true) {
			Socket clientSocket = null;
			BufferedReader in = null;
			PrintWriter out = null;
			try {
				clientSocket = serverSocket.accept();
				out = new PrintWriter(clientSocket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				handleClientSocket(in, out);
				
			} catch (Throwable ex) { // eat all exceptions to protect server from falling
				Activator.log(ex);
			} finally {
				try {
					if (in != null) {
						in.close();
					}
					
					if (out != null) {
						out.close();
					}
					
					if (clientSocket != null) {
						clientSocket.close();
					}
				} catch (IOException ex) {
					Activator.log(ex);
				}
			}
		}
	}

	private void handleClientSocket(BufferedReader in, PrintWriter out) throws IOException {
		HttpResponse response = new HttpResponse(out);
		try {
			HttpRequest request = readRequest(in);
			if (request == null) {
				return;
			}
			handler.handle(request, response);
		} catch (Throwable e) {
			response.setStatus(HttpResponse.ERROR);
			response.send(null);
		}
	}
	
	private HttpRequest readRequest(BufferedReader in) throws IOException, ParseError {
		HttpRequest httpRequest = new HttpRequest();

		String request = in.readLine();
		if (request == null) {
			return null;
		}
		int idx = request.indexOf(' ');
		if (idx == -1) {
			throw new ParseError();
		}
		String method = request.substring(0, idx);
		httpRequest.setMethod(method);
		int idx2 = request.indexOf(' ', idx + 1);
		if (idx2 == -1) {
			throw new ParseError();
		}
		String requestPath = request.substring(idx + 1, idx2);
		try {
			httpRequest.setRequest(requestPath);
		} catch (URISyntaxException e) {
			throw new ParseError();
		}
		
		String inputLine;
		while (((inputLine = in.readLine()) != null) && (inputLine.length() > 0)) {
			int colon = inputLine.indexOf(':');
			if (colon == -1) {
				throw new ParseError();
			}
			String key = inputLine.substring(0, colon);
			String value = inputLine.substring(colon + 1).trim();
			httpRequest.getHeaders().put(key, value);
		}
		
		StringBuilder body = new StringBuilder();
		char[] buf = new char[4096];
		int len;
		while (in.ready() && ((len = in.read(buf)) > 0)) {
			body.append(buf, 0, len);
		}
		httpRequest.setBody(body.toString());
		
		return httpRequest;

	}

	public void stop() {
		if (listeningThread != null) {
			listeningThread.interrupt();
		}
	}

}
