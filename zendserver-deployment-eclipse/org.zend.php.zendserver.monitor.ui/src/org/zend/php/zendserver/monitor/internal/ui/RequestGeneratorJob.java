/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.monitor.internal.ui;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.zend.core.notifications.NotificationManager;
import org.zend.sdklib.monitor.IZendIssue;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.data.Event;
import org.zend.webapi.core.connection.data.EventsGroupDetails;
import org.zend.webapi.core.connection.data.Parameter;
import org.zend.webapi.core.connection.data.ParameterList;
import org.zend.webapi.core.connection.data.SuperGlobals;
import org.zend.webapi.internal.core.connection.exception.InvalidResponseException;

/**
 * Recreate and execute HTTP request based on provided request parameters.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class RequestGeneratorJob extends Job {

	private IZendIssue zendIssue;

	public RequestGeneratorJob(IZendIssue zendIssue) {
		super(Messages.RequestGeneratorJob_RepeatTaskTitle);
		this.zendIssue = zendIssue;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		NotificationManager.registerProgress(
				Messages.RequestGeneratorJob_NotificationTitle,
				Messages.RequestGeneratorJob_RepeatTaskTitle,
				new IRunnableWithProgress() {

					public void run(IProgressMonitor monitor)
							throws InvocationTargetException,
							InterruptedException {
						monitor.beginTask(
								Messages.RequestGeneratorJob_RepeatTaskTitle,
								IProgressMonitor.UNKNOWN);
						final int result = generate();
						switch (result) {
						case 200:
							NotificationManager
									.registerInfo(
											Messages.RequestGeneratorJob_MessageTitle,
											MessageFormat
													.format(Messages.RequestGeneratorJob_MessageBody,
															result), 3000);

							break;
						case -1:
							NotificationManager
									.registerInfo(
											Messages.RequestGeneratorJob_MessageTitle,
											Messages.RequestGeneratorJob_RepeatFailedMessage,
											3000);
						default:
							break;
						}
						monitor.done();
					}
				}, false);
		return Status.OK_STATUS;
	}

	/**
	 * Create and execute HTTP request based on provided issue.
	 * 
	 * @param zendIssue
	 * @return HTTP response code
	 * @throws IOException
	 */
	private int generate() {
		String url = zendIssue.getIssue().getGeneralDetails().getUrl();
		List<EventsGroupDetails> groups = null;
		try {
			groups = zendIssue.getGroupDetails();
		} catch (InvalidResponseException e) {
			showMessage(
					Messages.RequestGeneratorJob_RepeatActionTitle,
					Messages.RequestGeneratorJob_RepeatActionNotSupportedMessage);
			return -1;
		} catch (WebApiException e) {
			showMessage(Messages.RequestGeneratorJob_RepeatActionTitle,
					Messages.RequestGeneratorJob_RepeatActionFailedMessage);
			return -1;
		}
		if (groups != null && groups.size() > 0) {

			HttpClient client = new HttpClient();
			HttpMethodBase method = null;

			Event event = groups.get(0).getEvent();
			SuperGlobals globals = event.getSuperGlobals();
			if (globals != null) {
				ParameterList params = globals.getPost();
				if (params != null) {
					List<Parameter> parameters = params.getParameters();
					if (parameters != null && parameters.size() > 0) {
						method = createPostRequest(url, parameters);
					}
				}
				params = globals.getGet();
				if (params != null) {
					if (method != null) {
						setGetParams(method, params);
					} else {
						method = createGetRequest(url, params);
					}
				}
				params = globals.getCookie();
				if (method != null && params != null) {
					setCookies(method, params);
				}
			}

			if (method != null) {
				int statusCode = -1;
				try {
					statusCode = client.executeMethod(method);
				} catch (IOException e) {
					Activator.log(e);
				} finally {
					method.releaseConnection();
				}
				return statusCode;
			}
		}
		return -1;
	}

	private HttpMethodBase createGetRequest(String url, ParameterList paramsList) {
		GetMethod method = new GetMethod(url);
		setGetParams(method, paramsList);
		return method;
	}

	private HttpMethodBase createPostRequest(String url,
			List<Parameter> parameters) {
		PostMethod method = new PostMethod(url);
		NameValuePair[] query = new NameValuePair[parameters.size()];
		for (int i = 0; i < query.length; i++) {
			Parameter param = parameters.get(i);
			method.addParameter(param.getName(), param.getValue());
		}
		return method;
	}

	private void setGetParams(HttpMethodBase method, ParameterList paramsList) {
		List<Parameter> params = paramsList.getParameters();
		if (params != null) {
			NameValuePair[] query = new NameValuePair[params.size()];
			for (int i = 0; i < query.length; i++) {
				Parameter param = params.get(i);
				query[i] = new NameValuePair(param.getName(), param.getValue());
			}
			method.setQueryString(query);
		}
	}

	private HttpMethodBase setCookies(HttpMethodBase method,
			ParameterList paramsList) {
		List<Parameter> params = paramsList.getParameters();
		if (params != null) {
			StringBuilder builder = new StringBuilder();
			for (Parameter parameter : params) {
				builder.append(parameter.getName());
				builder.append("="); //$NON-NLS-1$
				builder.append(parameter.getValue());
				builder.append(";"); //$NON-NLS-1$
			}
			String value = builder.toString();
			if (value.length() > 0) {
				value = value.substring(0, value.length() - 1);
				method.setRequestHeader("Cookie", value); //$NON-NLS-1$
			}
		}
		return method;
	}

	private void showMessage(final String title, final String message) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				MessageDialog.openInformation(
						org.zend.core.notifications.Activator.getDefault()
								.getParent(), title, message);
			}
		});
	}

}
