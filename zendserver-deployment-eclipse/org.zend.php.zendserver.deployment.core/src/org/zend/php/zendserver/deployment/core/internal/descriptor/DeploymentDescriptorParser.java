package org.zend.php.zendserver.deployment.core.internal.descriptor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.zend.php.zendserver.deployment.core.descriptor.IDependency;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IParameter;
import org.zend.php.zendserver.deployment.core.descriptor.IVariable;

public class DeploymentDescriptorParser extends DefaultHandler {
	
	public static final String PACKAGE_DEPENDENCIES_REQUIRED = "package/dependencies/required";
	public static final String DEPENDENCIES_PHP = "package/dependencies/required/php";
	public static final String DEPENDENCIES_EXTENSION = "package/dependencies/required/extension";
	public static final String DEPENDENCIES_DIRECTIVE = "package/dependencies/required/directive";
	public static final String DEPENDENCIES_ZENDSERVER = "package/dependencies/required/zendserver";
	public static final String DEPENDENCIES_ZENDFRAMEWORK = "package/dependencies/required/zendframework";
	public static final String DEPENDENCIES_ZSCOMPONENT = "package/dependencies/required/zendservercomponent";
	
	public static final String DEPENDENCY_NAME = "name";
	public static final String DEPENDENCY_EQUALS = "equals";
	public static final String DEPENDENCY_MIN = "min";
	public static final String DEPENDENCY_MAX = "max";
	public static final String DEPENDENCY_EXCLUDE = "exclude";
	public static final String DEPENDENCY_CONFLICTS = "conflicts";
	
	public static final String PACKAGE_PARAMETERS_PARAMETER = "package/parameters/parameter";
	public static final String PACKAGE_PARAMETERS_PARAMETER_DISPLAY = "package/parameters/parameter[display]";
	public static final String PACKAGE_PARAMETERS_PARAMETER_REQUIRED = "package/parameters/parameter[required]";
	public static final String PACKAGE_PARAMETERS_PARAMETER_READONLY = "package/parameters/parameter[readonly]";
	public static final String PACKAGE_PARAMETERS_PARAMETER_TYPE = "package/parameters/parameter[type]";
	public static final String PACKAGE_PARAMETERS_PARAMETER_IDENTICAL = "package/parameters/parameter[identical]";
	public static final String PACKAGE_PARAMETERS_PARAMETER_ID = "package/parameters/parameter[id]";
	public static final String PACKAGE_PARAMETERS_PARAMETER_DEFAULTVALUE = "package/parameters/parameter/defaultvalue";
	public static final String PACKAGE_PARAMETERS_PARAMETER_DESCRIPTION = "package/parameters/parameter/description";
	public static final String PACKAGE_PARAMETERS_PARAMETER_VALIDATION_ENUMS_ENUM = "package/parameters/parameter/validation/enums/enum";
	
	public static final String PACKAGE_VARIABLES_VARIABLE = "package/variables/variable";
	
	public static final String PACKAGE_PERSISTENTRESOURCES_RESOURCE = "package/persistentresources/resource";
	
	private IPath location = new Path("");
	private List<Integer> counters = new ArrayList<Integer>();
	private List<String> tagNames = new ArrayList<String>();
	
	private StringBuilder sb = new StringBuilder();
	
	private DeploymentDescriptor descriptor;
	private Map<String, String> unboundValues = new HashMap<String, String>();
	private List<String> validationEnums = new ArrayList<String>();
	private Dependency dependency;
	private List<String> excludeList = new ArrayList<String>();
	private Set<Object> changedElements;
	private boolean fRecordChanges;

	public DeploymentDescriptorParser(DeploymentDescriptor descr) {
		this.descriptor = descr;
	}
	
	public void setRecordChanges(boolean value) {
		this.fRecordChanges = value;
		if (fRecordChanges) {
			changedElements = new HashSet<Object>();
		}
	}
	
