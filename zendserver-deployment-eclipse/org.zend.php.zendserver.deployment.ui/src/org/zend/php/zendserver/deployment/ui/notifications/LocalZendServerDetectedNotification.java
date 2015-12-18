package org.zend.php.zendserver.deployment.ui.notifications;

import java.util.Date;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.LegacyActionTools;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.commons.notifications.ui.AbstractUiNotification;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.zend.php.zendserver.deployment.ui.notifications.base.INotificationExtension;

@SuppressWarnings("restriction")
public class LocalZendServerDetectedNotification extends AbstractUiNotification implements INotificationExtension {

	public static String ID = "org.zend.php.zendserver.deployment.ui.localZendServerDetection"; //$NON-NLS-1$

	private String description;

	public LocalZendServerDetectedNotification() {
		super(ID);
	}

	@Override
	public <T> T getAdapter(Class<T> arg0) {
		return Platform.getAdapterManager().getAdapter(this, arg0);
	}

	@Override
	public Image getNotificationImage() {
		// JFaceResources.getImageRegistry().getDescriptor(Dialog.DLG_IMG_MESSAGE_ERROR);
		// return Display.getDefault().getSystemImage(SWT.ICON_QUESTION);
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
		return this.description;
	}

	@Override
	public String getLabel() {
		return "Local Zend Server Found";
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public void createContent(Composite parent) {
		Composite notificationComposite = new Composite(parent, SWT.NO_FOCUS);
		GridLayout gridLayout = new GridLayout(2, false);
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

		if (description != null && !description.trim().equals("")) { //$NON-NLS-1$
			Label descriptionLabel = new Label(notificationComposite, SWT.NO_FOCUS);
			descriptionLabel.setText(LegacyActionTools.escapeMnemonics(description));
			descriptionLabel.setBackground(parent.getBackground());
			GridDataFactory.fillDefaults().span(2, SWT.DEFAULT).grab(true, false).align(SWT.FILL, SWT.TOP).applyTo(descriptionLabel);
		}
		
		Composite buttonsComposite = new Composite(notificationComposite, SWT.NO_FOCUS);
		GridLayout gridLayout2 = new GridLayout(2, true);
		GridDataFactory.fillDefaults().span(2, SWT.DEFAULT).grab(true, false).align(SWT.FILL, SWT.TOP).applyTo(buttonsComposite);
		buttonsComposite.setLayout(gridLayout2);
		buttonsComposite.setBackground(parent.getBackground());
		
		Button okButton = new Button(buttonsComposite, SWT.NONE);
		okButton.setText("OK");
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.TOP).applyTo(okButton);
		Button cancelButton = new Button(buttonsComposite, SWT.NONE);
		cancelButton.setText("Cancel");
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.TOP).applyTo(cancelButton);
		Button doNotShowButton = new Button(buttonsComposite, SWT.CHECK);
		doNotShowButton.setText("Do not notify again");
		GridDataFactory.fillDefaults().span(2, SWT.DEFAULT).grab(true, false).align(SWT.FILL, SWT.TOP).applyTo(doNotShowButton);
	}
}
