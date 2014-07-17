/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Zend Technologies Ltd. - initial API and implementation
 *******************************************************************************/
package org.zend.php.server.internal.ui.apache;

import java.text.MessageFormat;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.ui.wizards.IControlHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.zend.php.server.internal.ui.Messages;
import org.zend.php.server.ui.fragments.AbstractCompositeFragment;
import org.zend.php.server.ui.types.LocalApacheType;

/**
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public class LocalApacheCompositeFragment extends AbstractCompositeFragment {

	public static String ID = "org.zend.php.server.ui.apache.LocalApacheCompositeFragment"; //$NON-NLS-1$

	private static final String CONF = "conf"; //$NON-NLS-1$
	private static final String BIN = "bin"; //$NON-NLS-1$

	private Text serverNameText;
	private Text locationText;
	private Button browseButton;

	private String name;
	private String location;

	/**
	 * PlatformCompositeFragment constructor
	 * 
	 * @param parent
	 * @param handler
	 * @param isForEditing
	 */
	public LocalApacheCompositeFragment(Composite parent,
			IControlHandler handler, boolean isForEditing) {
		super(parent, handler, isForEditing,
				Messages.LocalApacheCompositeFragment_Name,
				Messages.LocalApacheCompositeFragment_Name,
				Messages.LocalApacheCompositeFragment_Desc);
		createControl(isForEditing);
	}

	/**
	 * Saves the page's state
	 */
	public void saveValues() {
		getServer().setName(name);
		getServer().setAttribute(LocalApacheType.LOCATION, location);
	}

	public boolean performOk() {
		saveValues();
		return true;
	}

	public String getId() {
		return ID;
	}

	public void validate() {
		if (name != null) {
			if (name.trim().isEmpty()) {
				setIncompleteMessage(Messages.LocalApacheCompositeFragment_NameEmptyMessage);
				return;
			}
			if (isDuplicateName(name)) {
				setMessage(
						Messages.LocalApacheCompositeFragment_NameConflictMessage,
						IMessageProvider.ERROR);
				return;
			}
		}
		if (location != null && !location.isEmpty()) {
			Server server = new Server();
			server.setAttribute(LocalApacheType.LOCATION, location);
			if (LocalApacheType.parseAttributes(server)) {
				Server conflictingServer = getConflictingServer(server);
				if (conflictingServer != null) {
					setMessage(
							MessageFormat.format(
									Messages.LocalApacheCompositeFragment_BaseUrlConflictError,
									conflictingServer.getName()),
							IMessageProvider.ERROR);
					return;
				}
			} else {
				setMessage(
						Messages.LocalApacheCompositeFragment_LocationInvalidMessage,
						IMessageProvider.ERROR);
				return;
			}
		} else {
			setIncompleteMessage("Specify an installation directory.");
			return;
		}
		setMessage(getDescription(), IMessageProvider.NONE);
	}

	@Override
	protected void createControl(Composite parent) {
		ModifyListener modifyListener = new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				updateData();
				validate();
			}
		};

		Label label = new Label(parent, SWT.NONE);
		label.setText(Messages.LocalApacheCompositeFragment_NameLabel);
		serverNameText = new Text(parent, SWT.BORDER);
		serverNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 2, 1));
		serverNameText.addModifyListener(modifyListener);
		serverNameText.forceFocus();

		label = new Label(parent, SWT.NONE);
		label.setText(Messages.LocalApacheCompositeFragment_LocationLabel);
		locationText = new Text(parent, SWT.BORDER);
		locationText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		locationText
				.setToolTipText(Messages.LocalApacheCompositeFragment_LocationTooltip);
		locationText.addModifyListener(modifyListener);

		browseButton = new Button(parent, SWT.PUSH);
		browseButton.setText(Messages.LocalApacheCompositeFragment_BrowseLabel);
		browseButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false));
		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(getShell());
				if (!"".equals(locationText.getText())) { //$NON-NLS-1$
					String initialDir = locationText.getText();
					dialog.setFilterPath(initialDir);
				}
				String result = dialog.open();
				if (result != null)
					locationText.setText(result.toString());
			}

		});
		new Label(parent, SWT.NONE);
		Label examplePathLabel = new Label(parent, SWT.NONE);
		examplePathLabel.setText(getExamplePath());
		examplePathLabel.setForeground(Display.getDefault().getSystemColor(
				SWT.COLOR_DARK_GRAY));
	}

	@Override
	protected void init() {
		Server server = getServer();
		if (server != null) {
			serverNameText.setText(server.getName());
			String location = server.getAttribute(LocalApacheType.LOCATION, ""); //$NON-NLS-1$
			locationText.setText(location);
		}
	}

	private void updateData() {
		name = serverNameText.getText();
		location = locationText.getText();
	}

	private String getExamplePath() {
		String os = Platform.getOS();
		if (Platform.OS_WIN32.equals(os)) {
			return Messages.LocalApacheCompositeFragment_ExamplePathWin32;
		}
		return Messages.LocalApacheCompositeFragment_ExamplePathUnix;
	}

}
