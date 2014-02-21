/*******************************************************************************
 * Copyright (c) Jan 30, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.service;

import org.zend.webapi.core.connection.data.IResponseData;
import org.zend.webapi.core.connection.request.IRequest;
import org.zend.webapi.internal.core.connection.request.ApplicationDeployRequest;
import org.zend.webapi.internal.core.connection.request.ApplicationGetStatusRequest;
import org.zend.webapi.internal.core.connection.request.ApplicationRedeployRequest;
import org.zend.webapi.internal.core.connection.request.ApplicationRemoveRequest;
import org.zend.webapi.internal.core.connection.request.ApplicationRollbackRequest;
import org.zend.webapi.internal.core.connection.request.ApplicationUpdateRequest;
import org.zend.webapi.internal.core.connection.request.BootstrapSingleServerRequest;
import org.zend.webapi.internal.core.connection.request.ClusterAddServerRequest;
import org.zend.webapi.internal.core.connection.request.ClusterDisableServerRequest;
import org.zend.webapi.internal.core.connection.request.ClusterEnableServerRequest;
import org.zend.webapi.internal.core.connection.request.ClusterGetServerStatusRequest;
import org.zend.webapi.internal.core.connection.request.ClusterReconfigureServerRequest;
import org.zend.webapi.internal.core.connection.request.ClusterRemoveServerRequest;
import org.zend.webapi.internal.core.connection.request.CodeTracingCreateRequest;
import org.zend.webapi.internal.core.connection.request.CodeTracingDeleteRequest;
import org.zend.webapi.internal.core.connection.request.CodeTracingDisableRequest;
import org.zend.webapi.internal.core.connection.request.CodeTracingEnableRequest;
import org.zend.webapi.internal.core.connection.request.CodeTracingIsEnabledRequest;
import org.zend.webapi.internal.core.connection.request.CodeTracingListRequest;
import org.zend.webapi.internal.core.connection.request.CodetracingDownloadTraceFileRequest;
import org.zend.webapi.internal.core.connection.request.ConfigurationExportRequest;
import org.zend.webapi.internal.core.connection.request.ConfigurationImportRequest;
import org.zend.webapi.internal.core.connection.request.DownloadLibraryVersionFileRequest;
import org.zend.webapi.internal.core.connection.request.GetSystemInfoRequest;
import org.zend.webapi.internal.core.connection.request.LibraryGetStatusRequest;
import org.zend.webapi.internal.core.connection.request.LibrarySynchronizeRequest;
import org.zend.webapi.internal.core.connection.request.LibraryVersionDeployRequest;
import org.zend.webapi.internal.core.connection.request.LibraryVersionGetStatusRequest;
import org.zend.webapi.internal.core.connection.request.MonitorChangeIssueStatusRequest;
import org.zend.webapi.internal.core.connection.request.MonitorExportIssueByEventsGroupRequest;
import org.zend.webapi.internal.core.connection.request.MonitorGetEventGroupDetailsRequest;
import org.zend.webapi.internal.core.connection.request.MonitorGetIssueDetailsRequest;
import org.zend.webapi.internal.core.connection.request.MonitorGetIssuesListPredefinedFilterRequest;
import org.zend.webapi.internal.core.connection.request.MonitorGetRequestSummaryRequest;
import org.zend.webapi.internal.core.connection.request.RestartPhpRequest;
import org.zend.webapi.internal.core.connection.request.StudioIsDebugModeEnabledRequest;
import org.zend.webapi.internal.core.connection.request.StudioStartDebugModeRequest;
import org.zend.webapi.internal.core.connection.request.StudioStartDebugRequest;
import org.zend.webapi.internal.core.connection.request.StudioStartProfileRequest;
import org.zend.webapi.internal.core.connection.request.StudioStopDebugModeRequest;

/**
 * The Zend Server Web API is intended to allow automation of the management and
 * deployment of Zend Server and Zend Server Cluster Manager, and allow
 * integration with other Zend or 3rd party software. <br>
 * 
 * Each registered service method is represented by its name and request and
 * response properties
 * 
 * @author Roy, 2011
 * @see IRequest
 * @see IResponseData
 * 
 */
