package org.zend.sdk.test.sdklib.monitor;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.zend.sdklib.internal.monitor.ZendIssue;
import org.zend.sdklib.monitor.IZendIssue;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.data.EventsGroupDetails;
import org.zend.webapi.core.connection.data.IResponseData;
import org.zend.webapi.core.connection.data.Issue;
import org.zend.webapi.core.connection.data.IssueDetails;
import org.zend.webapi.core.connection.data.values.IssueStatus;

public class TestZendIssue extends TestZendMonitor {

	private IZendIssue issue;

	@Test
	public void testGetIssueDetails() throws WebApiException, IOException {
		when(client.monitorGetIssueDetails(Mockito.anyInt())).thenReturn(
				(IssueDetails) getResponseData("monitorGetIssueDetails",
						IResponseData.ResponseType.ISSUE_DETAILS));
		assertNotNull(issue.getDetails());
	}

	@Test
	public void testGetGroupsDetails() throws WebApiException, IOException {
		when(client.monitorGetIssueDetails(Mockito.anyInt())).thenReturn(
				(IssueDetails) getResponseData("monitorGetIssueDetails",
						IResponseData.ResponseType.ISSUE_DETAILS));
		when(
				client.monitorGetEventGroupDetails(Mockito.anyString(),
						Mockito.anyInt())).thenReturn(
				(EventsGroupDetails) getResponseData(
						"monitorGetEventGroupDetails",
						IResponseData.ResponseType.EVENTS_GROUP_DETAILS));
		assertNotNull(issue.getGroupDetails());
	}

	@Test
	public void testChangeStatus() throws WebApiException, IOException {
		when(
				client.monitorChangeIssueStatus(Mockito.anyInt(),
						Mockito.any(IssueStatus.class))).thenReturn(
				(Issue) getResponseData("monitorChangeIssueStatus",
						IResponseData.ResponseType.ISSUE));
		assertNotNull(issue.changeStatus(IssueStatus.CLOSED));
	}

	@Before
	public void startup() throws MalformedURLException {
		super.startup();
		issue = spy(new ZendIssue(Mockito.mock(Issue.class), monitor));
	}

}
