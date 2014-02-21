package org.zend.php.zendserver.deployment.core.internal.descriptor;

import java.io.CharArrayWriter;
import java.io.IOException;

import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;

/**
 * Reading and writing DOM document from/to org.eclipse.jface.text.IDocument
 * 
 */
public class JFaceDocumentStore implements DocumentStore {
	
	private IDocument fDocument;
	private CharArrayWriter caw;

	public JFaceDocumentStore(IDocument document) {
		this.fDocument = document;
	}

	public void write() throws CoreException {
		fDocument.set(caw.toString());
	}

	public StreamResult getOutput() throws IOException {
		caw = new CharArrayWriter();
        return new StreamResult(caw);
	}
	
}