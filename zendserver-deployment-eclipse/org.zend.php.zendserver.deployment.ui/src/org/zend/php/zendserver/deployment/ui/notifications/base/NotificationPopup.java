package org.zend.php.zendserver.deployment.ui.notifications.base;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.LegacyActionTools;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.mylyn.commons.notifications.core.AbstractNotification;
import org.eclipse.mylyn.commons.notifications.ui.AbstractUiNotification;
import org.eclipse.mylyn.commons.ui.compatibility.CommonColors;
import org.eclipse.mylyn.commons.workbench.AbstractWorkbenchNotificationPopup;
import org.eclipse.mylyn.commons.workbench.forms.ScalingHyperlink;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;

@SuppressWarnings("restriction")
public class NotificationPopup extends AbstractWorkbenchNotificationPopup {

	private static final int NUM_NOTIFICATIONS_TO_DISPLAY = 4;
	
	private boolean groupNotifications = true;

	private List<AbstractNotification> notifications = new ArrayList<AbstractNotification>();

	public NotificationPopup(Shell parent) {
		super(parent.getDisplay());
	}

	@Override
	protected void createContentArea(Composite parent) {
		if (notifications.size() == 1) {
			createSingleNotificationContentArea(parent);
			return;
		}
		createMultiNotificationsContentArea(parent);
	}

	public List<AbstractNotification> getNotifications() {
		return new ArrayList<AbstractNotification>(notifications);
	}

	public void setNotifications(List<AbstractNotification> notifications) {
		this.notifications.clear();
		this.notifications.addAll(notifications);
	}

	@Override
	protected Image getPopupShellImage(int maximumHeight) {
		if (notifications.size() == 1 && notifications.get(0) instanceof INotificationExtension
				&& notifications.get(0) instanceof AbstractUiNotification) {
			AbstractUiNotification notification = (AbstractUiNotification) notifications.get(0);
			return notification.getNotificationKindImage();
		}

		return super.getPopupShellImage(maximumHeight);
	}

	@Override
	protected String getPopupShellTitle() {
		if (notifications.size() == 1 && notifications.get(0) instanceof INotificationExtension) {
			AbstractNotification notification = notifications.get(0);
			return notification.getLabel();
		}

		return super.getPopupShellTitle();
	}

	protected void createSingleNotificationContentArea(Composite parent) {
		AbstractNotification notification = notifications.get(0);
		if (notification instanceof INotificationExtension) {
			INotificationExtension notificationExt = (INotificationExtension) notification;
			notificationExt.createContent(parent, true);
			return;
		}
		createMylynNotificationArea(notification, parent);
	}

	protected void createMultiNotificationsContentArea(Composite parent) {
		int count = 0;
		for (final AbstractNotification notification : notifications) {

			if (count < NUM_NOTIFICATIONS_TO_DISPLAY || !groupNotifications) {
				if (notification instanceof INotificationExtension) {
					INotificationExtension notificationExt = (INotificationExtension) notification;
					notificationExt.createContent(parent, false);
				} else {
					createMylynNotificationArea(notification, parent);
				}
			} else {
				createSummaryArea(parent, count);
				break;
			}
			count++;
		}
	}

	protected void createMylynNotificationArea(final AbstractNotification notification, Composite parent) {
		Composite notificationComposite = new Composite(parent, SWT.NO_FOCUS);
		GridLayout gridLayout = new GridLayout(2, false);
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.TOP).applyTo(notificationComposite);
		notificationComposite.setLayout(gridLayout);
		notificationComposite.setBackground(parent.getBackground());

		final Label notificationLabelIcon = new Label(notificationComposite, SWT.NO_FOCUS);
		notificationLabelIcon.setBackground(parent.getBackground());
		if (notification instanceof AbstractUiNotification) {
			notificationLabelIcon.setImage(((AbstractUiNotification) notification).getNotificationKindImage());
		}
		final ScalingHyperlink itemLink = new ScalingHyperlink(notificationComposite, SWT.BEGINNING | SWT.NO_FOCUS);
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.TOP).applyTo(itemLink);
		itemLink.setForeground(CommonColors.HYPERLINK_WIDGET);
		itemLink.registerMouseTrackListener();
		itemLink.setText(LegacyActionTools.escapeMnemonics(notification.getLabel()));
		if (notification instanceof AbstractUiNotification) {
			itemLink.setImage(((AbstractUiNotification) notification).getNotificationImage());
		}
		itemLink.setBackground(parent.getBackground());
		itemLink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				if (notification instanceof AbstractUiNotification) {
					((AbstractUiNotification) notification).open();
				}
				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				if (window != null) {
					Shell windowShell = window.getShell();
					if (windowShell != null) {
						if (windowShell.getMinimized()) {
							windowShell.setMinimized(false);
						}

						windowShell.open();
						windowShell.forceActive();
					}
				}
			}
		});

		String descriptionText = null;
		if (notification.getDescription() != null) {
			descriptionText = notification.getDescription();
		}
		if (descriptionText != null && !descriptionText.trim().equals("")) { //$NON-NLS-1$
			Label descriptionLabel = new Label(notificationComposite, SWT.NO_FOCUS);
			descriptionLabel.setText(LegacyActionTools.escapeMnemonics(descriptionText));
			descriptionLabel.setBackground(parent.getBackground());
			GridDataFactory.fillDefaults().span(2, SWT.DEFAULT).grab(true, false).align(SWT.FILL, SWT.TOP)
					.applyTo(descriptionLabel);
		}
	}

	protected void createSummaryArea(Composite parent, int count) {
		final NotificationPopup popup = this;

		int numNotificationsRemain = notifications.size() - count;
		ScalingHyperlink remainingLink = new ScalingHyperlink(parent, SWT.NO_FOCUS);
		remainingLink.setForeground(JFaceResources.getColorRegistry().get(JFacePreferences.HYPERLINK_COLOR));
		remainingLink.registerMouseTrackListener();
		remainingLink.setBackground(parent.getBackground());

		remainingLink.setText(NLS.bind("{0} more", numNotificationsRemain)); //$NON-NLS-1$
		GridDataFactory.fillDefaults().applyTo(remainingLink);
		remainingLink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {
						popup.getShell().dispose();
						popup.groupNotifications = false;
						popup.open();
					}
				});
			}
		});
	}
}
