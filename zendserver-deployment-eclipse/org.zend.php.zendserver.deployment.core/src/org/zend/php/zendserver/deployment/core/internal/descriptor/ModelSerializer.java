package org.zend.php.zendserver.deployment.core.internal.descriptor;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.zend.php.zendserver.deployment.core.DeploymentCore;
import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorFactory;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IModelContainer;
import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;

public class ModelSerializer {

	private DocumentBuilderFactory fact;
	private DocumentBuilder builder;
	private XPathFactory factory;
	private Document document;
	private DocumentStore dest;

	public ModelSerializer() {
		fact = DocumentBuilderFactory.newInstance();
		try {
			builder = fact.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		factory = XPathFactory.newInstance();
	}
	
	public void load(InputStream src, IModelContainer model) throws XPathExpressionException, CoreException, SAXException, IOException {
		document = builder.parse(src);
		
		Node root = getNode(document, IDeploymentDescriptor.PACKAGE.xpath);
		
		loadProperties(root, model);
	}
	
	public void serialize(IModelContainer model) throws XPathExpressionException, CoreException, TransformerFactoryConfigurationError, TransformerException {
		if (document == null) {
			document = DeploymentDescriptorFactory.createEmptyDocument(builder);
		}
		
		Node root = getNode(document, IDeploymentDescriptor.PACKAGE.xpath);
		if (root == null) {
			root = addNode(document, IDeploymentDescriptor.PACKAGE.xpath);
		}
		
		writeProperties(root, model);
	}
	
	public void setOutput(DocumentStore dest) {
		this.dest = dest;
	}
	
	public void write() throws CoreException {
		if (dest == null) {
			return;
		}
		
		try {
			Result result = dest.getOutput();

	        Source source = new DOMSource(document);
	        
	        Transformer xformer = TransformerFactory.newInstance().newTransformer();
	        xformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        xformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            xformer.transform(source, result);
	        
			dest.write();
			
		} catch (TransformerFactoryConfigurationError e) {
			throw new CoreException(new Status(IStatus.ERROR, DeploymentCore.PLUGIN_ID, e.getMessage(), e));
		} catch (TransformerException e) {
			throw new CoreException(new Status(IStatus.ERROR, DeploymentCore.PLUGIN_ID, e.getMessage(), e));
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, DeploymentCore.PLUGIN_ID, e.getMessage(), e));
		}
	}
	
	/**
	 * Loads DOM model to java model adding new elements.
	 * 
	 * @param doc
	 * @param model
	 * @throws XPathExpressionException 
	 */
	private void loadChildren(Node doc, IModelContainer model) throws XPathExpressionException {
		Feature[] childNames = model.getChildNames();
		for (Feature c : childNames) {
			Node[] nodes = getNodes(doc, c.xpath);
			
			if (c.type == IModelObject.class) {
				for (int i = 0; i < nodes.length; i++) {
					Node node = nodes[i];
					IModelObject obj = (IModelObject) model.add(c, DeploymentDescriptorFactory.createModelElement(c));
					loadProperties(node, obj);
				}
			} else if (c.type == String.class){
				for (int i = 0; i < nodes.length; i++) {
					String string = nodes[i].getTextContent();
					string = stripWhitespaces(string);
					model.add(c, string);
				}
			} else throw new UnsupportedOperationException("Unsupported collection type "+c.type);
		}
	}


	private void writeChildren(Node doc, IModelContainer model) throws XPathExpressionException {
		Feature[] features = model.getChildNames();
		for (Feature c : features) {
			List<Object> children = model.getChildren(c);
			Node[] nodes = getNodes(doc, c.xpath);
			
			if (c.type == IModelObject.class) {
				for (int i = 0; i < children.size(); i++) {
					Node node;
					if (i < nodes.length) {
						node = nodes[i];
					} else {
						node = addNode(doc, c.xpath);
					}
					writeProperties(node, (IModelObject) children.get(i));
				}
			} else if (c.type == String.class) {
				for (int i = 0; i < children.size(); i++) {
					Node node;
					if (i < nodes.length) {
						node = nodes[i];
					} else {
						node = addNode(doc, c.xpath);
					}
					node.setTextContent((String)children.get(i));
				}
			} else throw new UnsupportedOperationException("Unsupported collection type "+c.type);
		}
	}
	
	
	private Node addNode(Node doc, String xpath) throws XPathExpressionException {
		if (xpath == null) {
			return doc;
		}
		
		int idx = xpath.lastIndexOf('/');
		String name;
		Node parent = null;
		if (idx > -1) {
			String parentXpath = xpath.substring(0, idx);
			name = xpath.substring(idx + 1);
		
			parent = getNode(doc, parentXpath);
			if (parent == null) {
				parent = addNode(doc, parentXpath);
			}
		} else {
			parent = doc;
			name = xpath;
		}
		
		Element e = document.createElement(name);
		parent.appendChild(e);
		
		return e;
	}

	private void loadProperties(Node doc, IModelObject obj) throws XPathExpressionException {
		Feature[] props = obj.getPropertyNames();
		for (Feature feature : props) {
			String value = getString(doc, feature.xpath, feature.attrName);
			if (value != null) {
				obj.set(feature, value);
			}
		}
		
		if (obj instanceof IModelContainer) {
			loadChildren(doc, (IModelContainer)obj);
		}
	}
	
	private void writeProperties(Node doc, IModelObject obj) throws XPathExpressionException {
		Feature[] props = obj.getPropertyNames();
		for (Feature feature : props) {
			String value = obj.get(feature);
			if (value != null) {
				setString(doc, feature.xpath, feature.attrName, value);
			}
		}
		
		if (obj instanceof IModelContainer) {
			writeChildren(doc, (IModelContainer) obj);
		}
	}
	
	private void setString(Node node, String xpath, String attrName, String value) throws XPathExpressionException {
		Node target = node;
		if (xpath != null) {
			target = getNode(node, xpath);
			if (target == null) {
				target = addNode(node, xpath);
			}
		}
		
		if (attrName != null) {
			((Element) target).setAttribute(attrName, value);
		} else {
			target.setTextContent(value);
		}
	}
	
	private String getString(Node node, String xpath, String attrName) throws XPathExpressionException {
		String s;
		if (attrName != null) {
			s = getXpathString(node, getXPath(xpath, attrName));
		} else {
			Node n = getNode(node, xpath);
			s = n == null ? null : n.getTextContent(); 
		}
		
		return stripWhitespaces(s);
	}
	
	private String getXPath(String nodePath, String attrName) {
		if (nodePath != null && attrName == null) {
			return nodePath;
		} else if (nodePath == null && attrName != null) {
			return "@"+attrName;
		} else {
			return nodePath + "@"+attrName;
		}
	}
	
	private String getXpathString(Node node, String xpath) throws XPathExpressionException {
		XPath obj = factory.newXPath();
		XPathExpression expr = obj.compile(xpath);
		String out = (String) expr.evaluate(node, XPathConstants.STRING);
		return out;
	}
	
	private Node getNode(Node node, String xpath) throws XPathExpressionException {
		XPath obj = factory.newXPath();
		XPathExpression expr = obj.compile(xpath);
		Node newnode = (Node) expr.evaluate(node, XPathConstants.NODE);
		return newnode;
	}
	
	private Node[] getNodes(Node node, String xpath) throws XPathExpressionException {
		XPath obj = factory.newXPath();
		XPathExpression expr = obj.compile(xpath);
		NodeList list = (NodeList) expr.evaluate(node, XPathConstants.NODESET);
		Node[] result = new Node[list.getLength()];
		for (int i = 0; i < list.getLength(); i++) {
			result[i] = list.item(i);
		}
		
		return result;
	}

	private String stripWhitespaces(String str) {
		if (str == null) {
			return null;
		}
		
		str = str.trim();
		
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
}
