package org.zend.php.zendserver.deployment.ui.notifications;

import java.util.Date;

import org.eclipse.mylyn.commons.notifications.ui.AbstractUiNotification;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class AdditionalEvent1Notification extends AbstractUiNotification {

	public AdditionalEvent1Notification() {
		super("org.zend.php.zendserver.deployment.ui.event1");
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
		//return Display.getDefault().getSystemImage(SWT.ICON_ERROR);
		return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
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
		return "Additional Event 1 Description";
	}

	@Override
	public String getLabel() {
		return "Additional Event 1";
	}

}
