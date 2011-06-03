package org.zend.php.zendserver.deployment.core.internal.descriptor;

import java.io.ByteArrayInputStream;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.IDocument;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.zend.php.zendserver.deployment.core.DeploymentCore;
import org.zend.php.zendserver.deployment.core.descriptor.IDependency;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptorModifier;
import org.zend.php.zendserver.deployment.core.descriptor.IParameter;
import org.zend.php.zendserver.deployment.core.descriptor.IVariable;


public class DeploymentDescriptorModifier implements IDeploymentDescriptorModifier {

	public static interface DOMModelReadWrite {
		Document read() throws CoreException;
		void write(Document doc) throws CoreException;
	}
	
	public static class FileDOMRW implements DOMModelReadWrite {

		private IFile fFile;

		public FileDOMRW(IFile file) {
			this.fFile = file;
		}
		
		public Document read() throws CoreException {
			try {
				DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = fact.newDocumentBuilder();

				if (!fFile.exists()) {
					return createEmptyDocument(builder);
				}
				
				return builder.parse(fFile.getContents());
			} catch (ParserConfigurationException e) {
				throw new CoreException(new Status(IStatus.ERROR, DeploymentCore.PLUGIN_ID, e.getMessage(), e));
			} catch (SAXException e) {
				throw new CoreException(new Status(IStatus.ERROR, DeploymentCore.PLUGIN_ID, e.getMessage(), e));
			} catch (IOException e) {
				throw new CoreException(new Status(IStatus.ERROR, DeploymentCore.PLUGIN_ID, e.getMessage(), e));
			}
		}

		public void write(Document doc) throws CoreException {
			try {
				PipedInputStream in = new PipedInputStream();
				PipedOutputStream out = new PipedOutputStream(in);
				Result result = new StreamResult(out);

		        Source source = new DOMSource(doc);
		        
		        Transformer xformer = TransformerFactory.newInstance().newTransformer();
		        xformer.setOutputProperty(OutputKeys.INDENT, "yes");
		        xformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
	            xformer.transform(source, result);
		        out.close();
				
				IProgressMonitor mon = new NullProgressMonitor();
				if (fFile.exists()) {
					fFile.setContents(in, true, true, mon);
				} else {
					fFile.create(in, true, mon);
				}
				
			} catch (TransformerFactoryConfigurationError e) {
				throw new CoreException(new Status(IStatus.ERROR, DeploymentCore.PLUGIN_ID, e.getMessage(), e));
			} catch (TransformerException e) {
				throw new CoreException(new Status(IStatus.ERROR, DeploymentCore.PLUGIN_ID, e.getMessage(), e));
			} catch (IOException e) {
				throw new CoreException(new Status(IStatus.ERROR, DeploymentCore.PLUGIN_ID, e.getMessage(), e));
			}
			
		}
		
	}
	
	public static class DocumentDOMRW implements DOMModelReadWrite {
		
		private IDocument fDocument;

		public DocumentDOMRW(IDocument document) {
			this.fDocument = document;
		}

		public Document read() throws CoreException {
			DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = null;
			try {
				builder = fact.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				System.out.println("Error marker for "+e.getMessage());
				//throw new CoreException(new Status(IStatus.ERROR, DeploymentCore.PLUGIN_ID, e.getMessage(), e));
				return null;
			}

			try {
				ByteArrayInputStream bais = new ByteArrayInputStream(fDocument.get().getBytes());
				return builder.parse(bais);
			} catch (SAXException e) {
				System.out.println("Error marker for "+e.getMessage());
				//throw new CoreException(new Status(IStatus.ERROR, DeploymentCore.PLUGIN_ID, e.getMessage(), e));
			} catch (IOException e) {
				System.out.println("Error marker for "+e.getMessage());
				//throw new CoreException(new Status(IStatus.ERROR, DeploymentCore.PLUGIN_ID, e.getMessage(), e));
			}
			
			return createEmptyDocument(builder);
		}

		public void write(Document domModel) throws CoreException {
			try {
				Source source = new DOMSource(domModel);

		        CharArrayWriter caw = new CharArrayWriter();
		        Result result = new StreamResult(caw);
		        
		        Transformer xformer = TransformerFactory.newInstance().newTransformer();
		        xformer.setOutputProperty(OutputKeys.INDENT, "yes");
		        xformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
	            
		        xformer.transform(source, result);

		        fDocument.set(caw.toString());
			} catch (TransformerFactoryConfigurationError e) {
				throw new CoreException(new Status(IStatus.ERROR, DeploymentCore.PLUGIN_ID, e.getMessage(), e));
			} catch (TransformerException e) {
				throw new CoreException(new Status(IStatus.ERROR, DeploymentCore.PLUGIN_ID, e.getMessage(), e));
			}
			
		}
		
	}
	
