package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.zend.php.zendserver.deployment.core.descriptor.IDirectiveDependency;
import org.zend.php.zendserver.deployment.core.descriptor.IExtensionDependency;
import org.zend.php.zendserver.deployment.core.descriptor.IPHPDependency;
import org.zend.php.zendserver.deployment.core.descriptor.IPHPLibraryDependency;
import org.zend.php.zendserver.deployment.core.descriptor.IParameter;
import org.zend.php.zendserver.deployment.core.descriptor.IVariable;
import org.zend.php.zendserver.deployment.core.descriptor.IZendComponentDependency;
import org.zend.php.zendserver.deployment.core.descriptor.IZendFramework2Dependency;
import org.zend.php.zendserver.deployment.core.descriptor.IZendFrameworkDependency;
import org.zend.php.zendserver.deployment.core.descriptor.IZendServerDependency;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.Messages;

public class DeploymentDescriptorLabelProvider extends LabelProvider {

	public Image getParameterImage(Object element) {
		IParameter param = (IParameter) element;

		String type = param.getType();

		if (IParameter.PASSWORD.equals(type)) {
			return Activator.getDefault().getImage(
					Activator.IMAGE_PARAMTYPE_PASSWORD);
		} else if (IParameter.STRING.equals(type)) {
			return Activator.getDefault().getImage(
					Activator.IMAGE_PARAMTYPE_STRING);
		} else if (IParameter.NUMBER.equals(type)) {
			return Activator.getDefault().getImage(
					Activator.IMAGE_PARAMTYPE_NUMBER);
		} else if (IParameter.CHOICE.equals(type)) {
			return Activator.getDefault().getImage(
					Activator.IMAGE_PARAMTYPE_CHOICE);
		} else if (IParameter.CHECKBOX.equals(type)) {
			return Activator.getDefault().getImage(
					Activator.IMAGE_PARAMTYPE_CHECKBOX);
		} else if (IParameter.HOSTNAME.equals(type)) {
			return Activator.getDefault().getImage(
					Activator.IMAGE_PARAMTYPE_HOSTNAME);
		} else if (IParameter.EMAIL.equals(type)) {
			return Activator.getDefault().getImage(
					Activator.IMAGE_PARAMTYPE_EMAIL);
		}

		return Activator.getDefault().getImage(
				Activator.IMAGE_PARAMTYPE_UNKNOWN);
	}

	public String getParameterText(Object element) {
		IParameter param = (IParameter) element;

		StringBuilder sb = new StringBuilder();

		String label = param.getDisplay();
		if (label == null || label.trim().equals("")) { //$NON-NLS-1$
			sb.append(param.getId());
		} else {
			sb.append(label);
		}

		if (param.isRequired()) {
			sb.append(Messages.DeploymentDescriptorLabelProvider_Required);
		}

		String defaultVal = param.getDefaultValue();
		if (defaultVal != null && !defaultVal.trim().equals("")) { //$NON-NLS-1$
			sb.append(Messages.DeploymentDescriptorLabelProvider_Equals
					+ defaultVal);
		}

		String type = param.getType();
		if (type != null) {
			sb.append(Messages.DeploymentDescriptorLabelProvider_From)
					.append(type)
					.append(Messages.DeploymentDescriptorLabelProvider_To);
		}

		return sb.toString();
	}

	public Image getDependencyImage(Object element) {
		if (element instanceof IPHPDependency) {
			return Activator.getDefault().getImage(Activator.IMAGE_PHP);

		} else if (element instanceof IExtensionDependency) {
			return Activator.getDefault().getImage(
					Activator.IMAGE_PHP_EXTENSION);

		} else if (element instanceof IDirectiveDependency) {
			return Activator.getDefault().getImage(
					Activator.IMAGE_PHP_DIRECTIVE);

		} else if (element instanceof IZendServerDependency) {
			return Activator.getDefault().getImage(Activator.IMAGE_ZENDSERVER);

		} else if (element instanceof IZendFrameworkDependency) {
			return Activator.getDefault().getImage(
					Activator.IMAGE_ZENDFRAMEWORK);

		} else if (element instanceof IZendComponentDependency) {
			return Activator.getDefault().getImage(
					Activator.IMAGE_ZENDSERVERCOMPONENT);
		} else if (element instanceof IPHPLibraryDependency) {
			return Activator.getDefault().getImage(Activator.IMAGE_LIBRARY);
		}

		return super.getImage(element);
	}

