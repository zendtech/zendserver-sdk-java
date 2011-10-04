package org.zend.php.zendserver.deployment.debug.core.config;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
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
import org.zend.php.zendserver.deployment.core.DeploymentCore;
import org.zend.php.zendserver.deployment.core.debugger.DeploymentAttributes;
import org.zend.php.zendserver.deployment.core.debugger.PHPLaunchConfigs;
import org.zend.php.zendserver.deployment.core.descriptor.DescriptorContainerManager;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.php.zendserver.deployment.core.targets.EclipseSSH2Settings;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.debug.core.Activator;
import org.zend.php.zendserver.deployment.debug.core.jobs.AbstractLaunchJob;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;

@SuppressWarnings("restriction")
public class LaunchUtils {

	public static final String AUTO_GENERATED_URL = "auto_generated_url"; //$NON-NLS-1$

	public static ILaunchConfiguration createConfiguration(IProject project,
			IDeploymentHelper helper) throws CoreException {
		ILaunchConfiguration config = null;
		ILaunchConfigurationWorkingCopy wc = getConfigurationType().newInstance(null,
				getNewConfigurationName(project.getName(), helper.getTargetHost()));

		// Set the debugger ID and the configuration delegate for this launch
		// configuration
		String debuggerID = PHPProjectPreferences.getDefaultDebuggerID(project);
		wc.setAttribute(PHPDebugCorePreferenceNames.PHP_DEBUGGER_ID, debuggerID);

		AbstractDebuggerConfiguration debuggerConfiguration = PHPDebuggersRegistry
				.getDebuggerConfiguration(debuggerID);
		wc.setAttribute(PHPDebugCorePreferenceNames.CONFIGURATION_DELEGATE_CLASS,
				debuggerConfiguration.getWebLaunchDelegateClass());

		wc.setAttribute(IPHPDebugConstants.RUN_WITH_DEBUG_INFO, PHPDebugPlugin.getDebugInfoOption());
		wc.setAttribute(IPHPDebugConstants.OPEN_IN_BROWSER, PHPDebugPlugin.getOpenInBrowserOption());
		// set true as default
		wc.setAttribute(IDebugParametersKeys.FIRST_LINE_BREAKPOINT, true);

		updateLaunchConfiguration(project, helper, wc);

		config = wc.doSave();
		return config;
	}

	public static IDeploymentHelper createDefaultHelper(String targetId, IProject project) {
		TargetsManager manager = TargetsManagerService.INSTANCE.getTargetManager();
		IZendTarget target = manager.getTargetById(targetId);
		return createDefaultHelper(project, target);
	}

	public static IDeploymentHelper createDefaultHelper(IProject project) {
		IZendTarget target = getTargetFromPreferences(project);
		return createDefaultHelper(project, target);
	}