	private String PARAMETER_ID = "id";
	private String PARAMETER_DISPLAY = "display";
	private String PARAMETER_DESCRIPTION = "description";
	private String PARAMETER_TYPE = "type";
	private String PARAMETER_REQUIRED = "required";
	private String PARAMETER_DEFAULT = "default";

	
	private DeploymentDescriptor fModel;
	private Document domModel;
	private XPathFactory factory;
	private DescriptorContainer fContainer;
	private boolean autoSave;
	private DOMModelReadWrite rw;
	
	public DeploymentDescriptorModifier(DeploymentDescriptor fModel, DOMModelReadWrite rw, DescriptorContainer container) {
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
		String oldVal = fModel.getName();
		if (name.equals(oldVal)) {
			return;
		}
		changeText(name, DeploymentDescriptorParser.PACKAGE_NAME);
		fModel.setName(name);
		fireChange(fModel);
	}

	public void setSummary(String summary) throws CoreException {
		String oldVal = fModel.getSummary();
		if (summary.equals(oldVal)) {
			return;
		}
		changeText(summary, DeploymentDescriptorParser.PACKAGE_SUMMARY);
		fModel.setSummary(summary);
		fireChange(fModel);
	}

	public void setDescription(String description) throws CoreException {
		String oldVal = fModel.getDescription();
		if (description.equals(oldVal)) {
			return;
		}
		changeText(description, DeploymentDescriptorParser.PACKAGE_DESCRIPTION);
		fModel.setDescription(description);
		fireChange(fModel);
	}

	public void setReleaseVersion(String version) throws CoreException {
		String oldVal = fModel.getReleaseVersion();
		if (((oldVal == null) && (version == null)) || version.equals(oldVal)) {
			return;
		}
		
		changeText(version == null ? "" : version, DeploymentDescriptorParser.PACKAGE_VERSION_RELEASE);
		fModel.setReleaseVersion(version);
		fireChange(fModel);
	}
	
	public void setApiVersion(String version) throws CoreException {
		String oldVal = fModel.getApiVersion();
		if (((oldVal == null) && (version == null)) || version.equals(oldVal)) {
			return;
		}
		
		changeText(version == null ? "" : version, DeploymentDescriptorParser.PACKAGE_VERSION_API);
		fModel.setApiVersion(version);
		fireChange(fModel);
	}

	public void setEulaLocation(String location) throws CoreException {
		String oldVal = fModel.getEulaLocation();
		if (((oldVal == null) && (location == null)) || location.equals(oldVal)) {
			return;
		}
		
		changeText(location == null ? "" : location, DeploymentDescriptorParser.PACKAGE_EULA);
		fModel.setEulaLocation(location);
		fireChange(fModel);
	}

	public void setIconLocation(String location) throws CoreException {
		String oldVal = fModel.getIconLocation();
		if (((oldVal == null) && (location == null)) || location.equals(oldVal)) {
			return;
		}
		
		changeText(location == null ? "" : location, DeploymentDescriptorParser.PACKAGE_ICON);
		fModel.setIconLocation(location);
		fireChange(fModel);
	}

	public void setDocumentRoot(String location) throws CoreException {
		String oldVal = fModel.getDocumentRoot();
		if (((oldVal == null) && (location == null)) || location.equals(oldVal)) {
			return;
		}
		
		changeText(location == null ? "" : location, DeploymentDescriptorParser.PACKAGE_DOCROOT);
		fModel.setDocumentRoot(location);
		fireChange(fModel);
	}

	public void setScriptsRoot(String location) throws CoreException {
		String oldVal = fModel.getScriptsRoot();
		if (((oldVal == null) && (location == null)) || location.equals(oldVal)) {
			return;
		}
		
		changeText(location == null ? "" : location, DeploymentDescriptorParser.PACKAGE_SCRIPTSDIR);
		fModel.setScriptsRoot(location);
		fireChange(fModel);
	}

	public void setHealthcheck(String url) throws CoreException {
		String oldVal = fModel.getHealthcheck();
		if (((oldVal == null) && (url == null)) || url.equals(oldVal)) {
			return;
		}
		
		changeText(url == null ? "" : url, DeploymentDescriptorParser.PACKAGE_HEALTHCHECK);
		fModel.setHealthcheck(url);
		fireChange(fModel);
	}
	
