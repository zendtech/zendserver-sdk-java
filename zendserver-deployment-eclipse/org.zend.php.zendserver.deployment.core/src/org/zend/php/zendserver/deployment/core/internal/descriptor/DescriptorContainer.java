package org.zend.php.zendserver.deployment.core.internal.descriptor;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.xpath.XPathExpressionException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IDocument;
import org.xml.sax.SAXException;
import org.zend.php.zendserver.deployment.core.DeploymentCore;
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
		lm.setOutput(new FileStore(file));
		fFile = file;
	}
	
	public ModelSerializer getModelSerializer() {
		return lm;
	}

	public void load() {
		InputStream src = null;
		InputStream src2 = null;
		
		if (fDocument != null) {
			src = new ByteArrayInputStream(fDocument.get().getBytes());
		} else if (fFile.exists()) {
			try {
				src = fFile.getContents();
				src2 = fFile.getContents();
			} catch (CoreException e) {
				DeploymentCore.log(e);
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
			lm.load(src, src2, fModel);
		} catch (XPathExpressionException e) {
			// seems model error, write to log
			DeploymentCore.log(e);
		} catch (SAXException e) {
			// should be catched by validator, we can ignore it
		} catch (IOException e) {
			// should be catched by validator, we can ignore it
		} finally {
			if (src != null) {
				try {
					src.close();
				} catch (IOException e) {
					// ignore
				}
			}

			if (src2 != null) {
				try {
					src2.close();
				} catch (IOException e) {
					// ignore
				}
			}
			
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
		
		IDeploymentDescriptor model = getDescriptorModel();
		model.addListener(new IDescriptorChangeListener() {
			
			public void descriptorChanged(ChangeEvent event) {
				if (isLoading) {
					return;
				}
				
				save(event);
			}
		});
	}

	public IMappingModel getMappingModel() {
		return fMappingModel;
	}
	
	public void initializeMappingModel(IDocument document) {
		if (fMappingModel == null || !fMappingModel.isLoaded()) {
			File container = fFile.getParent().getLocation().toFile();
			fMappingModel = MappingModelFactory.createModel(
					new EclipseMappingModelLoader(document), container);
		}
	}

	public void save() {
		save(null);
	}
	
	private void save(ChangeEvent event) {
		IDeploymentDescriptor model = getDescriptorModel();
		try {
			lm.serialize(model, event);
			lm.write();
		} catch (XPathExpressionException e) {
			// seems model error, write to log
			DeploymentCore.log(e);
		} catch (CoreException e) {
			DeploymentCore.log(e);
		} catch (TransformerFactoryConfigurationError e) {
			DeploymentCore.log(e);
		} catch (TransformerException e) {
			DeploymentCore.log(e);
		}
	}

	public IFile getMappingFile() {
		return getFile().getParent().getFile(new Path(MappingModelFactory.DEPLOYMENT_PROPERTIES));
	}
}
