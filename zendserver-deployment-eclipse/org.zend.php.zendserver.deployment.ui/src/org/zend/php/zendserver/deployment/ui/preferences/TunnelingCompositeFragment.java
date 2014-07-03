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
package org.zend.php.zendserver.deployment.ui.preferences;

import java.util.List;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.ui.wizards.IControlHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.zend.php.server.ui.fragments.AbstractCompositeFragment;
import org.zend.php.zendserver.deployment.core.tunnel.PortForwarding;
import org.zend.php.zendserver.deployment.core.tunnel.SSHTunnelConfiguration;
import org.zend.php.zendserver.deployment.ui.wizards.PortForwardingWizard;

/**
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public class TunnelingCompositeFragment extends AbstractCompositeFragment {

	public static String ID = "org.zend.php.zendserver.deployment.ui.preferences.TunnelingCompositeFragment"; //$NON-NLS-1$

	private Button enableButton;
	private Text usernameText;
	private Text passwordText;
	private Button browseButton;
	private Text privateKeyText;
	private Text proxyHostText;
	private Text proxyPortText;
	private Table table;
	private Button addButton;
	private Button editButton;
	private Button removeButton;

	private ModifyListener modifyListener = new ModifyListener() {

		public void modifyText(ModifyEvent e) {
			updateData();
			validate();
		}
	};

	private TableViewer portForwardingViewer;

	private SSHTunnelConfiguration config;

	public TunnelingCompositeFragment(Composite parent,
			IControlHandler handler, boolean isForEditing) {
		super(parent, handler, isForEditing,
				Messages.TunnelingCompositeFragment_Name,
				getTitle(isForEditing), getDescription(isForEditing));
		this.config = new SSHTunnelConfiguration();
		createControl(isForEditing);
	}

	/**
	 * Saves the page's state
	 */
	public void saveValues() {
		if (config.isEnabled()) {
			config.store(getServer());
		} else {
			SSHTunnelConfiguration.remove(getServer());
		}
	}

	public boolean performOk() {
		saveValues();
		return true;
	}

	public String getId() {
		return ID;
	}

	public void validate() {
		if (config.isEnabled()) {
			String username = config.getUsername();
			if (username == null || username.isEmpty()) {
				setMessage(
						Messages.TunnelingCompositeFragment_EmptyUsernameError,
						IMessageProvider.ERROR);
				return;
			}
			String password = config.getPassword();
			if (password == null || password.isEmpty()) {
				String privateKey = privateKeyText.getText();
				if (privateKey.isEmpty()) {
					setMessage(
							Messages.TunnelingCompositeFragment_NoPathNorPasswordError,
							IMessageProvider.ERROR);
					return;
				}
			}
			List<PortForwarding> portForwardings = config.getPortForwardings();
			if (portForwardings == null || portForwardings.isEmpty()) {
				setMessage(
						Messages.TunnelingCompositeFragment_NoForwardingError,
						IMessageProvider.ERROR);
				return;
			}
			String httpProxyPort = config.getHttpProxyPort();
			if (httpProxyPort != null && !httpProxyPort.isEmpty()) {
				try {
					Integer.valueOf(httpProxyPort);
				} catch (NumberFormatException e) {
					setMessage(
							Messages.TunnelingCompositeFragment_InvalidProxyPortError,
							IMessageProvider.ERROR);
					return;
				}
			}
		}
		setMessage(getDescription(), IMessageProvider.NONE);
	}

	@Override
	protected void createControl(Composite parent) {
		enableButton = new Button(parent, SWT.CHECK);
		enableButton.setText(Messages.TunnelingCompositeFragment_EnableLabel);
		enableButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				boolean enabled = enableButton.getSelection();
				updateState(enabled);
				updateData();
				validate();
			}
		});
		createCredentialsGroup(parent);
		createPortForwardingGroup(parent);
		createProxyGroup(parent);
	}

	@Override
	protected void init() {
		Server server = getServer();
		if (server != null) {
			SSHTunnelConfiguration currentConfig = SSHTunnelConfiguration
					.read(server);
			boolean enabled = currentConfig.isEnabled();
			enableButton.setSelection(enabled);
			updateState(enabled);
			String username = currentConfig.getUsername();
			if (username != null) {
				usernameText.setText(username);
			}
			String password = currentConfig.getPassword();
			if (password != null) {
				passwordText.setText(password);
			}
			String privateKey = currentConfig.getPrivateKey();
			if (privateKey != null) {
				privateKeyText.setText(privateKey);
			}
			config.setPortForwardings(currentConfig.getPortForwardings());
			portForwardingViewer.refresh();
			String proxyHost = currentConfig.getHttpProxyHost();
			if (proxyHost != null) {
				proxyHostText.setText(proxyHost);
			}
			String proxyPort = currentConfig.getHttpProxyPort();
			if (proxyPort != null) {
				proxyPortText.setText(proxyPort);
			}
		} else {
			enableButton.setSelection(false);
		}
		updateData();
		updateState(config.isEnabled());
		validate();
	}

	private void createCredentialsGroup(Composite parent) {
		Group credentialsGroup = createGroup(
				Messages.TunnelingCompositeFragment_CredentialsLabel, parent);
		usernameText = createText(
				Messages.TunnelingCompositeFragment_UsernameLabel,
				credentialsGroup);
		usernameText.forceFocus();
		passwordText = createPasswordText(
				Messages.TunnelingCompositeFragment_PasswordLabel,
				credentialsGroup);
		Label label = new Label(credentialsGroup, SWT.NONE);
		label.setText(Messages.TunnelingCompositeFragment_KeyLabel);
		privateKeyText = new Text(credentialsGroup, SWT.BORDER);
		privateKeyText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		privateKeyText
				.setToolTipText(Messages.TunnelingCompositeFragment_KeyTooltip);
		privateKeyText.addModifyListener(modifyListener);

		browseButton = new Button(credentialsGroup, SWT.PUSH);
		browseButton.setText(Messages.TunnelingCompositeFragment_BrowseLabel);
		browseButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false));
		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				final FileDialog d = new FileDialog(e.display.getActiveShell(),
						SWT.OPEN);
				final String file = d.open();
				if (file != null) {
					privateKeyText.setText(file);
					validate();
				}
			}
		});
	}

	private void createProxyGroup(Composite parent) {
		Group proxyGroup = createGroup(
				Messages.TunnelingCompositeFragment_ProxyLabel, parent);
		proxyHostText = createText(
				Messages.TunnelingCompositeFragment_ProxyHostLabel, proxyGroup);
		proxyPortText = createText(
				Messages.TunnelingCompositeFragment_ProxyPort, proxyGroup);
	}

	private void createPortForwardingGroup(Composite parent) {
		Group portForwardingGroup = createGroup(
				Messages.TunnelingCompositeFragment_ForwardingLabel, parent);
		portForwardingViewer = new TableViewer(portForwardingGroup);
		portForwardingViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				handleEdit(getSelection());
				portForwardingViewer.refresh();
				validate();
			}
		});
		portForwardingViewer
				.setContentProvider(new IStructuredContentProvider() {

					public void inputChanged(Viewer viewer, Object oldInput,
							Object newInput) {
					}

					public void dispose() {
					}

					public Object[] getElements(Object input) {
						if (input instanceof List<?>) {
							List<?> entries = (List<?>) input;
							return entries.toArray(new PortForwarding[entries
									.size()]);
						}
						if (input instanceof SSHTunnelConfiguration) {
							return ((SSHTunnelConfiguration) input)
									.getPortForwardings().toArray(
											new PortForwarding[0]);
						}
						return new PortForwarding[0];
					}

				});
		portForwardingViewer.setInput(config);

		table = portForwardingViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableViewerColumn columnViewer = new TableViewerColumn(
				portForwardingViewer, SWT.NONE);
		columnViewer.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((PortForwarding) element).getSide().getName();
			}
		});
		TableColumn column = columnViewer.getColumn();
		column.setWidth(50);
		column.setText(Messages.TunnelingCompositeFragment_SideLabel);

		columnViewer = new TableViewerColumn(portForwardingViewer, SWT.NONE);
		columnViewer.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((PortForwarding) element).getRemoteAddress();
			}
		});
		column = columnViewer.getColumn();
		column.setWidth(100);
		column.setText(Messages.TunnelingCompositeFragment_RemoteAddrLabel);

		columnViewer = new TableViewerColumn(portForwardingViewer, SWT.NONE);
		columnViewer.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return String.valueOf(((PortForwarding) element)
						.getRemotePort());
			}
		});
		column = columnViewer.getColumn();
		column.setWidth(100);
		column.setText(Messages.TunnelingCompositeFragment_RemotePortLabel);

		columnViewer = new TableViewerColumn(portForwardingViewer, SWT.NONE);
		columnViewer.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((PortForwarding) element).getLocalAddress();
			}
		});
		column = columnViewer.getColumn();
		column.setWidth(100);
		column.setText(Messages.TunnelingCompositeFragment_LocalAddrLabel);

		columnViewer = new TableViewerColumn(portForwardingViewer, SWT.NONE);
		columnViewer.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return String.valueOf(((PortForwarding) element).getLocalPort());
			}
		});
		column = columnViewer.getColumn();
		column.setWidth(100);
		column.setText(Messages.TunnelingCompositeFragment_LocalPortLabel);

		Composite buttonsContainer = new Composite(portForwardingGroup,
				SWT.NONE);
		buttonsContainer.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false,
				true));
		FillLayout layout = new FillLayout(SWT.VERTICAL);
		buttonsContainer.setLayout(layout);

		addButton = new Button(buttonsContainer, SWT.PUSH);
		addButton.setText(Messages.TunnelingCompositeFragment_AddLabel);
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleAdd();
				portForwardingViewer.refresh();
				validate();
			}
		});

		editButton = new Button(buttonsContainer, SWT.PUSH);
		editButton.setText(Messages.TunnelingCompositeFragment_EditLabel);
		editButton.setEnabled(false);
		editButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleEdit(getSelection());
				portForwardingViewer.setInput(config);
				validate();
			}
		});

		removeButton = new Button(buttonsContainer, SWT.PUSH);
		removeButton.setText(Messages.TunnelingCompositeFragment_RemoveLabel);
		removeButton.setEnabled(false);
		removeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleRemove(getSelection());
				portForwardingViewer.refresh();
				validate();
			}
		});
		table.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				boolean enabled = getSelection().length != 0;
				editButton.setEnabled(enabled);
				removeButton.setEnabled(enabled);
			}
		});
	}

	private void handleRemove(PortForwarding[] selection) {
		List<PortForwarding> portForwardings = config.getPortForwardings();
		for (PortForwarding entry : selection) {
			portForwardings.remove(entry);
		}
	}

	private void handleEdit(PortForwarding[] selection) {
		PortForwardingWizard wizard = new PortForwardingWizard(selection[0]);
		WizardDialog dialog = new WizardDialog(getShell(), wizard);
		if (dialog.open() == Window.OK) {
			PortForwarding portForwarding = wizard.getResult();
			List<PortForwarding> portForwardings = config.getPortForwardings();
			int index = portForwardings.indexOf(selection[0]);
			portForwardings.remove(selection[0]);
			portForwardings.add(index, portForwarding);
		}
	}

	private void handleAdd() {
		PortForwardingWizard wizard = new PortForwardingWizard();
		WizardDialog dialog = new WizardDialog(getShell(), wizard);
		if (dialog.open() == Window.OK) {
			PortForwarding portForwarding = wizard.getResult();
			List<PortForwarding> portForwardings = config.getPortForwardings();
			portForwardings.add(portForwarding);
		}
	}

	private PortForwarding[] getSelection() {
		IStructuredSelection selection = (IStructuredSelection) portForwardingViewer
				.getSelection();
		List<?> list = selection.toList();
		return list.toArray(new PortForwarding[list.size()]);
	}

	private Text createText(String name, Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(name);
		Text text = new Text(parent, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		text.addModifyListener(modifyListener);
		return text;
	}

	private Text createPasswordText(String name, Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(name);
		Text text = new Text(parent, SWT.BORDER | SWT.PASSWORD);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		text.addModifyListener(modifyListener);
		return text;
	}

	private Group createGroup(String name, Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		group.setText(name);
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3,
				1));
		group.setLayout(new GridLayout(3, false));
		return group;
	}

	private void updateData() {
		if (enableButton != null) {
			config.setEnabled(enableButton.getSelection());
		}
		if (usernameText != null) {
			String username = usernameText.getText();
			config.setUsername(username);
		}
		if (passwordText != null) {
			String password = passwordText.getText();
			config.setPassword(password);
		}
		if (privateKeyText != null) {
			String privateKey = privateKeyText.getText();
			config.setPrivateKey(privateKey);
		}
		if (proxyHostText != null) {
			String httpProxyHost = proxyHostText.getText();
			config.setHttpProxyHost(httpProxyHost);
		}
		if (proxyPortText != null) {
			String httpProxyPort = proxyPortText.getText();
			config.setHttpProxyPort(httpProxyPort);
		}
	}

	private void updateState(boolean enabled) {
		usernameText.setEnabled(enabled);
		passwordText.setEnabled(enabled);
		browseButton.setEnabled(enabled);
		privateKeyText.setEnabled(enabled);
		proxyHostText.setEnabled(enabled);
		proxyPortText.setEnabled(enabled);
		table.setEnabled(enabled);
		addButton.setEnabled(enabled);
		editButton.setEnabled(false);
		removeButton.setEnabled(false);
	}

	private static String getTitle(boolean isEditing) {
		return isEditing ? Messages.TunnelingCompositeFragment_EditTitle
				: Messages.TunnelingCompositeFragment_CreateTitle;
	}

	private static String getDescription(boolean isEditing) {
		return isEditing ? Messages.TunnelingCompositeFragment_DescEdit
				: Messages.TunnelingCompositeFragment_DescCreate;
	}

}
