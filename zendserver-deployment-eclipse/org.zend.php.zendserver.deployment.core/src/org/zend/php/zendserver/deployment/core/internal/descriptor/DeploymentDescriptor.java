package org.zend.php.zendserver.deployment.core.internal.descriptor;

import java.text.MessageFormat;
import java.util.List;

import org.zend.php.zendserver.deployment.core.Messages;
import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorPackage;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
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
import org.zend.php.zendserver.deployment.core.descriptor.ProjectType;
import org.zend.php.zendserver.deployment.core.utils.ObjectUtils;

public class DeploymentDescriptor extends ModelContainer implements
		IDeploymentDescriptor {

	private String name;
	private String type;
	private String summary;
	private String description;
	private String releaseVersion;
	private String apiVersion;
	private String eulaLocation;
	private String iconLocation;
	private String documentRoot;
	private String appDir;
	private String scriptsRoot;
	private String healthcheck;
	private String libDir;
	private String updateUrl;

	public DeploymentDescriptor() {
		super(new Feature[] { DeploymentDescriptorPackage.PKG_TYPE,
				DeploymentDescriptorPackage.PKG_NAME,
				DeploymentDescriptorPackage.SUMMARY,
				DeploymentDescriptorPackage.PKG_DESCRIPTION,
				DeploymentDescriptorPackage.VERSION_RELEASE,
				DeploymentDescriptorPackage.VERSION_API,
				DeploymentDescriptorPackage.ICON,
				DeploymentDescriptorPackage.EULA,
				DeploymentDescriptorPackage.LIBDIR,
				DeploymentDescriptorPackage.APPDIR,
				DeploymentDescriptorPackage.DOCROOT,
				DeploymentDescriptorPackage.SCRIPTSDIR,
				DeploymentDescriptorPackage.HEALTHCHECK,
				DeploymentDescriptorPackage.UPDATE_URL }, new Feature[] {
				DeploymentDescriptorPackage.DEPENDENCIES_PHP,
				DeploymentDescriptorPackage.DEPENDENCIES_EXTENSION,
				DeploymentDescriptorPackage.DEPENDENCIES_DIRECTIVE,
				DeploymentDescriptorPackage.DEPENDENCIES_ZENDSERVER,
				DeploymentDescriptorPackage.DEPENDENCIES_ZSCOMPONENT,
				DeploymentDescriptorPackage.DEPENDENCIES_ZENDFRAMEWORK,
				DeploymentDescriptorPackage.DEPENDENCIES_ZENDFRAMEWORK2,
				DeploymentDescriptorPackage.DEPENDENCIES_LIBRARY,
				DeploymentDescriptorPackage.PARAMETERS,
				DeploymentDescriptorPackage.VARIABLES,
				DeploymentDescriptorPackage.PERSISTENT_RESOURCES });
	}
	
	public ProjectType getType() {
		return ProjectType.byName(type);
	}
	
	public void setType(String type) {
		String oldType = this.type;
		
		// type "application" is always represented as null in the model
		String newType = (ProjectType.APPLICATION.getName().equals(type)) ? null : type;
		
		// fire the change event only if the type has changed
		if (!ObjectUtils.equals(oldType, newType)) {
			this.type = newType;
			fireChange(DeploymentDescriptorPackage.PKG_TYPE, type, oldType);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		String oldName = this.name;
		this.name = name;
		fireChange(DeploymentDescriptorPackage.PKG_NAME, name, oldName);
	}

	public String getReleaseVersion() {
		return releaseVersion;
	}

	public void setReleaseVersion(String version) {
		String oldValue = this.releaseVersion;
		this.releaseVersion = version;
		fireChange(DeploymentDescriptorPackage.VERSION_RELEASE, version,
				oldValue);
	}

	public String getEulaLocation() {
		return eulaLocation;
	}

	public void setEulaLocation(String eulaLocation) {
		String oldEula = this.eulaLocation;
		this.eulaLocation = eulaLocation;
		fireChange(DeploymentDescriptorPackage.EULA, eulaLocation, oldEula);
	}

	public String getIconLocation() {
		return iconLocation;
	}

	public void setIconLocation(String iconLocation) {
		String oldIcon = this.iconLocation;
		this.iconLocation = iconLocation;
		fireChange(DeploymentDescriptorPackage.ICON, iconLocation, oldIcon);
	}

	public String getDocumentRoot() {
		return documentRoot;
	}

	public void setDocumentRoot(String documentRoot) {
		String oldValue = this.documentRoot;
		this.documentRoot = documentRoot;
		fireChange(DeploymentDescriptorPackage.DOCROOT, documentRoot, oldValue);
	}

	public String getScriptsRoot() {
		return scriptsRoot;
	}

	public void setScriptsRoot(String scriptsRoot) {
		String oldValue = this.scriptsRoot;
		this.scriptsRoot = scriptsRoot;
		fireChange(DeploymentDescriptorPackage.SCRIPTSDIR, scriptsRoot,
				oldValue);
	}

	public String getHealthcheck() {
		return healthcheck;
	}

	public void setHealthcheck(String healthcheck) {
		String oldValue = this.healthcheck;
		this.healthcheck = healthcheck;
		fireChange(DeploymentDescriptorPackage.HEALTHCHECK, healthcheck,
				oldValue);
	}

	public String getSummary() {
		return summary;
	}

	public String getDescription() {
		return description;
	}

	public void setSummary(String summary) {
		String oldValue = this.summary;
		this.summary = summary;
		fireChange(DeploymentDescriptorPackage.SUMMARY, summary, oldValue);
	}

	public void setDescription(String description) {
		String oldValue = this.description;
		this.description = description;
		fireChange(DeploymentDescriptorPackage.PKG_DESCRIPTION, description,
				oldValue);
	}

	public void setApiVersion(String value) {
		String oldValue = this.apiVersion;
		this.apiVersion = value;
		fireChange(DeploymentDescriptorPackage.VERSION_API, value, oldValue);
	}

	public String getApiVersion() {
		return apiVersion;
	}

	public String getApplicationDir() {
		return appDir;
	}

	public void setApplicationDir(String appDir) {
		String oldValue = this.appDir;
		this.appDir = appDir;
		fireChange(DeploymentDescriptorPackage.APPDIR, appDir, oldValue);
	}
	
	public String getLibraryDir() {
		return libDir;
	}

	public void setLibraryDir(String libDir) {
		String oldValue = this.libDir;
		this.libDir = libDir;
		fireChange(DeploymentDescriptorPackage.LIBDIR, libDir, oldValue);
	}
	
	public String getUpdateUrl() {
		return updateUrl;
	}
	
	public void setUpdateUrl(String updateUrl) {
		String oldValue = this.updateUrl;
		this.updateUrl = updateUrl;
		fireChange(DeploymentDescriptorPackage.UPDATE_URL, updateUrl, oldValue);
	}
	
	public List<IParameter> getParameters() {
		return super.getList(DeploymentDescriptorPackage.PARAMETERS);
	}

	public List<IVariable> getVariables() {
		return super.getList(DeploymentDescriptorPackage.VARIABLES);
	}

	public List<IPHPDependency> getPHPDependencies() {
		return super.getList(DeploymentDescriptorPackage.DEPENDENCIES_PHP);
	}

	public List<IDirectiveDependency> getDirectiveDependencies() {
		return super
				.getList(DeploymentDescriptorPackage.DEPENDENCIES_DIRECTIVE);
	}

	public List<IExtensionDependency> getExtensionDependencies() {
		return super
				.getList(DeploymentDescriptorPackage.DEPENDENCIES_EXTENSION);
	}

	public List<IZendServerDependency> getZendServerDependencies() {
		return super
				.getList(DeploymentDescriptorPackage.DEPENDENCIES_ZENDSERVER);
	}

	public List<IZendComponentDependency> getZendComponentDependencies() {
		return super
				.getList(DeploymentDescriptorPackage.DEPENDENCIES_ZSCOMPONENT);
	}

	public List<IZendFrameworkDependency> getZendFrameworkDependencies() {
		return super
				.getList(DeploymentDescriptorPackage.DEPENDENCIES_ZENDFRAMEWORK);
	}
	
	public List<IZendFramework2Dependency> getZendFramework2Dependencies() {
		return super
				.getList(DeploymentDescriptorPackage.DEPENDENCIES_ZENDFRAMEWORK2);
	}
	
	public List<IPHPLibraryDependency> getPHPLibraryDependencies() {
		return super
				.getList(DeploymentDescriptorPackage.DEPENDENCIES_LIBRARY);
	}

	public List<String> getPersistentResources() {
		return super.getList(DeploymentDescriptorPackage.PERSISTENT_RESOURCES);
	}

	public void set(Feature key, String value) {
		switch (key.id) {
		case DeploymentDescriptorPackage.PKG_TYPE_ID:
			ProjectType type = ProjectType.byName(value);
			if (type != ProjectType.UNKNOWN) {
				setType(value);
			}
			break;
		case DeploymentDescriptorPackage.PKG_NAME_ID:
			setName(value);
			break;
		case DeploymentDescriptorPackage.SUMMARY_ID:
			setSummary(value);
			break;
		case DeploymentDescriptorPackage.PKG_DESCRIPTION_ID:
			setDescription(value);
			break;
		case DeploymentDescriptorPackage.VERSION_RELEASE_ID:
			setReleaseVersion(value);
			break;
		case DeploymentDescriptorPackage.VERSION_API_ID:
			setApiVersion(value);
			break;
		case DeploymentDescriptorPackage.EULA_ID:
			setEulaLocation(value);
			break;
		case DeploymentDescriptorPackage.ICON_ID:
			setIconLocation(value);
			break;
		case DeploymentDescriptorPackage.DOCROOT_ID:
			setDocumentRoot(value);
			break;
		case DeploymentDescriptorPackage.SCRIPTSDIR_ID:
			setScriptsRoot(value);
			break;
		case DeploymentDescriptorPackage.HEALTHCHECK_ID:
			setHealthcheck(value);
			break;
		case DeploymentDescriptorPackage.APPDIR_ID:
			setApplicationDir(value);
			break;
		case DeploymentDescriptorPackage.LIBDIR_ID:
			setLibraryDir(value);
			break;
		case DeploymentDescriptorPackage.UPDATE_URL_ID:
			setUpdateUrl(value);
			break;
		default:
			throw new IllegalArgumentException(MessageFormat.format(
					Messages.DeploymentDescriptor_CannotSetProperty, key));
		}

	}

	public String get(Feature key) {
		switch (key.id) {
		case DeploymentDescriptorPackage.PKG_TYPE_ID:
			return type;
		case DeploymentDescriptorPackage.PKG_NAME_ID:
			return name;
		case DeploymentDescriptorPackage.SUMMARY_ID:
			return summary;
		case DeploymentDescriptorPackage.PKG_DESCRIPTION_ID:
			return description;
		case DeploymentDescriptorPackage.VERSION_RELEASE_ID:
			return releaseVersion;
		case DeploymentDescriptorPackage.VERSION_API_ID:
			return apiVersion;
		case DeploymentDescriptorPackage.EULA_ID:
			return eulaLocation;
		case DeploymentDescriptorPackage.ICON_ID:
			return iconLocation;
		case DeploymentDescriptorPackage.DOCROOT_ID:
			return documentRoot;
		case DeploymentDescriptorPackage.SCRIPTSDIR_ID:
			return scriptsRoot;
		case DeploymentDescriptorPackage.HEALTHCHECK_ID:
			return healthcheck;
		case DeploymentDescriptorPackage.APPDIR_ID:
			return appDir;
		case DeploymentDescriptorPackage.LIBDIR_ID:
			return libDir;
		case DeploymentDescriptorPackage.UPDATE_URL_ID:
			return updateUrl;
		default:
			throw new IllegalArgumentException(MessageFormat.format(
					Messages.DeploymentDescriptor_CannotSetProperty, key));
		}
	}
}
