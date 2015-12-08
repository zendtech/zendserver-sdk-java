/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.debug.ui.wizards;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.zend.php.zendserver.deployment.debug.ui.listeners.IStatusChangeListener;

/**
 * @author Wojciech Galanciak, 2013
 * 
 */
public abstract class AbstractLibraryBlock {

	private IDialogSettings dialogSettings;

	protected IStatusChangeListener listener;

	protected AbstractLibraryBlock(IStatusChangeListener listener) {
		this.listener = listener;
	}

	public Composite createContents(final Composite parent,
			final boolean resizeShell) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		container.setLayout(layout);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		return container;
	}

	public abstract IStatus validatePage();

	public void setDialogSettings(IDialogSettings dialogSettings) {
		this.dialogSettings = dialogSettings;
	}

	protected IDialogSettings getDialogSettings() {
		return dialogSettings;
	}

	protected Label createLabelWithLabel(String labelText, String tooltip,
			Composite container) {
		Label label = new Label(container, SWT.NONE);
		label.setText(labelText);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		Label text = new Label(container, SWT.SINGLE);
		text.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				listener.statusChanged(validatePage());
			}
		});
		text.setToolTipText(tooltip);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return text;
	}

	protected Button createLabelWithCheckbox(String desc, String tooltip,
			Composite composite) {
		Button button = new Button(composite, SWT.CHECK);
		button.setText(desc);
		button.setToolTipText(tooltip);
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				listener.statusChanged(validatePage());
			}
		});
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		button.setLayoutData(gd);
		return button;
	}

	protected Text createLabelWithText(String labelText, String tooltip, Composite container, boolean required,
			int style) {
		return createLabelWithText(labelText, tooltip, container, required, style, true);
	}

	protected Text createLabelWithText(String labelText, String tooltip, Composite container, boolean required,
			int style, boolean addDefaultHandler) {
		Label label = new Label(container, SWT.NONE);
		if (required) {
			labelText += " * "; //$NON-NLS-1$
		}
		label.setText(labelText);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		Text text = new Text(container, SWT.BORDER | SWT.SINGLE | style);
		if (addDefaultHandler) {
			text.addKeyListener(new KeyAdapter() {
				public void keyReleased(KeyEvent e) {
					listener.statusChanged(validatePage());
				}
			});
		}
		text.setToolTipText(tooltip);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return text;
	}

}
