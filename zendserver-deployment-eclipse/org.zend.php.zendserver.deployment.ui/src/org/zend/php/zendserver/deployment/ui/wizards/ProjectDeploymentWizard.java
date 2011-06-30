/*******************************************************************************
 * Copyright (c) 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/

package org.zend.php.zendserver.deployment.ui.wizards;

import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.wizard.Wizard;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.php.zendserver.deployment.core.sdk.EclipseMappingModelLoader;
import org.zend.php.zendserver.deployment.core.sdk.SdkStatus;
import org.zend.php.zendserver.deployment.core.utils.DeploymentUtils;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.sdklib.application.ZendApplication;
import org.zend.webapi.core.connection.data.ApplicationInfo;
import org.zend.webapi.core.connection.data.ApplicationsList;
import org.zend.webapi.core.connection.data.values.ApplicationStatus;

public class ProjectDeploymentWizard extends Wizard {

	private DeploymentDescriptorPage descriptorPage;
	private ApplicationParametersPage parametersPage;
	private IDescriptorContainer model;

	public ProjectDeploymentWizard(IDescriptorContainer model) {
		if (!model.getFile().exists()) {
			descriptorPage = new DeploymentDescriptorPage(model);
		}
		this.model = model;
		parametersPage = new ApplicationParametersPage(model);
		setNeedsProgressMonitor(false);
		setWindowTitle(Messages.deployWizardTitle);
		setDefaultPageImageDescriptor(Activator
				.getImageDescriptor(Activator.IMAGE_DEPLOY_WIZARD));
	}

	@Override
	public void addPages() {
		super.addPages();
		if (!model.getFile().exists()) {
			addPage(descriptorPage);
		}
		addPage(parametersPage);
	}

	@Override
	public boolean performFinish() {
		final String name = getName();
		final String root = getRoot();
		final String appName = parametersPage.getUserAppName();
		final String baseUrl = parametersPage.getBaseURL();
		final String path = model.getFile().getParent().getLocation()
				.toString();
		final boolean defaultServer = parametersPage.isDefaultServer();
		final String targetId = parametersPage.getTarget().getId();
		final boolean isIgnoreFailures = parametersPage.isIgnoreFailures();
		final HashMap<String, String> userParams = parametersPage
				.getParameters();
		Job deployJob = new Job("Deploying application...") {
			private StatusChangeListener listener;

			public IStatus run(IProgressMonitor monitor) {
				try {
					listener = new StatusChangeListener(monitor);
					createDeploymentDescriptor(name, root, monitor);
					if (monitor.isCanceled()) {
						return Status.OK_STATUS;
					}
					ZendApplication application = new ZendApplication(
							new EclipseMappingModelLoader());
					application.addStatusChangeListener(listener);

					// TODO adjust to current deploy method
					ApplicationInfo info = null; /*
												 * application.deploy(path,
												 * baseUrl, targetId,
												 * userParams, appName,
												 * isIgnoreFailures,
												 * !defaultServer,
												 * defaultServer);
												 */
					if (monitor.isCanceled()) {
						return Status.OK_STATUS;
					}
					if (info != null
							&& info.getStatus() == ApplicationStatus.STAGING) {
						monitorApplicationStatus(targetId, info.getId(),
								application, monitor);
					}
				} catch (CoreException e) {
					return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
							e.getMessage(), e);
				}
				return new SdkStatus(listener.getStatus());
			}
		};
		deployJob.setUser(true);
		deployJob.schedule();
		return true;
	}

	@Override
	public boolean performCancel() {
		// TODO Auto-generated method stub
		return true;
	}

	private void monitorApplicationStatus(String targetId, int id,
			ZendApplication application, IProgressMonitor monitor) {
		monitor.beginTask("Checking application status...",
				IProgressMonitor.UNKNOWN);
		ApplicationStatus result = null;
		while (result != ApplicationStatus.DEPLOYED) {
			ApplicationsList info = application.getStatus(targetId,
					String.valueOf(id));
			if (info != null && info.getApplicationsInfo() != null) {
				result = info.getApplicationsInfo().get(0).getStatus();
				monitor.subTask("Current status is: " + result.getName());
			}
		}
		monitor.done();

	}

	private void createDeploymentDescriptor(final String name,
			final String folder, IProgressMonitor monitor) throws CoreException {
		IProject project = model.getFile().getProject();
		if (!model.getFile().exists()) {
			monitor.beginTask("Creating deployment descriptor...",
					IProgressMonitor.UNKNOWN);
			DeploymentUtils.createDescriptor(project, name, folder, monitor);
			monitor.done();
			project.refreshLocal(IResource.DEPTH_ONE, monitor);
		}
	}

	private String getRoot() {
		return descriptorPage != null ? descriptorPage.getDocumentRoot() : null;
	}

	private String getName() {
		return descriptorPage != null ? descriptorPage.getApplciationName()
				: null;
	}

}
