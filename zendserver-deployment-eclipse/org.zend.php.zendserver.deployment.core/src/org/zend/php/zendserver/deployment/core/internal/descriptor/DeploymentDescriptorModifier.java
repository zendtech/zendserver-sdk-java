package org.zend.php.zendserver.deployment.core.internal.descriptor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.zend.php.zendserver.deployment.core.DeploymentCore;
import org.zend.php.zendserver.deployment.core.descriptor.IDependency;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptorModifier;
import org.zend.php.zendserver.deployment.core.descriptor.IParameter;
import org.zend.php.zendserver.deployment.core.descriptor.IVariable;


public class DeploymentDescriptorModifier implements IDeploymentDescriptorModifier {

	private String PARAMETER_ID = "id";
	private String PARAMETER_DISPLAY = "display";
	private String PARAMETER_DESCRIPTION = "description";
	private String PARAMETER_TYPE = "type";
	private String PARAMETER_IDENTICAL = "identical";
	private String PARAMETER_REQUIRED = "required";
	private String PARAMETER_READONLY = "readonly";
	private String PARAMETER_DEFAULT = "default";

	
	private DeploymentDescriptor fModel;
	private Document domModel;
	private XPathFactory factory;
	private DescriptorContainer fContainer;
	private boolean autoSave;
	private DOMDocumentReadWriter rw;
	
	public DeploymentDescriptorModifier(DeploymentDescriptor fModel, DOMDocumentReadWriter rw, DescriptorContainer container) {
		factory = XPathFactory.newInstance();
		this.fModel = fModel;
		this.rw = rw;
		this.fContainer = container;
	}

	public static Document createEmptyDocument(DocumentBuilder builder) {
		Document document = builder.newDocument();
		Element rootElement = document.createElement("package");
		rootElement.setAttribute("packagerversion", "1.4.11");
		rootElement.setAttribute("version", "2.0");
		rootElement.setAttribute("xmlns:xsi",
				"http://www.w3.org/2001/XMLSchema-instance");
		rootElement.setAttribute("xsi:schemaLocation",
				"http://www.zend.com packageDescriptor.xsd");
		document.appendChild(rootElement);
		
		return document;
	}

	public void setName(String name) throws CoreException {
		set(fModel, IDeploymentDescriptor.NAME, name);
	}

	public void setSummary(String summary) throws CoreException {
		set(fModel, IDeploymentDescriptor.SUMMARY, summary);
	}

	public void setDescription(String description) throws CoreException {
		set(fModel, IDeploymentDescriptor.DESCRIPTION, description);
	}

	public void setReleaseVersion(String version) throws CoreException {
		set(fModel, IDeploymentDescriptor.VERSION_RELEASE, version);
	}
	
	public void setApiVersion(String version) throws CoreException {
		set(fModel, IDeploymentDescriptor.VERSION_API, version);
	}

	public void setEulaLocation(String location) throws CoreException {
		set(fModel, IDeploymentDescriptor.EULA, location);
	}

	public void setIconLocation(String location) throws CoreException {
		set(fModel, IDeploymentDescriptor.ICON, location);
	}

	public void setDocumentRoot(String location) throws CoreException {
		set(fModel, IDeploymentDescriptor.DOCROOT, location);
	}

	public void setScriptsRoot(String location) throws CoreException {
		set(fModel, IDeploymentDescriptor.SCRIPTSDIR, location);
	}

	public void setHealthcheck(String url) throws CoreException {
		set(fModel, IDeploymentDescriptor.HEALTHCHECK, url);
	}
	
	public void addParameter(IParameter param) throws CoreException {
		loadDomModel();
		Map<String, String> attrs = new HashMap<String, String>();
		attrs.put(PARAMETER_ID, param.getId());
		attrs.put(PARAMETER_REQUIRED, Boolean.toString(param.isRequired()));
		attrs.put(PARAMETER_DEFAULT, param.getDefaultValue());
		attrs.put(PARAMETER_DISPLAY, param.getDisplay());
		attrs.put(PARAMETER_TYPE, param.getType());
		Node n = addNode(IDeploymentDescriptor.PARAMETERS, attrs);
		addNode(n, PARAMETER_DESCRIPTION, param.getDescription());
		storeDomModel();
		fModel.add(IDeploymentDescriptor.PARAMETERS, param);
		fireChange(fModel);
	}

