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

public class DeploymentDescriptorParser extends DefaultHandler {

	public static final String PACKAGE = "package";
	public static final String PACKAGE_NAME = "package/name";
	public static final String PACKAGE_SUMMARY = "package/summary";
	public static final String PACKAGE_DESCRIPTION = "package/description";
	public static final String PACKAGE_VERSION_RELEASE = "package/version/release";
	public static final String PACKAGE_VERSION_API = "package/version/api";
	public static final String PACKAGE_EULA = "package/eula";
	public static final String PACKAGE_ICON = "package/icon";
	public static final String PACKAGE_DOCROOT = "package/docroot";
	public static final String PACKAGE_SCRIPTSDIR = "package/scriptsdir";
	public static final String PACKAGE_HEALTHCHECK = "package/healthcheck";

	public static final String PACKAGE_DEPENDENCIES_REQUIRED = "package/dependencies/required";
	public static final String DEPENDENCIES_PHP = "package/dependencies/required/php";
	public static final String DEPENDENCIES_PHP_EQUALS = "package/dependencies/required/php/equals";
	public static final String DEPENDENCIES_PHP_MIN = "package/dependencies/required/php/min";
	public static final String DEPENDENCIES_PHP_MAX = "package/dependencies/required/php/max";
	public static final String DEPENDENCIES_PHP_EXCLUDE = "package/dependencies/required/php/exclude";
	
	public static final String DEPENDENCIES_EXTENSION = "package/dependencies/required/extension";
	public static final String DEPENDENCIES_EXTENSION_NAME = "package/dependencies/required/extension/name";
	public static final String DEPENDENCIES_EXTENSION_EQUALS = "package/dependencies/required/extension/equals";
	public static final String DEPENDENCIES_EXTENSION_MIN = "package/dependencies/required/extension/min";
	public static final String DEPENDENCIES_EXTENSION_MAX = "package/dependencies/required/extension/max";
	public static final String DEPENDENCIES_EXTENSION_EXCLUDE = "package/dependencies/required/extension/exclude";
	public static final String DEPENDENCIES_EXTENSION_CONFLICTS = "package/dependencies/required/extension/conflicts";
	
	public static final String DEPENDENCIES_DIRECTIVE = "package/dependencies/required/directive";
	public static final String DEPENDENCIES_DIRECTIVE_NAME = "package/dependencies/required/directive/name";
	public static final String DEPENDENCIES_DIRECTIVE_MAX = "package/dependencies/required/directive/max";
	public static final String DEPENDENCIES_DIRECTIVE_MIN = "package/dependencies/required/directive/min";
	public static final String DEPENDENCIES_DIRECTIVE_EQUALS = "package/dependencies/required/directive/equals";
	
	public static final String DEPENDENCIES_ZENDSERVER = "package/dependencies/required/zendserver";
	public static final String DEPENDENCIES_ZENDSERVER_EQUALS = "package/dependencies/required/zendserver/equals";
	public static final String DEPENDENCIES_ZENDSERVER_MIN = "package/dependencies/required/zendserver/min";
	public static final String DEPENDENCIES_ZENDSERVER_MAX = "package/dependencies/required/zendserver/max";
	public static final String DEPENDENCIES_ZENDSERVER_EXCLUDE = "package/dependencies/required/zendserver/exclude";

	public static final String DEPENDENCIES_ZSCOMPONENT = "package/dependencies/required/zendservercomponent";
	public static final String DEPENDENCIES_ZSCOMPONENT_NAME = "package/dependencies/required/zendservercomponent/name";
	public static final String DEPENDENCIES_ZSCOMPONENT_EQUALS = "package/dependencies/required/zendservercomponent/equals";
	public static final String DEPENDENCIES_ZSCOMPONENT_MIN = "package/dependencies/required/zendservercomponent/min";
	public static final String DEPENDENCIES_ZSCOMPONENT_MAX = "package/dependencies/required/zendservercomponent/max";
	public static final String DEPENDENCIES_ZSCOMPONENT_EXCLUDE = "package/dependencies/required/zendservercomponent/exclude";
	public static final String DEPENDENCIES_ZSCOMPONENT_CONFLICTS = "package/dependencies/required/zendservercomponent/conflicts";

