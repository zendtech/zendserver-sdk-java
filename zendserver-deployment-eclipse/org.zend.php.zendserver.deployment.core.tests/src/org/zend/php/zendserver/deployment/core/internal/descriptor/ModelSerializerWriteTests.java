package org.zend.php.zendserver.deployment.core.internal.descriptor;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.xpath.XPathExpressionException;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.xml.sax.SAXException;
import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorFactory;
import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorPackage;
import org.zend.php.zendserver.deployment.core.descriptor.IDirectiveDependency;
import org.zend.php.zendserver.deployment.core.descriptor.IExtensionDependency;
import org.zend.php.zendserver.deployment.core.descriptor.IPHPDependency;
import org.zend.php.zendserver.deployment.core.descriptor.IParameter;
import org.zend.php.zendserver.deployment.core.descriptor.IVariable;
import org.zend.php.zendserver.deployment.core.descriptor.IZendServerDependency;


public class ModelSerializerWriteTests extends TestCase {
	
	public void testSerializeEmpty() throws SAXException, IOException, XPathExpressionException, ParserConfigurationException, CoreException, TransformerFactoryConfigurationError, TransformerException {
		ModelSerializer lm = new ModelSerializer();
		TextOutput txt = new TextOutput();
		lm.setOutput(txt);

		DeploymentDescriptor descr = new DeploymentDescriptor();
		
		lm.serialize(descr);
		lm.write();
		
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
"<package xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" packagerversion=\"1.4.11\" version=\"2.0\" xsi:schemaLocation=\"http://www.zend.com packageDescriptor.xsd\"/>\n", txt.toString());
	}

	public void testSerializeEmpty1() throws SAXException, IOException, XPathExpressionException, ParserConfigurationException, CoreException, TransformerFactoryConfigurationError, TransformerException {
		ModelSerializer lm = new ModelSerializer();
		TextOutput txt = new TextOutput();
		lm.setOutput(txt);

		DeploymentDescriptor descr = new DeploymentDescriptor();
		descr.setName("Magento");
		descr.setDocumentRoot("/usr/local/htdocs");
		descr.setSummary("This is a quick test");
		descr.setApiVersion("1.0.0");
		descr.setReleaseVersion("3.2.1");
		descr.setApplicationDir("public");
		descr.setScriptsRoot("scripts");
		descr.setDescription("My very cool first package");
		
		lm.serialize(descr);
		lm.write();
		
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
"<package xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" packagerversion=\"1.4.11\" version=\"2.0\" xsi:schemaLocation=\"http://www.zend.com packageDescriptor.xsd\">\n" +
"  <name>Magento</name>\n" +
"  <summary>This is a quick test</summary>\n" +
"  <description>My very cool first package</description>\n" +
"  <version>\n" +
"    <release>3.2.1</release>\n" +
"    <api>1.0.0</api>\n" +
"  </version>\n" +
"  <appdir>public</appdir>\n" +
"  <docroot>/usr/local/htdocs</docroot>\n" +
"  <scriptsdir>scripts</scriptsdir>\n" +
"</package>\n", txt.toString());
	}

	public void testSerializeEmpty2() throws SAXException, IOException, XPathExpressionException, ParserConfigurationException, CoreException, TransformerFactoryConfigurationError, TransformerException {
		ModelSerializer lm = new ModelSerializer();
		TextOutput txt = new TextOutput();
		lm.setOutput(txt);

		DeploymentDescriptor descr = new DeploymentDescriptor();
		IParameter param = (IParameter) DeploymentDescriptorFactory.createModelElement(DeploymentDescriptorPackage.PARAMETERS);
		descr.getParameters().add(param);
		param.setId("myNewParam1");
		param.setDisplay("What is your name");
		param.setDefaultValue("Jacek");
		param.setReadOnly(true);
		param.setType(IParameter.STRING);
		lm.serialize(descr);
		lm.write();
		
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
"<package xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" packagerversion=\"1.4.11\" version=\"2.0\" xsi:schemaLocation=\"http://www.zend.com packageDescriptor.xsd\">\n" +
"  <parameters>\n" +
"    <parameter display=\"What is your name\" id=\"myNewParam1\" readonly=\"true\" required=\"false\" type=\"string\">\n" +
"      <defaultvalue>Jacek</defaultvalue>\n" +
"    </parameter>\n" +
"  </parameters>\n" +
"</package>\n", txt.toString());
	}

	public void testSerializeEmpty3() throws SAXException, IOException, XPathExpressionException, ParserConfigurationException, CoreException, TransformerFactoryConfigurationError, TransformerException {
		ModelSerializer lm = new ModelSerializer();
		TextOutput txt = new TextOutput();
		lm.setOutput(txt);

		DeploymentDescriptor descr = new DeploymentDescriptor();
		
		IParameter param = (IParameter) DeploymentDescriptorFactory.createModelElement(DeploymentDescriptorPackage.PARAMETERS);
		descr.getParameters().add(param);
		param.setId("myNewParam1");
		param.setDisplay("What is your name");
		param.setDefaultValue("Jacek");
		param.setReadOnly(true);
		param.setType(IParameter.STRING);
		
		param = (IParameter) DeploymentDescriptorFactory.createModelElement(DeploymentDescriptorPackage.PARAMETERS);
		descr.getParameters().add(param);
		param.setId("myNewParam2");
		param.setDisplay("Host name");
		param.setDescription("A target host name");
		param.setIdentical("identical123");
		param.setDefaultValue("www.zend.com");
		param.setRequired(true);
		param.setType(IParameter.HOSTNAME);
		
		lm.serialize(descr);
		lm.write();
		
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
"<package xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" packagerversion=\"1.4.11\" version=\"2.0\" xsi:schemaLocation=\"http://www.zend.com packageDescriptor.xsd\">\n" +
"  <parameters>\n" +
"    <parameter display=\"What is your name\" id=\"myNewParam1\" readonly=\"true\" required=\"false\" type=\"string\">\n" +
"      <defaultvalue>Jacek</defaultvalue>\n" +
"    </parameter>\n" +
"    <parameter display=\"Host name\" id=\"myNewParam2\" identical=\"identical123\" readonly=\"false\" required=\"true\" type=\"hostname\">\n" +
"      <defaultvalue>www.zend.com</defaultvalue>\n" +
"      <description>A target host name</description>\n" +
"    </parameter>\n" +
"  </parameters>\n" +
"</package>\n", txt.toString());
	}
	

	public void testSerializeEmpty4() throws SAXException, IOException, XPathExpressionException, ParserConfigurationException, CoreException, TransformerFactoryConfigurationError, TransformerException {
		ModelSerializer lm = new ModelSerializer();
		TextOutput txt = new TextOutput();
		lm.setOutput(txt);

		DeploymentDescriptor descr = new DeploymentDescriptor();
		
		IParameter param = (IParameter) DeploymentDescriptorFactory.createModelElement(DeploymentDescriptorPackage.PARAMETERS);
		descr.getParameters().add(param);
		param.setId("myNewParam1");
		param.setDisplay("What is your name");
		param.setDefaultValue("Jacek");
		param.setReadOnly(true);
		param.setType(IParameter.STRING);
		
		IVariable var = (IVariable) DeploymentDescriptorFactory.createModelElement(DeploymentDescriptorPackage.VARIABLES);
		descr.getVariables().add(var);
		var.setName("variable1");
		var.setValue("ls -l");
		
		lm.serialize(descr);
		lm.write();
		
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
"<package xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" packagerversion=\"1.4.11\" version=\"2.0\" xsi:schemaLocation=\"http://www.zend.com packageDescriptor.xsd\">\n" +
"  <parameters>\n" +
"    <parameter display=\"What is your name\" id=\"myNewParam1\" readonly=\"true\" required=\"false\" type=\"string\">\n" +
"      <defaultvalue>Jacek</defaultvalue>\n" +
"    </parameter>\n" +
"  </parameters>\n" +
"  <variables>\n" +
"    <variable name=\"variable1\" value=\"ls -l\"/>\n" +
"  </variables>\n" +
"</package>\n", txt.toString());
	}

	public void testSerializeEmpty5() throws SAXException, IOException, XPathExpressionException, ParserConfigurationException, CoreException, TransformerFactoryConfigurationError, TransformerException {
		ModelSerializer lm = new ModelSerializer();
		TextOutput txt = new TextOutput();
		lm.setOutput(txt);

		DeploymentDescriptor descr = new DeploymentDescriptor();
		
		descr.getPersistentResources().add("/test1.txt");
		descr.getPersistentResources().add("file2.txt");
		
		lm.serialize(descr);
		lm.write();
		
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
"<package xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" packagerversion=\"1.4.11\" version=\"2.0\" xsi:schemaLocation=\"http://www.zend.com packageDescriptor.xsd\">\n" +
"  <persistentresources>\n" +
"    <resource>/test1.txt</resource>\n" +
"    <resource>file2.txt</resource>\n" +
"  </persistentresources>\n" +
"</package>\n", txt.toString());
	}
	
	public void testSerializeEmpty6() throws SAXException, IOException, XPathExpressionException, ParserConfigurationException, CoreException, TransformerFactoryConfigurationError, TransformerException {
		ModelSerializer lm = new ModelSerializer();
		TextOutput txt = new TextOutput();
		lm.setOutput(txt);

		DeploymentDescriptor descr = new DeploymentDescriptor();
		
		IPHPDependency dep = (IPHPDependency) DeploymentDescriptorFactory.createModelElement(DeploymentDescriptorPackage.DEPENDENCIES_PHP);
		descr.getPHPDependencies().add(dep);
		dep.setEquals("3.2.1");
		
		lm.serialize(descr);
		lm.write();
		
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
"<package xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" packagerversion=\"1.4.11\" version=\"2.0\" xsi:schemaLocation=\"http://www.zend.com packageDescriptor.xsd\">\n" +
"  <dependencies>\n" +
"    <required>\n" +
"      <php>\n" +
"        <equals>3.2.1</equals>\n" +
"      </php>\n" +
"    </required>\n" +
"  </dependencies>\n" +
"</package>\n", txt.toString());
	}
	
	public void testSerializeEmpty7() throws SAXException, IOException, XPathExpressionException, ParserConfigurationException, CoreException, TransformerFactoryConfigurationError, TransformerException {
		ModelSerializer lm = new ModelSerializer();
		TextOutput txt = new TextOutput();
		lm.setOutput(txt);

		DeploymentDescriptor descr = new DeploymentDescriptor();
		
		IPHPDependency dep = (IPHPDependency) DeploymentDescriptorFactory.createModelElement(DeploymentDescriptorPackage.DEPENDENCIES_PHP);
		descr.getPHPDependencies().add(dep);
		dep.setMin("3.2.1");
		dep.setMax("5.2.5");
		dep.getExclude().add("1.2.3");
		dep.getExclude().add("2.3.4");
		dep.getExclude().add("4.1.2");
		
		lm.serialize(descr);
		lm.write();
		
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
"<package xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" packagerversion=\"1.4.11\" version=\"2.0\" xsi:schemaLocation=\"http://www.zend.com packageDescriptor.xsd\">\n" +
"  <dependencies>\n" +
"    <required>\n" +
"      <php>\n" +
"        <min>3.2.1</min>\n" +
"        <max>5.2.5</max>\n" +
"        <exclude>1.2.3</exclude>\n" +
"        <exclude>2.3.4</exclude>\n" +
"        <exclude>4.1.2</exclude>\n" +
"      </php>\n" +
"    </required>\n" +
"  </dependencies>\n" +
"</package>\n", txt.toString());
	}

	public void testSerializeEmpty8() throws SAXException, IOException, XPathExpressionException, ParserConfigurationException, CoreException, TransformerFactoryConfigurationError, TransformerException {
		ModelSerializer lm = new ModelSerializer();
		TextOutput txt = new TextOutput();
		lm.setOutput(txt);

		DeploymentDescriptor descr = new DeploymentDescriptor();
		
		IPHPDependency dep = (IPHPDependency) DeploymentDescriptorFactory.createModelElement(DeploymentDescriptorPackage.DEPENDENCIES_PHP);
		descr.getPHPDependencies().add(dep);
		dep.setMin("3.2.1");
		dep.setMax("5.2.5");
		dep.getExclude().add("1.2.3");
		dep.getExclude().add("2.3.4");
		dep.getExclude().add("4.1.2");
		
		IDirectiveDependency dir = (IDirectiveDependency) DeploymentDescriptorFactory.createModelElement(DeploymentDescriptorPackage.DEPENDENCIES_DIRECTIVE);
		descr.getDirectiveDependencies().add(dir);
		dir.setName("register_globals");
		dir.setEquals("off");
		
		lm.serialize(descr);
		lm.write();
		
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
"<package xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" packagerversion=\"1.4.11\" version=\"2.0\" xsi:schemaLocation=\"http://www.zend.com packageDescriptor.xsd\">\n" +
"  <dependencies>\n" +
"    <required>\n" +
"      <php>\n" +
"        <min>3.2.1</min>\n" +
"        <max>5.2.5</max>\n" +
"        <exclude>1.2.3</exclude>\n" +
"        <exclude>2.3.4</exclude>\n" +
"        <exclude>4.1.2</exclude>\n" +
"      </php>\n" +
"      <directive>\n" +
"        <name>register_globals</name>\n" +
"        <equals>off</equals>\n" +
"      </directive>\n" +
"    </required>\n" +
"  </dependencies>\n" +
"</package>\n", txt.toString());
	}
	
	public void testSerializeRemoveEmptyParents() throws SAXException, IOException, XPathExpressionException, ParserConfigurationException, CoreException, TransformerFactoryConfigurationError, TransformerException {
		ModelSerializer lm = new ModelSerializer();
		TextOutput txt = new TextOutput();
		lm.setOutput(txt);

		DeploymentDescriptor descr = new DeploymentDescriptor();
		
		IPHPDependency dep = (IPHPDependency) DeploymentDescriptorFactory.createModelElement(DeploymentDescriptorPackage.DEPENDENCIES_PHP);
		descr.getPHPDependencies().add(dep);
		dep.setMin("3.2.1");
		
		lm.serialize(descr);
		
		descr.getPHPDependencies().remove(dep);
		
		lm.serialize(descr);
		lm.write();
		
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
"<package xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" packagerversion=\"1.4.11\" version=\"2.0\" xsi:schemaLocation=\"http://www.zend.com packageDescriptor.xsd\"/>\n", txt.toString());
	}
	
	public void testSerializeRemoveEmptyParents1() throws SAXException, IOException, XPathExpressionException, ParserConfigurationException, CoreException, TransformerFactoryConfigurationError, TransformerException {
		ModelSerializer lm = new ModelSerializer();
		TextOutput txt = new TextOutput();
		lm.setOutput(txt);

		DeploymentDescriptor descr = new DeploymentDescriptor();
		
		IPHPDependency dep = (IPHPDependency) DeploymentDescriptorFactory.createModelElement(DeploymentDescriptorPackage.DEPENDENCIES_PHP);
		descr.getPHPDependencies().add(dep);
		dep.setMin("3.2.1");
		
		IVariable var = (IVariable) DeploymentDescriptorFactory.createModelElement(DeploymentDescriptorPackage.VARIABLES);
		descr.getVariables().add(var);
		
		descr.setName("notemptyname");
		
		lm.serialize(descr);
		
		descr.setName(null);
		descr.getVariables().clear();
		descr.getPHPDependencies().clear();
		
		lm.serialize(descr);
		lm.write();
		
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
"<package xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" packagerversion=\"1.4.11\" version=\"2.0\" xsi:schemaLocation=\"http://www.zend.com packageDescriptor.xsd\"/>\n", txt.toString());
	}
	
	public void testSerializeRemoveEmptyParents2() throws SAXException, IOException, XPathExpressionException, ParserConfigurationException, CoreException, TransformerFactoryConfigurationError, TransformerException {
		ModelSerializer lm = new ModelSerializer();
		TextOutput txt = new TextOutput();
		lm.setOutput(txt);

		DeploymentDescriptor descr = new DeploymentDescriptor();
		
		IPHPDependency dep = (IPHPDependency) DeploymentDescriptorFactory.createModelElement(DeploymentDescriptorPackage.DEPENDENCIES_PHP);
		descr.getPHPDependencies().add(dep);
		dep.setMin("3.2.1");
		
		dep = (IPHPDependency) DeploymentDescriptorFactory.createModelElement(DeploymentDescriptorPackage.DEPENDENCIES_PHP);
		descr.getPHPDependencies().add(dep);
		dep.setMin("3.2.1");
		
		lm.serialize(descr);
		
		descr.getPHPDependencies().remove(dep);
		
		lm.serialize(descr);
		lm.write();
		
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
"<package xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" packagerversion=\"1.4.11\" version=\"2.0\" xsi:schemaLocation=\"http://www.zend.com packageDescriptor.xsd\">\n"+
"  <dependencies>\n" +
"    <required>\n" +
"      <php>\n" +
"        <min>3.2.1</min>\n" +
"      </php>\n" +
"    </required>\n" +
"  </dependencies>\n" +
"</package>\n", txt.toString());
	}

	public void testSerializeRemoveEmptyParents3() throws SAXException, IOException, XPathExpressionException, ParserConfigurationException, CoreException, TransformerFactoryConfigurationError, TransformerException {
		ModelSerializer lm = new ModelSerializer();
		TextOutput txt = new TextOutput();
		lm.setOutput(txt);

		DeploymentDescriptor descr = new DeploymentDescriptor();
		descr.setDocumentRoot("newRoot");
		
		lm.serialize(descr);
		lm.write();
		lm.load(txt.getInputStream(), txt.getInputStream(), descr);
		
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
"<package xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" packagerversion=\"1.4.11\" version=\"2.0\" xsi:schemaLocation=\"http://www.zend.com packageDescriptor.xsd\">\n" +
"  <docroot>newRoot</docroot>\n" +
"</package>\n", txt.toString());
		
		descr.setDocumentRoot(null);
		
		lm.serialize(descr);
		lm.write();
		
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
"<package xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" packagerversion=\"1.4.11\" version=\"2.0\" xsi:schemaLocation=\"http://www.zend.com packageDescriptor.xsd\">\n" +
"  </package>\n", txt.toString());
	}
	
	public void testAddInOrder() throws SAXException, IOException, XPathExpressionException, ParserConfigurationException, CoreException, TransformerFactoryConfigurationError, TransformerException {
		ModelSerializer lm = new ModelSerializer();
		TextOutput txt = new TextOutput();
		lm.setOutput(txt);

		DeploymentDescriptor descr = new DeploymentDescriptor();
		
		IVariable var = (IVariable) DeploymentDescriptorFactory.createModelElement(DeploymentDescriptorPackage.VARIABLES);
		descr.getVariables().add(var);
		
		lm.serialize(descr);
		
		IParameter param = (IParameter) DeploymentDescriptorFactory.createModelElement(DeploymentDescriptorPackage.PARAMETERS);
		descr.getParameters().add(param);
		
		lm.serialize(descr);
		lm.write();
		
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
"<package xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" packagerversion=\"1.4.11\" version=\"2.0\" xsi:schemaLocation=\"http://www.zend.com packageDescriptor.xsd\">\n"+
"  <parameters>\n" +
"    <parameter readonly=\"false\" required=\"false\"/>\n" +
"  </parameters>\n" +
"  <variables>\n" +
"    <variable/>\n" +
"  </variables>\n" +
"</package>\n", txt.toString());
	}
	
	public void testAddInOrder2() throws SAXException, IOException, XPathExpressionException, ParserConfigurationException, CoreException, TransformerFactoryConfigurationError, TransformerException {
		ModelSerializer lm = new ModelSerializer();
		TextOutput txt = new TextOutput();
		lm.setOutput(txt);

		DeploymentDescriptor descr = new DeploymentDescriptor();
		
		descr.setDocumentRoot("homeDir");
		
		lm.serialize(descr);
		lm.write();
		lm.load(txt.getInputStream(), txt.getInputStream(), descr);
		
		IParameter p = (IParameter) DeploymentDescriptorFactory.createModelElement(DeploymentDescriptorPackage.PARAMETERS);
		descr.getParameters().add(p);
		
		lm.serialize(descr);
		lm.write();
		lm.load(txt.getInputStream(), txt.getInputStream(), descr);
		
		descr.setDocumentRoot("homeDir");
		
		lm.serialize(descr);
		lm.write();
		lm.load(txt.getInputStream(), txt.getInputStream(), descr);
		
		
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
"<package xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" packagerversion=\"1.4.11\" version=\"2.0\" xsi:schemaLocation=\"http://www.zend.com packageDescriptor.xsd\">\n"+
"  <docroot>homeDir</docroot>\n" +
"  <parameters>\n" +
"    <parameter readonly=\"false\" required=\"false\"/>\n" +
"  </parameters>\n" +
"</package>\n", txt.toString());
	}
	
	public void testAddDependenciesInOrder() throws SAXException, IOException, XPathExpressionException, ParserConfigurationException, CoreException, TransformerFactoryConfigurationError, TransformerException {
		ModelSerializer lm = new ModelSerializer();
		TextOutput txt = new TextOutput();
		lm.setOutput(txt);

		DeploymentDescriptor descr = new DeploymentDescriptor();
		
		lm.serialize(descr);
		lm.write();
		lm.load(txt.getInputStream(), txt.getInputStream(), descr);
		
		IDirectiveDependency dirdep = (IDirectiveDependency) DeploymentDescriptorFactory.createModelElement(DeploymentDescriptorPackage.DEPENDENCIES_DIRECTIVE);
		dirdep.setName("log_errors");
		descr.getDirectiveDependencies().add(dirdep);
		
		lm.serialize(descr);
		lm.write();
		lm.load(txt.getInputStream(), txt.getInputStream(), descr);
		
		IExtensionDependency extdep = (IExtensionDependency) DeploymentDescriptorFactory.createModelElement(DeploymentDescriptorPackage.DEPENDENCIES_EXTENSION);
		extdep.setName("ctype");
		descr.getExtensionDependencies().add(extdep);
		
		lm.serialize(descr);
		lm.write();
		lm.load(txt.getInputStream(), txt.getInputStream(), descr);
		
		IPHPDependency phpdep = (IPHPDependency) DeploymentDescriptorFactory.createModelElement(DeploymentDescriptorPackage.DEPENDENCIES_PHP);
		phpdep.setEquals("1.2.3");
		descr.getPHPDependencies().add(phpdep);
		
		lm.serialize(descr);
		lm.write();
		lm.load(txt.getInputStream(), txt.getInputStream(), descr);
		
		IZendServerDependency zsdep = (IZendServerDependency) DeploymentDescriptorFactory.createModelElement(DeploymentDescriptorPackage.DEPENDENCIES_ZENDSERVER);
		zsdep.setEquals("1.2.3");
		descr.getZendServerDependencies().add(zsdep);
		
		lm.serialize(descr);
		lm.write();
		
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
"<package xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" packagerversion=\"1.4.11\" version=\"2.0\" xsi:schemaLocation=\"http://www.zend.com packageDescriptor.xsd\">\n"+
"  <dependencies>\n" +
"    <required>\n" +
"      <php>\n" +
"        <equals>1.2.3</equals>\n" +
"      </php>\n" +
"      <extension>\n" +
"        <name>ctype</name>\n" +
"      </extension>\n" +
"      <directive>\n" +
"        <name>log_errors</name>\n" +
"      </directive>\n" +
"      <zendserver>\n" +
"        <equals>1.2.3</equals>\n" +
"      </zendserver>\n" +
"    </required>\n" +
"  </dependencies>\n" +
"</package>\n", txt.toString());
	}

	
	public void testParamChoices() throws SAXException, IOException, XPathExpressionException, ParserConfigurationException, CoreException, TransformerFactoryConfigurationError, TransformerException {
		ModelSerializer lm = new ModelSerializer();
		TextOutput txt = new TextOutput();
		lm.setOutput(txt);

		DeploymentDescriptor descr = new DeploymentDescriptor();
		
		lm.serialize(descr);
		lm.write();
		lm.load(txt.getInputStream(), txt.getInputStream(), descr);
		
		IParameter param = (IParameter) DeploymentDescriptorFactory.createModelElement(DeploymentDescriptorPackage.PARAMETERS);
		descr.getParameters().add(param);
		
		param.getValidValues().add("value1");
		param.getValidValues().add("value2");
		param.getValidValues().add("value3");
		
		lm.serialize(descr);
		lm.write();
		
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
"<package xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" packagerversion=\"1.4.11\" version=\"2.0\" xsi:schemaLocation=\"http://www.zend.com packageDescriptor.xsd\">\n"+
"  <parameters>\n" +
"    <parameter readonly=\"false\" required=\"false\">\n" +
"      <validation>\n" +
"        <enums>\n" +
"          <enum>value1</enum>\n" +
"          <enum>value2</enum>\n" +
"          <enum>value3</enum>\n" +
"        </enums>\n" +
"      </validation>\n" +
"    </parameter>\n" +
"  </parameters>\n" +
"</package>\n", txt.toString());
	}
	
}
