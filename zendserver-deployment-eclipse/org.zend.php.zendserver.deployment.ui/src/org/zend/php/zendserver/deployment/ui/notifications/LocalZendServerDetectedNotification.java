package org.zend.php.zendserver.deployment.ui.notifications;

import java.util.Date;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.LegacyActionTools;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.commons.notifications.ui.AbstractUiNotification;
import org.eclipse.mylyn.internal.commons.notifications.ui.NotificationAction;
import org.eclipse.mylyn.internal.commons.notifications.ui.NotificationsPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.zend.php.zendserver.deployment.ui.AddLocalZendServerJob;
import org.zend.php.zendserver.deployment.ui.notifications.base.INotificationExtension;
import org.zend.php.zendserver.deployment.ui.notifications.base.NotificationHelper;

@SuppressWarnings("restriction")
public class LocalZendServerDetectedNotification extends AbstractUiNotification implements INotificationExtension {

	public static String ID = "org.zend.php.zendserver.deployment.ui.localZendServerDetection"; //$NON-NLS-1$

	private boolean isWebApiConfigured;

	public LocalZendServerDetectedNotification(boolean isWebApiConfigured) {
		super(ID);
		this.isWebApiConfigured = isWebApiConfigured;
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
		return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_INFO_TSK);
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
		String message = Messages.LocalZendServerDetectedNotification_ServerDetectedMessage;
		if (!isWebApiConfigured)
			message = Messages.LocalZendServerDetectedNotification_ServerDetectedWebApiMessage;
		return message;
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
		GridLayout gridLayout2 = new GridLayout(1, false);
		gridLayout2.marginHeight = 0;
		gridLayout2.marginWidth = 0;
		GridDataFactory.fillDefaults().span(2, SWT.DEFAULT).grab(true, false).align(SWT.FILL, SWT.TOP).applyTo(buttonsComposite);
		buttonsComposite.setLayout(gridLayout2);
		buttonsComposite.setBackground(parent.getBackground());
		
		Button okButton = new Button(buttonsComposite, SWT.NONE);
		okButton.setText(Messages.LocalZendServerDetectedNotification_Ok_Text);
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.TOP).applyTo(okButton);
		okButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Job performer = new AddLocalZendServerJob();
				performer.setUser(false);
				performer.setSystem(false);
				performer.schedule();
			}
		});
		Button doNotShowButton = new Button(buttonsComposite, SWT.CHECK);
		doNotShowButton.setText(Messages.LocalZendServerDetectedNotification_DoNotNotifyAgain_Text);
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.TOP).applyTo(doNotShowButton);
		doNotShowButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				NotificationAction notificationAction = NotificationHelper.getNotificationAction(ID);
				notificationAction.setSelected(!notificationAction.isSelected());
				NotificationsPlugin.getDefault().getModel().setDirty(true);
				NotificationsPlugin.getDefault().saveModel();
			}
		});
	}
}
