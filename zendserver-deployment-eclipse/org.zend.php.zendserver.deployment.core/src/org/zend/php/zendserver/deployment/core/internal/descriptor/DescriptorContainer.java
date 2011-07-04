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
import org.zend.php.zendserver.deployment.core.sdk.EclipseMappingModelLoader;
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
			System.out.println("load");
			long a= System.currentTimeMillis();
			lm.load(src, fModel);
			long b = System.currentTimeMillis();
			System.out.println("load "+(b-a)+"msec");
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
					System.out.println("serialize");
					long a = System.currentTimeMillis();
					lm.serialize(model, event);
					long b = System.currentTimeMillis();
					System.out.println("serialize "+(b-a)+"msec\nwrite");
					lm.write();
					long c = System.currentTimeMillis();
					System.out.println("write "+(c-b)+"msec");
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
		return fMappingModel;
	}

	public void initializeMappingModel(IDocument document) {
		if (fMappingModel == null) {
			fMappingModel = MappingModelFactory.createModel(
					new EclipseMappingModelLoader(document), fFile.getParent()
							.getLocation().toFile());
		}
	}

	public void save() {
		// TODO Auto-generated method stub
		
	}
}
