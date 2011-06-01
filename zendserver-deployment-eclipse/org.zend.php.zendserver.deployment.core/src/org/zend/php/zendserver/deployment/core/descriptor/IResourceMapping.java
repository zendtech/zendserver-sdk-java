package org.zend.php.zendserver.deployment.core.descriptor;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;

/**
 * Contains the mapping rules
 * 
 * @see ResourceMapper for a facility that translates paths using mapping.
 * 
 */
public interface IResourceMapping {

	Map<IPath, IMapping[]> getMappingRules();

	List<IPath> getExclusions();

}
