package org.zend.php.zendserver.deployment.core.descriptor;

import javax.xml.parsers.DocumentBuilder;

import org.eclipse.core.resources.IProject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.zend.php.zendserver.deployment.core.internal.descriptor.DeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.internal.descriptor.DirectiveDependency;
import org.zend.php.zendserver.deployment.core.internal.descriptor.ExtensionDependency;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;
import org.zend.php.zendserver.deployment.core.internal.descriptor.PHPDependency;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Parameter;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Variable;
import org.zend.php.zendserver.deployment.core.internal.descriptor.ZendComponentDependency;
import org.zend.php.zendserver.deployment.core.internal.descriptor.ZendFrameworkDependency;
import org.zend.php.zendserver.deployment.core.internal.descriptor.ZendServerDependency;


public class DeploymentDescriptorFactory {

	public static DeploymentDescriptor create(IProject project) {
		return new DeploymentDescriptor();
	}

	public static IModelObject createModelElement(Feature path) {
		if (IDeploymentDescriptor.PACKAGE.equals(path)) {
			return new DeploymentDescriptor();
		} else if (IDeploymentDescriptor.VARIABLES.equals(path)) {
			return new Variable();
		} else if (IDeploymentDescriptor.DEPENDENCIES_DIRECTIVE.equals(path)) {
			return new DirectiveDependency();
		} else if (IDeploymentDescriptor.DEPENDENCIES_EXTENSION.equals(path)) {
			return new ExtensionDependency();
		} else if (IDeploymentDescriptor.DEPENDENCIES_PHP.equals(path)) {
			return new PHPDependency();
		} else if (IDeploymentDescriptor.DEPENDENCIES_ZENDFRAMEWORK.equals(path)) {
			return new ZendFrameworkDependency();
		} else if (IDeploymentDescriptor.DEPENDENCIES_ZENDSERVER.equals(path)) {
			return new ZendServerDependency();
		} else if (IDeploymentDescriptor.DEPENDENCIES_ZSCOMPONENT.equals(path)) {
			return new ZendComponentDependency();
		} else if (IDeploymentDescriptor.PARAMETERS.equals(path)) {
			return new Parameter();
		}
		
		throw new IllegalArgumentException("Unknown model element "+path);
	}
	

	public static Feature getFeature(Object result) {
		if (result instanceof IDeploymentDescriptor) {
			return IDeploymentDescriptor.PACKAGE;
		} else if (result instanceof IVariable) {
			return IDeploymentDescriptor.VARIABLES;
		} else if (result instanceof IDirectiveDependency) {
			return IDeploymentDescriptor.DEPENDENCIES_DIRECTIVE;
		} else if (result instanceof IExtensionDependency) {
			return IDeploymentDescriptor.DEPENDENCIES_EXTENSION;
		} else if (result instanceof IPHPDependency) {
			return IDeploymentDescriptor.DEPENDENCIES_PHP;
		} else if (result instanceof IZendFrameworkDependency) {
			return IDeploymentDescriptor.DEPENDENCIES_ZENDFRAMEWORK;
		} else if (result instanceof IZendServerDependency) {
			return IDeploymentDescriptor.DEPENDENCIES_ZENDSERVER;
		} else if (result instanceof IZendComponentDependency) {
			return IDeploymentDescriptor.DEPENDENCIES_ZSCOMPONENT;
		} else if (result instanceof IParameter) {
			return IDeploymentDescriptor.PARAMETERS;
		}
		
		throw new IllegalArgumentException("Unknown model object "+result);
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
}
