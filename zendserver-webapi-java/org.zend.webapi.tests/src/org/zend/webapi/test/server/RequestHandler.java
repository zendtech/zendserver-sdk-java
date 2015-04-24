package org.zend.webapi.test.server;

import org.zend.webapi.test.server.response.ServerResponse;

public interface RequestHandler {

	ServerResponse getSystemInfo();

	ServerResponse clusterGetServerStatus();

	ServerResponse clusterAddServer();

	ServerResponse clusterRemoveServer();

	ServerResponse clusterDisableServer();

	ServerResponse clusterEnableServer();

	ServerResponse restartPhp();

	ServerResponse configurationExport();

	ServerResponse configurationImport();

	ServerResponse applicationGetStatus();

	ServerResponse applicationDeploy();

	ServerResponse applicationUpdate();

	ServerResponse applicationRemove();

	ServerResponse applicationRedeploy();

	ServerResponse clusterReconfigureServer();

	ServerResponse applicationRollback();

	ServerResponse codeTracingDisable();

	ServerResponse codeTracingEnable();

	ServerResponse codeTracingIsEnabled();

	ServerResponse codeTracingCreate();

	ServerResponse codeTracingList();

	ServerResponse codeTracingDelete();

	ServerResponse codetracingDownloadTraceFile();

	ServerResponse monitorGetRequestSummary();

	ServerResponse monitorGetIssuesListPredefinedFilter();

	ServerResponse monitorGetIssueDetails();

	ServerResponse monitorGetEventGroupDetails();

	ServerResponse monitorExportIssueByEventsGroup();

	ServerResponse monitorChangeIssueStatus();

	ServerResponse studioStartDebug();

	ServerResponse studioStartProfile();

	ServerResponse libraryGetStatus();

	ServerResponse libraryVersionGetStatus();

	ServerResponse libraryVersionDeploy();

	ServerResponse librarySynchronize();

	ServerResponse downloadLibraryVersionFile();

}
