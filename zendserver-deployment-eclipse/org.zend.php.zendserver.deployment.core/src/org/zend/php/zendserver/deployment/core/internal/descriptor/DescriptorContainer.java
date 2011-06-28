package org.zend.php.zendserver.deployment.core.internal.descriptor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.xpath.XPathExpressionException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.xml.sax.SAXException;
import org.zend.php.zendserver.deployment.core.descriptor.ChangeEvent;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorChangeListener;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.sdklib.mapping.IMappingModel;
import org.zend.sdklib.mapping.MappingModelFactory;

public class DescriptorContainer implements IDescriptorContainer {

	private IFile fFile;
	private DeploymentDescriptor fModel;
	private IMappingModel fMappingModel;
	private ModelSerializer lm;
	private IDocument fDocument;
	private boolean isLoading;

	public DescriptorContainer(IFile file) {
		lm = new ModelSerializer();
		fFile = file;
	}

	public void load() {
		InputStream src = null;
		
		if (fDocument != null) {
			src = new ByteArrayInputStream(fDocument.get().getBytes()); // TODO get fDocument encoding
		} else if (fFile.exists()) {
			try {
				src = fFile.getContents();
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (fModel == null) {
			fModel = new DeploymentDescriptor();		
		}
		
		if (src == null) {
			return;
		}
		
		try {
			isLoading = true;
			lm.load(src, fModel);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			isLoading = false;
		}
	}
	
	public IDeploymentDescriptor getDescriptorModel() {
		if (fModel == null) {
			load();
		}
		return fModel;
	}

	public IProject getProject() {
		return fFile.getProject();
	}

	public IFile getFile() {
		return fFile;
	}

	public void connect(IDocument document) {
		this.fDocument = document;
		lm.setOutput(new JFaceDocumentStore(document));
		
		final IDeploymentDescriptor model = getDescriptorModel();
		model.addListener(new IDescriptorChangeListener() {
			
			public void descriptorChanged(ChangeEvent event) {
				if (isLoading) {
					return;
				}
				
				try {
					lm.serialize(model);
					lm.write();
				} catch (XPathExpressionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TransformerFactoryConfigurationError e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TransformerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	public IMappingModel getMappingModel() {
		if (fMappingModel == null) {
			try {
				fMappingModel = MappingModelFactory.createDefaultModel(fFile
						.getParent().getLocation().toFile());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return fMappingModel;
	}

	public void save() {
		// TODO Auto-generated method stub
		
	}

	public void reconcile() {
		load();
	}

}