	public static void updateLaunchConfiguration(IProject project,
			IDeploymentHelper helper, ILaunchConfigurationWorkingCopy wc) throws CoreException {
		IResource resource = getFile(project);

		String pathToFile = null;
		String currentFile = wc.getAttribute(Server.FILE_NAME, (String) null);
		if (resource != null) {
			if (currentFile != null) {
				IPath currentPath = new Path(currentFile);
				IPath relativePath = resource.getProjectRelativePath();
				IPath currentRelativePath = currentPath
						.makeRelativeTo(new Path(project.getName()));
				if (relativePath.equals(currentRelativePath)) {
					wc.setAttribute(Server.FILE_NAME, resource.getFullPath()
							.toString());
					wc.setMappedResources(new IResource[] { resource });
				} else {
					IDescriptorContainer descriptorContainer = DescriptorContainerManager
							.getService().openDescriptorContainer(project);
					String documentRoot = descriptorContainer
							.getDescriptorModel().getDocumentRoot();
					if (documentRoot != null && !documentRoot.isEmpty()) {
						int index = documentRoot.indexOf("/"); //$NON-NLS-1$
						if (index != -1) {
							documentRoot = documentRoot.substring(index + 1);
						}
						pathToFile = currentRelativePath.makeRelativeTo(
								new Path(documentRoot)).toString();
					} else {
						pathToFile = currentRelativePath.toString();
					}
					wc.setAttribute(Server.FILE_NAME, currentPath.toString());
					wc.setMappedResources(new IResource[] { project
							.getWorkspace().getRoot().findMember(currentPath) });
				}
			} else {
				wc.setAttribute(Server.FILE_NAME, resource.getFullPath()
						.toString());
				wc.setMappedResources(new IResource[] { resource });
			}
		}

		Server server = findExistingServer(helper.getBaseURL());
		if (server == null) {
			server = createPHPServer(helper.getBaseURL(), helper.getTargetId());
		}
		wc.setAttribute(Server.NAME, server.getName());
		ServersManager.setDefaultServer(project, server);

		// always use non-generated url
		wc.setAttribute(AUTO_GENERATED_URL, false);
		URL baseURL = helper.getBaseURL();
		if (baseURL != null) {
			if (pathToFile != null) {
				wc.setAttribute(Server.BASE_URL, helper.getBaseURL().toString()
						+ "/" + pathToFile); //$NON-NLS-1$
			} else {
				wc.setAttribute(Server.BASE_URL, helper.getBaseURL().toString());
			}
		}
		wc.setAttribute(DeploymentAttributes.APP_ID.getName(), helper.getAppId());
		wc.setAttribute(DeploymentAttributes.BASE_URL.getName(), helper.getBaseURL().toString());
		wc.setAttribute(DeploymentAttributes.APPLICATION_NAME.getName(), helper.getAppName());
		wc.setAttribute(DeploymentAttributes.DEFAULT_SERVER.getName(), helper.isDefaultServer());
		wc.setAttribute(DeploymentAttributes.IGNORE_FAILURES.getName(), helper.isIgnoreFailures());
		wc.setAttribute(DeploymentAttributes.PROJECT_NAME.getName(), project.getName());
		if (helper.getTargetId() != null && !helper.getTargetId().isEmpty()) {
			wc.setAttribute(DeploymentAttributes.TARGET_ID.getName(),
					helper.getTargetId());
			wc.setAttribute(DeploymentAttributes.TARGET_HOST.getName(),
					helper.getTargetHost());
		}
		wc.setAttribute(DeploymentAttributes.PARAMETERS.getName(), helper.getUserParams());
		wc.setAttribute(DeploymentAttributes.OPERATION_TYPE.getName(), helper.getOperationType());
		String location = helper.getInstalledLocation();
		if (location != null && !location.isEmpty()) {
		wc.setAttribute(DeploymentAttributes.INSTALLED_LOCATION.getName(),
				helper.getInstalledLocation());
		}
	}
	
	public static ILaunchConfiguration findLaunchConfiguration(IProject project) {
		return findLaunchConfiguration(project, null);
	}
	
