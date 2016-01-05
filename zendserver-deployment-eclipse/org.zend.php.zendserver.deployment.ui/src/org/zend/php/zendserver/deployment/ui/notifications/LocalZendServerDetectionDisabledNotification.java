package org.zend.php.zendserver.deployment.ui.notifications;

import java.util.Date;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.LegacyActionTools;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.mylyn.commons.notifications.ui.AbstractUiNotification;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.notifications.base.INotificationExtension;

@SuppressWarnings("restriction")
public class LocalZendServerDetectionDisabledNotification extends AbstractUiNotification
		implements INotificationExtension {

	public static String ID = "org.zend.php.zendserver.deployment.ui.localZendServerDetectionDisabled"; //$NON-NLS-1$
	
	public LocalZendServerDetectionDisabledNotification() {
		super(ID);
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		return Platform.getAdapterManager().getAdapter(this, adapter);
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

		Link descriptionLabel = new Link(notificationComposite, SWT.NO_FOCUS);
		descriptionLabel.setText(LegacyActionTools.escapeMnemonics(getDescription()));
		descriptionLabel.setBackground(parent.getBackground());
		GridDataFactory.fillDefaults().span(2, SWT.DEFAULT).grab(true, false).align(SWT.FILL, SWT.TOP)
				.applyTo(descriptionLabel);
		descriptionLabel.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(shell,
						"org.eclipse.mylyn.commons.notifications.preferencePages.Notifications", null, null); //$NON-NLS-1$
				dialog.open();
			}
			
		});
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
		return Messages.LocalZendServerDetectionDisabledNotification_DetectionDisabled_Message;
	}

	@Override
	public String getLabel() {
		return Messages.LocalZendServerDetectionDisabledNotification_DetectionDisabled_Label;
	}

}