	public void load(IFile file) {
		try {
			load(file.getContents());
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	public void load(InputStream inputStream) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		
		try {
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(inputStream, this);
			
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void startDocument() throws SAXException {
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		location = location.append(qName);
		String locationStr = location.toPortableString().toLowerCase();
		int index = updateIndex(qName);
		
		boolean read = startReadDependency(locationStr);
		if (read) {
			return;
		}
		
		read = readVariable(locationStr, attributes, index);
		if (read) {
			return;
		}
		
		for (int i = 0; i < attributes.getLength(); i++) {
			String name = attributes.getQName(i).toLowerCase();
			String value = attributes.getValue(i);
			unboundValues.put(locationStr+"["+name+"]", value);
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		sb.append(ch, start, length);
	}
	
	private int updateIndex(String name) {
		int depth = location.segmentCount() - 1;
		if (depth < counters.size()) {
			String alastTag = tagNames.get(depth);
			int index;
			//if (name.equals(alastTag)) {
				index = counters.get(depth) + 1;
			//} else {
			//	index = 0;
			//	tagNames.set(depth, name);
			//}
			
			counters.set(depth, index);
			return index;
		} else {
			counters.add(0);
			tagNames.add(name);
			return 0;
		}
	}
	
	private int removeIndex() {
		int depth = location.segmentCount();
		if (depth < counters.size()) {
			counters.remove(depth);
			tagNames.remove(depth);
		}
		
		return counters.get(depth - 1);
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		String value = sb.toString().trim();
		String locationStr = location.toPortableString().toLowerCase();
		int index = removeIndex();
		
		boolean read = readBasicInfo(value, locationStr);
		if (read) {
			markChanged(descriptor);
		}
		
		if (!read) {
			unboundValues.put(locationStr, value);
			read = endReadDependency(value, locationStr, index);
		}
		if (!read) {
			read = readParameter(value, locationStr, index);
		}
		
		if (!read) {
			read = readPersistentResources(value, locationStr, index);
		}
		
		location = location.removeLastSegments(1);
		sb.setLength(0);
	}
	

	private boolean readVariable(String locationStr, Attributes attributes, int index) {
		if (PACKAGE_VARIABLES_VARIABLE.equals(locationStr)) {
			String name = attributes.getValue("name");
			String value = attributes.getValue("value");
			Variable var = new Variable(name, value);
			List<IVariable> list = descriptor.setVariables();
			if (index < list.size()) {
				copyVariable((Variable) list.get(index), var);
			} else {
				list.add(var);
			}
			markChanged(var);
			return true;
		}
		return false;
	}

	private void copyVariable(Variable dest, Variable src) {
		dest.setName(src.getName());
		dest.setValue(src.getValue());
	}

	private void markChanged(Object obj) {
		if (fRecordChanges) {
			changedElements.add(obj);
		}
	}

	private boolean readPersistentResources(String value, String locationStr, int index) {
		if (PACKAGE_PERSISTENTRESOURCES_RESOURCE.equals(locationStr)) {
			setListElement(descriptor.setPersistentResources(), index, value);
			return true;
		}
		return false;
	}

	private void setListElement(List list, int index, Object var) {
		if (index < list.size()) {
			list.set(index, var);
		} else {
			list.add(var);
		}
		markChanged(var);
	}

	private boolean readParameter(String value, String locationStr, int index) {
		if (PACKAGE_PARAMETERS_PARAMETER_VALIDATION_ENUMS_ENUM.equals(locationStr)) {
			validationEnums.add(value);
			return true;
		}
		
		if (PACKAGE_PARAMETERS_PARAMETER.equals(locationStr)) {
			String id = unboundValues.remove(PACKAGE_PARAMETERS_PARAMETER_ID);
			String type = unboundValues.remove(PACKAGE_PARAMETERS_PARAMETER_TYPE);
			String required = unboundValues.remove(PACKAGE_PARAMETERS_PARAMETER_REQUIRED);
			String readonly = unboundValues.remove(PACKAGE_PARAMETERS_PARAMETER_READONLY);
			String display = unboundValues.remove(PACKAGE_PARAMETERS_PARAMETER_DISPLAY);
			String description = unboundValues.remove(PACKAGE_PARAMETERS_PARAMETER_DESCRIPTION);
			String identical = unboundValues.remove(PACKAGE_PARAMETERS_PARAMETER_IDENTICAL);
			String defaultValue = unboundValues.remove(PACKAGE_PARAMETERS_PARAMETER_DEFAULTVALUE);
			
			description = stripWhitespaces(description);
			
			Parameter param = new Parameter(id, type, Boolean.parseBoolean(required), Boolean.parseBoolean(readonly), display, defaultValue, description, identical);
			
			if (validationEnums.size() > 0) {
				param.setValidValues((String[])validationEnums.toArray(new String[validationEnums.size()]));
				validationEnums.clear();
			}
			
			List<IParameter> list = descriptor.setParameters();
			if (index < list.size()) {
				copyParameter((Parameter) list.get(index), param);
			} else {
				list.add(param);
			}
			markChanged(param);
			return true;
		}
		
		return false;
	}
	
	private void copyParameter(Parameter dest, Parameter src) {
		dest.setDefaultValue(src.getDefaultValue());
		dest.setDescription(src.getDefaultValue());
		dest.setDisplay(src.getDisplay());
		dest.setId(src.getId());
		dest.setRequired(src.isRequired());
		dest.setReadOnly(src.isReadOnly());
		dest.setType(src.getType());
		dest.setIdentical(src.getIdentical());
		dest.setValidValues(src.getValidValues());
	}

	private boolean startReadDependency(String locationStr) {
		if (DEPENDENCIES_PHP.equals(locationStr)) {
			dependency = new Dependency(IDependency.PHP);
			return true;
		}
		if (DEPENDENCIES_DIRECTIVE.equals(locationStr)) {
			dependency = new Dependency(IDependency.DIRECTIVE);
			return true;
		}
		if (DEPENDENCIES_EXTENSION.equals(locationStr)) {
			dependency = new Dependency(IDependency.EXTENSION);
			return true;
		}
		if (DEPENDENCIES_ZENDFRAMEWORK.equals(locationStr)) {
			dependency = new Dependency(IDependency.ZENDFRAMEWORK);
			return true;
		}
		if (DEPENDENCIES_ZENDSERVER.equals(locationStr)) {
			dependency = new Dependency(IDependency.ZENDSERVER);
			return true;
		}
		if (DEPENDENCIES_ZSCOMPONENT.equals(locationStr)) {
			dependency = new Dependency(IDependency.ZENDSERVERCOMPONENT);
			return true;
		}
		
		if (locationStr.endsWith(DEPENDENCY_EXCLUDE)) {
			excludeList.clear();
			return true;
		}
		return false;
	}
	private boolean endReadDependency(String value, String locationStr, int index) {
		if (DEPENDENCIES_PHP.equals(locationStr) || 
			DEPENDENCIES_DIRECTIVE.equals(locationStr) || 
			DEPENDENCIES_EXTENSION.equals(locationStr) || 
			DEPENDENCIES_ZENDFRAMEWORK.equals(locationStr) || 
			DEPENDENCIES_ZENDSERVER.equals(locationStr) || 
			DEPENDENCIES_ZSCOMPONENT.equals(locationStr)) {
			IDependency toSet = dependency;
			dependency = null;
			List<IDependency> list = descriptor.setDependencies();
			if (index < list.size()) {
				copyDependency((Dependency)list.get(index), (Dependency)toSet);
			} else {
				list.add(toSet);
			}
			markChanged(toSet);
			return true;
		}
		
		readDependency(value, locationStr);
				
		return false;
	}

	private void copyDependency(Dependency dest, Dependency src) {
		dest.setType(src.getType());
		dest.setConflicts(src.getConflicts());
		dest.setEquals(src.getEquals());
		dest.setExcludes().clear();
		dest.setExcludes().addAll(src.getExclude());
		dest.setMax(src.getMax());
		dest.setMin(src.getMin());
		dest.setName(src.getName());
	}

	private boolean readDependency(String value, String locationStr) {
		if (locationStr.endsWith("name")) {
			dependency.setName(value);
			return true;
		}
		if (locationStr.endsWith("conflicts")) {
			dependency.setConflicts(value);
			return true;
		}
		if (locationStr.endsWith("equals")) {
			dependency.setEquals(value);
			return true;
		}
		if (locationStr.endsWith("min")) {
			dependency.setMin(value);
			return true;
		}
		if (locationStr.endsWith("max")) {
			dependency.setMax(value);
			return true;
		}
		if (locationStr.endsWith("exclude")) {
			dependency.setExcludes().addAll(excludeList);
			return true;
		}
		
		return false;
	}
	
	private boolean readBasicInfo(String value, String locationStr) {
		if (IDeploymentDescriptor.PACKAGE_NAME.equals(locationStr)) {
			descriptor.setName(value);
			return true;
		}
		
		if (IDeploymentDescriptor.PACKAGE_SUMMARY.equals(locationStr)) {
			descriptor.setSummary(value);
			return true;
		}
		
		if (IDeploymentDescriptor.PACKAGE_DESCRIPTION.equals(locationStr)) {
			descriptor.setDescription(value);
			return true;
		} 
		
		if (IDeploymentDescriptor.PACKAGE_VERSION_RELEASE.equals(locationStr)) {
			descriptor.setReleaseVersion(value);
			return true;
		}
		
		if (IDeploymentDescriptor.PACKAGE_VERSION_API.equals(locationStr)) {
			descriptor.setApiVersion(value);
			return true;
		} 
		
		if (IDeploymentDescriptor.PACKAGE_EULA.equals(locationStr)) {
			descriptor.setEulaLocation(value);
			return true;
		}
		
		if (IDeploymentDescriptor.PACKAGE_ICON.equals(locationStr)) {
			descriptor.setIconLocation(value);
			return true;
		} 
		
		if (IDeploymentDescriptor.PACKAGE_DOCROOT.equals(locationStr)) {
			descriptor.setDocumentRoot(value);
			return true;
		} 
		
		if (IDeploymentDescriptor.PACKAGE_SCRIPTSDIR.equals(locationStr)) {
			descriptor.setScriptsRoot(value);
			return true;
		} 
			
		return false;
	}
	
	private String stripWhitespaces(String str) {
		if (str == null) {
			return null;
		}
		
		StringBuilder sb = new StringBuilder(str);
		boolean isWhiteSpace = false;
		int lastWhSpcIdx = -1;
		for (int i = str.length() - 1; i >= 0; i--) {
			char c = sb.charAt(i);
			if (c == ' ' || c=='\t' || c=='\n') { // is white space
				if (!isWhiteSpace) { // whitespace after non-whitespaces
					lastWhSpcIdx = i;
				}
				isWhiteSpace = true;
			} else if (isWhiteSpace) { // not whitespce, after whitespaces
				sb.replace(i+1, lastWhSpcIdx + 1, " ");
				isWhiteSpace = false;
			}
		}
		
		return sb.toString();
	}

	public Object[] getChangedElements() {
		if (changedElements != null && changedElements.size() > 0) {
			return changedElements.toArray();
		}
		
		return null;
	}

}