public enum WebApiMethodType {

	/**
	 * @see GetSystemInfoRequest
	 */
	GET_SYSTEM_INFO("getSystemInfo", GetSystemInfoRequest.class),

	/**
	 * @see ClusterGetServerStatusRequest
	 */
	CLUSTER_GET_SERVER_STATUS("clusterGetServerStatus",
			ClusterGetServerStatusRequest.class),

	/**
	 * @see ClusterAddServerRequest
	 */
	CLUSTER_ADD_SERVER("clusterAddServer", ClusterAddServerRequest.class),

	/**
	 * @see ClusterRemoveServerRequest
	 */
	CLUSTER_REMOVE_SERVER("clusterRemoveServer",
			ClusterRemoveServerRequest.class),

	/**
	 * @see ClusterDisableServerRequest
	 */
	CLUSTER_DISABLE_SERVER("clusterDisableServer",
			ClusterDisableServerRequest.class),

	/**
	 * @see ClusterEnableServerRequest
	 */
	CLUSTER_ENABLE_SERVER("clusterEnableServer",
			ClusterEnableServerRequest.class),

	/**
	 * @see ClusterEnableServerRequest
	 */
	CLUSTER_RECONFIGURE_SERVER("clusterReconfigureServer",
			ClusterReconfigureServerRequest.class),

	/**
	 * @see RestartPhpRequest
	 */
	RESTART_PHP("restartPhp", RestartPhpRequest.class),

	/**
	 * @see ConfigurationExportRequest
	 */
	CONFIGURATION_EXPORT("configurationExport",
			ConfigurationExportRequest.class),

	/**
	 * @see ConfigurationImportRequest
	 */
	CONFIGURATION_IMPORT("configurationImport",
			ConfigurationImportRequest.class),

	/**
	 * @see ApplicationGetStatusRequest
	 */
	APPLICATION_GET_STATUS("applicationGetStatus",
			ApplicationGetStatusRequest.class),

	/**
	 * @see ApplicationDeployRequest
	 */
	APPLICATION_DEPLOY("applicationDeploy", ApplicationDeployRequest.class),

	/**
	 * @see ApplicationUpdateRequest
	 */
	APPLICATION_UPDATE("applicationUpdate", ApplicationUpdateRequest.class),

	/**
	 * @see ApplicationRemoveRequest
	 */
	APPLICATION_REMOVE("applicationRemove", ApplicationRemoveRequest.class),

	/**
	 * @see ApplicationRollbackRequest
	 */
	APPLICATION_ROLLBACK("applicationRollback",
			ApplicationRollbackRequest.class),

	/**
	 * @see ApplicationRedeployRequest
	 */
	APPLICATION_SYNCHRONIZE("applicationSynchronize",
			ApplicationRedeployRequest.class),

	/**
	 * @see CodeTracingDisableRequest
	 */
	CODE_TRACING_DISABLE("codetracingDisable", CodeTracingDisableRequest.class),

	/**
	 * @see CodeTracingEnableRequest
	 */
	CODE_TRACING_ENABLE("codetracingEnable", CodeTracingEnableRequest.class),

	/**
	 * @see CodeTracingIsEnabledRequest
	 */
	CODE_TRACING_IS_ENABLED("codetracingIsEnabled",
			CodeTracingIsEnabledRequest.class),

	/**
	 * @see CodeTracingCreateRequest
	 */
	CODE_TRACING_CREATE("codetracingCreate", CodeTracingCreateRequest.class),

	/**
	 * @see CodeTracingDeleteRequest
	 */
	CODE_TRACING_DELETE("codetracingDelete", CodeTracingDeleteRequest.class),

	/**
	 * @see CodeTracingListRequest
	 */
	CODE_TRACING_LIST("codetracingList", CodeTracingListRequest.class),

	/**
	 * @see CodetracingDownloadTraceFileRequest
	 */
	CODE_TRACING_DOWNLOAD_TRACE_FILE("codetracingDownloadTraceFile",
			CodetracingDownloadTraceFileRequest.class),

