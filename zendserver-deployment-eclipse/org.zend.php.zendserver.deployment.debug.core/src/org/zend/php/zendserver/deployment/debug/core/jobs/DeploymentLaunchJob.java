package org.zend.php.zendserver.deployment.debug.core.jobs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.zend.php.zendserver.deployment.core.debugger.IDeploymentHelper;
import org.zend.php.zendserver.deployment.core.sdk.EclipseMappingModelLoader;
import org.zend.php.zendserver.deployment.core.sdk.EclipseVariableResolver;
import org.zend.php.zendserver.deployment.core.sdk.SdkStatus;
import org.zend.php.zendserver.deployment.core.sdk.StatusChangeListener;
import org.zend.php.zendserver.deployment.debug.core.Activator;
import org.zend.php.zendserver.deployment.debug.core.IDeploymentContribution;
import org.zend.php.zendserver.deployment.debug.core.Messages;
import org.zend.php.zendserver.deployment.debug.core.config.DeploymentHelper;
import org.zend.sdklib.application.ZendApplication;
import org.zend.webapi.core.connection.data.ApplicationInfo;
import org.zend.webapi.core.connection.data.ApplicationsList;
import org.zend.webapi.core.connection.data.values.ApplicationStatus;
import org.zend.webapi.core.connection.response.ResponseCode;
import org.zend.webapi.internal.core.connection.exception.UnexpectedResponseCode;
import org.zend.webapi.internal.core.connection.exception.WebApiCommunicationError;

public abstract class DeploymentLaunchJob extends AbstractLaunchJob {

	private ResponseCode responseCode;

	protected DeploymentLaunchJob(String name, IDeploymentHelper helper,
			String projectPath) {
		super(name, helper, projectPath);
	}

	@Override
	public IStatus run(IProgressMonitor monitor) {
		StatusChangeListener listener = new StatusChangeListener(monitor);
		ZendApplication app = new ZendApplication(
				new EclipseMappingModelLoader());
		app.addStatusChangeListener(listener);
		app.setVariableResolver(new EclipseVariableResolver());
		List<IDeploymentContribution> contributions = getContributions();
		IStatus status = null;
		for (IDeploymentContribution c : contributions) {
			status = c.performBefore(monitor, helper);
			if (status.getSeverity() != IStatus.OK) {
				return status;
			}
		}
		ApplicationInfo info = performOperation(app, projectPath);
		if (monitor.isCanceled()) {
			return Status.CANCEL_STATUS;
		}
		if (info != null && info.getStatus() == ApplicationStatus.STAGING) {
			helper.setAppId(info.getId());
			status = monitorApplicationStatus(listener, helper.getTargetId(),
					info.getId(), app, monitor);
			if (status.getSeverity() == IStatus.OK) {
				for (IDeploymentContribution c : contributions) {
					status = c.performAfter(monitor, helper);
					if (status.getSeverity() != IStatus.OK) {
						return status;
					}
				}
			} else {
				return status;
			}
		}
		Throwable exception = listener.getStatus().getThrowable();
		if (exception instanceof UnexpectedResponseCode) {
			UnexpectedResponseCode codeException = (UnexpectedResponseCode) exception;
			responseCode = codeException.getResponseCode();
			switch (responseCode) {
			case BASE_URL_CONFLICT:
				return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
						Messages.DeploymentLaunchJob_AppUrlConflictMessage);
			case APPLICATION_CONFLICT:
				// in the case of deployment this response code should be
				// handled as a conflict; for other operations (including
				// update) it is an error
				if (helper.getOperationType() == DeploymentHelper.DEPLOY) {
					return new Status(IStatus.INFO, Activator.PLUGIN_ID,
							codeException.getMessage());
				}
				return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
						codeException.getMessage());
			case INVALID_PARAMETER:
				String message = codeException.getMessage();
				if (message != null) {
					message = message.trim();
					if (message
							.startsWith("Invalid userAppName parameter: Application name") //$NON-NLS-1$
							&& message.endsWith("already exists")) { //$NON-NLS-1$
						return new Status(IStatus.INFO, Activator.PLUGIN_ID,
								message);
					}
				}
			case INTERNAL_SERVER_ERROR:
				return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
						codeException.getMessage(), codeException);
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

	protected abstract ApplicationInfo performOperation(ZendApplication app,
			String projectPath);

	private IStatus monitorApplicationStatus(StatusChangeListener listener,
			String targetId, int id, ZendApplication application,
			IProgressMonitor monitor) {
		monitor.beginTask(Messages.statusJob_Title, IProgressMonitor.UNKNOWN);
		ApplicationStatus result = null;
		while (result != ApplicationStatus.DEPLOYED) {
			if (monitor.isCanceled()) {
				return Status.CANCEL_STATUS;
			}
			ApplicationsList info = application.getStatus(targetId,
					String.valueOf(id));
			if (info != null) {
				if (info.getApplicationsInfo() != null) {
					result = info.getApplicationsInfo().get(0).getStatus();
					helper.setInstalledLocation(info.getApplicationsInfo()
							.get(0).getInstalledLocation());
					if (isErrorStatus(result)) {
						return new Status(
								IStatus.ERROR,
								Activator.PLUGIN_ID,
								"Error on the Zend Server during application deployment: " //$NON-NLS-1$
										+ result.getName()
										+ "\nTo get more details, see Zend Server log file."); //$NON-NLS-1$
					}
					monitor.subTask(getStateLabel(result.getName()));
				} else {
					return new Status(
							IStatus.ERROR,
							Activator.PLUGIN_ID,
							"Cannot perform deployment operation on selected target. Verify if server license has not expired."); //$NON-NLS-1$
				}
			}
		}
		monitor.done();
		return new SdkStatus(listener.getStatus());
	}

	private String getStateLabel(String name) {
		String firstLetter = name.substring(0, 1);
		firstLetter = firstLetter.toUpperCase();
		name = firstLetter + name.substring(1) + "..."; //$NON-NLS-1$
		return name;
	}

	private boolean isErrorStatus(ApplicationStatus result) {
		switch (result) {
		case ACTIVATE_ERROR:
		case DEACTIVATE_ERROR:
		case STAGE_ERROR:
		case UNSTAGE_ERROR:
		case UPLOAD_ERROR:
		case UNKNOWN:
			return true;
		default:
			return false;
		}
	}

	private List<IDeploymentContribution> getContributions() {
		IConfigurationElement[] elements = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(
						Activator.DEPLOYMENT_CONTRIBUTION_EXTENSION_ID);
		List<IDeploymentContribution> result = new ArrayList<IDeploymentContribution>();
		for (IConfigurationElement element : elements) {
			if ("contribution".equals(element.getName())) { //$NON-NLS-1$
				try {
					Object contribution = element
							.createExecutableExtension("class"); //$NON-NLS-1$
					if (contribution instanceof IDeploymentContribution) {
						result.add((IDeploymentContribution) contribution);
					}
				} catch (CoreException e) {
					Activator.log(e);
				}
			}
		}
		return result;
	}

}
