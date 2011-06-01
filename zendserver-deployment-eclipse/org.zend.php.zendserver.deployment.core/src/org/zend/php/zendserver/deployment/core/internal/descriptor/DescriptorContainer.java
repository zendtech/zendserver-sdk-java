package org.zend.php.zendserver.deployment.core.internal.descriptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IDocument;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptorModifier;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorChangeListener;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.php.zendserver.deployment.core.descriptor.IResourceMapping;


public class DescriptorContainer implements IDescriptorContainer {

	private IFile fFile;
	private DeploymentDescriptor fModel;
	private IResourceMapping fResourceMapping;
	private IDescriptorChangeListener[] listeners;

	public DescriptorContainer(IFile file) {
		fFile = file;
	}

	public IDeploymentDescriptor getDescriptorModel() {
		if (fModel == null) {
			DeploymentDescriptor model = new DeploymentDescriptor();
			if (fFile.exists()) {
				DeploymentDescriptorParser parser = new DeploymentDescriptorParser(model);
				parser.load(fFile);
			}
			fModel = model;
		}
		return fModel;
	}

	public IProject getProject() {
		return fFile.getProject();
	}

	public IFile getFile() {
		return fFile;
	}

	public IDeploymentDescriptorModifier createWorkingCopy(IDocument document) {
		getDescriptorModel(); // make sure model gets created
		return new DeploymentDescriptorModifier(fModel, new DeploymentDescriptorModifier.DocumentDOMRW(document), this);
	}

	public IDeploymentDescriptorModifier createWorkingCopy() {
		getDescriptorModel(); // make sure model gets created
		return new DeploymentDescriptorModifier(fModel, new DeploymentDescriptorModifier.FileDOMRW(fFile), this);
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

	public void fireChange(Object o) {
		if (listeners == null) {
			return;
		}

		for (int i = 0; i < listeners.length; i++) {
			try {
				listeners[i].descriptorChanged(o);
			} catch (Throwable e) {
				// TODO
				e.printStackTrace();
			}
		}
	}

	public IResourceMapping getResourceMapping() {
		if (fResourceMapping == null) {
			ResourceMappingParser parser = new ResourceMappingParser();
			fResourceMapping = parser.load(fFile);
		}
		return fResourceMapping;
	}

}
