package org.zend.php.zendserver.deployment.core.internal.descriptor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.xml.sax.SAXException;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorChangeListener;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.sdklib.mapping.IMappingModel;
import org.zend.sdklib.mapping.MappingModelFactory;

public class DescriptorContainer implements IDescriptorContainer {

	private IFile fFile;
	private DeploymentDescriptor fModel;
	private IMappingModel fMappingModel;
	private IDescriptorChangeListener[] listeners;
	private ModelSerializer lm;

	public DescriptorContainer(IFile file) {
		lm = new ModelSerializer();
		fFile = file;
	}

	private void load(InputStream src) {
		DeploymentDescriptor model = new DeploymentDescriptor();
		if (fFile.exists()) {
			try {
				lm.load(src, model);
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
			}
		}
		fModel = model;
	}
	
	public IDeploymentDescriptor getDescriptorModel() {
		if (fModel == null) {
			try {
				load(fFile.getContents());
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
		lm.setOutput(new JFaceDocumentStore(document));
	}

	public void addChangeListener(IDescriptorChangeListener listener) {
		List<IDescriptorChangeListener> listenersList;
		listenersList = (listeners == null) ? new ArrayList<IDescriptorChangeListener>()
				: new ArrayList<IDescriptorChangeListener>(
						Arrays.asList(listeners));
		listenersList.add(listener);
		listeners = listenersList
				.toArray(new IDescriptorChangeListener[listenersList.size()]);
	}

	public void removeChangeListener(IDescriptorChangeListener listener) {
		List<IDescriptorChangeListener> listenersList = new ArrayList<IDescriptorChangeListener>(
				Arrays.asList(listeners));
		if (listenersList.remove(listener)) {
			listeners = listenersList
					.toArray(new IDescriptorChangeListener[listenersList.size()]);
		}
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

}
