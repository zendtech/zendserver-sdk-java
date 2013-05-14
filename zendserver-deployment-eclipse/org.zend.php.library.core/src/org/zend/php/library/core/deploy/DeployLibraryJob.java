/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.library.core.deploy;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.zend.php.zendserver.deployment.core.debugger.IDeploymentHelper;
import org.zend.php.zendserver.deployment.core.sdk.EclipseMappingModelLoader;
import org.zend.php.zendserver.deployment.core.sdk.EclipseVariableResolver;
import org.zend.php.zendserver.deployment.core.sdk.SdkStatus;
import org.zend.php.zendserver.deployment.core.sdk.StatusChangeListener;
import org.zend.php.zendserver.deployment.debug.core.Activator;
import org.zend.php.zendserver.deployment.debug.core.Messages;
import org.zend.php.zendserver.deployment.debug.core.jobs.AbstractLaunchJob;
import org.zend.sdklib.application.ZendLibrary;
import org.zend.webapi.core.connection.response.ResponseCode;
import org.zend.webapi.internal.core.connection.exception.UnexpectedResponseCode;
import org.zend.webapi.internal.core.connection.exception.WebApiCommunicationError;

/**
 * Job responsible for deploying PHP Library to selected Zend Target.
 * 
 * @author Wojciech Galanciak, 2013
 * 
 */
public class DeployLibraryJob extends AbstractLaunchJob {

	public DeployLibraryJob(IDeploymentHelper helper, IProject project) {
		super(Messages.deploymentJob_Title, helper, project.getLocation()
				.toString());
	}

	public DeployLibraryJob(IDeploymentHelper helper, String projectPath) {
		super(Messages.deploymentJob_Title, helper, projectPath);
	}

	private ResponseCode responseCode;

	protected DeployLibraryJob(String name, IDeploymentHelper helper,
			String projectPath) {
		super(name, helper, projectPath);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	public IStatus run(IProgressMonitor monitor) {
		StatusChangeListener listener = new StatusChangeListener(monitor);
		ZendLibrary lib = new ZendLibrary(new EclipseMappingModelLoader());
		lib.addStatusChangeListener(listener);
		lib.setVariableResolver(new EclipseVariableResolver());
		lib.deploy(projectPath, helper.getTargetId());
		if (monitor.isCanceled()) {
			return Status.CANCEL_STATUS;
		}
		Throwable exception = listener.getStatus().getThrowable();
		if (exception instanceof UnexpectedResponseCode) {
			UnexpectedResponseCode codeException = (UnexpectedResponseCode) exception;
			responseCode = codeException.getResponseCode();
			switch (responseCode) {
			// TODO change it to a different error code
			case INTERNAL_SERVER_ERROR:
				return Status.OK_STATUS;
				// case INTERNAL_SERVER_ERROR:
				// return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
				// codeException.getMessage(), codeException);
			default:
				break;
			}
		} else if (exception instanceof WebApiCommunicationError) {
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					Messages.DeploymentLaunchJob_ConnectionRefusedMessage);
		}
		return new SdkStatus(listener.getStatus());
	}

	public ResponseCode getResponseCode() {
		return responseCode;
	}

}
