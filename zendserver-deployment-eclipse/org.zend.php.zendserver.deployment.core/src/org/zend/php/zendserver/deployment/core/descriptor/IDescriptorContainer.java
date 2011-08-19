package org.zend.php.zendserver.deployment.core.descriptor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IDocument;
import org.zend.php.zendserver.deployment.core.internal.descriptor.ModelSerializer;
import org.zend.sdklib.mapping.IMappingModel;

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
	 * Initialize mapping model using specified document
	 * 
	 * @param document
	 */
	void initializeMappingModel(IDocument document);

	/**
	 * Mappings specification
	 */
	IMappingModel getMappingModel();

	/**
	 * @deprecated use getFileInstead
	 */
	IProject getProject();

	/**
	 * File containing descriptor, file may not exist yet  
	 * @return
	 */
	IFile getFile();

	void save();

	void connect(IDocument document);

	/**
	 * Causes container to (re)read connected document or file and update model accordingly.
	 * 
	 * IDescriptorContainer doesn't listen to document changes by itself to avoid
	 * slowing down modifications.
	 */
	void load();

	ModelSerializer getModelSerializer();

	/**
	 * File containing mapping properties, file may not exist yet  
	 * @return
	 */
	IFile getMappingFile();

}
