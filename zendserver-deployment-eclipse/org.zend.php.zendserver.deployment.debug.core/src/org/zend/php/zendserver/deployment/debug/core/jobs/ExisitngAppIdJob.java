package org.zend.php.zendserver.deployment.debug.core.jobs;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.zend.php.zendserver.deployment.core.debugger.IDeploymentHelper;
import org.zend.php.zendserver.deployment.core.sdk.EclipseMappingModelLoader;
import org.zend.php.zendserver.deployment.core.sdk.SdkStatus;
import org.zend.php.zendserver.deployment.core.sdk.StatusChangeListener;
import org.zend.php.zendserver.deployment.debug.core.Activator;
import org.zend.php.zendserver.deployment.debug.core.Messages;
import org.zend.sdklib.application.ZendApplication;
import org.zend.webapi.core.connection.data.ApplicationInfo;
import org.zend.webapi.core.connection.data.ApplicationsList;

public class ExisitngAppIdJob extends AbstractLaunchJob {

	private List<URL> possibleURLs = new ArrayList<URL>();

	public ExisitngAppIdJob(IDeploymentHelper helper, IProject project) {
		super(Messages.ExisitngAppIdJob_JobTitle, helper, project.getLocation()
				.toString());
	}

	public ExisitngAppIdJob(IDeploymentHelper helper, String projectPath) {
		super(Messages.ExisitngAppIdJob_JobTitle, helper, projectPath);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		StatusChangeListener listener = new StatusChangeListener(monitor);
		ZendApplication app = new ZendApplication(
				new EclipseMappingModelLoader());
		app.addStatusChangeListener(listener);
		ApplicationsList list = app.getStatus(helper.getTargetId());
		List<ApplicationInfo> infos = list.getApplicationsInfo();
		if (infos != null) {
			URL baseURL = helper.getBaseURL();
			possibleURLs.add(baseURL);
			initLocalUrls(baseURL);
			if (helper.isDefaultServer()) {
				try {
					possibleURLs
							.add(new URL(baseURL.getProtocol(),
									IDeploymentHelper.DEFAULT_SERVER, baseURL
											.getFile()));
					possibleURLs.add(new URL(baseURL.getProtocol(),
							"localhost", baseURL.getFile())); //$NON-NLS-1$
				} catch (MalformedURLException e) {
					// ignore
				}
			}
			for (ApplicationInfo info : infos) {
				URL baseUrl = null;
				try {
					baseUrl = new URL(info.getBaseUrl());
				} catch (MalformedURLException e) {
					// / ignore
				}
				for (URL url : possibleURLs) {
					if (compareURLs(url, baseUrl)) {
						helper.setAppId(info.getId());
						helper.setInstalledLocation(info.getInstalledLocation());
						return new SdkStatus(listener.getStatus());
					}
				}
			}
		}
		return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
				Messages.ExisitngAppIdJob_AppNameConflictMessage, null);
	}

	private void initLocalUrls(URL baseURL) {
		try {
			Enumeration<NetworkInterface> nets = NetworkInterface
					.getNetworkInterfaces();
			if (nets != null) {
				for (NetworkInterface netInterface : Collections.list(nets)) {
					Enumeration<InetAddress> inetAddresses = netInterface
							.getInetAddresses();
					if (inetAddresses != null) {
						for (InetAddress address : Collections
								.list(inetAddresses)) {
							if (address instanceof Inet4Address) {
								try {
									possibleURLs.add(new URL(baseURL
											.getProtocol(), address
											.getHostAddress(), baseURL
											.getFile()));
								} catch (MalformedURLException e) {
									// just continue
								}
							}
						}
					}
				}
			}
		} catch (SocketException e) {
			// just continue
		}
	}

	private boolean compareURLs(URL current, URL url) {
		if (current == null || url == null) {
			return false;
		}
		if (current.getProtocol().equals(url.getProtocol())
				&& current.getHost().equals(url.getHost())
				&& current.getFile().equals(url.getFile())) {
			return true;
		}
		return false;
	}

}
