package org.zend.php.zendserver.deployment.core.descriptor;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

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
		Map<IPath, IMapping[]> rules = mapping.getMappingRules();
		Set<Entry<IPath, IMapping[]>> entries = rules.entrySet();
		for (Entry<IPath, IMapping[]> entry : entries) {
			IMapping[] mappings = entry.getValue();
			for (IMapping mapping : mappings) {
				if (mapping.getPath().equals(path)) {
					return entry.getKey();
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

	public Set<IPath> getFolders() {
		Map<IPath, IMapping[]> rules = mapping.getMappingRules();
		return rules != null ? rules.keySet() : new HashSet<IPath>();
	}

	public IMapping[] getMappings(IPath path) {
		Map<IPath, IMapping[]> rules = mapping.getMappingRules();
		return rules != null ? rules.get(path) : new IMapping[0];
	}

	public IResource getResource(IPath path) {
		return rootFolder.findMember(path);
	}

	public boolean isExcluded(String path) {
		return isExcluded(new Path(path));
	}

	public boolean isExcluded(IPath path) {
		for (IPath exclusion : mapping.getExclusions()) {
			IResource resource = rootFolder.findMember(exclusion);
			if (resource != null) {
				if (resource.getFullPath().equals(path)) {
					return true;
				}
			}
		}
		return false;
	}

}
