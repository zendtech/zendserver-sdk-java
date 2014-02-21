package org.zend.php.zendserver.deployment.core.internal.validation;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
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
        	reportProblem(file, new ValidationStatus(line, start, end, severity, message));
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
		
		IDocument doc = null;
		try {
			doc = getIDocument(file);
		} catch (Throwable e) {
			DeploymentCore.log(e);
			return;
		}
		
		DescriptorSemanticValidator validator = new DescriptorSemanticValidator();
		validator.setFile(file);
		ValidationStatus[] statuses = validator.validate(model.getDescriptorModel(), doc);
		
		reportProblems(file, statuses);
	}

	public static void reportProblems(IFile file, ValidationStatus[] statuses) {
		List<ValidationStatus> existing = new ArrayList<ValidationStatus>();
		IMarker[] markers = null;
		try {
			markers = file.findMarkers(IncrementalDeploymentBuilder.PROBLEM_MARKER, true, IResource.DEPTH_ZERO);
			for (IMarker marker : markers) {
				existing.add(markerToStatus(marker));
			}
		} catch (CoreException e) {
			DeploymentCore.log(e);
		}
		
		// find which errors are new, already exist, or need to be removed 
		List<ValidationStatus> toReport = new ArrayList<ValidationStatus>(Arrays.asList(statuses));
		toReport.removeAll(existing); // remove all existing statuses from the new ones list to get a list of those to report
		existing.removeAll(Arrays.asList(statuses)); // remove all re-reported statuses to get a list of obsolete statuses
		
		// add new statuses
		for (ValidationStatus status : toReport) {
			reportProblem(file, status);
		}
		// remove obsolete statuses
		for (ValidationStatus status : existing) {
			try {
				status.getMarker().delete();
			} catch (CoreException e) {
				DeploymentCore.log(e);
			}
		}
	}
	
	public static ValidationStatus markerToStatus(IMarker marker) {
		int line = marker.getAttribute(IMarker.LINE_NUMBER, 0);
		int start = marker.getAttribute(IMarker.CHAR_START, 0);
		int end = marker.getAttribute(IMarker.CHAR_END, 0);
		String message = marker.getAttribute(IMarker.MESSAGE, null);
		int severity = marker.getAttribute(IMarker.SEVERITY, 0);
		int featureId = marker.getAttribute(IncrementalDeploymentBuilder.FEATURE_ID, -1);
		int objectId = marker.getAttribute(IncrementalDeploymentBuilder.OBJECT_ID, -1);
		int no = marker.getAttribute(IncrementalDeploymentBuilder.OBJECT_NUMBER, -1);
		
		ValidationStatus status = new ValidationStatus(objectId, no, featureId, line, start, end, severity, message);
		status.setMarker(marker);
		return status;
	}
	
	

	public static IMarker reportProblem(IFile file, ValidationStatus status) {
		IMarker marker;
		try {
			marker = file.createMarker(IncrementalDeploymentBuilder.PROBLEM_MARKER);
			marker.setAttribute(IMarker.LINE_NUMBER, status.getLine());
			marker.setAttribute(IMarker.CHAR_START, status.getStart());
			marker.setAttribute(IMarker.CHAR_END, status.getEnd());
			marker.setAttribute(IMarker.MESSAGE, status.getMessage());
			//marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_NORMAL);
			marker.setAttribute(IMarker.SEVERITY, status.getSeverity());
			if (status.getFeatureId() != -1) {
				marker.setAttribute(IncrementalDeploymentBuilder.FEATURE_ID, status.getFeatureId());
			}
			if (status.getObjectNo() != -1) {
				marker.setAttribute(IncrementalDeploymentBuilder.OBJECT_NUMBER, status.getObjectNo());
			}
			if (status.getObjectId() != -1) {
				marker.setAttribute(IncrementalDeploymentBuilder.OBJECT_ID, status.getObjectId());
			}
			return marker;
		} catch (CoreException e) {
			DeploymentCore.log(e);
		}
		
		return null;
	}

}
