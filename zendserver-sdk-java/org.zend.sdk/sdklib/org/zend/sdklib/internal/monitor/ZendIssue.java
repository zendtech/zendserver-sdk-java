/*******************************************************************************
 * Copyright (c) Feb 20, 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.internal.monitor;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.zend.sdklib.logger.ILogger;
import org.zend.sdklib.logger.Log;
import org.zend.sdklib.monitor.IZendIssue;
import org.zend.sdklib.monitor.ZendMonitor;
import org.zend.webapi.core.WebApiClient;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.data.EventsGroup;
import org.zend.webapi.core.connection.data.EventsGroupDetails;
import org.zend.webapi.core.connection.data.Issue;
import org.zend.webapi.core.connection.data.IssueDetails;
import org.zend.webapi.core.connection.data.IssueFile;
import org.zend.webapi.core.connection.data.values.IssueStatus;
import org.zend.webapi.core.progress.BasicStatus;
import org.zend.webapi.core.progress.StatusCode;

/**
 * Internal implementation of {@link IZendIssue}.
 * 
 * @author Wojciech Galanciak, 2012
 */
public class ZendIssue implements IZendIssue {

	private Issue issue;
	private ZendMonitor monitor;
	private ILogger log;

	public ZendIssue(Issue issue, ZendMonitor monitor) {
		this.log = Log.getInstance().getLogger(this.getClass().getName());
		this.issue = issue;
		this.monitor = monitor;
	}

	/**
	 * Factory method to create list of {@link IZendIssue} based on list of
	 * {@link Issue}.
	 * 
	 * @param issues
	 *            list of {@link Issue} instances
	 * @param targetId
	 *            target id
	 * @return
	 */
	public static List<IZendIssue> create(List<Issue> issues,
			ZendMonitor monitor) {
		List<IZendIssue> result = new ArrayList<IZendIssue>();
		for (Issue issue : issues) {
			result.add(new ZendIssue(issue, monitor));
		}
		return result;
	}

	@Override
	public Issue getIssue() {
		return issue;
	}

	@Override
	public IssueDetails getDetails() {
		try {
			return monitor.getClient().monitorGetIssueDetails(issue.getId());
		} catch (WebApiException e) {
			String message = MessageFormat.format(
					"Error during retrieving issue details for '{0}'",
					issue.getId());
			monitor.statusChanged(new BasicStatus(StatusCode.ERROR,
					"Retrieving Issue Details", message, e));
			log.error(message + "':");
			log.error("\tpossible error: " + e.getMessage());
		} catch (MalformedURLException e) {
			String message = MessageFormat.format(
					"Error during retrieving issue details for '{0}'",
					issue.getId());
			monitor.statusChanged(new BasicStatus(StatusCode.ERROR,
					"Retrieving Issue Details", message, e));
			log.error(e);
		}
		return null;
	}

	@Override
	public boolean changeStatus(IssueStatus status) {
		try {
			Issue result = monitor.getClient().monitorChangeIssueStatus(
					issue.getId(), status);
			if (result.getStatus() == status) {
				return true;
			} else {
				log.error("Failed to change issue status for " + issue.getId());
				return false;
			}
		} catch (WebApiException e) {
			String message = MessageFormat.format(
					"Error during changing issue status of '{0}'",
					issue.getId());
			monitor.statusChanged(new BasicStatus(StatusCode.ERROR,
					"Changing Issue Status", message, e));
			log.error(message + "':");
			log.error("\tpossible error: " + e.getMessage());
		} catch (MalformedURLException e) {
			String message = MessageFormat.format(
					"Error during changing issue status of '{0}'",
					issue.getId());
			monitor.statusChanged(new BasicStatus(StatusCode.ERROR,
					"Changing Issue Status", message, e));
			log.error(e);
		}
		return false;
	}

	@Override
	public List<EventsGroupDetails> getGroupDetails() {
		try {
			WebApiClient client = monitor.getClient();
			IssueDetails issueDetails = client.monitorGetIssueDetails(Integer
					.valueOf(issue.getId()));
			if (issueDetails != null && issueDetails.getEventsGroups() != null) {
				List<EventsGroup> groups = issueDetails.getEventsGroups()
						.getGroups();
				List<EventsGroupDetails> result = new ArrayList<EventsGroupDetails>();
				for (EventsGroup group : groups) {
					EventsGroupDetails groupDetails = client
							.monitorGetEventGroupDetails(
									String.valueOf(issue.getId()),
									group.getEventsGroupId());
					if (groupDetails != null) {
						result.add(groupDetails);
					}
				}
				return result;
			}
		} catch (WebApiException e) {
			String message = MessageFormat.format(
					"Error during retrieving group details for '{0}'",
					issue.getId());
			monitor.statusChanged(new BasicStatus(StatusCode.ERROR,
					"Retrieving Group Details", message, e));
			log.error(message + "':");
			log.error("\tpossible error: " + e.getMessage());
		} catch (MalformedURLException e) {
			String message = MessageFormat.format(
					"Error during retrieving group details for '{0}'",
					issue.getId());
			monitor.statusChanged(new BasicStatus(StatusCode.ERROR,
					"Retrieving Group Details", message, e));
			log.error(e);
		}
		return null;
	}

	@Override
	public List<File> export() {
		return export(getTempFolder());
	}

	@Override
	public List<File> export(File destination) {
		try {
			WebApiClient client = monitor.getClient();
			List<EventsGroupDetails> groups = getGroupDetails();
			if (groups != null) {
				List<File> result = new ArrayList<File>();
				for (EventsGroupDetails group : groups) {
					IssueFile issueFile = client
							.monitorExportIssueByEventsGroup(group
									.getEventsGroup().getEventsGroupId());
					result.add(store(issueFile, destination));
				}
				return result;
			}
		} catch (WebApiException e) {
			String message = MessageFormat.format(
					"Error during exporting issue with id {0}", issue.getId());
			monitor.statusChanged(new BasicStatus(StatusCode.ERROR,
					"Exporting Issue File", message, e));
			log.error(message + ":");
			log.error("\tpossible error: " + e.getMessage());
		} catch (MalformedURLException e) {
			String message = MessageFormat.format(
					"Error during exporting issue with id {0}", issue.getId());
			monitor.statusChanged(new BasicStatus(StatusCode.ERROR,
					"Exporting Issue File", message, e));
			log.error(e);
		} catch (IOException e) {
			String message = MessageFormat.format(
					"Error during exporting issue with id {0}", issue.getId());
			monitor.statusChanged(new BasicStatus(StatusCode.ERROR,
					"Exporting Issue File", message, e));
			log.error(e);
		}
		return null;
	}

	private File store(IssueFile issueFile, File destination)
			throws IOException {
		File file = new File(destination, issueFile.getFilename());
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();
		FileOutputStream stream = new FileOutputStream(file);
		stream.write(issueFile.getFileContent());
		closeStream(stream);
		return file;
	}

	private void closeStream(Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				log.error(e);
			}
		}
	}

	private File getTempFolder() {
		String tempDir = System.getProperty("java.io.tmpdir");
		File tempFile = new File(tempDir + File.separator
				+ new Random().nextLong());
		if (!tempFile.exists()) {
			tempFile.mkdir();
		}
		return tempFile;
	}

}
