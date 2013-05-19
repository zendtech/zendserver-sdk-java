/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.library.core.deploy;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.zend.php.library.core.LibraryUtils;
import org.zend.php.library.internal.core.LibraryCore;
import org.zend.php.zendserver.deployment.core.descriptor.DescriptorContainerManager;
import org.zend.php.zendserver.deployment.core.sdk.EclipseMappingModelLoader;
import org.zend.php.zendserver.deployment.core.sdk.EclipseVariableResolver;
import org.zend.php.zendserver.deployment.core.sdk.SdkStatus;
import org.zend.php.zendserver.deployment.core.sdk.StatusChangeListener;
import org.zend.php.zendserver.deployment.debug.core.Activator;
import org.zend.php.zendserver.deployment.debug.core.Messages;
import org.zend.sdklib.application.ZendLibrary;
import org.zend.webapi.core.connection.data.LibraryInfo;
import org.zend.webapi.core.connection.data.LibraryList;
import org.zend.webapi.core.connection.data.LibraryVersion;
import org.zend.webapi.core.connection.data.LibraryVersions;
import org.zend.webapi.internal.core.connection.exception.UnexpectedResponseCode;
import org.zend.webapi.internal.core.connection.exception.WebApiCommunicationError;

/**
 * Job responsible for synchronizing concrete library in specific version on
 * selected Zend Target.
 * 
 * @author Wojciech Galanciak, 2013
 * 
 */
public class SynchronizeLibraryJob extends AbstractLibraryJob {

	public SynchronizeLibraryJob(LibraryDeployData data) {
		super(Messages.deploymentJob_Title, data);
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
		int id = findLibraryId(lib, data.getTargetId());
		if (id != -1) {
			if (new File(data.getRoot(),
					DescriptorContainerManager.DESCRIPTOR_PATH).exists()) {
				lib.synchronize(data.getRoot().getAbsolutePath(), id,
						data.getTargetId());
			} else {
				try {
					File root = LibraryUtils.getTemporaryDescriptor(
							data.getName(), data.getVersion());
					lib.synchronize(data.getRoot().getAbsolutePath(),
							root.getAbsolutePath(), id, data.getTargetId());
				} catch (IOException e) {
					LibraryCore.log(e);
				}
			}
			if (monitor.isCanceled()) {
				return Status.CANCEL_STATUS;
			}
			Throwable exception = listener.getStatus().getThrowable();
			if (exception instanceof UnexpectedResponseCode) {
				UnexpectedResponseCode codeException = (UnexpectedResponseCode) exception;
				responseCode = codeException.getResponseCode();
				switch (responseCode) {
				// TODO change to the different error code
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
		return Status.CANCEL_STATUS;
	}

	private int findLibraryId(ZendLibrary library, String targetId) {
		String version = data.getVersion();
		String name = data.getName();
		LibraryList list = library.getStatus(targetId);
		if (list != null) {
			List<LibraryInfo> libs = list.getLibrariesInfo();
			for (LibraryInfo libraryInfo : libs) {
				if (libraryInfo.getLibraryName().equals(name)) {
					LibraryVersions versions = libraryInfo.getLibraryVersions();
					if (versions != null) {
						List<LibraryVersion> libVersions = versions
								.getVersions();
						for (LibraryVersion libraryVersion : libVersions) {
							if (libraryVersion.getVersion().equals(version)) {
								return libraryVersion.getLibraryVersionId();
							}
						}
					}
				}
			}
		}
		return -1;
	}

}
