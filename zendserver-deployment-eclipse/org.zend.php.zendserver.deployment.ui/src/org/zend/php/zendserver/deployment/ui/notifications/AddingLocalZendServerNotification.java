package org.zend.php.zendserver.deployment.ui.notifications;

import java.util.Date;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.LegacyActionTools;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.commons.notifications.ui.AbstractUiNotification;
import org.eclipse.mylyn.commons.ui.compatibility.CommonColors;
import org.eclipse.mylyn.commons.workbench.forms.ScalingHyperlink;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.zend.php.server.ui.IHelpContextIds;
import org.zend.php.zendserver.deployment.ui.notifications.base.INotificationExtension;

@SuppressWarnings("restriction")
public class AddingLocalZendServerNotification extends AbstractUiNotification implements INotificationExtension {

	public enum NotificationTypes {
		INFORMATION,
		WARNING,
		ERROR
	}
	
	public static String ID = "org.zend.php.zendserver.deployment.ui.addingLocalZendServer"; //$NON-NLS-1$
	
	private String label;
	private String description;
	private NotificationTypes type;
	
	public AddingLocalZendServerNotification(NotificationTypes type) {
		super(ID);
		this.type = type;
	}

	@Override
	public <T> T getAdapter(Class<T> arg0) {
		return Platform.getAdapterManager().getAdapter(this, arg0);
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
		GridDataFactory.fillDefaults().span(2, SWT.DEFAULT).grab(true, false).align(SWT.FILL, SWT.TOP)
				.applyTo(descriptionLabel);
		descriptionLabel.setText(LegacyActionTools.escapeMnemonics(getDescription()));
		descriptionLabel.setBackground(parent.getBackground());
		
		ScalingHyperlink itemLink = new ScalingHyperlink(notificationComposite, SWT.BEGINNING
				| SWT.NO_FOCUS);
		GridDataFactory.fillDefaults().span(2, SWT.DEFAULT).grab(true, false).align(SWT.FILL, SWT.TOP).applyTo(itemLink);
		itemLink.setForeground(CommonColors.HYPERLINK_WIDGET);
		itemLink.registerMouseTrackListener();
		itemLink.setText(LegacyActionTools.escapeMnemonics(Messages.AddingLocalZendServerNotification_MoreAboutZendServer_LinkText));
		itemLink.setBackground(parent.getBackground());
		itemLink.setHref(IHelpContextIds.ZEND_SERVER);
		itemLink.addHyperlinkListener(new HyperlinkAdapter() {

			@Override
			public void linkActivated(HyperlinkEvent e) {
				org.eclipse.swt.program.Program.launch((String) e.getHref());
			}
		});
	}

	@Override
	public Image getNotificationImage() {
		return null;
	}

	@Override
	public Image getNotificationKindImage() {
		String imageId = ISharedImages.IMG_OBJS_INFO_TSK;
		switch(this.type) {
		case WARNING:
			imageId = ISharedImages.IMG_OBJS_WARN_TSK;
			break;
		case ERROR:
			imageId = ISharedImages.IMG_OBJS_ERROR_TSK;
			break;
		}
		return PlatformUI.getWorkbench().getSharedImages().getImage(imageId);
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
		if(description != null)
			return description;
		
		return ""; //$NON-NLS-1$
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public String getLabel() {
		if(label != null)
			return label;
		
		return ""; //$NON-NLS-1$
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
