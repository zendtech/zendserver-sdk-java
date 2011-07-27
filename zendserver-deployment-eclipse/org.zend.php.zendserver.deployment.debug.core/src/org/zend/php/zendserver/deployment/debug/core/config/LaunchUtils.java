package org.zend.php.zendserver.deployment.debug.core.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.php.debug.core.debugger.parameters.IDebugParametersKeys;
import org.eclipse.php.internal.debug.core.IPHPDebugConstants;
import org.eclipse.php.internal.debug.core.PHPDebugPlugin;
import org.eclipse.php.internal.debug.core.debugger.AbstractDebuggerConfiguration;
import org.eclipse.php.internal.debug.core.preferences.PHPDebugCorePreferenceNames;
import org.eclipse.php.internal.debug.core.preferences.PHPDebuggersRegistry;
import org.eclipse.php.internal.debug.core.preferences.PHPProjectPreferences;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.server.core.manager.ServersManager;
import org.zend.php.zendserver.deployment.core.debugger.DeploymentAttributes;
import org.zend.php.zendserver.deployment.core.descriptor.DescriptorContainerManager;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.php.zendserver.deployment.core.sdk.EclipseMappingModelLoader;
import org.zend.sdklib.mapping.IMapping;
import org.zend.sdklib.mapping.IMappingEntry;
import org.zend.sdklib.mapping.IMappingEntry.Type;
import org.zend.sdklib.mapping.IMappingModel;
import org.zend.sdklib.mapping.MappingModelFactory;

@SuppressWarnings("restriction")
public class LaunchUtils {

	public static final String AUTO_GENERATED_URL = "auto_generated_url"; //$NON-NLS-1$

	public static ILaunchConfiguration createConfiguration(IProject project, int appId,
			IDeploymentHelper entry) throws CoreException {
		ILaunchConfiguration config = null;

		ILaunchConfigurationWorkingCopy wc = getConfigurationType().newInstance(null,
				getNewConfigurationName(project.getName()));

		// Set the debugger ID and the configuration delegate for this launch
		// configuration
		String debuggerID = PHPProjectPreferences.getDefaultDebuggerID(project);
		wc.setAttribute(PHPDebugCorePreferenceNames.PHP_DEBUGGER_ID, debuggerID);

		AbstractDebuggerConfiguration debuggerConfiguration = PHPDebuggersRegistry
				.getDebuggerConfiguration(debuggerID);
		wc.setAttribute(PHPDebugCorePreferenceNames.CONFIGURATION_DELEGATE_CLASS,
				debuggerConfiguration.getWebLaunchDelegateClass());

		// TODO find real server name
		wc.setAttribute(Server.NAME, "Zend Server");

		IResource resource = getFile(project);
		if (resource != null) {
			wc.setAttribute(Server.FILE_NAME, resource.getFullPath().toString());
			wc.setMappedResources(new IResource[] { resource });
		}

		wc.setAttribute(AUTO_GENERATED_URL, false);
		if (entry.isDefaultServer()) {
			Server server = ServersManager.getServer(wc.getAttribute(Server.NAME, ""));
			if (server != null) {
				wc.setAttribute(Server.BASE_URL, server.getBaseURL() + entry.getBasePath());
			}
		} else {
			wc.setAttribute(Server.BASE_URL, entry.getVirtualHost() + "/" + entry.getBasePath());
		}

		wc.setAttribute(IPHPDebugConstants.RUN_WITH_DEBUG_INFO, PHPDebugPlugin.getDebugInfoOption());
		wc.setAttribute(IPHPDebugConstants.OPEN_IN_BROWSER, PHPDebugPlugin.getOpenInBrowserOption());

		// set true as default
		wc.setAttribute(IDebugParametersKeys.FIRST_LINE_BREAKPOINT, true);

		wc.setAttribute(DeploymentAttributes.BASE_PATH.getName(), entry.getBasePath());
		wc.setAttribute(DeploymentAttributes.APP_ID.getName(), appId);
		wc.setAttribute(DeploymentAttributes.APPLICATION_NAME.getName(), entry.getAppName());
		wc.setAttribute(DeploymentAttributes.DEFAULT_SERVER.getName(), entry.isDefaultServer());
		wc.setAttribute(DeploymentAttributes.IGNORE_FAILURES.getName(), entry.isIgnoreFailures());
		wc.setAttribute(DeploymentAttributes.PROJECT_NAME.getName(), project.getName());
		wc.setAttribute(DeploymentAttributes.TARGET_ID.getName(), entry.getTargetId());
		wc.setAttribute(DeploymentAttributes.VIRTUAL_HOST.getName(), entry.getVirtualHost());
		wc.setAttribute(DeploymentAttributes.PARAMETERS.getName(), entry.getUserParams());

		config = wc.doSave();
		return config;
	}

