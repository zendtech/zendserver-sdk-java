/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.manager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.zend.sdklib.internal.library.AbstractChangeNotifier;
import org.zend.sdklib.internal.target.OpenShiftTarget;
import org.zend.sdklib.internal.target.UserBasedTargetLoader;
import org.zend.sdklib.internal.target.ZendDevCloud;
import org.zend.sdklib.internal.target.ZendTarget;
import org.zend.sdklib.internal.target.ZendTargetAutoDetect;
import org.zend.sdklib.target.ITargetLoader;
import org.zend.sdklib.target.IZendTarget;
import org.zend.sdklib.target.LicenseExpiredException;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.data.values.ServerType;
import org.zend.webapi.core.connection.data.values.WebApiVersion;
import org.zend.webapi.core.connection.data.values.ZendServerVersion;
import org.zend.webapi.core.connection.response.ResponseCode;
import org.zend.webapi.core.progress.BasicStatus;
import org.zend.webapi.core.progress.StatusCode;

/**
 * Target environments manager for the This is a thread-safe class that can be
 * used across threads
 * 
 * @author Roy, 2011
 */
public class TargetsManager extends AbstractChangeNotifier {

	public static final String DEFAULT_KEY = "sdk"; //$NON-NLS-1$

	/**
	 * All targets loaded in the manager
	 */
	private List<IZendTarget> all = new ArrayList<IZendTarget>(1);

	/**
	 * The mechanism that is responsible to load the targets
	 */
	private final ITargetLoader loader;

	/**
	 * Default target Id (for fast execution)
	 */
	private String defaultId = null;

	public TargetsManager() {
		this(new UserBasedTargetLoader());
	}

	public TargetsManager(ITargetLoader loader) {
		this.loader = loader;
		load();
	}

	public void reload() {
		this.all.clear();
		load();
	}

	/**
	 * @param target
	 * @return
	 * @throws LicenseExpiredException
	 * @throws WebApiException
	 */
	public synchronized IZendTarget add(IZendTarget target)
			throws TargetException, LicenseExpiredException {
		return add(target, false);
	}

	/**
	 * @param target
	 * @param suppressConnect
	 * @return
	 * @throws LicenseExpiredException
	 * @throws WebApiException
	 */
	public synchronized IZendTarget add(IZendTarget target,
			boolean suppressConnect) throws TargetException,
			LicenseExpiredException {
		if (!validTarget(target)) {
			return null;
		}

		// try to connect to server
		try {
			if (!suppressConnect && !target.connect()) {
				return null;
			}
		} catch (WebApiException e) {
			throw new TargetException(e);
		} catch (LicenseExpiredException e) {
			throw new TargetException(e);
		}

		IZendTarget existingTarget = getTarget(target.getHost(),
				target.getKey());
		if (existingTarget != null) {
			return updateTarget(existingTarget, target, suppressConnect);
		} else {
			
			//check whether the target descriptor exists
			//it could be added by another studio installation
			if(this.loader.isAvailable(target))
				this.loader.update(target);
			else
				this.loader.add(target);

			// adds the target to the list
			final boolean added = this.all.add(target);

			if (this.all.size() == 1) {
				defaultId = this.all.get(0).getId();
			}

			if (added) {
				statusChanged(new BasicStatus(StatusCode.UNKNOWN,
						"added target", "added target"));
			}

			return added ? target : null;
		}
	}

	private IZendTarget getTarget(URL host, String key) {
		for (IZendTarget t : all) {
			try {
				if (host.toURI().equals(t.getHost().toURI())
						&& key.equals(t.getKey())) {
					return t;
				}
			} catch (URISyntaxException e) {
				// ignore
			}
		}

		return null;
	}

	public synchronized IZendTarget remove(IZendTarget target) {
		if (target == null) {
			throw new IllegalArgumentException("Target cannot be null");
		}
		
		if (!this.all.contains(target)) {
			throw new IllegalArgumentException(MessageFormat.format("Target with id ''{0}'' does not exist.", target.getId()));
		}

		//check whether the target descriptor still exist
		//it could be deleted by another studio installation
		if(this.loader.isAvailable(target))
			this.loader.remove(target);

		// remove the specified target
		final boolean removed = this.all.remove(target);

		if (this.all.size() == 0) {
			defaultId = null;
		}

		if (removed) {
			statusChanged(new BasicStatus(StatusCode.UNKNOWN, "removed target",
					"removed target"));
		}

		return removed ? target : null;
	}

