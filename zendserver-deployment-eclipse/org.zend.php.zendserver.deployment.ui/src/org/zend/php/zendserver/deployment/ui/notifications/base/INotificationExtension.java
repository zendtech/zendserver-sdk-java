package org.zend.php.zendserver.deployment.ui.notifications.base;

import org.eclipse.swt.widgets.Composite;

/**
 * Notification extension which allows notification to provide its own content
 * within notification dialog.
 */
public interface INotificationExtension {

	/**
	 * Provides notification content.
	 * 
	 * <p>
	 * The <code>isSingle</code> parameter indicates whether notification is the
	 * only one notification displayed within the dialog. If so, notification
	 * should not display its icon and label.
	 * 
	 * @param parent
	 *            - composite parent for notification content
	 * @param isSingle
	 *            - if <code>true</code> the notification is only one displayed
	 *            within the dialog; <code>false</code> otherwise
	 */
	void createContent(Composite parent, boolean isSingle);
}
