package org.zend.php.zendserver.deployment.ui.notifications;

import org.eclipse.jface.action.LegacyActionTools;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.mylyn.commons.ui.compatibility.CommonColors;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.notifications.base.AbstractExtendedUiNotification;

public class LocalZendServerDetectionDisabledNotification extends AbstractExtendedUiNotification {

	public static String ID = "org.zend.php.zendserver.deployment.ui.localZendServerDetectionDisabled"; //$NON-NLS-1$
	
	public LocalZendServerDetectionDisabledNotification() {
		super(ID);
	}

	@Override
	protected void createBody(Composite parent, boolean isSingle) {
		Link descriptionLabel = new Link(parent, SWT.NO_FOCUS | SWT.WRAP);
		descriptionLabel.setText(LegacyActionTools.escapeMnemonics(getDescription()));
		descriptionLabel.setForeground(CommonColors.HYPERLINK_WIDGET);
		descriptionLabel.setBackground(parent.getBackground());
		GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.END)
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
	public Image getNotificationKindImage() {
		return Activator.getDefault().getImage(Activator.IMAGE_ZEND_SERVER_ICON);
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
