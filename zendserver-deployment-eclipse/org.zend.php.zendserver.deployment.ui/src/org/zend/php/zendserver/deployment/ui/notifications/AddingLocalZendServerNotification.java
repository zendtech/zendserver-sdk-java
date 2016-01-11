package org.zend.php.zendserver.deployment.ui.notifications;

import org.eclipse.jface.action.LegacyActionTools;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.mylyn.commons.workbench.forms.ScalingHyperlink;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.zend.php.server.ui.IHelpContextIds;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.notifications.base.AbstractExtendedUiNotification;

public class AddingLocalZendServerNotification extends AbstractExtendedUiNotification {

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
	public void createBody(Composite parent, boolean isSingle) {
		super.createBody(parent, isSingle);
		
		ScalingHyperlink itemLink = new ScalingHyperlink(parent, SWT.BEGINNING
				| SWT.NO_FOCUS);
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).applyTo(itemLink);
		itemLink.setForeground(JFaceResources.getColorRegistry().get(JFacePreferences.HYPERLINK_COLOR));
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
	public Image getNotificationKindImage() {
		switch(this.type) {
		case WARNING:
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_WARN_TSK);
		case ERROR:
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
		}
		return Activator.getDefault().getImage(Activator.IMAGE_ZEND_SERVER_ICON);
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