	public static final String DEPENDENCIES_ZENDFRAMEWORK = "package/dependencies/required/zendframework";
	public static final String DEPENDENCIES_ZENDFRAMEWORK_EQUALS = "package/dependencies/required/zendframework/equals";
	public static final String DEPENDENCIES_ZENDFRAMEWORK_MIN = "package/dependencies/required/zendframework/min";
	public static final String DEPENDENCIES_ZENDFRAMEWORK_MAX = "package/dependencies/required/zendframework/max";
	public static final String DEPENDENCIES_ZENDFRAMEWORK_EXCLUDE = "package/dependencies/required/zendframework/exclude";
	
	public static final String PACKAGE_PARAMETERS_PARAMETER = "package/parameters/parameter";
	public static final String PACKAGE_PARAMETERS_PARAMETER_DISPLAY = "package/parameters/parameter[display]";
	public static final String PACKAGE_PARAMETERS_PARAMETER_REQUIRED = "package/parameters/parameter[required]";
	public static final String PACKAGE_PARAMETERS_PARAMETER_TYPE = "package/parameters/parameter[type]";
	public static final String PACKAGE_PARAMETERS_PARAMETER_ID = "package/parameters/parameter[id]";
	public static final String PACKAGE_PARAMETERS_PARAMETER_DEFAULTVALUE = "package/parameters/parameter/defaultvalue";
	public static final String PACKAGE_PARAMETERS_PARAMETER_DESCRIPTION = "package/parameters/parameter/description";
	public static final String PACKAGE_PARAMETERS_PARAMETER_DESCRIPTION_SHORT = "package/parameters/parameter/description/short";
	public static final String PACKAGE_PARAMETERS_PARAMETER_DESCRIPTION_LONG = "package/parameters/parameter/description/long";
	public static final String PACKAGE_PARAMETERS_PARAMETER_CONDITION_WEBSERVER_CONDITION_TYPE = "package/parameters/parameter/condition/webservercondition[type]";
	public static final String PACKAGE_PARAMETERS_PARAMETER_VALIDATION_ENUMS_ENUM = "package/parameters/parameter/validation/enums/enum";
	
	public static final String PACKAGE_VARIABLES_VARIABLE = "package/variables/variable";
	
	public static final String PACKAGE_PERSISTENTRESOURCES_RESOURCE = "package/persistentresources/resource";
	
	private IPath location = new Path("");
	
	private StringBuilder sb = new StringBuilder();
	
	private DeploymentDescriptor descriptor;
	private Map<String, String> unboundValues = new HashMap<String, String>();
	private List<String> validationEnums = new ArrayList<String>();
	private PHPDependency phpDep;
	private DirectiveDependency directiveDep;
	private ExtensionDependency extDep;
	private ZendFrameworkDependency zfDep;
	private ZendServerDependency zsDep;
	private ZendServerComponentDependency zsCompDep;
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
		
		boolean read = startReadDependency(locationStr);
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
	
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		String value = sb.toString().trim();
		String locationStr = location.toPortableString().toLowerCase();
		
		boolean read = readBasicInfo(value, locationStr);
		if (read) {
			markChanged(descriptor);
		}
		
		if (!read) {
			unboundValues.put(locationStr, value);
			read = endReadDependency(value, locationStr);
		}
		if (!read) {
			read = readParameter(value, locationStr);
		}
		
		if (!read) {
			read = readVariable(value, locationStr);
		}
		
		if (!read) {
			read = readPersistentResources(value, locationStr);
		}
		
