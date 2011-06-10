package org.zend.php.zendserver.deployment.core.internal.descriptor;

import java.io.ByteArrayInputStream;
import java.io.CharArrayWriter;
import java.io.IOException;

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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.IDocument;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.zend.php.zendserver.deployment.core.DeploymentCore;

/**
 * Reading and writing DOM document from/to org.eclipse.jface.text.IDocument
 * 
 */
public class EclipseDocumentDOMReadWriter implements DOMDocumentReadWriter {
	
	private IDocument fDocument;

	public EclipseDocumentDOMReadWriter(IDocument document) {
		this.fDocument = document;
	}

	public Document read() throws CoreException {
		DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = fact.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			System.out.println("Error marker for "+e.getMessage());
			//throw new CoreException(new Status(IStatus.ERROR, DeploymentCore.PLUGIN_ID, e.getMessage(), e));
			return null;
		}

		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(fDocument.get().getBytes());
			return builder.parse(bais);
		} catch (SAXException e) {
			System.out.println("Error marker for "+e.getMessage());
			//throw new CoreException(new Status(IStatus.ERROR, DeploymentCore.PLUGIN_ID, e.getMessage(), e));
		} catch (IOException e) {
			System.out.println("Error marker for "+e.getMessage());
			//throw new CoreException(new Status(IStatus.ERROR, DeploymentCore.PLUGIN_ID, e.getMessage(), e));
		}
		
		return DeploymentDescriptorModifier.createEmptyDocument(builder);
	}

	public void write(Document domModel) throws CoreException {
		try {
			Source source = new DOMSource(domModel);

	        CharArrayWriter caw = new CharArrayWriter();
	        Result result = new StreamResult(caw);
	        
	        Transformer xformer = TransformerFactory.newInstance().newTransformer();
	        xformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        xformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            
	        xformer.transform(source, result);

	        fDocument.set(caw.toString());
		} catch (TransformerFactoryConfigurationError e) {
			throw new CoreException(new Status(IStatus.ERROR, DeploymentCore.PLUGIN_ID, e.getMessage(), e));
		} catch (TransformerException e) {
			throw new CoreException(new Status(IStatus.ERROR, DeploymentCore.PLUGIN_ID, e.getMessage(), e));
		}
		
	}
	
}