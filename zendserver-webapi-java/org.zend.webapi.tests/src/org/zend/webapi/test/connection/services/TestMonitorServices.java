/*******************************************************************************
 * Copyright (c) Feb 13, 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.test.connection.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import junit.framework.Assert;

import org.junit.Test;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.data.EventsGroupDetails;
import org.zend.webapi.core.connection.data.Issue;
import org.zend.webapi.core.connection.data.IssueDetails;
import org.zend.webapi.core.connection.data.IssueFile;
import org.zend.webapi.core.connection.data.IssueList;
import org.zend.webapi.core.connection.data.RequestSummary;
import org.zend.webapi.core.connection.data.values.IssueStatus;
import org.zend.webapi.core.connection.response.ResponseCode;
import org.zend.webapi.test.AbstractTestServer;
import org.zend.webapi.test.Configuration;
import org.zend.webapi.test.DataUtils;

public class TestMonitorServices extends AbstractTestServer {

	public static final String CONFIG_FOLDER = "issue/";
	public static final String EXAMPLE_CODE_TRACE = "abc.zsf";

	@Test
	public void testMonitorGetRequestSummary() throws WebApiException,
			MalformedURLException {
		initMock(handler.monitorGetRequestSummary(),
				"monitorGetRequestSummary",
				ResponseCode.OK);
		RequestSummary summary = Configuration.getClient()
				.monitorGetRequestSummary("00000000000000000000000000000001");
		DataUtils.checkValidRequestSummary(summary);
	}

	@Test
	public void testMonitorGetIssuesListPredefinedFilter()
			throws WebApiException, MalformedURLException {
		initMock(handler.monitorGetIssuesListPredefinedFilter(),
				"monitorGetIssuesListByPredefinedFilter", ResponseCode.OK);
		IssueList issueList = Configuration.getClient()
				.monitorGetIssuesListPredefinedFilter("All Open Events", null, null,
						null, null);
		DataUtils.checkValidIssueList(issueList);
	}

	@Test
	public void testMonitorGetIssueDetails() throws WebApiException,
			MalformedURLException {
		initMock(handler.monitorGetIssueDetails(), "monitorGetIssueDetails",
				ResponseCode.OK);
		IssueDetails issueDetails = Configuration.getClient()
				.monitorGetIssueDetails(30);
		DataUtils.checkValidIssueDetails(issueDetails);
	}

	@Test
	public void testMonitorGetEventGroupDetails() throws WebApiException,
			MalformedURLException {
		initMock(handler.monitorGetEventGroupDetails(),
				"monitorGetEventGroupDetails",
				ResponseCode.OK);
		EventsGroupDetails details = Configuration.getClient()
				.monitorGetEventGroupDetails("1", 1);
		DataUtils.checkValidEventsGroupDetails(details);
	}

	@Test
	public void testMonitorExportIssueByEventsGroup() throws WebApiException,
			FileNotFoundException, IOException {
		initFileMock(handler.monitorExportIssueByEventsGroup(),
				"monitorExportIssueByEventsGroup", ResponseCode.OK,
				CONFIG_FOLDER, EXAMPLE_CODE_TRACE);
		IssueFile file = Configuration.getClient()
				.monitorExportIssueByEventsGroup(0);
		Assert.assertTrue(file.getFileSize() > 0);
		Assert.assertNotNull(file.getFilename());
		Assert.assertNotNull(file.getFileContent());
		Assert.assertEquals(file.getFileSize(), file.getFileContent().length);
	}

	@Test
	public void testMonitorChangeIssueStatus() throws WebApiException,
			MalformedURLException {
		initMock(handler.monitorChangeIssueStatus(),
				"monitorChangeIssueStatus", ResponseCode.OK);
		Issue issue = Configuration.getClient()
				.monitorChangeIssueStatus(1, IssueStatus.CLOSED);
		DataUtils.checkValidIssue(issue);
	}

}
