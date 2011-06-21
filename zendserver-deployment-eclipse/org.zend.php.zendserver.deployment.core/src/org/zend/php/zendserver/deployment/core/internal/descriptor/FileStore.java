package org.zend.php.zendserver.deployment.core.internal.descriptor;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.zend.php.zendserver.deployment.core.DeploymentCore;

/**
 * Reading and writing DOM document from/to java.io.File
 */
public class FileStore implements DocumentStore {

	private IFile fFile;
	private PipedInputStream pipedInput;
	private PipedOutputStream pipedOutput;

	public FileStore(IFile file) {
		this.fFile = file;
	}

	public void write() throws CoreException {
		try {
			pipedOutput.close();
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, DeploymentCore.PLUGIN_ID, e.getMessage(), e));
		}
		
		IProgressMonitor mon = new NullProgressMonitor();
		if (fFile.exists()) {
			fFile.setContents(pipedInput, true, true, mon);
		} else {
			fFile.create(pipedInput, true, mon);
		}
	}

	public StreamResult getOutput() throws IOException {
		pipedInput = new PipedInputStream();
		pipedOutput = new PipedOutputStream(pipedInput);
		return new StreamResult(pipedOutput);
	}
	
}