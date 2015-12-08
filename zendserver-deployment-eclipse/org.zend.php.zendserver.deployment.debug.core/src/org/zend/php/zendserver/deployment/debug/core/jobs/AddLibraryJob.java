package org.zend.php.zendserver.deployment.debug.core.jobs;

import java.io.IOException;
import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.zend.php.zendserver.deployment.core.debugger.LibraryDeployData;
import org.zend.php.zendserver.deployment.core.utils.LibraryManager;
import org.zend.php.zendserver.deployment.debug.core.Activator;
import org.zend.php.zendserver.deployment.debug.core.Messages;

public class AddLibraryJob extends Job {

	private LibraryDeployData data;

	public AddLibraryJob(LibraryDeployData data) {
		super(Messages.AddLibraryJob_Name);
		this.data = data;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			String taskName = MessageFormat.format(Messages.AddLibraryJob_TaskName, getData().getName(),
					getData().getVersion());
			monitor.beginTask(taskName, IProgressMonitor.UNKNOWN);
			LibraryManager.addLibrary(data);
			return Status.OK_STATUS;
		} catch (IOException e) {
			String message = MessageFormat.format(Messages.AddLibraryJob_CouldNotAddLibrary_Error, getData().getName(),
					getData().getVersion());
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, message, e);
		} finally {
			monitor.done();
		}
	}

	public LibraryDeployData getData() {
		return data;
	}
}
