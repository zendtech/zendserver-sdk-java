package org.zend.php.zendserver.deployment.core.descriptor;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.zend.sdklib.mapping.IMapping;
import org.zend.sdklib.mapping.IResourceMapping;

/**
 * Maps paths to resources following IResourceMapping config
 * 
 */
public class ResourceMapper {

	private IContainer rootFolder;
	private IResourceMapping mapping;

	public ResourceMapper(IDescriptorContainer fModel) {
		this.rootFolder = fModel.getFile().getParent();
		this.mapping = fModel.getResourceMapping();
	}

	public IPath getPath(IPath path) {
		Map<String, Set<IMapping>> rules = mapping.getInclusion();
		Set<Entry<String, Set<IMapping>>> entries = rules.entrySet();
		for (Entry<String, Set<IMapping>> entry : entries) {
			Set<IMapping> mappings = entry.getValue();
			for (IMapping mapping : mappings) {
				if (mapping.getPath().equals(path)) {
					return new Path(entry.getKey());
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