	/**
	 * Remove all temporary targets. Target is temporary when
	 * {@link IZendTarget#isTemporary()} returns <code>true</code>.
	 */
	public void removeAllTemporary() {
		List<IZendTarget> toRemove = new ArrayList<IZendTarget>();
		for (IZendTarget target : all) {
			if (target.isTemporary()) {
				toRemove.add(target);
			}
		}
		for (IZendTarget target : toRemove) {
			remove(target);
		}
	}

	/**
	 * Finds a target given target id
	 * 
	 * @param i
	 * @return the specified target
	 */
	public synchronized IZendTarget getTargetById(String id) {
		if (id == null) {
			return null;
		}

		for (IZendTarget target : getTargets()) {
			if (id.equals(target.getId())) {
				return target;
			}
		}

		return null;
	}

	public synchronized IZendTarget detectLocalhostTarget(String targetId,
			String key, String secretKey) throws DetectionException,
			LicenseExpiredException {
		return detectLocalhostTarget(targetId, key, secretKey, true, true);
	}

	/**
	 * Returns a target that represents the localhost zend server. Returned
	 * target may be not fully initialized, requiring some extra commands.
	 * Clients should use {@link IZendTarget#isTemporary()} to test whether they
	 * can connect safely.
	 * 
	 * @param targetId
	 *            target id to use, null if not specified
	 * @param key
	 *            key to use, null if not specified
	 * @return the detected localhost target, or null if detection failed
	 * @throws DetectionException
	 * @throws LicenseExpiredException
	 */
	public synchronized IZendTarget detectLocalhostTarget(String targetId,
			String key) throws DetectionException, LicenseExpiredException {
		return detectLocalhostTarget(targetId, key, true, true);
	}

	/**
	 * 
	 * @param targetId
	 *            target id to use
	 * @param key
	 *            key name to use
	 * @param add
	 *            whether to add found target to targets list
	 * @param createKey
	 *            whether to attempt to automatically generate key secret in
	 *            server config file
	 * @return
	 * @throws DetectionException
	 * @throws LicenseExpiredException
	 */
	public synchronized IZendTarget detectLocalhostTarget(String targetId,
			String key, boolean add, boolean createKey)
			throws DetectionException, LicenseExpiredException {
		return detectLocalhostTarget(targetId, key, null, add, createKey);
	}

	/**
	 * 
	 * @param targetId
	 *            target id to use
	 * @param key
	 *            key name to use
	 * @param add
	 *            whether to add found target to targets list
	 * @param createKey
	 *            whether to attempt to automatically generate key secret in
	 *            server config file
	 * @return
	 * @throws DetectionException
	 * @throws LicenseExpiredException
	 */
	public synchronized IZendTarget detectLocalhostTarget(String targetId,
			String key, String secretKey, boolean add, boolean createKey)
			throws DetectionException, LicenseExpiredException {

		if (targetId == null) {
			targetId = createUniqueId(null);
		}
		key = key != null ? key : DEFAULT_KEY + "." //$NON-NLS-1$
				+ System.getProperty("user.name"); //$NON-NLS-1$

		final IZendTarget existing = getExistingLocalhost();

		ZendTargetAutoDetect detection = getAutoDetector();

		String existingSecret = null;
		if (secretKey != null) {
			existingSecret = secretKey;
		} else {
			try {
				existingSecret = detection.findExistingSecretKey(key);
			} catch (IOException e) {
			}
		}

		// only return existing, if it's key still exists and is valid
		if ((existing != null) && (existingSecret != null)
				&& existingSecret.equals(existing.getSecretKey())) {
			return existing;
		}

		// if there's no key in server config and we don't want to create
		// automatic one, throw an error
		if ((existingSecret == null) && (!createKey)) {
			throw new DetectionException("Key entry '" + key + "' not found.");
		}
		IZendTarget local = null;
		try {
			// localhost not found - create one
			if (secretKey != null) {
				local = detection.createLocalhostTarget(targetId, key,
						secretKey);
			} else {
				local = detection.createLocalhostTarget(targetId, key);
			}

			if (add) {
				return add(local);
			} else {
				return local;
			}

		} catch (IOException e) {

			log.warning(e);
			throw new PrivilegesException(e.getMessage());

		} catch (TargetException e) {
			Throwable cause = e.getCause();
			if (cause instanceof WebApiException) {
				WebApiException webE = (WebApiException) cause;
				final ResponseCode responseCode = webE.getResponseCode();
				if (local != null
						&& (responseCode == null
								|| responseCode == ResponseCode.UNKNOWN_METHOD || responseCode == ResponseCode.PAGE_NOT_FOUND)) {
					// try to repeat for zs6
					try {
						local.connect(WebApiVersion.V1_3,
								ServerType.ZEND_SERVER);
						if (add) {
							return add(local, true);
						} else {
							return local;
						}
					} catch (WebApiException e1) {
						final ResponseCode rCode = e1.getResponseCode();
						int code = (rCode != null) ? rCode.getCode() : -1;
						final String message = e1.getMessage();
						throw new ServerVersionException(code, message);
					} catch (TargetException e1) {
						throw new DetectionException(e1);
					} catch (LicenseExpiredException e1) {
						throw e1;
					}
				}
				if (cause instanceof WebApiException) {
					int code = (responseCode != null) ? responseCode.getCode()
							: -1;
					final String message = webE.getMessage();

					throw new ServerVersionException(code, message);
				} else {
					throw new DetectionException(e);
				}
			}
			return null;
		}
	}

