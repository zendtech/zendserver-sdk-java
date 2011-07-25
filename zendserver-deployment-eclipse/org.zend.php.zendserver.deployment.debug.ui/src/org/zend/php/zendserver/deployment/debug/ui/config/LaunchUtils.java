package org.zend.php.zendserver.deployment.debug.ui.config;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.eclipse.core.resources.IProject;
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
import org.zend.php.zendserver.deployment.core.debugger.DeploymentAttributes;
import org.zend.php.zendserver.deployment.core.sdk.EclipseMappingModelLoader;
import org.zend.sdklib.mapping.IMappingModel;
import org.zend.sdklib.mapping.MappingModelFactory;
import org.zend.sdklib.target.IZendTarget;

public class LaunchUtils {

	public static final String AUTO_GENERATED_URL = "auto_generated_url"; //$NON-NLS-1$

	public static ILaunchConfiguration createConfiguration(IProject project, URL baseURL,
			Map<String, String> parameters, IZendTarget target, String appName,
			boolean defaultServer, boolean ignoreFailures,
			String vHost) throws CoreException {
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

		// base URL + index.html, we can find file path based on project
		// relative path and resource mapping
		wc.setAttribute(Server.FILE_NAME, getFilename(project));

		// deployment base URL
		wc.setAttribute(AUTO_GENERATED_URL, false);
		wc.setAttribute(Server.BASE_URL, baseURL.toString());

		wc.setAttribute(IPHPDebugConstants.RUN_WITH_DEBUG_INFO, PHPDebugPlugin.getDebugInfoOption());
		wc.setAttribute(IPHPDebugConstants.OPEN_IN_BROWSER, PHPDebugPlugin.getOpenInBrowserOption());

		// set true as default
		wc.setAttribute(IDebugParametersKeys.FIRST_LINE_BREAKPOINT, true);

		wc.setAttribute(DeploymentAttributes.BASE_PATH.getName(), baseURL.getPath());
		wc.setAttribute(DeploymentAttributes.APPLICATION_NAME.getName(), appName);
		wc.setAttribute(DeploymentAttributes.DEFAULT_SERVER.getName(), defaultServer);
		wc.setAttribute(DeploymentAttributes.IGNORE_FAILURES.getName(), ignoreFailures);
		wc.setAttribute(DeploymentAttributes.PROJECT_NAME.getName(), project.getName());
		wc.setAttribute(DeploymentAttributes.TARGET_ID.getName(), target.getId());
		wc.setAttribute(DeploymentAttributes.VIRTUAL_HOST.getName(), vHost);
		wc.setAttribute(DeploymentAttributes.PARAMETERS.getName(), parameters);

		config = wc.doSave();
		return config;
	}

	public static ILaunchConfiguration findLaunchConfiguration(IProject project) {
		ILaunchConfiguration config = null;
		try {
			ILaunchConfiguration[] configs = DebugPlugin.getDefault().getLaunchManager()
					.getLaunchConfigurations(getConfigurationType());

			int numConfigs = configs == null ? 0 : configs.length;
			for (int i = 0; i < numConfigs; i++) {
				String projectName = configs[i].getAttribute(
						DeploymentAttributes.PROJECT_NAME.getName(), (String) null);
				if (project.getName().equals(projectName)) {
					config = configs[i].getWorkingCopy();
					break;
				}
			}
		} catch (CoreException ce) {
			// TODO log
		}
		return config;
	}

	public static ILaunchConfigurationType getConfigurationType() {
		ILaunchManager lm = DebugPlugin.getDefault().getLaunchManager();
		return lm.getLaunchConfigurationType(IPHPDebugConstants.PHPServerLaunchType);
	}

	private static String getFilename(IProject project) {
		File container = project.getLocation().toFile();
		IMappingModel model = MappingModelFactory.createModel(new EclipseMappingModelLoader(),
				container);
		String path = null;
		try {
			path = model.getPackagePath("index.html");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return path != null ? path.substring(path.indexOf(File.separator) + 1) : null;
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
			// TODO log
		}
		return DebugPlugin.getDefault().getLaunchManager()
				.generateLaunchConfigurationName(configurationName);
	}

}
