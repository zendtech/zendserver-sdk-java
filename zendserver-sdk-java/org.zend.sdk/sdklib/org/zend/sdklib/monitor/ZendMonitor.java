/*******************************************************************************
 * Copyright (c) Feb 20, 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.monitor;

import java.net.MalformedURLException;
import java.text.MessageFormat;
import java.util.List;

import org.zend.sdklib.internal.application.ZendConnection;
import org.zend.sdklib.internal.monitor.ZendIssue;
import org.zend.sdklib.mapping.IMappingLoader;
import org.zend.sdklib.target.ITargetLoader;
import org.zend.webapi.core.WebApiClient;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.data.Issue;
import org.zend.webapi.core.connection.data.IssueList;
import org.zend.webapi.core.progress.BasicStatus;
import org.zend.webapi.core.progress.StatusCode;

/**
 * Utility class which provides methods to perform operation related to code
 * monitor feature provided by Zend Server.
 * 
 * @author Wojciech Galanciak, 2012
 */
public class ZendMonitor extends ZendConnection {

	/**
	 * Predefined filters.
	 */
	public enum Filters {

		ALL_OPEN_EVENTS("All Open Events"),

		ALL_EVENTS("All Events"),

		PERFORMANCE_ISSUES("Performance Issues"),

		CRITICAL_ERRORS("Critical Errors");

		private String name;

		private Filters(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

	}

	private String targetId;

	public ZendMonitor(String targetId) {
		super();
		this.targetId = targetId;
	}

	public ZendMonitor(String targetId, IMappingLoader mappingLoader) {
		super(mappingLoader);
		this.targetId = targetId;
	}

	public ZendMonitor(String targetId, ITargetLoader loader) {
		super(loader);
		this.targetId = targetId;
	}

	public ZendMonitor(String targetId, ITargetLoader loader,
			IMappingLoader mappingLoader) {
		super(loader, mappingLoader);
		this.targetId = targetId;
	}

	/**
	 * Provides list of issues by using {@link Filters#ALL_EVENTS} filter.
	 * 
	 * @return list of issues
	 */
	public List<IZendIssue> getAllIssues() {
		try {
			IssueList list = getClient(targetId)
					.monitorGetIssuesListPredefinedFilter(
							Filters.ALL_EVENTS.getName(), null, null, null,
							null);
			if (list != null) {
				return ZendIssue.create(list.getIssues(), this);
			}
		} catch (WebApiException e) {
			String message = MessageFormat.format(
					"Error during retrieving all issues from '{0}'", targetId);
			notifier.statusChanged(new BasicStatus(StatusCode.ERROR,
					"Retrieving All Issues", message, e));
			log.error(message + "':");
			log.error("\tpossible error: " + e.getMessage());
		} catch (MalformedURLException e) {
			String message = MessageFormat.format(
					"Error during retrieving all issues from '{0}'", targetId);
			notifier.statusChanged(new BasicStatus(StatusCode.ERROR,
					"Retrieving All Issues", message, e));
			log.error(e);
		}
		return null;
	}

	/**
	 * Provides issue instance based on specified id.
	 * 
	 * @return concrete issue
	 */
	public IZendIssue get(int issueId) {
		try {
			IssueList list = getClient(targetId)
					.monitorGetIssuesListPredefinedFilter(
							Filters.ALL_EVENTS.getName(), null, null, null,
							null);
			if (list != null) {
				List<Issue> issues = list.getIssues();
				for (Issue issue : issues) {
					if (issue.getId() == issueId) {
						return new ZendIssue(issue, this);
					}
				}
			}
		} catch (WebApiException e) {
			String message = MessageFormat.format(
					"Error during retrieving issue with id '{0}'", issueId);
			notifier.statusChanged(new BasicStatus(StatusCode.ERROR,
					"Retrieving Issue", message, e));
			log.error(message + "':");
			log.error("\tpossible error: " + e.getMessage());
		} catch (MalformedURLException e) {
			String message = MessageFormat.format(
					"Error during retrieving issue with id '{0}'", issueId);
			notifier.statusChanged(new BasicStatus(StatusCode.ERROR,
					"Retrieving Issue", message, e));
			log.error(e);
		}
		return null;
	}