	public void addParameter(IParameter param) throws CoreException {
		loadDomModel();
		Map<String, String> attrs = new HashMap<String, String>();
		attrs.put(PARAMETER_ID, param.getId());
		attrs.put(PARAMETER_REQUIRED, Boolean.toString(param.isRequired()));
		attrs.put(PARAMETER_DEFAULT, param.getDefaultValue());
		attrs.put(PARAMETER_DISPLAY, param.getDisplay());
		attrs.put(PARAMETER_TYPE, param.getType());
		Node n = addNode(DeploymentDescriptorParser.PACKAGE_PARAMETERS_PARAMETER, attrs);
		addNode(n, PARAMETER_DESCRIPTION, param.getDescription());
		storeDomModel();
		fModel.setParameters().add(param);
		fireChange(fModel);
	}

	public void removeParameter(IParameter param) throws CoreException {
		int idx = fModel.setParameters().indexOf(param) + 1;
		fModel.setParameters().remove(param);
		loadDomModel();
		removeNode(DeploymentDescriptorParser.PACKAGE_PARAMETERS_PARAMETER  + "[" + idx + "]");
		storeDomModel();
		fireChange(fModel);
	}

	public void setParameterRequired(IParameter input, boolean required) throws CoreException {
		int idx = fModel.setParameters().indexOf(input) + 1;
		((Parameter) input).setRequired(required);
		changeAttribute(DeploymentDescriptorParser.PACKAGE_PARAMETERS_PARAMETER + "["+idx+"]" ,PARAMETER_REQUIRED, Boolean.toString(required));
		fireChange(input);
	}

	public void setParameterId(IParameter input, String text) throws CoreException {
		int idx = fModel.setParameters().indexOf(input) + 1;
		((Parameter) input).setId(text);
		changeAttribute(DeploymentDescriptorParser.PACKAGE_PARAMETERS_PARAMETER + "["+idx+"]" ,PARAMETER_ID, text);
		fireChange(input);
	}

	public void setParameterDisplay(IParameter input, String text) throws CoreException {
		int idx = fModel.setParameters().indexOf(input) + 1;
		((Parameter) input).setDisplay(text);
		changeAttribute(DeploymentDescriptorParser.PACKAGE_PARAMETERS_PARAMETER + "["+idx+"]" ,PARAMETER_DISPLAY, text);
		fireChange(input);
	}

	public void setParameterDefault(IParameter input, String text) throws CoreException {
		int idx = fModel.setParameters().indexOf(input) + 1;
		((Parameter) input).setDefaultValue(text);
		changeAttribute(DeploymentDescriptorParser.PACKAGE_PARAMETERS_PARAMETER + "["+idx+"]" ,PARAMETER_DEFAULT, text);
		fireChange(input);
	}

	public void setParameterType(IParameter input, String text) throws CoreException {
		int idx = fModel.setParameters().indexOf(input) + 1;
		((Parameter) input).setType(text);
		changeAttribute(DeploymentDescriptorParser.PACKAGE_PARAMETERS_PARAMETER + "["+idx+"]" ,PARAMETER_TYPE, text);
		fireChange(input);
	}

	public void setParameterDescription(IParameter input, String text) throws CoreException {
		int idx = fModel.setParameters().indexOf(input) + 1;
		((Parameter) input).setDescription(text);
		changeText(text, DeploymentDescriptorParser.PACKAGE_PARAMETERS_PARAMETER + "["+idx+"]/description");
		fireChange(input);
	}

	public void setParameterLongDescription(IParameter input, String text) throws CoreException {
		int idx = fModel.setParameters().indexOf(input) + 1;
		((Parameter) input).setLongDescription(text);
		changeText(text, DeploymentDescriptorParser.PACKAGE_PARAMETERS_PARAMETER + "["+idx+"]/description/long");
		fireChange(input);
	}

	public void addVariable(IVariable var) throws CoreException {
		loadDomModel();
		Map<String, String> attrs = new HashMap<String, String>();
		Node node = addNode(DeploymentDescriptorParser.PACKAGE_VARIABLES_VARIABLE, attrs);
		node.setTextContent(var.getValue());
		storeDomModel();
		fModel.setVariables().add(var);
		fireChange(fModel);
	}

	public void removeVariable(IVariable var) throws CoreException {
		int idx = fModel.setVariables().indexOf(var) + 1;
		fModel.setVariables().remove(var);
		loadDomModel();
		removeNode(DeploymentDescriptorParser.PACKAGE_VARIABLES_VARIABLE  + "[" + idx + "]");
		storeDomModel();
		fireChange(fModel);
	}

	public void setVariableName(IVariable var, String text) throws CoreException {
		int idx = fModel.setVariables().indexOf(var) + 1;
		((Variable) var).setName(text);
		changeAttribute(DeploymentDescriptorParser.PACKAGE_VARIABLES_VARIABLE + "["+idx+"]", "name", text);
		fireChange(var);
	}
	