	public ZendTargetAutoDetect getAutoDetector() {
		return new ZendTargetAutoDetect();
	}

	public synchronized String applyKeyToLocalhost(String key, String secretKey)
			throws IOException {
		final ZendTargetAutoDetect detection = getAutoDetector();
		final String appliedSecretKey = detection
				.applySecretKey(key, secretKey);
		return appliedSecretKey;
	}

	private IZendTarget getExistingLocalhost() {
		final IZendTarget[] list = getTargets();
		for (IZendTarget t : list) {
			if (ZendTargetAutoDetect.localhost.getHost().equals(
					t.getHost().getHost())) {
				log.info(MessageFormat
						.format("Local server {0} has been already detected with id {1}. ",
								t.getHost(), t.getId()));
				return t;
			}
		}
		return null;
	}

	public synchronized IZendTarget[] getTargets() {
		return (IZendTarget[]) this.all
				.toArray(new ZendTarget[this.all.size()]);
	}

	/**
	 * Creates and adds new target based on provided parameters.
	 * 
	 * @param host
	 * @param key
	 * @param secretKey
	 * @return
	 * @throws LicenseExpiredException
	 */
	public IZendTarget createTarget(String host, String key, String secretKey)
			throws LicenseExpiredException {
		return createTarget(createUniqueId(null), host, key, secretKey);
	}

	/**
	 * @param targetId
	 * @param host
	 * @param key
	 * @param secretKey
	 * @return
	 * @throws LicenseExpiredException
	 */
	public IZendTarget createTarget(String targetId, String host, String key,
			String secretKey) throws LicenseExpiredException {
		return createTarget(targetId, host, key, secretKey, null);
	}

	/**
	 * Creates and adds new target based on provided parameters.
	 * 
	 * @param targetId
	 * @param host
	 * @param key
	 * @param secretKey
	 * @return
	 * @throws LicenseExpiredException
	 */
	public IZendTarget createTarget(String targetId, String host, String key,
			String secretKey, Properties extraProperties)
			throws LicenseExpiredException {
		try {
			final ZendTarget t = new ZendTarget(targetId, new URL(host), key,
					secretKey);

			if (extraProperties != null && !extraProperties.isEmpty()) {
				final Set<Entry<Object, Object>> entrySet = extraProperties
						.entrySet();
				for (Entry<Object, Object> entry : entrySet) {
					t.addProperty(entry.getKey().toString(), entry.getValue()
							.toString());
				}
			}

			IZendTarget target = add(t);
			if (target == null) {
				return null;
			}
			return target;
		} catch (MalformedURLException e) {
			log.error("Error adding Zend Target " + targetId);
			log.error("\tPossible error: " + e.getMessage());
		} catch (TargetException e) {
			log.error("Error adding Zend Target " + targetId);
			log.error("\tPossible error: " + e.getMessage());
		}
		return null;
	}

	private IZendTarget updateTarget(IZendTarget existing,
			IZendTarget newTarget, boolean suppressConnect)
			throws LicenseExpiredException {
		IZendTarget updated = updateTarget(existing.getId(), newTarget
				.getHost().toString(), newTarget.getDefaultServerURL()
				.toString(), newTarget.getKey(), newTarget.getSecretKey(),
				suppressConnect);
		ZendTarget updatedZT = (ZendTarget) updated;

		ZendTarget newZT = (ZendTarget) newTarget;
		String[] newZTKeys = newZT.getPropertiesKeys();

		for (String key : newZTKeys) {
			updatedZT.addProperty(key, newZT.getProperty(key));
		}

		return updated;
	}

