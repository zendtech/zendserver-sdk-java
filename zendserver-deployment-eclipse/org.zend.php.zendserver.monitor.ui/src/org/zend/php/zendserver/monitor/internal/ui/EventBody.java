/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.monitor.internal.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.zend.core.notifications.ui.IActionListener;
import org.zend.core.notifications.ui.IBody;
import org.zend.core.notifications.util.FontCache;
import org.zend.php.zendserver.monitor.core.EventSource;
import org.zend.php.zendserver.monitor.internal.ui.dialogs.EventDetailsDialog;
import org.zend.sdklib.monitor.IZendIssue;

/**
 * Implementation of {@link IBody} for Zend Server event notification.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class EventBody implements IBody {

	private String application;
	private IZendIssue zendIssue;
	private String targetId;
	private EventSource eventSource;

	public EventBody(String targetId, EventSource eventSource,
			IZendIssue zendIssue, String application) {
		this.application = application;
		this.zendIssue = zendIssue;
		this.targetId = targetId;
		this.eventSource = eventSource;
	}

	@Override
	public void createContent(Composite container) {
		Composite composite = createEntryComposite(container);
		createLabel(composite, Messages.EventBody_ApplicationLabel);
		createText(composite, application);
		createLabel(composite, Messages.EventBody_TypeLabel);
		createText(composite, zendIssue.getIssue().getRule());
		createDoubleClickLabel(container);
	}

	@Override
	public void addActionListener(IActionListener listener) {
		// do not use listener
	}

	private void createDoubleClickLabel(Composite container) {
		Label doubleClickLabel = new Label(container, SWT.CENTER);
		doubleClickLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 3, 1));
		Font f = doubleClickLabel.getFont();
		FontData fd = f.getFontData()[0];
		fd.height = 12;
		doubleClickLabel.setFont(FontCache.getFont(fd));
		doubleClickLabel.setText(Messages.EventBody_DoubleClickLabel);
		doubleClickLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				IWorkbenchWindow window = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow();
				if (window != null) {
					EventDetailsDialog dialog = new EventDetailsDialog(window
							.getShell(), zendIssue, targetId, eventSource);
					dialog.create();
					dialog.open();
				}
			}
		});
	}

	private Composite createEntryComposite(Composite container) {
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3,
				1));
		return composite;
	}

	private void createText(Composite parent, String text) {
		if (text != null) {
			Label textLabel = new Label(parent, SWT.NONE);
			Font textFont = textLabel.getFont();
			FontData textFontData = textFont.getFontData()[0];
			textFontData.height = 11;
			textFontData.setStyle(SWT.BOLD);
			textLabel.setFont(FontCache.getFont(textFontData));
			textLabel.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false,
					true));
			textLabel.setForeground(Display.getDefault().getSystemColor(
					SWT.COLOR_BLACK));
			textLabel.setText(text);
		}
	}

	private void createLabel(Composite parent, String text) {
		Label label = new Label(parent, SWT.NONE);
		Font labelFont = label.getFont();
		FontData labelFontData = labelFont.getFontData()[0];
		labelFontData.height = 11;
		label.setFont(FontCache.getFont(labelFontData));
		label.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true));
		label.setForeground(Display.getDefault()
				.getSystemColor(SWT.COLOR_BLACK));
		label.setText(text);
	}

}
