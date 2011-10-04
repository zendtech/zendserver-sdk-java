package org.zend.php.zendserver.deployment.ui.chrome;

import org.mortbay.jetty.Server;


/**
 * Listens for requests on network socket and executes Eclipse commands.
 * Request to execute a command should contain command id in the request path last segment.
 * Parameters passed in request query will be provided in command execution event.
 * For example:
 * 
 * http://127.0.0.1/org.zend.php.deployment.debug.OpenSshTunnel?container=myContainer
 * 
 * invokes org.zend.php.deploymentdebug.OpenSshTunnel with single param "container".
 *
 */
public class SocketCommandListener {

	private static final String SERVER_ID = "socketCommand"; //$NON-NLS-1$
	
	private Thread listeningThread;
	private static final int port = 28029;

	public void start() {
		listeningThread = new Thread(new Runnable() {

			public void run() {
				Server server = new Server(port);
				server.setHandler(new CommandHandler());
				try {
					server.start();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					server.join();
				} catch (InterruptedException e) {
					// quit
				}
			}
			
		});
		listeningThread.start();
		
		
		
	}
	
	public void stop() {
		if (listeningThread != null) {
			listeningThread.interrupt();
		}
	}

}