	public IZendTarget updateTarget(String targetId, String host,
			String defaultServer, String key, String secretKey)
			throws LicenseExpiredException {
		return updateTarget(targetId, host, defaultServer, key, secretKey,
				false);
	}

	public IZendTarget updateTarget(String targetId, String host,
			String defaultServer, String key, String secretKey,
			boolean suppressConnect) throws LicenseExpiredException {
		ZendTarget target = (ZendTarget) getTargetById(targetId);
		if (target == null) {
			log.info("Target with id '" + targetId + "' does not exist.");
			return null;
		}
		try {
			if (host != null) {
				target.setHost(new URL(host));
			}
			if (defaultServer != null) {
				target.setDefaultServerURL(new URL(defaultServer));
			}
			if (key != null) {
				target.setKey(key);
			}
			if (secretKey != null) {
				target.setSecretKey(secretKey);
			}
			try {
				if (!suppressConnect
						&& !target.connect(WebApiVersion.V1_3,
								ServerType.ZEND_SERVER)) {
					return null;
				}
			} catch (WebApiException e) {
				try {
					if (!target.connect(WebApiVersion.UNKNOWN,
							ServerType.ZEND_SERVER)) {
						return null;
					}
				} catch (WebApiException ex) {
					if (!target.connect()) {
						return null;
					}
				}
			}
			IZendTarget updated = loader.update(target);
			if (updated != null) {
				statusChanged(new BasicStatus(StatusCode.UNKNOWN,
						"updated target", "updated target"));
			}
			return updated;
		} catch (MalformedURLException e) {
			log.error(e);
		} catch (WebApiException e) {
			log.error("Error during updating Zend Target with id '" + targetId
					+ "'");
			log.error("\tPossible error: " + e.getMessage());
		} catch (LicenseExpiredException e) {
			throw e;
		}
		return null;
	}

	public IZendTarget updateTarget(IZendTarget target, boolean suppressConnect) {
		try {
			if (!suppressConnect && !target.connect()) {
				return null;
			}
			IZendTarget updated = loader.update(target);
			if (updated != null) {
				IZendTarget old = getTargetById(updated.getId());
				int index = all.indexOf(old);
				all.remove(index);
				all.add(index, updated);
				statusChanged(new BasicStatus(StatusCode.UNKNOWN,
						"updated target", "updated target"));
			}
			return updated;
		} catch (WebApiException e) {
			log.error("Error during updating Zend Target with id '"
					+ target.getId() + "'");
			log.error("\tPossible error: " + e.getMessage());
		} catch (LicenseExpiredException e) {
			log.error("Error during updating Zend Target with id '"
					+ target.getId() + "'");
			log.error("\tPossible error: " + e.getMessage());
		}
		return null;
	}

	public IZendTarget updateTarget(IZendTarget target) {
		return updateTarget(target, false);
	}

	/**
	 * Check for conflicts and errors in new target
	 * 
	 * @param target
	 * @return
	 */
	private boolean validTarget(IZendTarget target) {
		if (target == null) {
			log.error(new IllegalArgumentException("Target cannot be null."));
			return false;
		}
		if (target.getId() == null) {
			log.error(new IllegalArgumentException(
					"Target is not valid. Target id cannot be null."));
			return false;
		}
		IZendTarget dupTarget = getTargetById(target.getId());
		if (dupTarget != null) {
			if (!dupTarget.isTemporary()) {
				log.error("Target with id '" + target.getId()
						+ "' already exists.");
				return false;
			} else {
				remove(dupTarget);
			}
		}
		return true;
	}

	/**
	 * @return the default target id if exists. null if no default is assigned
	 */
	public synchronized String getDefaultTargetId() {
		if (all.size() == 1) {
			return all.get(0).getId();
		}
		return defaultId;
	}

	/**
	 * Creates new target id unique in target manager.
	 * 
	 * @param prefix
	 *            Optional prefix for generated id. Might be null.
	 * 
	 * @return unique id.
	 */
	public String createUniqueId(String prefix) {
		if (prefix == null) {
			prefix = ""; //$NON-NLS-1$
		}

		int idgenerator = getTargets().length;
		String id;
		do {
			id = prefix + Integer.toString(idgenerator++);
		} while (!isIdAvailable(id));

		return id;
	}

