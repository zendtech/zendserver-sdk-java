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

import java.io.File;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.server.core.manager.ServersManager;
import org.eclipse.php.internal.server.ui.ServerEditDialog;
import org.eclipse.php.internal.ui.wizards.CompositeFragment;
import org.eclipse.php.internal.ui.wizards.IControlHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.zend.php.server.internal.ui.Messages;
import org.zend.php.server.internal.ui.ServersUI;
import org.zend.php.server.ui.types.LocalApacheType;

/**
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public class LocalApacheCompositeFragment extends CompositeFragment {

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
		super(parent, handler, isForEditing);
		setDisplayName(Messages.LocalApacheCompositeFragment_Title);
		setTitle(Messages.LocalApacheCompositeFragment_Title);
		setDescription(Messages.LocalApacheCompositeFragment_Desc);

		controlHandler.setTitle(Messages.LocalApacheCompositeFragment_Title);
		controlHandler
				.setDescription(Messages.LocalApacheCompositeFragment_Desc);

		if (isForEditing) {
			setData(((ServerEditDialog) controlHandler).getServer());
		}
		createControl(isForEditing);
	}

	/**
	 * Override the super setData to handle only Server types.
	 * 
	 * @throws IllegalArgumentException
	 *             if the given object is not a {@link Server}
	 */
	public void setData(Object server) throws IllegalArgumentException {
		if (server != null && !(server instanceof Server)) {
			throw new IllegalArgumentException(""); //$NON-NLS-1$
		}
		super.setData(server);
	}

	/**
	 * Returns the Server that is attached to this fragment.
	 * 
	 * @return The attached Server.
	 */
	public Server getServer() {
		return (Server) getData();
	}

	/**
	 * Saves the page's state
	 */
	public void saveValues() {
		getServer().setName(name);
		getServer().setAttribute(LocalApacheType.LOCATION, location);
	}

	public boolean performOk() {
		try {
			saveValues();
			return true;
		} catch (Throwable e) {
			ServersUI.logError(e);
			return false;
		}
	}

	public String getId() {
		return ID;
	}

	public void validate() {
		if (name != null) {
			if (name.trim().isEmpty()) {
				setMessage(
						Messages.LocalApacheCompositeFragment_NameEmptyMessage,
						IMessageProvider.ERROR);
				return;
			}
			/*
			 * if (checkServerName(name)) { setMessage(
			 * Messages.LocalApacheCompositeFragment_NameConflictMessage,
			 * IMessageProvider.ERROR); return; }
			 */
		}
		if (location != null) {
			File binFile = new File(location, BIN);
			File confFile = new File(location, CONF);
			if (!binFile.exists() || !confFile.exists()) {
				setMessage(
						Messages.LocalApacheCompositeFragment_LocationInvalidMessage,
						IMessageProvider.ERROR);
				return;
			}
		}
		setMessage(getDescription(), IMessageProvider.NONE);
	}

	/**
	 * Create the page
	 */
	protected void createControl(boolean isForEditing) {
		// set layout for this composite (whole page)
		GridLayout pageLayout = new GridLayout();
		setLayout(pageLayout);

		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		composite.setLayout(new GridLayout(3, false));

		ModifyListener modifyListener = new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				updateData();
				validate();
			}
		};

		Label label = new Label(composite, SWT.NONE);
		label.setText(Messages.LocalApacheCompositeFragment_NameLabel);
		serverNameText = new Text(composite, SWT.BORDER);
		serverNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 2, 1));
		serverNameText.addModifyListener(modifyListener);

		label = new Label(composite, SWT.NONE);
		label.setText(Messages.LocalApacheCompositeFragment_LocationLabel);
		locationText = new Text(composite, SWT.BORDER);
		locationText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		locationText
				.setToolTipText(Messages.LocalApacheCompositeFragment_LocationTooltip);
		locationText.addModifyListener(modifyListener);

		browseButton = new Button(composite, SWT.BORDER);
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
		new Label(composite, SWT.NONE);
		Label examplePathLabel = new Label(composite, SWT.NONE);
		examplePathLabel.setText(getExamplePath());
		examplePathLabel.setForeground(Display.getDefault().getSystemColor(
				SWT.COLOR_DARK_GRAY));

		init();
	}

	protected void setMessage(String message, int type) {
		controlHandler.setMessage(message, type);
		setComplete(type != IMessageProvider.ERROR);
		controlHandler.update();
	}

	private boolean checkServerName(String name) {
		name = name.trim();
		if (name.equals(getServer().getName())) {
			return true;
		}
		Server[] allServers = ServersManager.getServers();
		if (allServers != null) {
			int size = allServers.length;
			for (int i = 0; i < size; i++) {
				Server server = allServers[i];
				if (name.equals(server.getName()))
					return false;
			}
		}
		return true;
	}

	private void init() {
		Server server = getServer();
		if (server != null) {
			serverNameText.setText(server.getName());
			String location = server.getAttribute(LocalApacheType.LOCATION, ""); //$NON-NLS-1$
			locationText.setText(location);
		}
		setTitle(Messages.LocalApacheCompositeFragment_Title);
		controlHandler.setTitle(getTitle());
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