	public String getDependencyText(Object element) {
		if (element instanceof IPHPDependency) {
			IPHPDependency dep = (IPHPDependency) element;
			if (dep.getEquals() == null && dep.getMax() == null
					&& dep.getMin() == null) {
				return Messages.DeploymentDescriptorLabelProvider_PHPVersion;
			}
			return Messages.DeploymentDescriptorLabelProvider_PHP
					+ format(dep.getEquals(), dep.getMin(), dep.getMax(), null);

		} else if (element instanceof IExtensionDependency) {
			IExtensionDependency dep = (IExtensionDependency) element;
			if (dep.getName() == null) {
				return Messages.DeploymentDescriptorLabelProvider_PHPExtension;
			}
			return dep.getName()
					+ format(dep.getEquals(), dep.getMin(), dep.getMax(),
							dep.getConflicts());

		} else if (element instanceof IDirectiveDependency) {
			IDirectiveDependency dep = (IDirectiveDependency) element;
			if (dep.getName() == null) {
				return Messages.DeploymentDescriptorLabelProvider_PHPDirective;
			}
			return dep.getName()
					+ format(dep.getEquals(), dep.getMin(), dep.getMax(), null);

		} else if (element instanceof IZendServerDependency) {
			IZendServerDependency dep = (IZendServerDependency) element;
			if (dep.getEquals() == null && dep.getMax() == null
					&& dep.getMin() == null) {
				return Messages.DeploymentDescriptorLabelProvider_ZendServerVersion;
			}
			return Messages.DeploymentDescriptorLabelProvider_ZendServer
					+ format(dep.getEquals(), dep.getMin(), dep.getMax(), null);

		} else if (element instanceof IZendFramework2Dependency) {
			IZendFramework2Dependency dep = (IZendFramework2Dependency) element;
			if (dep.getEquals() == null && dep.getMax() == null
					&& dep.getMin() == null) {
				return Messages.DeploymentDescriptorLabelProvider_ZendFramework2Version;
			}
			return Messages.DeploymentDescriptorLabelProvider_ZendFramework2
					+ format(dep.getEquals(), dep.getMin(), dep.getMax(), null);

		} else if (element instanceof IZendFrameworkDependency) {
			IZendFrameworkDependency dep = (IZendFrameworkDependency) element;
			if (dep.getEquals() == null && dep.getMax() == null
					&& dep.getMin() == null) {
				return Messages.DeploymentDescriptorLabelProvider_ZendFrameworkVersion;
			}
			return Messages.DeploymentDescriptorLabelProvider_ZendFramework
					+ format(dep.getEquals(), dep.getMin(), dep.getMax(), null);

		} else if (element instanceof IZendComponentDependency) {
			IZendComponentDependency dep = (IZendComponentDependency) element;
			if (dep.getName() == null) {
				return Messages.DeploymentDescriptorLabelProvider_ZendServerComponent;
			}
			return dep.getName()
					+ format(dep.getEquals(), dep.getMin(), dep.getMax(),
							dep.getConflicts());
		} else if (element instanceof IPHPLibraryDependency) {
			IPHPLibraryDependency dep = (IPHPLibraryDependency) element;
			if (dep.getName() == null) {
				return Messages.DeploymentDescriptorLabelProvider_PHPLibrary;
			}
			return dep.getName()
					+ format(dep.getEquals(), dep.getMin(), dep.getMax(), null);
		}

		return super.getText(element);
	}

	private String format(String equals, String min, String max,
			String conflicts) {
		if (conflicts != null) {
			return Messages.DeploymentDescriptorLabelProvider_conflictsWith
					+ conflicts;
		}

		if (equals != null) {
			return Messages.DeploymentDescriptorLabelProvider_Equals + equals;
		}

		if (min != null && max != null) {
			return Messages.bind(
					Messages.DeploymentDescriptorLabelProvider_range, min, max);
		}

		if (min != null) {
			return Messages.DeploymentDescriptorLabelProvider_greaterThan + min;
		}

		if (max != null) {
			return Messages.DeploymentDescriptorLabelProvider_lessThan + max;
		}

		return ""; //$NON-NLS-1$
	}

	public Image getVariableImage(Object element) {
		return Activator.getDefault().getImage(Activator.IMAGE_VARIABLE);
	}

	public String getVariableText(Object element) {
		IVariable param = (IVariable) element;

		return param.getName();
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof IParameter) {
			return getParameterImage(element);
		}

		if (element instanceof IVariable) {
			return getVariableImage(element);
		}

		return getDependencyImage(element);
	}

	@Override
	public String getText(Object element) {
		if (element instanceof IParameter) {
			return getParameterText(element);
		}

		if (element instanceof IVariable) {
			return getVariableText(element);
		}

		return getDependencyText(element);
	}
}