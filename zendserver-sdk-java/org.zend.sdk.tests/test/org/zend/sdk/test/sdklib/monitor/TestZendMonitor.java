package org.zend.sdk.test.sdklib.monitor;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.zend.sdk.test.AbstractWebApiTest;
import org.zend.sdklib.internal.target.UserBasedTargetLoader;
import org.zend.sdklib.monitor.ZendMonitor;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.data.IResponseData;
import org.zend.webapi.core.connection.data.IssueList;

public class TestZendMonitor extends AbstractWebApiTest {

	protected ZendMonitor monitor;

	@Test
	public void testGetAllIssues() throws WebApiException, IOException {
		when(
				client.monitorGetIssuesListPredefinedFilter(anyString(),
						Mockito.any(Integer.class), Mockito.any(Integer.class),
						anyString(), anyString())).thenReturn(
				(IssueList) getResponseData(
						"monitorGetIssuesListPredefinedFilter",
						IResponseData.ResponseType.ISSUE_LIST));
		// assertNotNull(monitor.getAllIssues());
	}

	@Test
	public void testGetById() throws WebApiException, IOException {
		when(
				client.monitorGetIssuesListPredefinedFilter(anyString(),
						Mockito.any(Integer.class), Mockito.any(Integer.class),
						anyString(), anyString())).thenReturn(
				(IssueList) getResponseData(
						"monitorGetIssuesListPredefinedFilter",
						IResponseData.ResponseType.ISSUE_LIST));
		// assertNotNull(monitor.get(11));
	}

	@Test
	public void testGetCriticalErrors() throws WebApiException, IOException {
		when(
				client.monitorGetIssuesListPredefinedFilter(anyString(),
						Mockito.any(Integer.class), Mockito.any(Integer.class),
						anyString(), anyString())).thenReturn(
				(IssueList) getResponseData(
						"monitorGetIssuesListPredefinedFilter",
						IResponseData.ResponseType.ISSUE_LIST));
		// assertNotNull(monitor.getCriticalErrors());
	}

	@Test
	public void testGetOpenIssues() throws WebApiException, IOException {
		when(
				client.monitorGetIssuesListPredefinedFilter(anyString(),
						Mockito.any(Integer.class), Mockito.any(Integer.class),
						anyString(), anyString())).thenReturn(
				(IssueList) getResponseData(
						"monitorGetIssuesListPredefinedFilter",
						IResponseData.ResponseType.ISSUE_LIST));
		// assertNotNull(monitor.getOpenIssues());
	}

	@Test
	public void testGetPerformanceIssues() throws WebApiException, IOException {
		when(
				client.monitorGetIssuesListPredefinedFilter(anyString(),
						Mockito.any(Integer.class), Mockito.any(Integer.class),
						anyString(), anyString())).thenReturn(
				(IssueList) getResponseData(
						"monitorGetIssuesListPredefinedFilter",
						IResponseData.ResponseType.ISSUE_LIST));
		// assertNotNull(monitor.getPerformanceIssues());
	}

	@Before
	public void startup() throws MalformedURLException {
		super.startup();
		monitor = spy(new ZendMonitor("targetId", new UserBasedTargetLoader()));
		doReturn(client).when(monitor).getClient(anyString());
	}

}
