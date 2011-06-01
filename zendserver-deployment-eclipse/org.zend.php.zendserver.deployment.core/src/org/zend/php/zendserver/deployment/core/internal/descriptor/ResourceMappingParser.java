package org.zend.php.zendserver.deployment.core.internal.descriptor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.zend.php.zendserver.deployment.core.PreferenceManager;
import org.zend.php.zendserver.deployment.core.descriptor.IMapping;
import org.zend.php.zendserver.deployment.core.descriptor.IResourceMapping;


public class ResourceMappingParser {

	private static final String CONTENT = "/*";
	private static final String SEPARATOR = ",";
	private static final String PACKAGE_PROPERTIES = "packaging.properties";
	private static final String EXCLUSIONS = "exclusions";

	public IResourceMapping load(IFile file) {
		IResource mappingFile = ((IContainer) file.getParent())
				.findMember(PACKAGE_PROPERTIES);
		if (mappingFile == null) {
			return load((File) null);
		}
		return load(new File(mappingFile.getLocationURI()));
	}

	public IResourceMapping load(File file) {
		ResourceMapping mapping = new ResourceMapping();
		if (file == null || !file.exists()) {
			mapping.setExclusions(Arrays.asList(getExclusionsPreference()));
			return mapping;
		}
		Properties props = loadProperties(file);
		mapping.setMappingRules(getMappingRules(props));
		mapping.setExclusions(getExclusions(props));
		return mapping;
	}

	private List<IPath> getExclusions(Properties props) {
		List<IPath> result = new ArrayList<IPath>();
		String value = (String) props.getProperty(EXCLUSIONS);
		if (value != null) {
			String[] files = value.split(SEPARATOR);
			result.addAll(Arrays.asList(convertToPaths(files)));
		}
		result.addAll(Arrays.asList(getExclusionsPreference()));
		return result;
	}

	private Properties loadProperties(File file) {
		Properties props = new Properties();
		try {
			props.load(new FileReader(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return props;
	}

	private Map<IPath, IMapping[]> getMappingRules(Properties props) {
		Map<IPath, IMapping[]> result = new HashMap<IPath, IMapping[]>();
		Enumeration<?> e = props.propertyNames();
		while (e.hasMoreElements()) {
			String folderName = (String) e.nextElement();
			if (!EXCLUSIONS.equals(folderName)) {
				String[] files = ((String) props.getProperty(folderName))
						.split(SEPARATOR);
				IMapping[] mappings = new IMapping[files.length];
				for (int i = 0; i < mappings.length; i++) {
					String file = files[i];
					boolean isContent = file.endsWith(CONTENT);
					if (isContent) {
						file = file.substring(0,
								file.length() - SEPARATOR.length());
					}
					mappings[i] = new Mapping(new Path(file), isContent);
				}
				result.put(new Path(folderName), mappings);
			}
		}
		return result;
	}

	private IPath[] convertToPaths(String[] files) {
		IPath[] result = new IPath[files.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = new Path(files[i]);
		}
		return result;
	}

	private IPath[] getExclusionsPreference() {
		String pref = PreferenceManager.getInstance().getString(
				PreferenceManager.EXCLUDE);
		if (!"".equals(pref)) {
			return convertToPaths(pref.split(SEPARATOR));
		}
		return new IPath[0];
	}
}
