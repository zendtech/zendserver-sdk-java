package org.zend.php.zendserver.deployment.ui.notifications;

import java.util.Date;

import org.eclipse.mylyn.commons.notifications.ui.AbstractUiNotification;
import org.eclipse.swt.graphics.Image;

public class AdditionalEvent3Notification extends AbstractUiNotification {

	public AdditionalEvent3Notification() {
		super("org.zend.php.zendserver.deployment.ui.event3");
	}

	@Override
	public <T> T getAdapter(Class<T> arg0) {
		return null;
	}

	@Override
	public Image getNotificationImage() {
		return null;
	}

	@Override
	public Image getNotificationKindImage() {
		return null;
	}

	@Override
	public void open() {
	}

	@Override
	public Date getDate() {
		return new Date();
	}

	@Override
	public String getDescription() {
		return "Additional Event 3 Description";
	}

	@Override
	public String getLabel() {
		return "Additional Event 3";
	}

}
