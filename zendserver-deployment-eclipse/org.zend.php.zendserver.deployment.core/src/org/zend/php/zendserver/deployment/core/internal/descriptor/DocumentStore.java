package org.zend.php.zendserver.deployment.core.internal.descriptor;

import java.io.IOException;

import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.CoreException;

/**
 * Generic mechanism for writing DOM Document
 * 
 * Ita takes output, writes and calls write() once the document has been written
 *
 */
public interface DocumentStore {
	
	void write() throws CoreException;
	
	StreamResult getOutput() throws IOException;
}