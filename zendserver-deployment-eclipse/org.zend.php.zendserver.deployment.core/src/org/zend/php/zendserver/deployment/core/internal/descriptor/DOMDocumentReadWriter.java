package org.zend.php.zendserver.deployment.core.internal.descriptor;

import org.eclipse.core.runtime.CoreException;
import org.w3c.dom.Document;

/**
 * Generic mechanism for reading and writing DOM Document
 *
 */
public interface DOMDocumentReadWriter {
	
	Document read() throws CoreException;
	
	void write(Document doc) throws CoreException;
}