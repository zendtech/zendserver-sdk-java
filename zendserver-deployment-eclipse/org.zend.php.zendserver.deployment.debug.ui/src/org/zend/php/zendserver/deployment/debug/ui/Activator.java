package org.zend.php.zendserver.deployment.debug.ui;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.php.internal.debug.core.PHPDebugPlugin;
import org.eclipse.php.internal.debug.core.preferences.PHPDebugCorePreferenceNames;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.server.core.manager.ServersManager;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.zend.php.server.core.utils.ServerUtils;
import org.zend.php.zendserver.deployment.debug.core.DebugModeManager;
import org.zend.php.zendserver.deployment.debug.ui.preferences.DebugModeCompositeFragment;
import org.zend.sdklib.target.IZendTarget;

/**
 * The activator class controls the plug-in life cycle
 */
@SuppressWarnings("restriction")
public class Activator extends AbstractUIPlugin {

	private class BeakAtFirstLineListener implements IPreferenceChangeListener {

		public void preferenceChange(PreferenceChangeEvent event) {
			if (PHPDebugCorePreferenceNames.STOP_AT_FIRST_LINE.equals(event
					.getKey())) {
				Server[] servers = ServersManager.getServers();
				DebugModeManager manager = DebugModeManager.getManager();
				for (Server server : servers) {
					IZendTarget target = ServerUtils.getTarget(server);
					if (target != null && manager.isInDebugMode(target)
							&& askForRestart(target, server.getName())) {
						Job restartJob = new DebugModeCompositeFragment.RestartJob(
								target);
						restartJob.setUser(true);
						restartJob.schedule();
					}
				}
			}
		}

		private boolean askForRestart(IZendTarget target, String name) {
			return MessageDialog.openQuestion(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(),
					Messages.Activator_RestartTitle, MessageFormat.format(
							Messages.Activator_RestartMessage, name));
		}
	};

	// The plug-in ID
	public static final String PLUGIN_ID = "org.zend.php.zendserver.deployment.debug.ui"; //$NON-NLS-1$

	public static final String WIZARD_CONTRIBUTION_EXTENSION = PLUGIN_ID
			+ ".deployWizardContribution"; //$NON-NLS-1$

	public static final String IMAGE_RUN_APPLICATION = "icons/obj16/run_exc.gif"; //$NON-NLS-1$
	public static final String IMAGE_DEBUG_APPLICATION = "icons/obj16/debug_exc.gif"; //$NON-NLS-1$
	public static final String IMAGE_DEPLOY_APPLICATION = "icons/obj16/deploy_exc.png"; //$NON-NLS-1$
	public static final String IMAGE_WIZBAN_DEP = "icons/wizban/newdep_wiz.png"; //$NON-NLS-1$
	public static final String IMAGE_WIZBAN_DEBUG = "icons/wizban/debug_wiz.png"; //$NON-NLS-1$
	public static final String IMAGE_WIZBAN_DEPLOY = "icons/wizban/deploy_wiz.png"; //$NON-NLS-1$
	public static final String IMAGE_EXPORT_PARAMS_DEP = "icons/wizban/exportParams_wiz.png"; //$NON-NLS-1$

	public static final String IMAGE_SSH_TUNNEL = "icons/obj16/ssh_tunnel.png"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	private IPreferenceChangeListener breakAtFirstListener;

	/**
	 * The constructor
	 */
	public Activator() {
		breakAtFirstListener = new BeakAtFirstLineListener();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		IEclipsePreferences prefs = InstanceScope.INSTANCE
				.getNode(PHPDebugPlugin.ID);
		prefs.addPreferenceChangeListener(breakAtFirstListener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		IEclipsePreferences prefs = InstanceScope.INSTANCE
				.getNode(PHPDebugPlugin.ID);
		prefs.removePreferenceChangeListener(breakAtFirstListener);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public static void log(Throwable e) {
		getDefault().getLog().log(
				new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage(), e));
	}

}