	public void removeParameter(IParameter param) throws CoreException {
		int idx = fModel.getParameters().indexOf(param) + 1;
		fModel.remove(IDeploymentDescriptor.PARAMETERS, idx);
		loadDomModel();
		removeNode(IDeploymentDescriptor.PARAMETERS  + "[" + idx + "]");
		storeDomModel();
		fireChange(fModel);
	}

	public void setParameterRequired(IParameter input, boolean required) throws CoreException {
		int idx = fModel.getParameters().indexOf(input) + 1;
		((Parameter) input).setRequired(required);
		changeAttribute(IDeploymentDescriptor.PARAMETERS + "["+idx+"]" ,PARAMETER_REQUIRED, Boolean.toString(required));
		fireChange(input);
	}

	public void setParameterReadonly(IParameter input, boolean readonly) throws CoreException {
		int idx = fModel.getParameters().indexOf(input) + 1;
		((Parameter) input).setReadOnly(readonly);
		changeAttribute(IDeploymentDescriptor.PARAMETERS + "["+idx+"]" ,PARAMETER_READONLY, Boolean.toString(readonly));
		fireChange(input);
	}
	
	public void setParameterId(IParameter input, String text) throws CoreException {
		int idx = fModel.getParameters().indexOf(input) + 1;
		((Parameter) input).setId(text);
		changeAttribute(IDeploymentDescriptor.PARAMETERS + "["+idx+"]" ,PARAMETER_ID, text);
		fireChange(input);
	}

	public void setParameterDisplay(IParameter input, String text) throws CoreException {
		int idx = fModel.getParameters().indexOf(input) + 1;
		((Parameter) input).setDisplay(text);
		changeAttribute(IDeploymentDescriptor.PARAMETERS + "["+idx+"]" ,PARAMETER_DISPLAY, text);
		fireChange(input);
	}

	public void setParameterDefault(IParameter input, String text) throws CoreException {
		int idx = fModel.getParameters().indexOf(input) + 1;
		((Parameter) input).setDefaultValue(text);
		changeAttribute(IDeploymentDescriptor.PARAMETERS + "["+idx+"]" ,PARAMETER_DEFAULT, text);
		fireChange(input);
	}

	public void setParameterType(IParameter input, String text) throws CoreException {
		int idx = fModel.getParameters().indexOf(input) + 1;
		((Parameter) input).setType(text);
		changeAttribute(IDeploymentDescriptor.PARAMETERS + "["+idx+"]" ,PARAMETER_TYPE, text);
		fireChange(input);
	}

	public void setParameterIdentical(IParameter input, String text) throws CoreException {
		int idx = fModel.getParameters().indexOf(input) + 1;
		((Parameter) input).setIdentical(text);
		changeAttribute(IDeploymentDescriptor.PARAMETERS + "["+idx+"]" ,PARAMETER_IDENTICAL, text);
		fireChange(input);
	}
	
	public void setParameterDescription(IParameter input, String text) throws CoreException {
		int idx = fModel.getParameters().indexOf(input) + 1;
		((Parameter) input).setDescription(text);
		changeText(text, IDeploymentDescriptor.PARAMETERS + "["+idx+"]/description");
		fireChange(input);
	}
	
	public void setParameterValidation(IParameter input, String[] array) throws CoreException {
		loadDomModel();
		int idx = fModel.getParameters().indexOf(input) + 1;
		((Parameter) input).setValidValues(array);
		String xpath = IDeploymentDescriptor.PARAMETERS + "["+idx+"]/validation/enums";
		Node node = getNode(xpath);
		
		if (node != null) {
			node.getParentNode().removeChild(node);
		}
		
		if (array == null || array.length == 0) {
			return;
		}
		
		node = addNode(xpath, null);
		
		for (int i = 0; i < array.length; i++) {
			Node newNode = addNode(xpath+"/enum", null);
			newNode.setTextContent(array[i]);
		}
		storeDomModel();
		fireChange(input);
	}

	public void addVariable(IVariable var) throws CoreException {
		loadDomModel();
		Map<String, String> attrs = new HashMap<String, String>();
		if (var.getName() != null) {
			attrs.put("name", var.getName());
		}
		if (var.getValue() != null) {
			attrs.put("value", var.getValue());
		}
		Node node = addNode(IDeploymentDescriptor.VARIABLES, attrs);
		node.setTextContent(var.getValue());
		storeDomModel();
		fModel.add(IDeploymentDescriptor.VARIABLES, var);
		fireChange(fModel);
	}