	/**
	 * Allows to check if specified target is a phpcloud container.
	 * 
	 * @param target
	 * @return <code>true</code> if provided target is a phpcloud container;
	 *         otherwise return <code>false</code>
	 */
	public static boolean isPhpcloud(IZendTarget target) {
		return target != null && isPhpcloud(target.getHost().getHost());
	}

	/**
	 * Allows to check if specified host is a phpcloud container's host.
	 * 
	 * @param targetHost
	 * @return <code>true</code> if provided host is a phpcloud container's
	 *         host; otherwise return <code>false</code>
	 */
	public static boolean isPhpcloud(String targetHost) {
		return targetHost != null
				&& targetHost.contains(ZendDevCloud.DEVPASS_HOST);
	}

	/**
	 * Allows to check if specified target is an OpenShift container.
	 * 
	 * @param target
	 * @return <code>true</code> if provided target is an OpenShift container;
	 *         otherwise return <code>false</code>
	 */
	public static boolean isOpenShift(IZendTarget target) {
		return target != null && isOpenShift(target.getHost().getHost());
	}

	/**
	 * Allows to check if specified host is an OpenShift container's host.
	 * 
	 * @param targetHost
	 * @return <code>true</code> if provided host is an OpenShift container's
	 *         host; otherwise return <code>false</code>
	 */
	public static boolean isOpenShift(String targetHost) {
		return targetHost != null
				&& targetHost.contains(OpenShiftTarget.getLibraDomain());
	}

	/**
	 * Allows to check if specified target has exact Zend Server version.
	 * 
	 * @param target
	 * @return <code>true</code> version matches; otherwise return
	 *         <code>false</code>
	 */
	public static boolean checkExactVersion(IZendTarget target,
			ZendServerVersion expectedVersion) {
		if (target != null) {
			ZendServerVersion version = ZendServerVersion.byName(target
					.getProperty(IZendTarget.SERVER_VERSION));
			return version.compareTo(expectedVersion) == 0;
		}
		return false;
	}

	/**
	 * Allows to check if specified target has minimal accepted Zend Server
	 * version.
	 * 
	 * @param target
	 * @return <code>true</code> version matches; otherwise return
	 *         <code>false</code>
	 */
	public static boolean checkMinVersion(IZendTarget target,
			ZendServerVersion minVersion) {
		if (target != null) {
			ZendServerVersion version = ZendServerVersion.byName(target
					.getProperty(IZendTarget.SERVER_VERSION));
			return version.compareTo(minVersion) >= 0;
		}
		return false;
	}

	/**
	 * Allows to check if specified target has maximal accepted Zend Server
	 * version.
	 * 
	 * @param target
	 * @return <code>true</code> version matches; otherwise return
	 *         <code>false</code>
	 */
	public static boolean checkMaxVersion(IZendTarget target,
			ZendServerVersion maxVersion) {
		if (target != null) {
			ZendServerVersion version = ZendServerVersion.byName(target
					.getProperty(IZendTarget.SERVER_VERSION));
			return version.compareTo(maxVersion) <= 0;
		}
		return false;
	}

	public static boolean isLocalhost(IZendTarget target) {
		return target != null && isLocalhost(target.getHost().getHost());
	}

	public static boolean isLocalhost(String host) {
		if (host == null) {
			return false;
		}
		if ("localhost".equals(host) || "127.0.0.1".equals(host)) { //$NON-NLS-1$ //$NON-NLS-2$
			return true;
		}
		try {
			return InetAddress.getLocalHost().getHostAddress().equals(host);
		} catch (UnknownHostException e) {
			// skip and continue
		}
		return false;
	}

	protected void load() {
		final IZendTarget[] loadAll = loader.loadAll();
		for (IZendTarget zTarget : loadAll) {
			if (!validTarget(zTarget)) {
				log.error(new IllegalArgumentException(
						"Conflict found when adding " + zTarget.getId()));
			} else {
				this.all.add(zTarget);
			}
		}
		if (this.all.size() > 0) {
			defaultId = this.all.get(0).getId();
		}
	}

	private boolean isIdAvailable(String id) {
		IZendTarget[] targets = getTargets();
		for (IZendTarget target : targets) {
			String targetId = target.getId();
			if (targetId.equals(id) || targetId.startsWith(id + "_")) {
				return false;
			}
		}
		return true;
	}

}
