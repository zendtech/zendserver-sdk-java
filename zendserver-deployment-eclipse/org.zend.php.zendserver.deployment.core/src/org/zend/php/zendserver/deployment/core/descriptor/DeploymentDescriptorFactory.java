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
import org.zend.php.zendserver.deployment.core.internal.descriptor.PHPLibraryDependency;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Parameter;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Variable;
import org.zend.php.zendserver.deployment.core.internal.descriptor.ZendComponentDependency;
import org.zend.php.zendserver.deployment.core.internal.descriptor.ZendFramework2Dependency;
import org.zend.php.zendserver.deployment.core.internal.descriptor.ZendFrameworkDependency;
import org.zend.php.zendserver.deployment.core.internal.descriptor.ZendServerDependency;


public class DeploymentDescriptorFactory {

	public static DeploymentDescriptor create(IProject project) {
		return new DeploymentDescriptor();
	}

	public static IModelObject createModelElement(Feature path) {
		switch (path.id) {
			case DeploymentDescriptorPackage.PACKAGE_ID:
				return new DeploymentDescriptor();
			case DeploymentDescriptorPackage.VARIABLES_ID:
				return new Variable();
			case DeploymentDescriptorPackage.DEPENDENCIES_DIRECTIVE_ID:
				return new DirectiveDependency();
			case DeploymentDescriptorPackage.DEPENDENCIES_EXTENSION_ID:
				return new ExtensionDependency();
			case DeploymentDescriptorPackage.DEPENDENCIES_PHP_ID:
				return new PHPDependency();
			case DeploymentDescriptorPackage.DEPENDENCIES_ZENDFRAMEWORK2_ID:
				return new ZendFramework2Dependency();
			case DeploymentDescriptorPackage.DEPENDENCIES_ZENDFRAMEWORK_ID:
				return new ZendFrameworkDependency();
			case DeploymentDescriptorPackage.DEPENDENCIES_ZENDSERVER_ID:
				return new ZendServerDependency();
			case DeploymentDescriptorPackage.DEPENDENCIES_ZSCOMPONENT_ID:
				return new ZendComponentDependency();
			case DeploymentDescriptorPackage.DEPENDENCIES_LIBRARY_ID:
				return new PHPLibraryDependency();
			case DeploymentDescriptorPackage.PARAMETERS_ID:
				return new Parameter();
		}
		
		throw new IllegalArgumentException("Unknown model element "+path); //$NON-NLS-1$
	}
	

	public static Feature getFeature(Object result) {
		if (result instanceof IDeploymentDescriptor) {
			return DeploymentDescriptorPackage.PACKAGE;
		} else if (result instanceof IVariable) {
			return DeploymentDescriptorPackage.VARIABLES;
		} else if (result instanceof IDirectiveDependency) {
			return DeploymentDescriptorPackage.DEPENDENCIES_DIRECTIVE;
		} else if (result instanceof IExtensionDependency) {
			return DeploymentDescriptorPackage.DEPENDENCIES_EXTENSION;
		} else if (result instanceof IPHPDependency) {
			return DeploymentDescriptorPackage.DEPENDENCIES_PHP;
		} else if (result instanceof IZendFramework2Dependency) {
			return DeploymentDescriptorPackage.DEPENDENCIES_ZENDFRAMEWORK2;
		} else if (result instanceof IZendFrameworkDependency) {
			return DeploymentDescriptorPackage.DEPENDENCIES_ZENDFRAMEWORK;
		} else if (result instanceof IZendServerDependency) {
			return DeploymentDescriptorPackage.DEPENDENCIES_ZENDSERVER;
		} else if (result instanceof IZendComponentDependency) {
			return DeploymentDescriptorPackage.DEPENDENCIES_ZSCOMPONENT;
		} else if (result instanceof IParameter) {
			return DeploymentDescriptorPackage.PARAMETERS;
		} else if (result instanceof IPHPLibraryDependency) {
			return DeploymentDescriptorPackage.DEPENDENCIES_LIBRARY;
		}
		
		throw new IllegalArgumentException("Unknown model object "+result); //$NON-NLS-1$
	}
	
	public static Document createEmptyDocument(DocumentBuilder builder) {
		Document document = builder.newDocument();
		Element rootElement = document.createElement(DeploymentDescriptorPackage.PACKAGE.xpath);
		rootElement.setAttribute("packagerversion", "1.4.11"); //$NON-NLS-1$ //$NON-NLS-2$
		rootElement.setAttribute("version", "2.0"); //$NON-NLS-1$ //$NON-NLS-2$
		rootElement.setAttribute("xmlns:xsi", //$NON-NLS-1$
				"http://www.w3.org/2001/XMLSchema-instance"); //$NON-NLS-1$
		rootElement.setAttribute("xsi:schemaLocation", //$NON-NLS-1$
				"http://www.zend.com packageDescriptor.xsd"); //$NON-NLS-1$
		document.appendChild(rootElement);
		
		return document;
	}	
}
