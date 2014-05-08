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
package org.zend.php.server.internal.ui.zendserver;

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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.zend.php.server.internal.ui.ServersUI;
import org.zend.php.server.ui.types.LocalApacheType;

import com.zend.php.zendserver.utils.ZendServerManager;

/**
 * @author Wojciech Galanciak, 2014
 *
 */
public class LocalZendServerCompositeFragment extends CompositeFragment {

	public static String ID = "org.zend.php.server.ui.apache.LocalZendServerCompositeFragment"; //$NON-NLS-1$

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
	public LocalZendServerCompositeFragment(Composite parent,
			IControlHandler handler, boolean isForEditing) {
		super(parent, handler, isForEditing);
		setDisplayName("Local Zend Server");
		setTitle("Local Zend Server");
		setDescription("Local Zend Server Configuration");

		controlHandler.setDescription("Local Zend Server Configuration");
		controlHandler.setTitle("Local Zend Server");
		// controlHandler.setImageDescriptor());
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
			throw new IllegalArgumentException("");
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
				controlHandler.setMessage("Server name cannot be empty.",
						IMessageProvider.ERROR);
				return;

			}
		}
		controlHandler.setMessage(getDescription(), IMessageProvider.NONE);
	}

	@Override
	public boolean isComplete() {
		return true;
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
		label.setText("Server Name:");
		serverNameText = new Text(composite, SWT.BORDER);
		serverNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 2, 1));
		serverNameText
				.setToolTipText("Location where Apache HTTP Server is installed.");
		serverNameText.addModifyListener(modifyListener);

		label = new Label(composite, SWT.NONE);
		label.setText("Apache2 Location:");
		locationText = new Text(composite, SWT.BORDER);
		locationText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		locationText
				.setToolTipText("Location where Apache HTTP Server is installed.");
		locationText.addModifyListener(modifyListener);

		browseButton = new Button(composite, SWT.BORDER);
		browseButton.setText("Browse...");
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
		} else {
			Server localZs = ZendServerManager.getInstance()
					.getLocalZendServer();
			System.out.println(localZs);
		}
		setTitle("Local Zend Server");
		controlHandler.setTitle(getTitle());
	}

	private void updateData() {
		name = serverNameText.getText();
		location = locationText.getText();
	}

}
