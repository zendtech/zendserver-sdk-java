package org.zend.php.zendserver.deployment.core.internal.descriptor;

import java.io.File;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.zend.php.zendserver.deployment.core.descriptor.IMapping;
import org.zend.php.zendserver.deployment.core.descriptor.IResourceMapping;
import org.zend.php.zendserver.deployment.core.internal.descriptor.ResourceMappingParser;


public class ResourceMappingTest extends TestCase {

	public void testParser() {
		ResourceMappingParser parser = new ResourceMappingParser();
		IResourceMapping mapping = parser
				.load(new File("packaging.properties"));
		Map<IPath, IMapping[]> mappingRules = mapping.getMappingRules();
		assertNotNull(mappingRules);
		IMapping[] rule = mappingRules.get(new Path("public"));
		assertNotNull(rule);
		assertTrue(rule.length == 3);
		List<IPath> exclusions = mapping.getExclusions();
		assertNotNull(mappingRules);
		assertTrue(exclusions.size() == 4);
	}

}
