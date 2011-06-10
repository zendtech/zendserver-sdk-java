package org.zend.php.zendserver.deployment.core.internal.descriptor;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.zend.php.zendserver.deployment.core.DeploymentCore;

/**
 * Reading and writing DOM document from/to java.io.File
 */
public class JavaFileDOMReadWriter implements DOMDocumentReadWriter {

	private IFile fFile;

	public JavaFileDOMReadWriter(IFile file) {
		this.fFile = file;
	}
	
	public Document read() throws CoreException {
		try {
			DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = fact.newDocumentBuilder();

			if (!fFile.exists()) {
				return DeploymentDescriptorModifier.createEmptyDocument(builder);
			}
			
			return builder.parse(fFile.getContents());
		} catch (ParserConfigurationException e) {
			throw new CoreException(new Status(IStatus.ERROR, DeploymentCore.PLUGIN_ID, e.getMessage(), e));
		} catch (SAXException e) {
			throw new CoreException(new Status(IStatus.ERROR, DeploymentCore.PLUGIN_ID, e.getMessage(), e));
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, DeploymentCore.PLUGIN_ID, e.getMessage(), e));
		}
	}

	public void write(Document doc) throws CoreException {
		try {
			PipedInputStream in = new PipedInputStream();
			PipedOutputStream out = new PipedOutputStream(in);
			Result result = new StreamResult(out);

	        Source source = new DOMSource(doc);
	        
	        Transformer xformer = TransformerFactory.newInstance().newTransformer();
	        xformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        xformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            xformer.transform(source, result);
	        out.close();
			
			IProgressMonitor mon = new NullProgressMonitor();
			if (fFile.exists()) {
				fFile.setContents(in, true, true, mon);
			} else {
				fFile.create(in, true, mon);
			}
			
		} catch (TransformerFactoryConfigurationError e) {
			throw new CoreException(new Status(IStatus.ERROR, DeploymentCore.PLUGIN_ID, e.getMessage(), e));
		} catch (TransformerException e) {
			throw new CoreException(new Status(IStatus.ERROR, DeploymentCore.PLUGIN_ID, e.getMessage(), e));
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, DeploymentCore.PLUGIN_ID, e.getMessage(), e));
		}
		
	}
	
}