package org.zend.php.zendserver.deployment.core.descriptor;

import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.zend.sdklib.mapping.IMapping;
import org.zend.sdklib.mapping.IMappingEntry;
import org.zend.sdklib.mapping.IMappingEntry.Type;
import org.zend.sdklib.mapping.IMappingModel;

/**
 * Maps paths to resources following IResourceMapping config
 * 
 */
public class ResourceMapper {

	private IContainer rootFolder;
	private IMappingModel mapping;

	public ResourceMapper(IDescriptorContainer fModel) {
		this.rootFolder = fModel.getFile().getParent();
		this.mapping = fModel.getMappingModel();
	}

	public IPath getPath(IPath path) {
		List<IMappingEntry> entries = mapping.getEnties();
		for (IMappingEntry entry : entries) {
			if (entry.getType() == Type.INCLUDE) {
				List<IMapping> mappings = entry.getMappings();
				for (IMapping mapping : mappings) {
					if (mapping.getPath().equals(path)) {
						return new Path(entry.getFolder());
					}
				}
			}
		}
		return path;
	}

	public IPath getPath(String path) {
		return getPath(new Path(path));
	}

	public IPath[] getPaths(IPath[] paths) {
		IPath[] result = new IPath[paths.length];
		for (int i = 0; i < paths.length; i++) {
			result[i] = getPath(paths[i]);
		}

		return result;
	}

	public IPath[] getPaths(String[] paths) {
		IPath[] result = new IPath[paths.length];
		for (int i = 0; i < paths.length; i++) {
			result[i] = getPath(paths[i]);
		}

		return result;
	}

	public IResource[] getResources(String[] paths) {
		IResource[] result = new IResource[paths.length];
		for (int i = 0; i < paths.length; i++) {
			result[i] = rootFolder.findMember(getPath(paths[i]));
		}

		return result;
	}

	public IResource getResource(IPath path) {
		return rootFolder.findMember(path);
	}

}
