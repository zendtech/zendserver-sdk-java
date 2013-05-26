/*******************************************************************************
 * Copyright (c) 2000, 2006 Zend Technologies LTD
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.zend.php.common.callout;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.php.internal.ui.util.ElementCreationProxy;
import org.zend.core.notifications.NotificationManager;
import org.zend.core.notifications.ui.NotificationSettings;
import org.zend.core.notifications.ui.NotificationType;
import org.zend.php.common.Activator;


/**
 *  
 */
public class CalloutManager {

	public static void initiateCalloutTesters() {
		String calloutExtensionName = "org.zend.php.common.callout"; //$NON-NLS-1$
		IConfigurationElement[] elements = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(calloutExtensionName);
		for (int i = 0; i < elements.length; i++) {
			IConfigurationElement element = elements[i];
			if (element.getName().equals("tester")) { //$NON-NLS-1$
				ElementCreationProxy ecProxy = new ElementCreationProxy(
						element, calloutExtensionName);
				Runnable starter = (Runnable) ecProxy.getObject();
				starter.run();
			}
		}
	}

	/**
	 * Show notification of specified type which contains a message with one
	 * link to help.
	 * 
	 * @param title
	 *            notification title
	 * @param message
	 *            notification message
	 * @param helpContextId
	 *            help context id
	 * @param delay
	 *            time in ms after which notification should be hidden
	 * @param type
	 *            notification type
	 * @param doNotShow
	 *            if <code>true</code> then "do not show" checkbox is added to
	 *            the notification
	 * @param calloutId
	 *            it is requited if doNotShow parameter is true
	 */
	public static void showMessageWithHelp(String title, String message,
			String helpContextId, int delay, NotificationType type,
			boolean doNotShow, String calloutId) {
		MessageWithHelpBody body = new MessageWithHelpBody(message,
				helpContextId);
		if (doNotShow && calloutId != null) {
			body.doNotShowCheckbox(true, calloutId);
		}
		NotificationSettings settings = new NotificationSettings();
		settings.setTitle(title).setType(NotificationType.INFO).setBody(body)
				.setBorder(true).setDelay(delay).setClosable(true)
				.setType(type);
		if (shouldShow(calloutId)) {
			NotificationManager.registerNotification(NotificationManager
					.createNotification(settings));
		}
	}

	/**
	 * Show warning notification which contains a message with one link to help.
	 * 
	 * @param title
	 *            notification title
	 * @param message
	 *            notification message
	 * @param helpContextId
	 *            help context id
	 * @param delay
	 *            time in ms after which notification should be hidden
	 */
	public static void showWarningWithHelp(String title, String message,
			String helpContextId, int delay) {
		showMessageWithHelp(title, message, helpContextId, delay,
				NotificationType.WARNING, false, null);
	}

	/**
	 * Show warning notification which contains a message with one link to help
	 * and "Do not show this message again" checkbox.
	 * 
	 * @param title
	 *            notification title
	 * @param message
	 *            notification message
	 * @param helpContextId
	 *            help context id
	 * @param delay
	 *            time in ms after which notification should be hidden
	 * @param calloutId
	 *            id which will be used as a preference key to store
	 *            "do not show again" checkbox selection
	 */
	public static void showWarningWithHelp(String title, String message,
			String helpContextId, int delay, String calloutId) {
		showMessageWithHelp(title, message, helpContextId, delay,
				NotificationType.WARNING, true, calloutId);
	}

	/**
	 * Show info notification which contains a message with one link to help.
	 * 
	 * @param title
	 *            notification title
	 * @param message
	 *            notification message
	 * @param helpContextId
	 *            help context id
	 * @param delay
	 *            time in ms after which notification should be hidden
	 */
	public static void showInfoWithHelp(String title, String message,
			String helpContextId, int delay) {
		showMessageWithHelp(title, message, helpContextId, delay,
				NotificationType.INFO, false, null);
	}

	/**
	 * Show info notification which contains a message with one link to help and
	 * "Do not show this message again" checkbox.
	 * 
	 * @param title
	 *            notification title
	 * @param message
	 *            notification message
	 * @param helpContextId
	 *            help context id
	 * @param delay
	 *            time in ms after which notification should be hidden
	 * @param calloutId
	 *            id which will be used as a preference key to store
	 *            "do not show again" checkbox selection
	 * 
	 */
	public static void showInfoWithHelp(String title, String message,
			String helpContextId, int delay, String calloutId) {
		showMessageWithHelp(title, message, helpContextId, delay,
				NotificationType.INFO, true, calloutId);
	}

	private static boolean shouldShow(String calloutId) {
		if (calloutId != null) {
			IPreferenceStore preferenceStore = Activator.getDefault()
					.getPreferenceStore();
			preferenceStore.setDefault(calloutId, true);
			return preferenceStore.getBoolean(calloutId);
		}
		return true;
	}

}