	public void removeVariable(IVariable var) throws CoreException {
		int idx = fModel.getVariables().indexOf(var) + 1;
		fModel.remove(IDeploymentDescriptor.VARIABLES, idx);
		loadDomModel();
		removeNode(IDeploymentDescriptor.VARIABLES  + "[" + idx + "]");
		storeDomModel();
		fireChange(fModel);
	}

	public void setVariableName(IVariable var, String text) throws CoreException {
		int idx = fModel.getVariables().indexOf(var) + 1;
		((Variable) var).setName(text);
		changeAttribute(IDeploymentDescriptor.VARIABLES + "["+idx+"]", "name", text);
		fireChange(var);
	}
	
	public void setVariableValue(IVariable var, String text) throws CoreException {
		int idx = fModel.getVariables().indexOf(var) + 1;
		((Variable) var).setValue(text);
		changeAttribute(IDeploymentDescriptor.VARIABLES + "["+idx+"]", "value", text);
		fireChange(var);
	}

	public void addDependency(IDependency prereq) throws CoreException {
		loadDomModel();
		Map<String, String> attrs = new HashMap<String, String>();
		Node node = addNode(IDeploymentDescriptor.DEPENDENCIES + "/" + prereq.getType(), attrs);
		if (prereq.getName() != null) {
			addNode(node, DeploymentDescriptorParser.DEPENDENCY_NAME, prereq.getName());
		}
		if (prereq.getMin() != null) {
			addNode(node, DeploymentDescriptorParser.DEPENDENCY_MIN, prereq.getMin());
		}
		if (prereq.getMax() != null) {
			addNode(node, DeploymentDescriptorParser.DEPENDENCY_MAX, prereq.getMax());
		}
		if (prereq.getEquals() != null) {
			addNode(node, DeploymentDescriptorParser.DEPENDENCY_EQUALS, prereq.getEquals());
		}
		if (prereq.getConflicts() != null) {
			addNode(node, DeploymentDescriptorParser.DEPENDENCY_CONFLICTS, prereq.getConflicts());
		}
		if (prereq.getExclude() != null && prereq.getExclude().size() > 0) {
			List<String> excludes = prereq.getExclude();
			for (String exclude : excludes) {
				addNode(node, DeploymentDescriptorParser.DEPENDENCY_EXCLUDE, exclude);
			}
		}
		
		storeDomModel();
		fModel.add(IDeploymentDescriptor.DEPENDENCIES, prereq);
		fireChange(fModel);
	}

	public void removeDependency(IDependency prereq) throws CoreException {
		int idx = fModel.getDependencies().indexOf(prereq) + 1;
		fModel.remove(IDeploymentDescriptor.DEPENDENCIES, idx);
		loadDomModel();
		removeNode(IDeploymentDescriptor.DEPENDENCIES  + "/*[" + idx + "]");
		storeDomModel();
		fireChange(fModel);
	}

	public void setDependencyName(IDependency dep, String text) throws CoreException {
		int idx = fModel.getDependencies().indexOf(dep) + 1;
		((Dependency) dep).setName(text);
		changeText(text, IDeploymentDescriptor.DEPENDENCIES + "/*["+idx+"]/name");
		fireChange(dep);
	}

	public void setDependencyMin(IDependency dep, String text) throws CoreException {
		int idx = fModel.getDependencies().indexOf(dep) + 1;
		((Dependency) dep).setMin(text);
		changeText(text, IDeploymentDescriptor.DEPENDENCIES + "/*["+idx+"]/min");
		fireChange(dep);
	}

	public void setDependencyMax(IDependency dep, String text) throws CoreException {
		int idx = fModel.getDependencies().indexOf(dep) + 1;
		((Dependency) dep).setMax(text);
		changeText(text, IDeploymentDescriptor.DEPENDENCIES + "/*["+idx+"]/max");
		fireChange(dep);
	}

	public void setDependencyEquals(IDependency dep, String text) throws CoreException {
		int idx = fModel.getDependencies().indexOf(dep) + 1;
		((Dependency) dep).setEquals(text);
		changeText(text, IDeploymentDescriptor.DEPENDENCIES + "/*["+idx+"]/equals");
		fireChange(dep);
	}

	public void setDependencyConflicts(IDependency dep, String text) throws CoreException {
		int idx = fModel.getDependencies().indexOf(dep) + 1;
		((Dependency) dep).setConflicts(text);
		changeText(text, IDeploymentDescriptor.DEPENDENCIES + "/*["+idx+"]/conflicts");
		fireChange(dep);
	}

