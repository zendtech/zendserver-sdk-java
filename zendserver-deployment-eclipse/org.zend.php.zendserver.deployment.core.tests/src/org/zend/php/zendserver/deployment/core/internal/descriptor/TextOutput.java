package org.zend.php.zendserver.deployment.core.internal.descriptor;

import java.io.CharArrayWriter;
import java.io.IOException;

import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.CoreException;

public class TextOutput implements DocumentStore {
	
	private CharArrayWriter caw;

	public void write() throws CoreException {
		// empty
	}

	public StreamResult getOutput() throws IOException {
		caw = new CharArrayWriter();
        return new StreamResult(caw);
	}
	
	public String toString() {
		return caw == null ? "null" : caw.toString();
	}

}
