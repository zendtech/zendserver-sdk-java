package org.zend.php.zendserver.deployment.core.descriptor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IDocument;

/**
 * Contains descriptor model. Container can be a file, URL, zip entry, or
 * whatever. Container provides read-only and read-write access to model
 * 
 */
public interface IDescriptorContainer {

	/**
	 * Descriptor model
	 */
	IDeploymentDescriptor getDescriptorModel();
	
	/**
	 * Mappings specification
	 */
	IResourceMapping getResourceMapping();
	
	/**
	 * Working copy connected with provided document. All changes will be reflected in this document.
	 * Useful e.g. to reflect model changes in live document in editor.
	 * 
	 * @param iDocument
	 * @return
	 */
	IDeploymentDescriptorModifier createWorkingCopy(IDocument iDocument);
	
	/**
	 * Working copy connected with container file. All changes will be reflected with underlying file.
	 * 
	 * @return
	 */
	IDeploymentDescriptorModifier createWorkingCopy();
	
	/**
	 * Adds a listener to be notified on any model changes.
	 * 
	 * @param listener
	 */
	void addChangeListener(IDescriptorChangeListener listener);
	
	/**
	 * Removes listener from the listeners list.
	 * 
	 * @param listener
	 */
	void removeChangeListener(IDescriptorChangeListener listener);

	/**
	 * @deprecated use getFileInstead
	 */
	IProject getProject();

	/**
	 * File containing descriptor, file may not exist yet  
	 * @return
	 */
	IFile getFile();

	/**
	 * Notify container that model has changed and model listeners need to be notified
	 * Note that, when using IDeploymentDescriptorModifier, changes are generated automatically.
	 * This is useful to notify changes from underlying document
	 * 
	 * @param o changed object
	 */
	void fireChange(Object o);

}
