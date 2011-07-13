package org.zend.php.zendserver.deployment.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.zend.php.zendserver.deployment.core.descriptor.DescriptorContainerManager;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;
import org.zend.php.zendserver.deployment.core.internal.validation.ValidationStatus;
import org.zend.php.zendserver.deployment.core.internal.validation.Validator;
import org.zend.sdklib.mapping.MappingModelFactory;
import org.zend.sdklib.mapping.validator.IMappingValidator;
import org.zend.sdklib.mapping.validator.MappingParseException;
import org.zend.sdklib.mapping.validator.MappingParseStatus;
import org.zend.sdklib.mapping.validator.MappingValidator;

public class IncrementalDeploymentBuilder extends IncrementalProjectBuilder {

	private static final String DESCRIPTOR_SCHEMA = "deployment.xsd"; //$NON-NLS-1$
	public static final String PROBLEM_MARKER = "org.zend.php.zendserver.deployment.core.problemmarker"; //$NON-NLS-1$
	public static final String ID = DeploymentCore.PLUGIN_ID + ".DeploymentBuilder"; //$NON-NLS-1$

	public IncrementalDeploymentBuilder() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected IProject[] build(int kind, Map<String, String> args,
			IProgressMonitor monitor) throws CoreException {
		IResourceDelta delta = getDelta(getProject());
		if (delta == null) {
			return null;
		}
		
		delta.accept(new IResourceDeltaVisitor() {
			
			public boolean visit(IResourceDelta delta) throws CoreException {
				IResource resource = delta.getResource();
				if ((resource instanceof IFile) && (DescriptorContainerManager.DESCRIPTOR_PATH.equals(resource.getName()))) {
					validateDescriptor((IFile)resource);
				}
				
				if ((resource instanceof IFile)
						&& (MappingModelFactory.DEPLOYMENT_PROPERTIES.equals(resource.getName()))) {
					validateMapping((IFile) resource);
				}
				
				if (resource instanceof IProject) {
					return true;
				}
				
				if (resource instanceof IFolder) {
					return false;
				}
				return false;
			}
		});
		
		return null;
	}

	protected void validateMapping(IFile file) {
		IMappingValidator validator = new MappingValidator(file.getParent().getLocation().toFile());
		try {
			try {
				if (file.exists()) {
					validator.parse(file.getContents());
					file.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_ZERO);
				}
			} catch (MappingParseException e) {
				handleMappingValidationErrors(file, e.getErrors());
			}
		} catch (CoreException e) {
			DeploymentCore.log(e);
		}
	}

	protected void handleMappingValidationErrors(IFile file, List<MappingParseStatus> errors)
			throws CoreException {
		file.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_ZERO);
		for (MappingParseStatus status : errors) {
			IMarker marker = file.createMarker(PROBLEM_MARKER);
			marker.setAttribute(IMarker.LINE_NUMBER, status.getLine());
			marker.setAttribute(IMarker.CHAR_START, status.getStart());
			marker.setAttribute(IMarker.CHAR_END, status.getEnd());
			marker.setAttribute(IMarker.MESSAGE, status.getMessage());
			marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_NORMAL);
			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
		}
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

	protected void validateDescriptor(IFile file) {
		try {
			file.deleteMarkers(PROBLEM_MARKER, true, IResource.DEPTH_ZERO);
		} catch (CoreException e) {
			DeploymentCore.log(e);
		}
		
		validateXSD(file);
		validateSemantics(file);
	}
	
	private void validateSemantics(IFile file) {
		IDescriptorContainer model = DescriptorContainerManager.getService()
				.openDescriptorContainer(file);
		
		Validator validator = new Validator();
		ValidationStatus[] statuses = validator.validate(model.getDescriptorModel());
		
		for (ValidationStatus status : statuses) {
			reportProblem(file, status);
			
		}
	}

	protected void reportProblem(IFile file, ValidationStatus status) {
		IMarker marker;
		try {
			marker = file.createMarker(PROBLEM_MARKER);
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
