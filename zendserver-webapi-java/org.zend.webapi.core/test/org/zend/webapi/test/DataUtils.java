package org.zend.webapi.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.zend.webapi.core.connection.data.ApplicationInfo;
import org.zend.webapi.core.connection.data.ApplicationServer;
import org.zend.webapi.core.connection.data.ApplicationServers;
import org.zend.webapi.core.connection.data.ApplicationsList;
import org.zend.webapi.core.connection.data.CodeTrace;
import org.zend.webapi.core.connection.data.CodeTracingList;
import org.zend.webapi.core.connection.data.CodeTracingStatus;
import org.zend.webapi.core.connection.data.DeployedVersion;
import org.zend.webapi.core.connection.data.DeployedVersions;
import org.zend.webapi.core.connection.data.IResponseData;
import org.zend.webapi.core.connection.data.IResponseData.ResponseType;
import org.zend.webapi.core.connection.data.LicenseInfo;
import org.zend.webapi.core.connection.data.MessageList;
import org.zend.webapi.core.connection.data.ServerInfo;
import org.zend.webapi.core.connection.data.ServersList;
import org.zend.webapi.core.connection.data.SystemInfo;
import org.zend.webapi.core.connection.data.values.ApplicationStatus;
import org.zend.webapi.core.connection.data.values.LicenseInfoStatus;
import org.zend.webapi.core.connection.data.values.ServerStatus;
import org.zend.webapi.core.connection.data.values.SystemEdition;
import org.zend.webapi.core.connection.data.values.SystemStatus;
import org.zend.webapi.core.connection.data.values.WebApiVersion;

public class DataUtils {

	private static List<String> componentStatus = new ArrayList<String>();

	static {
		componentStatus.add("Active");
		componentStatus.add("Inactive");
	}

	public static void checkValidServersList(ServersList serversList) {
		Assert.assertNotNull(serversList);
		List<ServerInfo> servers = serversList.getServerInfo();
		for (ServerInfo serverInfo : servers) {
			checkValidServerInfo(serverInfo);
		}
	}

	public static void checkValidClusterServerStatus(
			ServersList clusterServerStatus) {
		Assert.assertNotNull(clusterServerStatus);
		Assert.assertEquals(IResponseData.ResponseType.SERVERS_LIST,
				clusterServerStatus.getType());
		List<ServerInfo> servers = clusterServerStatus.getServerInfo();
		for (ServerInfo serverInfo : servers) {
			checkValidServerInfo(serverInfo);
		}
	}

	public static void checkValidServerInfo(ServerInfo serverInfo) {
		Assert.assertNotNull(serverInfo);
		Assert.assertTrue(Integer.valueOf(serverInfo.getId()) instanceof Integer);
		Assert.assertNotNull(serverInfo.getAddress());
		Assert.assertNotNull(serverInfo.getName());
		Assert.assertNotSame(ServerStatus.UNKNOWN, serverInfo.getStatus());
		checkValidMessageList(serverInfo.getMessageList());
	}

	public static void checkValidSystemInfo(SystemInfo systemInfo) {
		Assert.assertNotNull(systemInfo);
		checkValidEdition(systemInfo.getEdition());
		checkValidLicenceInfo(systemInfo.getLicenseInfo());
		checkValidLicenceInfo(systemInfo.getManagerLicenseInfo());
		Assert.assertNotNull(systemInfo.getOperatingSystem());
		Assert.assertNotNull(systemInfo.getPhpVersion());
		checkValidSystemStatus(systemInfo.getStatus());
		checkValidApiVersions(systemInfo.getSupportedApiVersions());
		Assert.assertNotNull(systemInfo.getVersion());
		checkValidMessageList(systemInfo.getMessageList());
	}

	public static void checkValidApiVersions(
			List<WebApiVersion> supportedApiVersions) {
		Assert.assertNotNull(supportedApiVersions);
		for (WebApiVersion webApiVersion : supportedApiVersions) {
			Assert.assertNotNull(webApiVersion.getFullName());
			Assert.assertNotNull(webApiVersion.getVersionName());
		}
	}

	public static void checkValidSystemStatus(SystemStatus status) {
		Assert.assertNotSame(SystemStatus.UNKNOWN,
				SystemStatus.byName(status.getTitle()));
	}

	public static void checkValidLicenceInfo(LicenseInfo licenseInfo) {
		Assert.assertNotNull(licenseInfo);
		LicenseInfoStatus status = licenseInfo.getStatus();
		Assert.assertNotSame(LicenseInfoStatus.UNKNOWN,
				LicenseInfoStatus.byName(status.getName()));
		if (status != LicenseInfoStatus.EXPIRED) {
			Assert.assertNotNull(licenseInfo.getOrderNumber());
			// Assert.assertNotNull(licenseInfo.getValidUntil());
			Assert.assertTrue(licenseInfo.getServerLimit() >= 0);
		}
	}

