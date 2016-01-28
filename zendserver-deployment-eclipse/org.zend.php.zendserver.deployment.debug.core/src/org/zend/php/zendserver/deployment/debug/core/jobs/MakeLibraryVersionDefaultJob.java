package org.zend.php.zendserver.deployment.debug.core.jobs;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.zend.php.zendserver.deployment.core.debugger.LibraryDeployData;
import org.zend.php.zendserver.deployment.core.sdk.EclipseMappingModelLoader;
import org.zend.php.zendserver.deployment.core.sdk.EclipseVariableResolver;
import org.zend.php.zendserver.deployment.core.sdk.SdkStatus;
import org.zend.php.zendserver.deployment.core.sdk.StatusChangeListener2;
import org.zend.php.zendserver.deployment.debug.core.Activator;
import org.zend.sdklib.application.ZendLibrary;
import org.zend.webapi.internal.core.connection.exception.WebApiCommunicationError;

public class MakeLibraryVersionDefaultJob extends AbstractLibraryJob {

	public MakeLibraryVersionDefaultJob(LibraryDeployData data) {
		super("Setting library version as default", data);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			String taskName = MessageFormat.format("Setting library version ''{0} [{1}]'' as default...",
					getData().getName(), getData().getVersion());
			monitor.beginTask(taskName, IProgressMonitor.UNKNOWN);
			StatusChangeListener2 listener = new StatusChangeListener2(monitor);
			ZendLibrary lib = new ZendLibrary(new EclipseMappingModelLoader());
			lib.addStatusChangeListener(listener);
			lib.setVariableResolver(new EclipseVariableResolver());

			monitor.subTask("Sending request...");
			lib.setDefaultVersion(data.getTargetId(), data.getVersionId());

			Throwable exception = listener.getStatus().getThrowable();
			if (exception instanceof WebApiCommunicationError) {
				return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
						"Cannot establish connection with selected server.", exception);
			}
			return new SdkStatus(listener.getStatus());
		} finally {
			monitor.done();
		}
	}

}
