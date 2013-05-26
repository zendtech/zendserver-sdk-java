/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies.
 * All rights reserved. This program and the accompanying materials
 * are the copyright of Zend Technologies and is protected under
 * copyright laws of the United States.
 * You must not copy, adapt or redistribute this document for 
 * any use. 
 *
 *******************************************************************************/
package org.zend.php.common.callout;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PlatformUI;
import org.zend.core.notifications.ui.IActionListener;
import org.zend.core.notifications.ui.IBody;
import org.zend.core.notifications.ui.NotificationSettings;
import org.zend.core.notifications.util.FontName;
import org.zend.core.notifications.util.Fonts;
import org.zend.php.common.Activator;


public class MessageWithHelpBody implements IBody {

	private String message;
	private String helpContextId;
	private String calloutId;
	private boolean doNotShow;

	public MessageWithHelpBody(String message, String helpContextId) {
		super();
		this.message = message;
		this.helpContextId = helpContextId;
	}

	public Composite createContent(Composite container,
			NotificationSettings settings) {
		Composite composite = new Composite(container, SWT.NONE);
		GridLayout layout = new GridLayout(1, true);
		layout.horizontalSpacing = layout.verticalSpacing = 2;
		composite.setLayout(layout);
		Link text = new Link(composite, SWT.WRAP);
		text.setFont(Fonts.get(FontName.DEFAULT));
		text.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, true));
		text.setText(message);
		if (helpContextId != null) {
			text.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent selectionEvent) {
					PlatformUI.getWorkbench().getHelpSystem()
							.displayHelp(helpContextId);
				}
			});
		}
		if (doNotShow) {
			createDoNotShowCheckbox(composite);
		}
		return composite;
	}

	public void doNotShowCheckbox(boolean enabled, String calloutId) {
		this.doNotShow = enabled;
		this.calloutId = calloutId;
	}

	public void addActionListener(IActionListener listener) {
		// do nothing
	}

	public void addMenuItems(Menu menu) {
	}

	private void createDoNotShowCheckbox(Composite parent) {
		final Button doNotShowCheckbox = new Button(parent, SWT.CHECK);
		doNotShowCheckbox.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				IPreferenceStore preferenceStore = Activator.getDefault()
						.getPreferenceStore();
				preferenceStore.setValue(calloutId,
						!doNotShowCheckbox.getSelection());
			}
		});
		doNotShowCheckbox.setText(Messages.MessageWithHelpBody_0);
	}

}
