package org.zend.php.zendserver.deployment.ui.notifications.base;

import java.util.Date;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.LegacyActionTools;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.mylyn.commons.notifications.ui.AbstractUiNotification;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

@SuppressWarnings("restriction")
public abstract class AbstractExtendedUiNotification extends AbstractUiNotification implements INotificationExtension {

	public AbstractExtendedUiNotification(String eventId) {
		super(eventId);
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	@Override
	public void createContent(Composite parent, boolean isSingle) {
		Composite notificationComposite = new Composite(parent, SWT.NO_FOCUS);
		GridLayoutFactory.fillDefaults().numColumns(1).margins(0, 0).applyTo(notificationComposite);
		GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).applyTo(notificationComposite);
		notificationComposite.setBackground(parent.getBackground());

		Composite headerComposite = new Composite(notificationComposite, SWT.NO_FOCUS);
		GridLayoutFactory.fillDefaults().numColumns(1).margins(0, 0).applyTo(headerComposite);
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING).applyTo(headerComposite);
		headerComposite.setBackground(parent.getBackground());
		createHeader(headerComposite, isSingle);
		
		Composite bodyComposite = new Composite(notificationComposite, SWT.NO_FOCUS);
		GridLayoutFactory.fillDefaults().numColumns(1).margins(0, 0).applyTo(bodyComposite);
		GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).applyTo(bodyComposite);
		bodyComposite.setBackground(parent.getBackground());
		createBody(bodyComposite, isSingle);
	}

	@Override
	public Image getNotificationImage() {
		//not used
		return null;
	}

	@Override
	public void open() {
		//does nothing
	}

	@Override
	public Date getDate() {
		return new Date();
	}
	
	@Override
	abstract public Image getNotificationKindImage();

	@Override
	abstract public String getDescription();

	@Override
	abstract public String getLabel();
	
	protected void createHeader(Composite parent, boolean isSingle) {
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).margins(0, 0).applyTo(parent);
		
		final Label notificationLabelIcon = new Label(parent, SWT.NO_FOCUS);
		GridDataFactory.fillDefaults().exclude(isSingle).applyTo(notificationLabelIcon);
		notificationLabelIcon.setImage(getNotificationKindImage());
		notificationLabelIcon.setBackground(parent.getBackground());

		final Label notificationLabel = new Label(parent, SWT.NO_FOCUS);
		GridDataFactory.fillDefaults().exclude(isSingle).grab(true, false).align(SWT.FILL, SWT.CENTER).applyTo(notificationLabel);
		notificationLabel.setText(LegacyActionTools.escapeMnemonics(getLabel()));
		notificationLabel.setBackground(parent.getBackground());
	}
	
	protected void createBody(Composite parent, boolean isSingle) {
		Label descriptionLabel = new Label(parent, SWT.NO_FOCUS | SWT.WRAP);
		GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.END)
				.applyTo(descriptionLabel);
		descriptionLabel.setText(LegacyActionTools.escapeMnemonics(getDescription()));
		descriptionLabel.setBackground(parent.getBackground());
	}
}