	public static ILaunchConfiguration findLaunchConfiguration(IProject project) {
		try {
			ILaunchConfiguration[] configs = DebugPlugin.getDefault().getLaunchManager()
					.getLaunchConfigurations(getConfigurationType());

			int numConfigs = configs == null ? 0 : configs.length;
			for (int i = 0; i < numConfigs; i++) {
				String projectName = configs[i].getAttribute(
						DeploymentAttributes.PROJECT_NAME.getName(), (String) null);
				if (project.getName().equals(projectName)) {
					return configs[i].getWorkingCopy();
				}
			}
		} catch (CoreException ce) {
			return null;
		}
		return null;
	}

	public static ILaunchConfigurationType getConfigurationType() {
		ILaunchManager lm = DebugPlugin.getDefault().getLaunchManager();
		return lm.getLaunchConfigurationType(IPHPDebugConstants.PHPServerLaunchType);
	}

	private static IResource getFile(IProject project) throws CoreException {
		IDescriptorContainer descriptorContainer = DescriptorContainerManager.getService()
				.openDescriptorContainer(project);
		String documentRoot = descriptorContainer.getDescriptorModel().getDocumentRoot();
		if (documentRoot != null && !documentRoot.isEmpty()) {
			IResource documentResource = project.findMember(documentRoot);
			if (documentResource instanceof IContainer) {
				return getFile(((IContainer) documentResource).members());
			}
		}
		IMappingModel model = MappingModelFactory.createModel(new EclipseMappingModelLoader(),
				new File(project.getLocation().toString()));
		IMappingEntry entry = model.getEntry(IMappingModel.APPDIR, Type.INCLUDE);
		List<IMapping> mappings = entry.getMappings();
		for (IMapping mapping : mappings) {
			IResource mappedResource = project.findMember(mapping.getPath());
			if (mappedResource instanceof IContainer) {
				return getFile(((IContainer) mappedResource).members());
			} else {
				return mappedResource;
			}
		}
		return null;
	}

	private static IResource getFile(IResource[] members) throws CoreException {
		List<IResource> toCheck = new ArrayList<IResource>();
		for (IResource member : members) {
			if (member instanceof IContainer) {
				IResource[] children = ((IContainer) member).members();
				for (IResource child : children) {
					if (child instanceof IContainer) {
						toCheck.add(child);
					} else {
						return child;
					}
				}
			} else {
				return member;
			}
		}
		return getFile(toCheck.toArray(new IResource[0]));
	}

	private static String getNewConfigurationName(String fileName) {
		String configurationName = "New_configuration";
		try {
			IPath path = Path.fromOSString(fileName);
			String fileExtention = path.getFileExtension();
			String lastSegment = path.lastSegment();
			if (lastSegment != null) {
				if (fileExtention != null) {
					lastSegment = lastSegment.replaceFirst("." + fileExtention, "");
				}
				configurationName = lastSegment;
			}
		} catch (Exception e) {
			// ignore and use default configurationName value
		}
		return DebugPlugin.getDefault().getLaunchManager()
				.generateLaunchConfigurationName(configurationName);
	}

}