	public static void checkValidEdition(SystemEdition edition) {
		Assert.assertNotNull(edition);
		boolean isCorrect = edition == SystemEdition.ZEND_SERVER
				|| edition == SystemEdition.ZEND_SERVER_CLUSER_MANAGER
				|| edition == SystemEdition.ZEND_SERVER_COMMUNITY_EDITION;
		Assert.assertTrue(isCorrect);
	}

	public static void checkValidMessageList(MessageList messageList) {
		Assert.assertNotNull(messageList);
		List<String> errors = messageList.getError();
		if (errors != null) {
			for (String error : errors) {
				Assert.assertNotNull(error);
			}
		}
		List<String> infoList = messageList.getInfo();
		if (infoList != null) {
			for (String info : infoList) {
				Assert.assertNotNull(info);
			}
		}
		List<String> warnings = messageList.getWarning();
		if (warnings != null) {
			for (String warining : warnings) {
				Assert.assertNotNull(warining);
			}
		}
	}

	public static void checkValidApplicationsList(
			ApplicationsList applicationsList) {
		Assert.assertNotNull(applicationsList);
		List<ApplicationInfo> appsInfo = applicationsList.getApplicationsInfo();
		for (ApplicationInfo applicationInfo : appsInfo) {
			checkValidApplicationInfo(applicationInfo);
		}
	}

	public static void checkValidApplicationInfo(ApplicationInfo applicationInfo) {
		Assert.assertNotNull(applicationInfo);
		Assert.assertNotNull(applicationInfo.getAppName());
		Assert.assertNotNull(applicationInfo.getUserAppName());
		Assert.assertNotNull(applicationInfo.getBaseUrl());
		Assert.assertNotNull(applicationInfo.getInstalledLocation());
		Assert.assertNotSame(ApplicationStatus.UNKNOWN,
				ApplicationStatus.byName(applicationInfo.getStatus().getName()));
		if (applicationInfo.getMessageList() != null) {
			checkValidMessageList(applicationInfo.getMessageList());
		}
		if (applicationInfo.getDeployedVersions() != null) {
			checkValidDeployedVersions(applicationInfo.getDeployedVersions());
		}
		if (applicationInfo.getServers() != null) {
			checkValidApplicationServers(applicationInfo.getServers());
		}
	}

	public static void checkValidApplicationServers(ApplicationServers servers) {
		Assert.assertNotNull(servers);
		List<ApplicationServer> serversList = servers.getApplicationServers();
		for (ApplicationServer server : serversList) {
			checkValidApplicationServer(server);
		}
	}

	public static void checkValidApplicationServer(ApplicationServer server) {
		Assert.assertNotNull(server);
		Assert.assertNotNull(server.getDeployedVersion());
		Assert.assertNotSame(ApplicationStatus.UNKNOWN,
				ApplicationStatus.byName(server.getStatus().getName()));
	}

	public static void checkValidDeployedVersions(DeployedVersions versionsList) {
		Assert.assertNotNull(versionsList);
		List<DeployedVersion> versions = versionsList.getDeployedVersions();
		if (versions != null) {
			for (DeployedVersion versionInfo : versions) {
				checkValidDeployedVersion(versionInfo);
			}
		}
	}

	public static void checkValidDeployedVersion(
			DeployedVersion deployedVersionInfo) {
		Assert.assertNotNull(deployedVersionInfo);
		Assert.assertNotNull(deployedVersionInfo.getVersion());
	}

	// TODO check if use On|Off or 1|0
	public static void checkValidCodeTracingStatus(CodeTracingStatus status) {
		Assert.assertNotNull(status);
		Assert.assertEquals(ResponseType.CODE_TRACING_STATUS, status.getType());
		Assert.assertNotNull(status.getAlwaysDump());
		Assert.assertNotNull(status.getAwaitsRestart());
		Assert.assertTrue(componentStatus.contains(status.getComponentStatus()));
		Assert.assertNotNull(status.getDeveloperMode());
		Assert.assertNotNull(status.getTraceEnabled());
	}

	public static void checkValidCodeTrace(CodeTrace trace) {
		Assert.assertNotNull(trace);
		Assert.assertEquals(ResponseType.CODE_TRACE, trace.getType());
		Assert.assertNotNull(trace.getApplicationId());
		Assert.assertNotNull(trace.getCreatedBy());
		Assert.assertNotNull(trace.getDate());
		Assert.assertNotNull(trace.getId());
		Assert.assertNotNull(trace.getUrl());
	}

	public static void checkValidCodeTracingList(CodeTracingList traces) {
		Assert.assertNotNull(traces);
		Assert.assertEquals(ResponseType.CODE_TRACING_LIST, traces.getType());
		List<CodeTrace> tracesList = traces.getTraces();
		Assert.assertNotNull(tracesList);
		for (CodeTrace codeTrace : tracesList) {
			checkValidCodeTrace(codeTrace);
		}
	}

}
