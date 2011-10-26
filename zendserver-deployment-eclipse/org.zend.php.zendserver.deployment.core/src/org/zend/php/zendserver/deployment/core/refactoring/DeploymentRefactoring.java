package org.zend.php.zendserver.deployment.core.refactoring;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.ReplaceEdit;
import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorPackage;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;
import org.zend.php.zendserver.deployment.core.sdk.EclipseMappingModelLoader;
import org.zend.sdklib.internal.mapping.Mapping;
import org.zend.sdklib.mapping.IMapping;
import org.zend.sdklib.mapping.IMappingEntry;
import org.zend.sdklib.mapping.IMappingLoader;
import org.zend.sdklib.mapping.IMappingModel;

public class DeploymentRefactoring {

	private String name;

	public DeploymentRefactoring(String name) {
		this.name = name;
	}
	
	public TextFileChange createDescriptorTextChange(IDescriptorContainer container) {
		int origLength = container.getModelSerializer().getDocumentLength();
		IDocument resultDocument = new Document();
		container.connect(resultDocument);
		container.save();
		
		TextFileChange change = new TextFileChange(name, container.getFile());
		change.setEdit(new ReplaceEdit(0, origLength, resultDocument.get()));
		return change;
	}
	
	public TextFileChange createMappingTextChange(IDescriptorContainer container) {
		IMappingModel mappingModel = container.getMappingModel();
		IMappingLoader loader = mappingModel.getLoader();
		EclipseMappingModelLoader eclipseLoader = (EclipseMappingModelLoader) loader;
		File f = mappingModel.getMappingFile();
		int origLength = getFileLength(f);
		
		IDocument resultDocument = new Document();
		eclipseLoader.setDocument(resultDocument);
		try {
			mappingModel.store();
		} catch (IOException e) {
			// no IOException, because we're writing to in-memory Document
		}
		
		TextFileChange change = new TextFileChange(name, container.getMappingFile());
		change.setEdit(new ReplaceEdit(0, origLength, resultDocument.get()));
		return change;
	}

	private int getFileLength(File f) {
		char[] buf = new char[4096];
		int i, len = 0;
		
		FileReader fr = null;
		try {
			fr = new FileReader(f);
			while ((i = fr.read(buf)) != -1) {
				len += i;
			}
		} catch (IOException e) {
		} finally {
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
				}
			}
		}
		return len;
	}

	public boolean updatePathInDescriptor(String oldFullPath, String newFullPath,
			IDeploymentDescriptor descriptor) {
		boolean updated = false;
		
		updated |= updateIfEquals(descriptor, DeploymentDescriptorPackage.EULA, oldFullPath, newFullPath);
		updated |= updateIfEquals(descriptor, DeploymentDescriptorPackage.ICON, oldFullPath, newFullPath);
		
		String appDir = descriptor.getApplicationDir();
		updated |= updateIfEquals(descriptor, DeploymentDescriptorPackage.DOCROOT, appDir+'/'+oldFullPath, appDir+'/'+newFullPath);
		
		
		return updated;
	}

	public boolean updateIfEquals(IModelObject object, Feature f, String oldValue, String newValue) {
		String path = object.get(f);
		if (oldValue.equals(path)) {
			object.set(f, newValue);
			return true;
		}
		
		return false;
	}

	public boolean updatePathInMapping(String oldFullPath, String newFullPath,
			IMappingModel mappingModel) {
		boolean exactMatchFound = false;
		boolean parentMatchFound = false;
		
		IPath oldFull = new Path(oldFullPath);
		IPath newFull = new Path(newFullPath);
		
		List<IMappingEntry> entries = mappingModel.getEnties();
		for (IMappingEntry entry : entries) {
			boolean oldPathHasMapping = false;
			boolean newPathHasMapping = false;
			
			List<IMapping> mappings = entry.getMappings();
			for (IMapping mapping : mappings) {
				String currentPath = mapping.getPath();
				if (oldFullPath.equals(currentPath)) {
					// old location is directly in maping, change it to new one
					mapping.setPath(newFullPath);
					exactMatchFound = true;
				} else {
					IPath current = new Path(currentPath);
					if (current.isPrefixOf(oldFull)) {
						oldPathHasMapping = true;
					}
					if (current.isPrefixOf(newFull)) {
						newPathHasMapping = true;
					}
				}
			}
			
			// old location was in mapping, but new one is not, hence add new one
			if (oldPathHasMapping && !newPathHasMapping) {
				mappings.add(new Mapping(newFullPath, false));
				parentMatchFound = true;
			}
		}
		
		return exactMatchFound || parentMatchFound;
	}

	public boolean removePathFromMapping(String oldFullPath,
			IMappingModel mappingModel) {
		boolean exactMatchFound = false;
		
		List<IMappingEntry> entries = mappingModel.getEnties();
		for (IMappingEntry entry : entries) {
			IMapping toRemove = null;
			
			List<IMapping> mappings = entry.getMappings();
			for (IMapping mapping : mappings) {
				String currentPath = mapping.getPath();
				if (oldFullPath.equals(currentPath)) {
					exactMatchFound = true;
					toRemove = mapping;
				}
			}
			
			if (toRemove != null) {
				mappings.remove(toRemove);
			}
		}
		
		return exactMatchFound;
	}

	public boolean updateProjectName(String oldProjectName,
			String newProjectName, IDeploymentDescriptor descriptor) {
		if (newProjectName.equals(oldProjectName)) {
			return false;
		}
		
		descriptor.setName(newProjectName);
		return true;
	}
	
}
