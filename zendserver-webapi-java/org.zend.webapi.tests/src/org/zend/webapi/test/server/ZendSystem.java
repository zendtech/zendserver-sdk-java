package org.zend.webapi.test.server;

import org.restlet.Component;
import org.restlet.data.Protocol;
import org.zend.webapi.core.connection.data.values.SystemEdition;
import org.zend.webapi.test.server.response.ServerResponse;

public class ZendSystem {

	private Component component;

	private RequestHandler handler;

	private static ZendSystem instance;

	private SystemEdition edition;

	private ZendSystem(SystemEdition edition) {
		this.component = new Component();
		this.edition = edition;
	}

	public static ZendSystem initializeServer(SystemEdition edition,
			Protocol protocol, int port, RequestHandler handler) {
		instance = new ZendSystem(edition);
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
		component.getDefaultHost().attach(new ServerApplication(edition));
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

	public ServerResponse monitorGetRequestSummary() {
		return handler.monitorGetRequestSummary();
	}

	public ServerResponse monitorGetIssuesListByPredefinedFilter() {
		return handler.monitorGetIssuesListPredefinedFilter();
	}

	public ServerResponse monitorGetIssueDetails() {
		return handler.monitorGetIssueDetails();
	}

	public ServerResponse monitorGetEventGroupDetails() {
		return handler.monitorGetEventGroupDetails();
	}

	public ServerResponse monitorExportIssueByEventsGroup() {
		return handler.monitorExportIssueByEventsGroup();
	}

	public ServerResponse monitorChangeIssueStatus() {
		return handler.monitorChangeIssueStatus();
	}

	public ServerResponse studioStartDebug() {
		return handler.studioStartDebug();
	}

	public ServerResponse studioStartProfile() {
		return handler.studioStartProfile();
	}

	public ServerResponse libraryGetStatus() {
		return handler.libraryGetStatus();
	}

	public ServerResponse libraryVersionGetStatus() {
		return handler.libraryVersionGetStatus();
	}

	public ServerResponse libraryVersionDeploy() {
		return handler.libraryVersionDeploy();
	}

	public ServerResponse librarySynchronize() {
		return handler.librarySynchronize();
	}

	public ServerResponse downloadLibraryVersionFile() {
		return handler.downloadLibraryVersionFile();
	}

}
