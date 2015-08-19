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
import org.zend.webapi.internal.core.connection.request.ExtensionsListRequest;
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
import org.zend.webapi.internal.core.connection.request.VhostGetDetailsRequest;
import org.zend.webapi.internal.core.connection.request.VhostGetStatusRequest;

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
	GET_SYSTEM_INFO("getSystemInfo", GetSystemInfoRequest.class), //$NON-NLS-1$

	/**
	 * @see ClusterGetServerStatusRequest
	 */
	CLUSTER_GET_SERVER_STATUS("clusterGetServerStatus", //$NON-NLS-1$
			ClusterGetServerStatusRequest.class),

	/**
	 * @see ClusterAddServerRequest
	 */
	CLUSTER_ADD_SERVER("clusterAddServer", ClusterAddServerRequest.class), //$NON-NLS-1$

	/**
	 * @see ClusterRemoveServerRequest
	 */
	CLUSTER_REMOVE_SERVER("clusterRemoveServer", //$NON-NLS-1$
			ClusterRemoveServerRequest.class),

	/**
	 * @see ClusterDisableServerRequest
	 */
	CLUSTER_DISABLE_SERVER("clusterDisableServer", //$NON-NLS-1$
			ClusterDisableServerRequest.class),

	/**
	 * @see ClusterEnableServerRequest
	 */
	CLUSTER_ENABLE_SERVER("clusterEnableServer", //$NON-NLS-1$
			ClusterEnableServerRequest.class),

	/**
	 * @see ClusterEnableServerRequest
	 */
	CLUSTER_RECONFIGURE_SERVER("clusterReconfigureServer", //$NON-NLS-1$
			ClusterReconfigureServerRequest.class),

	/**
	 * @see RestartPhpRequest
	 */
	RESTART_PHP("restartPhp", RestartPhpRequest.class), //$NON-NLS-1$

	/**
	 * @see ConfigurationExportRequest
	 */
	CONFIGURATION_EXPORT("configurationExport", //$NON-NLS-1$
			ConfigurationExportRequest.class),

	/**
	 * @see ConfigurationImportRequest
	 */
	CONFIGURATION_IMPORT("configurationImport", //$NON-NLS-1$
			ConfigurationImportRequest.class),

	/**
	 * @see ApplicationGetStatusRequest
	 */
	APPLICATION_GET_STATUS("applicationGetStatus", //$NON-NLS-1$
			ApplicationGetStatusRequest.class),

	/**
	 * @see ApplicationDeployRequest
	 */
	APPLICATION_DEPLOY("applicationDeploy", ApplicationDeployRequest.class), //$NON-NLS-1$

	/**
	 * @see ApplicationUpdateRequest
	 */
	APPLICATION_UPDATE("applicationUpdate", ApplicationUpdateRequest.class), //$NON-NLS-1$

	/**
	 * @see ApplicationRemoveRequest
	 */
	APPLICATION_REMOVE("applicationRemove", ApplicationRemoveRequest.class), //$NON-NLS-1$

	/**
	 * @see ApplicationRollbackRequest
	 */
	APPLICATION_ROLLBACK("applicationRollback", //$NON-NLS-1$
			ApplicationRollbackRequest.class),

	/**
	 * @see ApplicationRedeployRequest
	 */
	APPLICATION_SYNCHRONIZE("applicationSynchronize", //$NON-NLS-1$
			ApplicationRedeployRequest.class),

	/**
	 * @see CodeTracingDisableRequest
	 */
	CODE_TRACING_DISABLE("codetracingDisable", CodeTracingDisableRequest.class), //$NON-NLS-1$

	/**
	 * @see CodeTracingEnableRequest
	 */
	CODE_TRACING_ENABLE("codetracingEnable", CodeTracingEnableRequest.class), //$NON-NLS-1$

	/**
	 * @see CodeTracingIsEnabledRequest
	 */
	CODE_TRACING_IS_ENABLED("codetracingIsEnabled", //$NON-NLS-1$
			CodeTracingIsEnabledRequest.class),

	/**
	 * @see CodeTracingCreateRequest
	 */
	CODE_TRACING_CREATE("codetracingCreate", CodeTracingCreateRequest.class), //$NON-NLS-1$

	/**
	 * @see CodeTracingDeleteRequest
	 */
	CODE_TRACING_DELETE("codetracingDelete", CodeTracingDeleteRequest.class), //$NON-NLS-1$

	/**
	 * @see CodeTracingListRequest
	 */
	CODE_TRACING_LIST("codetracingList", CodeTracingListRequest.class), //$NON-NLS-1$

	/**
	 * @see CodetracingDownloadTraceFileRequest
	 */
	CODE_TRACING_DOWNLOAD_TRACE_FILE("codetracingDownloadTraceFile", //$NON-NLS-1$
			CodetracingDownloadTraceFileRequest.class),

	/**
	 * @see ExtensionsListRequest
	 */
	EXTENSIONS_LIST("configurationExtensionsList", //$NON-NLS-1$
			ExtensionsListRequest.class),
	
	/**
	 * @see MonitorGetRequestSummaryRequest
	 */
	MONITOR_GET_REQUEST_SUMMARY("monitorGetRequestSummary", //$NON-NLS-1$
			MonitorGetRequestSummaryRequest.class),

	/**
	 * @see MonitorGetIssuesListPredefinedFilterRequest
	 */
	MONITOR_GET_ISSUES_LIST_PREDEFINED_FILTER(
			"monitorGetIssuesListPredefinedFilter", //$NON-NLS-1$
			MonitorGetIssuesListPredefinedFilterRequest.class),

	/**
	 * @see MonitorGetIssueDetailsRequest
	 */
	MONITOR_GET_ISSUE_DETAILS("monitorGetIssuesListByPredefinedFilter", //$NON-NLS-1$
			MonitorGetIssueDetailsRequest.class),

	/**
	 * @see MonitorGetEventGroupDetailsRequest
	 */
	MONITOR_GET_EVENT_GROUP_DETAILS("monitorGetEventGroupDetails", //$NON-NLS-1$
			MonitorGetEventGroupDetailsRequest.class),

	/**
	 * @see MonitorExportIssueByEventsGroupRequest
	 */
	MONITOR_EXPORT_ISSUE_BY_EVENTS_GROUP("monitorExportIssueByEventsGroup", //$NON-NLS-1$
			MonitorExportIssueByEventsGroupRequest.class),

	/**
	 * @see MonitorChangeIssueStatusRequest
	 */
	MONITOR_CHANGE_ISSUE_STATUS("monitorChangeIssueStatus", //$NON-NLS-1$
			MonitorChangeIssueStatusRequest.class),

	/**
	 * @see StudioStartDebugRequest
	 */
	STUDIO_START_DEBUG("studioStartDebug", StudioStartDebugRequest.class), //$NON-NLS-1$

	/**
	 * @see StudioStartProfileRequest
	 */
	STUDIO_START_PROFILE("studioStartDebug", StudioStartProfileRequest.class), //$NON-NLS-1$

	/**
	 * @see StudioStartDebugModeRequest
	 */
	STUDIO_START_DEBUG_MODE("studioStartDebugMode", //$NON-NLS-1$
			StudioStartDebugModeRequest.class),

	/**
	 * @see StudioStartDebugModeRequest
	 */
	STUDIO_STOP_DEBUG_MODE("studioStopDebugMode", //$NON-NLS-1$
			StudioStopDebugModeRequest.class),

	/**
	 * @see StudioStartDebugModeRequest
	 */
	STUDIO_IS_DEBUG_MODE_ENABLED("studioIsDebugModeEnabled", //$NON-NLS-1$
			StudioIsDebugModeEnabledRequest.class),

	/**
	 * @see LibraryGetStatusRequest
	 */
	LIBRARY_GET_STATUS("libraryGetStatus", LibraryGetStatusRequest.class), //$NON-NLS-1$

	/**
	 * @see LibraryVersionGetStatusRequest
	 */
	LIBRARY_VERSION_GET_STATUS("libraryVersionGetStatus", //$NON-NLS-1$
			LibraryVersionGetStatusRequest.class),

	/**
	 * @see LibraryVersionDeployRequest
	 */
	LIBRARY_VERSION_DEPLOY("libraryVersionDeploy", //$NON-NLS-1$
			LibraryVersionDeployRequest.class),

	/**
	 * @see LibrarySynchronizeRequest
	 */
	LIBRARY_SYNCHRONIZE("librarySynchronize", LibrarySynchronizeRequest.class), //$NON-NLS-1$

	/**
	 * @see DownloadLibraryVersionFileRequest
	 */
	DOWNLOAD_LIBRARY_VERSION_FILE("downloadLibraryVersionFile", //$NON-NLS-1$
			DownloadLibraryVersionFileRequest.class),

	/**
	 * @see BootstrapSingleServerRequest
	 */
	BOOTSTRAP_SINGLE_SERVER("bootstrapSingleServer", //$NON-NLS-1$
			BootstrapSingleServerRequest.class),

	/**
	 * @see VhostGetStatusRequest
	 */
	VHOST_GET_STATUS_REQUEST("vhostGetStatusRequest", //$NON-NLS-1$
			VhostGetStatusRequest.class),
	/**
	 * @see VhostGetDetailsRequest
	 */
	VHOST_GET_DETAILS_REQUEST("vhostGetDetails", //$NON-NLS-1$
			VhostGetDetailsRequest.class);
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