	/**
	 * Provides list of issues by using {@link Filters#ALL_OPEN_EVENTS} filter.
	 * 
	 * @return list of issues
	 */
	public List<IZendIssue> getOpenIssues() {
		try {
			IssueList list = getClient(targetId)
					.monitorGetIssuesListPredefinedFilter(
							Filters.ALL_OPEN_EVENTS.getName(), null, null,
							null, null);
			if (list != null) {
				return ZendIssue.create(list.getIssues(), this);
			}
		} catch (WebApiException e) {
			String message = MessageFormat.format(
					"Error during retrieving all open issues from '{0}'",
					targetId);
			notifier.statusChanged(new BasicStatus(StatusCode.ERROR,
					"Retrieving All Open Issues", message, e));
			log.error(message + "':");
			log.error("\tpossible error: " + e.getMessage());
		} catch (MalformedURLException e) {
			String message = MessageFormat.format(
					"Error during retrieving all open issues from '{0}'",
					targetId);
			notifier.statusChanged(new BasicStatus(StatusCode.ERROR,
					"Retrieving All Open Issues", message, e));
			log.error(e);
		}
		return null;
	}

	/**
	 * Provides list of issues by using {@link Filters#CRITICAL_ERRORS} filter.
	 * 
	 * @return list of issues
	 */
	public List<IZendIssue> getCriticalErrors() {
		try {
			IssueList list = getClient(targetId)
					.monitorGetIssuesListPredefinedFilter(
							Filters.CRITICAL_ERRORS.getName(), null, null,
							null, null);
			if (list != null) {
				return ZendIssue.create(list.getIssues(), this);
			}
		} catch (WebApiException e) {
			String message = MessageFormat.format(
					"Error during retrieving critical errors from '{0}'",
					targetId);
			notifier.statusChanged(new BasicStatus(StatusCode.ERROR,
					"Retrieving Critical Errors", message, e));
			log.error(message + "':");
			log.error("\tpossible error: " + e.getMessage());
		} catch (MalformedURLException e) {
			String message = MessageFormat.format(
					"Error during retrieving critical errors from '{0}'",
					targetId);
			notifier.statusChanged(new BasicStatus(StatusCode.ERROR,
					"Retrieving Critical Errors", message, e));
			log.error(e);
		}
		return null;
	}

	/**
	 * Provides list of issues by using {@link Filters#PERFORMANCE_ISSUES}
	 * filter.
	 * 
	 * @return list of issues
	 */
	public List<IZendIssue> getPerformanceIssues() {
		try {
			IssueList list = getClient(targetId)
					.monitorGetIssuesListPredefinedFilter(
							Filters.PERFORMANCE_ISSUES.getName(), null, null,
							null, null);
			if (list != null) {
				return ZendIssue.create(list.getIssues(), this);
			}
		} catch (WebApiException e) {
			String message = MessageFormat.format(
					"Error during retrieving performance issues from '{0}'",
					targetId);
			notifier.statusChanged(new BasicStatus(StatusCode.ERROR,
					"Retrieving Performance Issues", message, e));
			log.error(message + "':");
			log.error("\tpossible error: " + e.getMessage());
		} catch (MalformedURLException e) {
			String message = MessageFormat.format(
					"Error during retrieving performance issues from '{0}'",
					targetId);
			notifier.statusChanged(new BasicStatus(StatusCode.ERROR,
					"Retrieving Performance Issues", message, e));
			log.error(e);
		}
		return null;
	}

	public WebApiClient getClient() throws MalformedURLException {
		return getClient(targetId);
	}

}