	public static ILaunchConfiguration findLaunchConfiguration(IProject project, String targetId) {
		try {
			ILaunchConfiguration[] configs = DebugPlugin.getDefault().getLaunchManager()
					.getLaunchConfigurations(getConfigurationType());

			int numConfigs = configs == null ? 0 : configs.length;
			for (int i = 0; i < numConfigs; i++) {
				String projectName = configs[i].getAttribute(
						DeploymentAttributes.PROJECT_NAME.getName(), (String) null);
				
				String configTargetId = configs[i].getAttribute(DeploymentAttributes.TARGET_ID.getName(), (String)null);
				boolean targetIdMatches = (targetId == null) || (targetId.equals(configTargetId)); 
						
				if (project.getName().equals(projectName) && targetIdMatches) {
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
		return lm.getLaunchConfigurationType(PHPLaunchConfigs.LAUNCH_CONFIG_TYPE);
	}

	public static IProject getProjectFromFilename(ILaunchConfiguration config) throws CoreException {
		String fileName = config.getAttribute(Server.FILE_NAME, (String) null);
		String projectName = null;
		if (fileName != null) {
			IPath filePath = new Path(fileName);
			if (filePath.segmentCount() > 0) {
				projectName = filePath.segment(0);
			}
		}
		
		if (projectName == null) {
			projectName = config.getAttribute(DeploymentAttributes.PROJECT_NAME.getName(), (String) null);
		}
		
		if (projectName != null) {
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			return root.getProject(projectName);
		}
		
		return null;
	}

	public static boolean isAutoDeployAvailable() {
		return getAutoDeployJob() != null ? true : false;
	}

	public static AbstractLaunchJob getAutoDeployJob() {
		IConfigurationElement[] config = Platform
				.getExtensionRegistry()
				.getConfigurationElementsFor(Activator.AUTO_DEPLOY_EXTENSION_ID);
		try {
			for (IConfigurationElement e : config) {

				final Object o = e.createExecutableExtension("class"); //$NON-NLS-1$
				if (o instanceof AbstractLaunchJob) {
					return (AbstractLaunchJob) o;
				}
			}
		} catch (CoreException e) {
			return null;
		}
		return null;
	}

	public static IZendTarget getDefaultTarget(IProject project) {
		Server server = ServersManager.getDefaultServer(project);
		String serverHost = server.getHost();
		if (server != null) {
			IZendTarget[] targets = TargetsManagerService.INSTANCE
					.getTargetManager().getTargets();
			if (targets != null) {
				for (IZendTarget target : targets) {
					if (serverHost.equals(target.getHost().toString())) {
						return target;
					}
				}
			}
		}
		return null;
	}

	public static boolean configureTargetSSH(String targetId) {
		IZendTarget target = TargetsManagerService.INSTANCE.getTargetManager()
				.getTargetById(targetId);
		if (target == null) {
			return false;
		}
		EclipseSSH2Settings.registerDevCloudTarget(target, true);
		return true;
	}

	private static IResource getFile(IProject project) throws CoreException {
		return project.findMember(DescriptorContainerManager.DESCRIPTOR_PATH);
	}

	private static String getNewConfigurationName(String fileName, String targetHost) {
		String configurationName = "New_configuration"; //$NON-NLS-1$
		try {
			IPath path = Path.fromOSString(fileName);
			String fileExtention = path.getFileExtension();
			String lastSegment = path.lastSegment();
			if (lastSegment != null) {
				if (fileExtention != null) {
					lastSegment = lastSegment.replaceFirst("." + fileExtention, ""); //$NON-NLS-1$ //$NON-NLS-2$
				}
				configurationName = lastSegment;
			}
		} catch (Exception e) {
			// ignore and use default configurationName value
		}
		return DebugPlugin.getDefault().getLaunchManager()
				.generateLaunchConfigurationName(configurationName)
				+ "_" + targetHost; //$NON-NLS-1$
	}
	
	private static Server createPHPServer(URL baseURL, String targetId) {
		try {
			URL url = new URL(baseURL.getProtocol(), baseURL.getHost(), baseURL.getPort(), ""); //$NON-NLS-1$
			String urlString = url.toString();
			Server server = new Server("Zend Target (id: " + targetId + " host: " + url.getHost() //$NON-NLS-1$ //$NON-NLS-2$
					+ ")", urlString, urlString, ""); //$NON-NLS-1$ //$NON-NLS-2$
			ServersManager.addServer(server);
			ServersManager.save();
			return server;
		} catch (MalformedURLException e) {
			// ignore, verified earlier
		}
		return null;
	}

	private static Server findExistingServer(URL baseURL) {
		if (baseURL == null) {
			return null;
		}
		Server[] servers = ServersManager.getServers();
		for (Server server : servers) {
			try {
				URL serverBaseURL = new URL(server.getBaseURL());
				if (serverBaseURL.getHost().equals(baseURL.getHost())
						&& serverBaseURL.getPort() == baseURL.getPort()) {
					return server;
				}
			} catch (MalformedURLException e) {
				// ignore and continue searching
			}
		}
		return null;
	}

	private static IDeploymentHelper createDefaultHelper(IProject project, IZendTarget target) {
		if (target != null) {
			try {
				IDeploymentHelper helper = new DeploymentHelper();
				URL targetUrl = target.getDefaultServerURL();
				URL baseUrl = new URL(targetUrl.getProtocol(), targetUrl.getHost(),
						targetUrl.getPort(), "/" + project.getName()); //$NON-NLS-1$
				helper.setBaseURL(baseUrl.toString());
				helper.setDefaultServer(true);
				helper.setTargetId(target.getId());
				helper.setTargetHost(target.getHost().getHost().toString());
				helper.setIgnoreFailures(false);
				helper.setOperationType(IDeploymentHelper.DEPLOY);
				helper.setProjectName(project.getName());
				return helper;
			} catch (MalformedURLException e) {
				return null;
			}
		}
		return null;
	}

	private static IZendTarget getTargetFromPreferences(IProject project) {
		IEclipsePreferences pref = new ProjectScope(project).getNode(DeploymentCore.PLUGIN_ID);
		String targetId = pref.get("targetId", null); //$NON-NLS-1$
		String targetHost = pref.get("targetHost", null); //$NON-NLS-1$
		if (targetId != null) {
			TargetsManager manager = TargetsManagerService.INSTANCE.getTargetManager();
			IZendTarget target = manager.getTargetById(targetId);
			if (target != null && target.getHost().toString().equals(targetHost)) {
				return target;
			}
		}
		return null;
	}

}