	public void setVariableValue(IVariable var, String text) throws CoreException {
		int idx = fModel.setVariables().indexOf(var) + 1;
		((Variable) var).setValue(text);
		changeAttribute(DeploymentDescriptorParser.PACKAGE_VARIABLES_VARIABLE + "["+idx+"]", "value", text);
		fireChange(var);
	}

	public void addDependency(IDependency prereq) throws CoreException {
		loadDomModel();
		Map<String, String> attrs = new HashMap<String, String>();
		addNode(DeploymentDescriptorParser.PACKAGE_DEPENDENCIES_REQUIRED, attrs);
		storeDomModel();
		fModel.setDependencies().add(prereq);
		fireChange(fModel);
	}

	public void removeDependency(IDependency prereq) throws CoreException {
		int idx = fModel.setDependencies().indexOf(prereq) + 1;
		fModel.setDependencies().remove(prereq);
		loadDomModel();
		removeNode(DeploymentDescriptorParser.PACKAGE_DEPENDENCIES_REQUIRED  + "[" + idx + "]");
		storeDomModel();
		fireChange(fModel);
	}

	public void setDependencyName(IDependency dep, String text) throws CoreException {
		int idx = fModel.setDependencies().indexOf(dep) + 1;
		((Dependency) dep).setName(text);
		changeText(text, DeploymentDescriptorParser.PACKAGE_DEPENDENCIES_REQUIRED + "/*["+idx+"]/name");
		fireChange(dep);
	}

	public void setDependencyMin(IDependency dep, String text) throws CoreException {
		int idx = fModel.setDependencies().indexOf(dep) + 1;
		((Dependency) dep).setMin(text);
		changeText(text, DeploymentDescriptorParser.PACKAGE_DEPENDENCIES_REQUIRED + "/*["+idx+"]/min");
		fireChange(dep);
	}

	public void setDependencyMax(IDependency dep, String text) throws CoreException {
		int idx = fModel.setDependencies().indexOf(dep) + 1;
		((Dependency) dep).setMax(text);
		changeText(text, DeploymentDescriptorParser.PACKAGE_DEPENDENCIES_REQUIRED + "/*["+idx+"]/max");
		fireChange(dep);
	}

	public void setDependencyEquals(IDependency dep, String text) throws CoreException {
		int idx = fModel.setDependencies().indexOf(dep) + 1;
		((Dependency) dep).setEquals(text);
		changeText(text, DeploymentDescriptorParser.PACKAGE_DEPENDENCIES_REQUIRED + "/*["+idx+"]/equals");
		fireChange(dep);
	}

	public void setDependencyConflicts(IDependency dep, String text) throws CoreException {
		int idx = fModel.setDependencies().indexOf(dep) + 1;
		((Dependency) dep).setConflicts(text);
		changeText(text, DeploymentDescriptorParser.PACKAGE_DEPENDENCIES_REQUIRED + "/*["+idx+"]/conflicts");
		fireChange(dep);
	}

	public void addDependencyExclude(IDependency dep, String text) throws CoreException {
		int idx = fModel.setDependencies().indexOf(dep) + 1;
		((Dependency) dep).setExcludes().add(text);
		loadDomModel();
		Node n = addNode(DeploymentDescriptorParser.PACKAGE_DEPENDENCIES_REQUIRED + "/*["+idx+"]/exclude", null);
		n.setTextContent(text);
		storeDomModel();
		fireChange(dep);
	}

	public void removeDependencyExclude(IDependency dep, String text) throws CoreException {
		int idx = fModel.setDependencies().indexOf(dep) + 1;
		int excludeIdx = ((Dependency) dep).setExcludes().indexOf(text);
		((Dependency) dep).setExcludes().remove(text);
		loadDomModel();
		removeNode(DeploymentDescriptorParser.PACKAGE_DEPENDENCIES_REQUIRED + "/*["+idx+"]/exclude["+excludeIdx+"]");
		storeDomModel();
		fireChange(dep);
	}

	public void addPersistentResource(String path) throws CoreException {
		loadDomModel();
		Node n = addNode(DeploymentDescriptorParser.PACKAGE_PERSISTENTRESOURCES_RESOURCE, null);
		n.setTextContent(path);
		storeDomModel();
		fModel.setPersistentResources().add(path);
		fireChange(fModel);
	}

	public void removePersistentResource(String path) throws CoreException {
		int idx = fModel.setPersistentResources().indexOf(path) + 1;
		fModel.setPersistentResources().remove(path);
		loadDomModel();
		removeNode(DeploymentDescriptorParser.PACKAGE_PERSISTENTRESOURCES_RESOURCE  + "[" + idx + "]");
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
}
