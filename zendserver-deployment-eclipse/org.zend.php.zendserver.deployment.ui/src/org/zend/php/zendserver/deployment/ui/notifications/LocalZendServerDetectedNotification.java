package org.zend.php.zendserver.deployment.ui.notifications;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.LegacyActionTools;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.commons.notifications.core.AbstractNotification;
import org.eclipse.mylyn.commons.notifications.core.NotificationSink;
import org.eclipse.mylyn.commons.notifications.core.NotificationSinkEvent;
import org.eclipse.mylyn.commons.notifications.ui.AbstractUiNotification;
import org.eclipse.mylyn.commons.ui.compatibility.CommonColors;
import org.eclipse.mylyn.commons.workbench.forms.ScalingHyperlink;
import org.eclipse.mylyn.internal.commons.notifications.ui.NotificationAction;
import org.eclipse.mylyn.internal.commons.notifications.ui.NotificationsPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.AddLocalZendServerJob;
import org.zend.php.zendserver.deployment.ui.notifications.base.INotificationExtension;
import org.zend.php.zendserver.deployment.ui.notifications.base.NotificationHelper;

@SuppressWarnings("restriction")
public class LocalZendServerDetectedNotification extends AbstractUiNotification implements INotificationExtension {

	public static String ID = "org.zend.php.zendserver.deployment.ui.localZendServerDetection"; //$NON-NLS-1$

	public LocalZendServerDetectedNotification() {
		super(ID);
	}

	@Override
	public <T> T getAdapter(Class<T> arg0) {
		return Platform.getAdapterManager().getAdapter(this, arg0);
	}

	@Override
	public Image getNotificationImage() {
		return null;
	}

	@Override
	public Image getNotificationKindImage() {
		return Activator.getDefault().getImage(Activator.IMAGE_ZEND_SERVER_ICON);
	}

	@Override
	public void open() {
		//do nothing
	}

	@Override
	public Date getDate() {
		return new Date();
	}

	@Override
	public String getDescription() {
		return Messages.LocalZendServerDetectedNotification_LocalZendServerFound_Message;
	}

	@Override
	public String getLabel() {
		return Messages.LocalZendServerDetectedNotification_LocalZendServerFound_Label;
	}

	@Override
	public void createContent(Composite parent) {
		Composite notificationComposite = new Composite(parent, SWT.NO_FOCUS);
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.TOP).applyTo(notificationComposite);
		notificationComposite.setLayout(gridLayout);
		notificationComposite.setBackground(parent.getBackground());

		final Label notificationLabelIcon = new Label(notificationComposite, SWT.NO_FOCUS);
		notificationLabelIcon.setBackground(parent.getBackground());
		notificationLabelIcon.setImage(getNotificationKindImage());

		final Label notificationLabel = new Label(notificationComposite, SWT.NO_FOCUS);
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.TOP).applyTo(notificationLabel);
		notificationLabel.setText(LegacyActionTools.escapeMnemonics(getLabel()));
		notificationLabel.setBackground(parent.getBackground());

		Label descriptionLabel = new Label(notificationComposite, SWT.NO_FOCUS);
		descriptionLabel.setText(LegacyActionTools.escapeMnemonics(getDescription()));
		descriptionLabel.setBackground(parent.getBackground());
		GridDataFactory.fillDefaults().span(2, SWT.DEFAULT).grab(true, false).align(SWT.FILL, SWT.TOP)
				.applyTo(descriptionLabel);
		
		Composite buttonsComposite = new Composite(notificationComposite, SWT.NO_FOCUS);
		GridLayout gridLayout2 = new GridLayout(2, false);
		gridLayout2.marginHeight = 0;
		gridLayout2.marginWidth = 0;
		GridDataFactory.fillDefaults().span(2, SWT.DEFAULT).grab(true, false).align(SWT.FILL, SWT.TOP).applyTo(buttonsComposite);
		buttonsComposite.setLayout(gridLayout2);
		buttonsComposite.setBackground(parent.getBackground());
		
		final ScalingHyperlink addServerLink = new ScalingHyperlink(buttonsComposite, SWT.BEGINNING | SWT.NO_FOCUS);
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.RIGHT, SWT.TOP).applyTo(addServerLink);
		addServerLink.setForeground(CommonColors.HYPERLINK_WIDGET);
		addServerLink.registerMouseTrackListener();
		addServerLink.setText(Messages.LocalZendServerDetectedNotification_AddServer_Text);
		addServerLink.setBackground(parent.getBackground());
		addServerLink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				Job performer = new AddLocalZendServerJob();
				performer.setUser(false);
				performer.setSystem(false);
				performer.schedule();
			}
		});

		final ScalingHyperlink doNotShowLink = new ScalingHyperlink(buttonsComposite, SWT.BEGINNING | SWT.NO_FOCUS);
		GridDataFactory.fillDefaults().grab(false, false).align(SWT.RIGHT, SWT.TOP).applyTo(doNotShowLink);
		doNotShowLink.setForeground(CommonColors.HYPERLINK_WIDGET);
		doNotShowLink.registerMouseTrackListener();
		doNotShowLink.setText(Messages.LocalZendServerDetectedNotification_DoNotNotifyAgain_Text);
		doNotShowLink.setBackground(parent.getBackground());
		doNotShowLink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				NotificationAction notificationAction = NotificationHelper.getNotificationAction(ID);
				notificationAction.setSelected(!notificationAction.isSelected());
				NotificationsPlugin.getDefault().getModel().setDirty(true);
				NotificationsPlugin.getDefault().saveModel();
				
				List<AbstractNotification> notifications = new ArrayList<AbstractNotification>();
				notifications.add(new LocalZendServerDetectionDisabledNotification());
				NotificationSinkEvent sinkEvent = new NotificationSinkEvent(notifications);
				NotificationSink sink = notificationAction.getSinkDescriptor().getSink();
				sink.notify(sinkEvent);
			}
		});
	}
}
