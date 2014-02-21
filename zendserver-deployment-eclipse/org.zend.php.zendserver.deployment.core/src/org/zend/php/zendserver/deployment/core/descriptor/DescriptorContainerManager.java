package org.zend.php.zendserver.deployment.core.descriptor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.zend.php.zendserver.deployment.core.internal.descriptor.DescriptorContainer;

/**
 * Service for opening descriptor containers
 * 
 */
public class DescriptorContainerManager {

	private static DescriptorContainerManager service;

	public static final String DESCRIPTOR_PATH = "deployment.xml"; //$NON-NLS-1$

	public static DescriptorContainerManager getService() {
		if (service == null) {
			service = new DescriptorContainerManager();
		}
		return service;
	}

	private DescriptorContainerManager() {
		// singleton
	}

	/**
	 * @param project
	 * @return
	 */
	public IDescriptorContainer openDescriptorContainer(IProject project) {
		IFile file = project
				.getFile((DescriptorContainerManager.DESCRIPTOR_PATH));
		return openDescriptorContainer(file);
	}

	public IDescriptorContainer openDescriptorContainer(IFile file) {
		return new DescriptorContainer(file);
	}

}