	/**
	 * @see MonitorGetRequestSummaryRequest
	 */
	MONITOR_GET_REQUEST_SUMMARY("monitorGetRequestSummary",
			MonitorGetRequestSummaryRequest.class),

	/**
	 * @see MonitorGetIssuesListPredefinedFilterRequest
	 */
	MONITOR_GET_ISSUES_LIST_PREDEFINED_FILTER(
			"monitorGetIssuesListPredefinedFilter",
			MonitorGetIssuesListPredefinedFilterRequest.class),

	/**
	 * @see MonitorGetIssueDetailsRequest
	 */
	MONITOR_GET_ISSUE_DETAILS("monitorGetIssuesListByPredefinedFilter",
			MonitorGetIssueDetailsRequest.class),

	/**
	 * @see MonitorGetEventGroupDetailsRequest
	 */
	MONITOR_GET_EVENT_GROUP_DETAILS("monitorGetEventGroupDetails",
			MonitorGetEventGroupDetailsRequest.class),

	/**
	 * @see MonitorExportIssueByEventsGroupRequest
	 */
	MONITOR_EXPORT_ISSUE_BY_EVENTS_GROUP("monitorExportIssueByEventsGroup",
			MonitorExportIssueByEventsGroupRequest.class),

	/**
	 * @see MonitorChangeIssueStatusRequest
	 */
	MONITOR_CHANGE_ISSUE_STATUS("monitorChangeIssueStatus",
			MonitorChangeIssueStatusRequest.class),

	/**
	 * @see StudioStartDebugRequest
	 */
	STUDIO_START_DEBUG("studioStartDebug", StudioStartDebugRequest.class),

	/**
	 * @see StudioStartProfileRequest
	 */
	STUDIO_START_PROFILE("studioStartDebug", StudioStartProfileRequest.class),
	
	/**
	 * @see StudioStartDebugModeRequest
	 */
	STUDIO_START_DEBUG_MODE("studioStartDebugMode",
			StudioStartDebugModeRequest.class),

	/**
	 * @see StudioStartDebugModeRequest
	 */
	STUDIO_STOP_DEBUG_MODE("studioStopDebugMode",
			StudioStopDebugModeRequest.class),

	/**
	 * @see StudioStartDebugModeRequest
	 */
	STUDIO_IS_DEBUG_MODE_ENABLED("studioIsDebugModeEnabled",
			StudioIsDebugModeEnabledRequest.class), 
		
	/**
	 * @see LibraryGetStatusRequest
	 */
	LIBRARY_GET_STATUS("libraryGetStatus", LibraryGetStatusRequest.class),

	/**
	 * @see LibraryVersionGetStatusRequest
	 */
	LIBRARY_VERSION_GET_STATUS("libraryVersionGetStatus",
			LibraryVersionGetStatusRequest.class),

	/**
	 * @see LibraryVersionDeployRequest
	 */
	LIBRARY_VERSION_DEPLOY("libraryVersionDeploy",
			LibraryVersionDeployRequest.class), 
	
	/**
	 * @see LibrarySynchronizeRequest
	 */
	LIBRARY_SYNCHRONIZE("librarySynchronize", LibrarySynchronizeRequest.class),

	/**
	 * @see DownloadLibraryVersionFileRequest
	 */
	DOWNLOAD_LIBRARY_VERSION_FILE("downloadLibraryVersionFile",
			DownloadLibraryVersionFileRequest.class),
			
	/**
	 * @see BootstrapSingleServerRequest
	 */
	BOOTSTRAP_SINGLE_SERVER("bootstrapSingleServer",
			BootstrapSingleServerRequest.class);

	/**
	 * Name of the service
	 */
	private final String name;

	/**
	 * Request class
	 */
	private final Class<? extends IRequest> requestClass;

	private WebApiMethodType(String name, Class<? extends IRequest> requestClass) {
		this.name = name;
		this.requestClass = requestClass;
	}

	/**
	 * @return name of the method
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @return request bound to this method
	 */
	public Class<? extends IRequest> getRequestClass() {
		return requestClass;
	}

}
