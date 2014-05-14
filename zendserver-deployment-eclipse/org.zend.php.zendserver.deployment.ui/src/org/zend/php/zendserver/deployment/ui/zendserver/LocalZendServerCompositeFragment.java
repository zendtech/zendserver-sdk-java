/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies.
 * All rights reserved. This program and the accompanying materials
 * are the copyright of Zend Technologies and is protected under
 * copyright laws of the United States.
 * You must not copy, adapt or redistribute this document for 
 * any use. 
 *
 *******************************************************************************/
package org.zend.php.zendserver.deployment.ui.zendserver;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.ui.wizards.IControlHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.zend.php.server.ui.fragments.AbstractCompositeFragment;
import org.zend.php.zendserver.deployment.core.targets.ZendServerManager;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.preferences.LocalTargetDetector;

/**
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public class LocalZendServerCompositeFragment extends AbstractCompositeFragment {

	public static String ID = "org.zend.php.zendserver.deployment.ui.zendserver.LocalZendServerCompositeFragment"; //$NON-NLS-1$

	private Text serverNameText;

	private String name;

	public LocalZendServerCompositeFragment(Composite parent,
			IControlHandler handler, boolean isForEditing) {
		super(parent, handler, isForEditing,
				Messages.LocalZendServerCompositeFragment_Name,
				Messages.LocalZendServerCompositeFragment_Desc);
	}

	/**
	 * Saves the page's state
	 */
	public void saveValues() {
		getServer().setName(name);
	}

	public boolean performOk() {
		try {
			saveValues();
			final Server server = ZendServerManager.getInstance()
					.getLocalZendServer(getServer());
			if (server != null) {
				String location = server.getAttribute(
						ZendServerManager.ZENDSERVER_INSTALL_LOCATION, null);
				if (location == null || location.isEmpty()) {
					setMessage(
							Messages.LocalZendServerCompositeFragment_CannotDetectError,
							IMessageProvider.ERROR);
					return false;
				}
			}
			// TODO do not launch it when not finished (back to previous page)
			controlHandler.run(true, true, new IRunnableWithProgress() {

				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					monitor.beginTask(
							Messages.LocalZendServerCompositeFragment_DetectTitle,
							IProgressMonitor.UNKNOWN);

					LocalTargetDetector detector = new LocalTargetDetector(
							server);
					detector.detect();
					if (detector.getStatus().getSeverity() != IStatus.OK) {
						
					}
				}
			});

			return true;
		} catch (Throwable e) {
			Activator.log(e);
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
						Messages.LocalZendServerCompositeFragment_EmptyNameError,
						IMessageProvider.ERROR);
				return;

			}
		}
		setMessage(getDescription(), IMessageProvider.NONE);
	}

	@Override
	public boolean isComplete() {
		return true;
	}

	@Override
	protected void createControl(Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(Messages.LocalZendServerCompositeFragment_NameLabel);
		serverNameText = new Text(parent, SWT.BORDER);
		serverNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 2, 1));
		serverNameText
				.setToolTipText(Messages.LocalZendServerCompositeFragment_NameTooltip);
		serverNameText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				updateData();
				validate();
			}
		});
	}

	@Override
	protected void init() {
		Server server = getServer();
		if (server != null) {
			serverNameText.setText(server.getName());
		}
		setTitle(Messages.LocalZendServerCompositeFragment_Name);
		controlHandler.setTitle(getTitle());
	}

	private void updateData() {
		name = serverNameText.getText();
	}

}