	public void addDependencyExclude(IDependency dep, String text) throws CoreException {
		int idx = fModel.getDependencies().indexOf(dep) + 1;
		((Dependency) dep).setExcludes().add(text);
		loadDomModel();
		Node n = addNode(IDeploymentDescriptor.DEPENDENCIES + "/*["+idx+"]/exclude", null);
		n.setTextContent(text);
		storeDomModel();
		fireChange(dep);
	}

	public void removeDependencyExclude(IDependency dep, String text) throws CoreException {
		int idx = fModel.getDependencies().indexOf(dep) + 1;
		int excludeIdx = ((Dependency) dep).setExcludes().indexOf(text);
		((Dependency) dep).setExcludes().remove(text);
		loadDomModel();
		removeNode(IDeploymentDescriptor.DEPENDENCIES + "/*["+idx+"]/exclude["+excludeIdx+"]");
		storeDomModel();
		fireChange(dep);
	}

	public void addPersistentResource(String path) throws CoreException {
		loadDomModel();
		Node n = addNode(IDeploymentDescriptor.PERSISTENT_RESOURCES, null);
		n.setTextContent(path);
		storeDomModel();
		fModel.addPersistentResource(path);
		fireChange(fModel);
	}

	public void removePersistentResource(String path) throws CoreException {
		int idx = fModel.getPersistentResources().indexOf(path) + 1;
		fModel.removePersistentResource(idx);
		loadDomModel();
		removeNode(IDeploymentDescriptor.PERSISTENT_RESOURCES  + "[" + idx + "]");
		storeDomModel();
		fireChange(fModel);
	}
	
	private void changeAttribute(String path, String param, String value) throws CoreException {
		loadDomModel();
		Node result = getNode(path);
		((Element) result).setAttribute(param, value);
		storeDomModel();
	}
	
	private void changeText(String newVal, String path) throws CoreException {
		loadDomModel();
		Node result = getNode(path);
		if (result == null) {
			result = addNode(path, null);
		}
		result.setTextContent(newVal);
		storeDomModel();
	}
	

	private void removeNode(String xpath) throws CoreException {
		Node n = getNode(xpath);
		Node parent = n.getParentNode();
		parent.removeChild(n);
	}
	
	private Node addNode(String xpath, Map<String, String> attrs) throws CoreException {
		int idx = xpath.lastIndexOf('/');
		String parentXpath = xpath.substring(0, idx);
		String name = xpath.substring(idx + 1);
		
		Node n = getNode(parentXpath);
		if (n == null) {
			n = addNode(parentXpath, null);
		}
		
		Element e = domModel.createElement(name);
		
		if (attrs != null) {
			for (Iterator<Entry<String, String>> i = attrs.entrySet().iterator(); i.hasNext(); ) {
				Entry<String, String> attr = i.next();
				if (attr.getKey() != null && attr.getValue() != null) {
					e.setAttribute(attr.getKey(), attr.getValue());
				}
			}
		}
		n.appendChild(e);
		return e;
	}
	
	private void addNode(Node n, String name, String text) {
		Element e = domModel.createElement(name);
		e.setTextContent(text);
		n.appendChild(e);
	}

	private Node getNode(String path) throws CoreException {
		Node result;
		try {
			XPath xpath = factory.newXPath();
			XPathExpression expr = xpath.compile(path);
			result = (Node) expr.evaluate(domModel, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			throw new CoreException(new Status(IStatus.ERROR, DeploymentCore.PLUGIN_ID, e.getMessage(), e));
		}
	
		return result;
	}

	private void loadDomModel() throws CoreException {
		if (domModel == null) {
			domModel = rw.read();
		}		
	}
	
	private void storeDomModel() throws CoreException {
		if (autoSave) {
			doStoreDomModel();
		}
	}
		
	private void doStoreDomModel() throws CoreException {	
		rw.write(domModel);
	}

	public IDeploymentDescriptor getDescriptor() {
		return fModel;
	}
	
	private void fireChange(Object o) {
		fContainer.fireChange(o);
	}

	public void save() throws CoreException {
		doStoreDomModel();
	}

	public void setAutoSave(boolean autoSave) {
		this.autoSave = autoSave;
	}

	public void set(IDeploymentDescriptor target, String property, String value)
			throws CoreException {
		String oldVal = target.get(property);
		if (((oldVal == null) && (value == null)) || value.equals(oldVal)) {
			return;
		}
		
		if (value == null) {
			removeNode(property);
		} else {
			changeText(value, property);
		}
		target.set(property, value);
		fireChange(fModel);
	}
}