		location = location.removeLastSegments(1);
		sb.setLength(0);
	}

	private void markChanged(Object obj) {
		if (fRecordChanges) {
			changedElements.add(obj);
		}
	}

	private boolean readPersistentResources(String value, String locationStr) {
		if (PACKAGE_PERSISTENTRESOURCES_RESOURCE.equals(locationStr)) {
			descriptor.setPersistentResources().add(value);
			markChanged(descriptor);
			return true;
		}
		return false;
	}

	private boolean readVariable(String value, String locationStr) {
		if (PACKAGE_VARIABLES_VARIABLE.equals(locationStr)) {
			Variable var = new Variable(value);
			descriptor.setVariables().add(var);
			markChanged(var);
			return true;
		}
		return false;
	}

	private boolean readParameter(String value, String locationStr) {
		if (PACKAGE_PARAMETERS_PARAMETER_VALIDATION_ENUMS_ENUM.equals(locationStr)) {
			validationEnums.add(value);
			return true;
		}
		
		if (PACKAGE_PARAMETERS_PARAMETER.equals(locationStr)) {
			String id = unboundValues.remove(PACKAGE_PARAMETERS_PARAMETER_ID);
			String type = unboundValues.remove(PACKAGE_PARAMETERS_PARAMETER_TYPE);
			String required = unboundValues.remove(PACKAGE_PARAMETERS_PARAMETER_REQUIRED);
			String display = unboundValues.remove(PACKAGE_PARAMETERS_PARAMETER_DISPLAY);
			String description = unboundValues.remove(PACKAGE_PARAMETERS_PARAMETER_DESCRIPTION);
			String descriptionShort = unboundValues.remove(PACKAGE_PARAMETERS_PARAMETER_DESCRIPTION_SHORT);
			String descriptionLong = unboundValues.remove(PACKAGE_PARAMETERS_PARAMETER_DESCRIPTION_LONG);
			String defaultValue = unboundValues.remove(PACKAGE_PARAMETERS_PARAMETER_DEFAULTVALUE);
			String serverTypeCondition = unboundValues.remove(PACKAGE_PARAMETERS_PARAMETER_CONDITION_WEBSERVER_CONDITION_TYPE);
			
			description = stripWhitespaces(description);
			descriptionShort = stripWhitespaces(descriptionShort);
			descriptionLong = stripWhitespaces(descriptionLong);
			
			if ((descriptionShort == null) && (description != null)) {
				descriptionShort = description;
			}
			
			Parameter param = new Parameter(id, type, Boolean.parseBoolean(required), display, defaultValue, descriptionShort, descriptionLong);
			if (serverTypeCondition != null) {
				param.setServerType(serverTypeCondition);
			}
			
			if (validationEnums.size() > 0) {
				param.setValidValues((String[])validationEnums.toArray(new String[validationEnums.size()]));
				validationEnums.clear();
			}
			descriptor.setParameters().add(param);
			markChanged(param);
			return true;
		}
		
		return false;
	}
	
	private boolean startReadDependency(String locationStr) {
		if (DEPENDENCIES_PHP.equals(locationStr)) {
			phpDep = new PHPDependency();
			return true;
		}
		if (DEPENDENCIES_DIRECTIVE.equals(locationStr)) {
			directiveDep = new DirectiveDependency();
			return true;
		}
		if (DEPENDENCIES_EXTENSION.equals(locationStr)) {
			extDep = new ExtensionDependency();
			return true;
		}
		if (DEPENDENCIES_ZENDFRAMEWORK.equals(locationStr)) {
			zfDep = new ZendFrameworkDependency();
			return true;
		}
		if (DEPENDENCIES_ZENDSERVER.equals(locationStr)) {
			zsDep = new ZendServerDependency();
			return true;
		}
		if (DEPENDENCIES_ZSCOMPONENT.equals(locationStr)) {
			zsCompDep = new ZendServerComponentDependency();
			return true;
		}
		if (DEPENDENCIES_PHP_EXCLUDE.equals(locationStr)) {
			excludeList.clear();
			return true;
		}

		if (DEPENDENCIES_EXTENSION_EXCLUDE.equals(locationStr)) {
			excludeList.clear();
			return true;
		}

		if (DEPENDENCIES_ZENDSERVER_EXCLUDE.equals(locationStr)) {
			excludeList.clear();
			return true;
		}
		
		if (DEPENDENCIES_ZSCOMPONENT_EXCLUDE.equals(locationStr)) {
			excludeList.clear();
			return true;
		}
		if (DEPENDENCIES_ZENDFRAMEWORK_EXCLUDE.equals(locationStr)) {
			excludeList.clear();
			return true;
		}
		return false;
	}
	private boolean endReadDependency(String value, String locationStr) {
		if (DEPENDENCIES_PHP.equals(locationStr)) {
			descriptor.setDependencies().add(phpDep);
			markChanged(phpDep);
			phpDep = null;
			return true;
		}
		if (DEPENDENCIES_DIRECTIVE.equals(locationStr)) {
			descriptor.setDependencies().add(directiveDep);
			markChanged(directiveDep);
			directiveDep = null;
			return true;
		}
		if (DEPENDENCIES_EXTENSION.equals(locationStr)) {
			descriptor.setDependencies().add(extDep);
			markChanged(extDep);
			extDep = null;
			return true;
		}
		if (DEPENDENCIES_ZENDFRAMEWORK.equals(locationStr)) {
			descriptor.setDependencies().add(zfDep);
			markChanged(extDep);
			zfDep = null;
			return true;
		}
		if (DEPENDENCIES_ZENDSERVER.equals(locationStr)) {
			descriptor.setDependencies().add(zsDep);
			markChanged(zsDep);
			zsDep = null;
			return true;
		}
		if (DEPENDENCIES_ZSCOMPONENT.equals(locationStr)) {
			descriptor.setDependencies().add(zsCompDep);
			markChanged(zsCompDep);
			zsCompDep = null;
			return true;
		}
		
		readPHPDependency(value, locationStr);
		
		readExtensionDependency(value, locationStr);
		
		readDirectiveDependency(value, locationStr);
		
		readZendServerDependency(value, locationStr);

		readZendServerComponentDependency(value, locationStr);

		readZendFrameworkDependency(value, locationStr);
		
		return false;
	}

	private boolean readZendFrameworkDependency(String value, String locationStr) {
		if (DEPENDENCIES_ZENDFRAMEWORK_EQUALS.equals(locationStr)) {
			zfDep.setEquals(value);
			return true;
		}
		if (DEPENDENCIES_ZENDFRAMEWORK_MIN.equals(locationStr)) {
			zfDep.setMin(value);
			return true;
		}
		if (DEPENDENCIES_ZENDFRAMEWORK_MAX.equals(locationStr)) {
			zfDep.setMax(value);
			return true;
		}
		if (DEPENDENCIES_ZENDFRAMEWORK_EXCLUDE.equals(locationStr)) {
			zfDep.setExcludes().addAll(excludeList);
			return true;
		}
		
		return false;
	}

	private boolean readZendServerComponentDependency(String value, String locationStr) {
		if (DEPENDENCIES_ZSCOMPONENT_NAME.equals(locationStr)) {
			zsCompDep.setName(value);
			return true;
		}
		if (DEPENDENCIES_ZSCOMPONENT_EQUALS.equals(locationStr)) {
			zsCompDep.setEquals(value);
			return true;
		}
		if (DEPENDENCIES_ZSCOMPONENT_MIN.equals(locationStr)) {
			zsCompDep.setMin(value);
			return true;
		}
		if (DEPENDENCIES_ZSCOMPONENT_MAX.equals(locationStr)) {
			zsCompDep.setMax(value);
			return true;
		}
		if (DEPENDENCIES_ZSCOMPONENT_EXCLUDE.equals(locationStr)) {
			zsCompDep.setExcludes().addAll(excludeList);
			return true;
		}
		if (DEPENDENCIES_ZSCOMPONENT_CONFLICTS.equals(locationStr)) {
			zsCompDep.setConflicts(value);
			return true;
		}
		
		return false;
	}

	private boolean readZendServerDependency(String value, String locationStr) {
		if (DEPENDENCIES_ZENDSERVER_EQUALS.equals(locationStr)) {
			zsDep.setEquals(value);
			return true;
		}
		if (DEPENDENCIES_ZENDSERVER_MIN.equals(locationStr)) {
			zsDep.setMin(value);
			return true;
		}
		if (DEPENDENCIES_ZENDSERVER_MAX.equals(locationStr)) {
			zsDep.setMax(value);
			return true;
		}
		if (DEPENDENCIES_ZENDSERVER_EXCLUDE.equals(locationStr)) {
			zsDep.setExcludes().addAll(excludeList);
			return true;
		}
		
		return false;
	}

	private boolean readDirectiveDependency(String value, String locationStr) {
		if (DEPENDENCIES_DIRECTIVE_NAME.equals(locationStr)) {
			directiveDep.setName(value);
			return true;
		}
		if (DEPENDENCIES_DIRECTIVE_MAX.equals(locationStr)) {
			directiveDep.setMax(value);
			return true;
		}
		if (DEPENDENCIES_DIRECTIVE_MIN.equals(locationStr)) {
			directiveDep.setMin(value);
			return true;
		}
		if (DEPENDENCIES_DIRECTIVE_EQUALS.equals(locationStr)) {
			directiveDep.setEquals(value);
			return true;
		}
		
		return false;
	}

	private boolean readExtensionDependency(String value, String locationStr) {
		if (DEPENDENCIES_EXTENSION_NAME.equals(locationStr)) {
			extDep.setName(value);
			return true;
		}
		if (DEPENDENCIES_EXTENSION_EQUALS.equals(locationStr)) {
			extDep.setEquals(value);
			return true;
		}
		if (DEPENDENCIES_EXTENSION_MIN.equals(locationStr)) {
			extDep.setMin(value);
			return true;
		}
		if (DEPENDENCIES_EXTENSION_MAX.equals(locationStr)) {
			extDep.setMax(value);
			return true;
		}
		if (DEPENDENCIES_EXTENSION_EXCLUDE.equals(locationStr)) {
			extDep.setExcludes().addAll(excludeList);
			return true;
		}
		if (DEPENDENCIES_EXTENSION_CONFLICTS.equals(locationStr)) {
			extDep.setConflicts(value);
			return true;
		}
		
		return false;
	}

	private boolean readPHPDependency(String value, String locationStr) {
		if (DEPENDENCIES_PHP_EQUALS.equals(locationStr)) {
			phpDep.setEquals(value);
			return true;
		}
		if (DEPENDENCIES_PHP_MIN.equals(locationStr)) {
			phpDep.setMin(value);
			return true;
		}
		if (DEPENDENCIES_PHP_MAX.equals(locationStr)) {
			phpDep.setMax(value);
			return true;
		}
		if (DEPENDENCIES_PHP_EXCLUDE.equals(locationStr)) {
			phpDep.setExcludes().addAll(excludeList);
			return true;
		}
		
		return false;
	}
	
	private boolean readBasicInfo(String value, String locationStr) {
		if (PACKAGE_NAME.equals(locationStr)) {
			descriptor.setName(value);
			return true;
		}
		
		if (PACKAGE_SUMMARY.equals(locationStr)) {
			descriptor.setSummary(value);
			return true;
		}
		
		if (PACKAGE_DESCRIPTION.equals(locationStr)) {
			descriptor.setDescription(value);
			return true;
		} 
		
		if (PACKAGE_VERSION_RELEASE.equals(locationStr)) {
			descriptor.setReleaseVersion(value);
			return true;
		}
		
		if (PACKAGE_VERSION_API.equals(locationStr)) {
			descriptor.setApiVersion(value);
			return true;
		} 
		
		if (PACKAGE_EULA.equals(locationStr)) {
			descriptor.setEulaLocation(value);
			return true;
		}
		
		if (PACKAGE_ICON.equals(locationStr)) {
			descriptor.setIconLocation(value);
			return true;
		} 
		
		if (PACKAGE_DOCROOT.equals(locationStr)) {
			descriptor.setDocumentRoot(value);
			return true;
		} 
		
		if (PACKAGE_SCRIPTSDIR.equals(locationStr)) {
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
