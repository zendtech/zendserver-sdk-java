package org.zend.webapi.test.server;

import org.restlet.Component;
import org.restlet.data.Protocol;
import org.zend.webapi.core.connection.data.values.SystemEdition;
import org.zend.webapi.test.server.response.ServerResponse;

public class ZendSystem {

	private Component component;

	private RequestHandler handler;

	private static ZendSystem instance;

	private ZendSystem() {
		this.component = new Component();
	}

	public static ZendSystem initializeServer(SystemEdition edition,
			Protocol protocol, int port, RequestHandler handler) {
		if (instance == null) {
			instance = new ZendSystem();
		}
		instance.handler = handler;
		instance.prepareServer(protocol, port);
		return instance;
	}

	public static ZendSystem getInstance() {
		if (instance == null) {
			throw new IllegalStateException("ZendSystem is not initialized.");
		}
		return instance;
	}

	public void startServer() throws Exception {
		component.start();
	}

	public void stopServer() throws Exception {
		component.stop();
	}

	private void prepareServer(Protocol protocol, int port) {
		component.getServers().clear();
		component.getServers().add(protocol, port);
		component.getDefaultHost().attach(new ServerApplication());
	}

	public ServerResponse getSystemInfo() {
		return handler.getSystemInfo();
	}

	public ServerResponse clusterGetServerStatus() {
		return handler.clusterGetServerStatus();
	}

	public ServerResponse clusterAddServer() {
		return handler.clusterAddServer();
	}

	public ServerResponse clusterRemoveServer() {
		return handler.clusterRemoveServer();
	}

	public ServerResponse clusterDisableServer() {
		return handler.clusterDisableServer();
	}

	public ServerResponse clusterEnableServer() {
		return handler.clusterEnableServer();
	}

	public ServerResponse restartPhp() {
		return handler.restartPhp();
	}

	public ServerResponse configurationExport() {
		return handler.configurationExport();
	}

	public ServerResponse configurationImport() {
		return handler.configurationImport();
	}

	public ServerResponse applicationGetStatus() {
		return handler.applicationGetStatus();
	}

	public ServerResponse applicationDeploy() {
		return handler.applicationDeploy();
	}

	public ServerResponse applicationUpdate() {
		return handler.applicationUpdate();
	}

	public ServerResponse applicationRemove() {
		return handler.applicationRemove();
	}

	public ServerResponse applicationRedeploy() {
		return handler.applicationRedeploy();
	}

	public ServerResponse clusterReconfigureServer() {
		return handler.clusterReconfigureServer();
	}

	public ServerResponse applicationRollback() {
		return handler.applicationRollback();
	}

	public ServerResponse codeTracingDisable() {
		return handler.codeTracingDisable();
	}

	public ServerResponse codeTracingEnable() {
		return handler.codeTracingEnable();
	}

	public ServerResponse codeTracingIsEnabled() {
		return handler.codeTracingIsEnabled();
	}

	public ServerResponse codeTracingCreate() {
		return handler.codeTracingCreate();
	}

	public ServerResponse codeTracingList() {
		return handler.codeTracingList();
	}

	public ServerResponse codeTracingDelete() {
		return handler.codeTracingDelete();
	}

	public ServerResponse codetracingDownloadTraceFile() {
		return handler.codetracingDownloadTraceFile();
	}

}
