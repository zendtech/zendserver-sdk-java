package org.zend.php.zendserver.deployment.core.internal.validation;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.zend.php.zendserver.deployment.core.DeploymentCore;
import org.zend.php.zendserver.deployment.core.IncrementalDeploymentBuilder;
import org.zend.php.zendserver.deployment.core.descriptor.DescriptorContainerManager;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;

public class DescriptorValidator {

	private static final String DESCRIPTOR_SCHEMA = "deployment.xsd"; //$NON-NLS-1$
	
	public void validate(IFile file) {
		validateXSD(file);
		validateSemantics(file);
		
	}
	
	protected void validateXSD(IFile file) {
        SchemaFactory factory = 
            SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema"); //$NON-NLS-1$
        
        Source schemaSource = new StreamSource(getClass().getResourceAsStream(DESCRIPTOR_SCHEMA));
        Schema schema = null;
        javax.xml.validation.Validator validator = null;
        Source source = null;
		try {
			schema = factory.newSchema(schemaSource);
			
			validator = schema.newValidator();
	        
	        source = new StreamSource(file.getContents());
		} catch (SAXException e) {
			DeploymentCore.log(e);
			return;
		} catch (CoreException e) {
			DeploymentCore.log(e);
			return;
		}
        
		IDocument doc = null;
		try {
			doc = getIDocument(file);
		} catch (Throwable e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		
        try {
            validator.validate(source);
        }
        catch (SAXException ex) {
        	Feature target = null;
        	
        	int line = 0;
        	int start = 0;
        	int end = 0;
        	
        	if (ex instanceof SAXParseException) {
        		SAXParseException parseEx = (SAXParseException) ex;
        		line = parseEx.getLineNumber() - 1;
        		try {
					start = doc.getLineOffset(line) + parseEx.getColumnNumber() - 1;
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // TODO calculate offset from start of the doc, not from start of line
        		end = start; // TODO find next whitespace
        	}
        	
        	int severity = ValidationStatus.ERROR; // TODO validation error level should be configurable?
        	String message = ex.getMessage();
        	reportProblem(file, new ValidationStatus(target, line, start, end, severity, message));
        } catch (IOException e) {
			DeploymentCore.log(e);
		}  
	}
	
	private IDocument getIDocument(IFile file) throws Throwable {
		InputStream is = file.getContents();
		StringBuilder sb = new StringBuilder();
		byte[] buf = new byte[4096];
		int count;
		while ((count = is.read(buf)) > 0) {
			sb.append(new String(buf, 0, count));
		}
		
		
		return new Document(sb.toString());
	}

	private void validateSemantics(IFile file) {
		IDescriptorContainer model = DescriptorContainerManager.getService()
				.openDescriptorContainer(file);
		
		ValidatorSemanticValidator validator = new ValidatorSemanticValidator();
		ValidationStatus[] statuses = validator.validate(model.getDescriptorModel());
		
		for (ValidationStatus status : statuses) {
			reportProblem(file, status);
			
		}
	}

	protected void reportProblem(IFile file, ValidationStatus status) {
		IMarker marker;
		try {
			marker = file.createMarker(IncrementalDeploymentBuilder.PROBLEM_MARKER);
			marker.setAttribute(IMarker.LINE_NUMBER, status.getLine());
			marker.setAttribute(IMarker.CHAR_START, status.getStart());
			marker.setAttribute(IMarker.CHAR_END, status.getEnd());
			marker.setAttribute(IMarker.MESSAGE, status.getMessage());
			marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_NORMAL);
			marker.setAttribute(IMarker.SEVERITY, status.getSeverity());
		} catch (CoreException e) {
			DeploymentCore.log(e);
		}
	}

}
