/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.debug.core.jobs;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.zend.php.zendserver.deployment.core.debugger.LibraryDeployData;
import org.zend.php.zendserver.deployment.core.descriptor.DescriptorContainerManager;
import org.zend.php.zendserver.deployment.core.sdk.EclipseMappingModelLoader;
import org.zend.php.zendserver.deployment.core.sdk.EclipseVariableResolver;
import org.zend.php.zendserver.deployment.core.sdk.SdkStatus;
import org.zend.php.zendserver.deployment.core.sdk.StatusChangeListener2;
import org.zend.php.zendserver.deployment.core.utils.LibraryUtils;
import org.zend.php.zendserver.deployment.debug.core.Activator;
import org.zend.php.zendserver.deployment.debug.core.Messages;
import org.zend.sdklib.application.ZendLibrary;
import org.zend.webapi.core.connection.data.GenericResponseDataVisitor;
import org.zend.webapi.core.connection.data.LibraryInfo;
import org.zend.webapi.core.connection.data.LibraryList;
import org.zend.webapi.core.connection.data.LibraryVersion;
import org.zend.webapi.core.connection.data.LibraryVersions;
import org.zend.webapi.core.connection.response.ResponseCode;
import org.zend.webapi.internal.core.connection.exception.WebApiCommunicationError;

/**
 * Job responsible for deploying PHP Library to selected Zend Target.
 * 
 * @author Wojciech Galanciak, 2013
 * 
 */
public class DeployLibraryJob extends AbstractLibraryJob {

	private class DeployLibraryResponseDataVisitor extends GenericResponseDataVisitor {
		private int libraryVersionId = -1;
		
		@Override
		public boolean visit(LibraryVersion libraryVersion) {
			libraryVersionId = libraryVersion.getLibraryVersionId();
			return true;
		}

		public int getLibraryVersionId() {
			return libraryVersionId;
		}
	}

	public DeployLibraryJob(LibraryDeployData data) {
		super(Messages.DeployLibraryJob_Name, data);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	public IStatus run(IProgressMonitor monitor) {
		try {
			String taskName = MessageFormat.format(Messages.DeployLibraryJob_TaskName, getData().getName(),
					getData().getVersion());
			monitor.beginTask(taskName, IProgressMonitor.UNKNOWN);
			StatusChangeListener2 listener = new StatusChangeListener2(monitor);
			ZendLibrary lib = new ZendLibrary(new EclipseMappingModelLoader());
			lib.addStatusChangeListener(listener);
			lib.setVariableResolver(new EclipseVariableResolver());

			monitor.subTask(Messages.DeployLibraryJob_CheckingLibraryVersionSubTask_Name);
			if (isLibraryVersionAvailable(lib, data)) {
				this.responseCode = ResponseCode.LIBRARY_CONFLICT;
				return Status.OK_STATUS;
			}
			if (monitor.isCanceled()) {
				return Status.CANCEL_STATUS;
			}

			monitor.subTask(Messages.DeployLibraryJob_PreparingDeploymenySubTask_Name);
			LibraryList result = null;
			if ((data.isZpkPackage() && data.getRoot().getName().endsWith(".zpk")) //$NON-NLS-1$
					|| new File(data.getRoot(), DescriptorContainerManager.DESCRIPTOR_PATH).exists()) {
				result = lib.deploy(data.getRoot().getAbsolutePath(), data.getTargetId(), data.isZpkPackage());
			} else {
				try {
					File root = LibraryUtils.getTemporaryDescriptor(data.getName(), data.getVersion());
					result = lib.deploy(data.getRoot().getAbsolutePath(), root.getAbsolutePath(), data.getTargetId(),
							data.isZpkPackage());
					FileUtils.deleteDirectory(root);
				} catch (IOException e) {
					Activator.log(e);
					return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
							Messages.DeployLibraryJob_TemporaryDeploymentDescriptor_Error, e);
				}
			}
			Throwable exception = listener.getStatus().getThrowable();
			if (exception instanceof WebApiCommunicationError) {
				return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
						Messages.DeployLibraryJob_ConnectionRefused_Error, exception);
			}
			if(listener.getStatus() == Status.OK_STATUS) {
				DeployLibraryResponseDataVisitor dataVisitor = new DeployLibraryResponseDataVisitor();
				result.accept(dataVisitor);
				data.setVersionId(dataVisitor.getLibraryVersionId());
			}
			return new SdkStatus(listener.getStatus());
		} finally {
			monitor.done();
		}
	}

	private boolean isLibraryVersionAvailable(ZendLibrary zendLibrary, LibraryDeployData deployData) {
		LibraryList list = zendLibrary.getStatus(deployData.getTargetId());
		if (list == null)
			return false;

		List<LibraryInfo> libs = list.getLibrariesInfo();
		for (LibraryInfo libraryInfo : libs) {
			String name = libraryInfo.getLibraryName();
			if (!name.equals(deployData.getName()))
				continue;

			LibraryVersions libraryVersions = libraryInfo.getLibraryVersions();
			if (libraryVersions == null)
				continue;

			List<LibraryVersion> libVersions = libraryVersions.getVersions();
			for (LibraryVersion libraryVersion : libVersions) {
				String version = libraryVersion.getVersion();
				if (version.equals(deployData.getVersion()))
					return true;
			}
		}
		return false;
	}
}
