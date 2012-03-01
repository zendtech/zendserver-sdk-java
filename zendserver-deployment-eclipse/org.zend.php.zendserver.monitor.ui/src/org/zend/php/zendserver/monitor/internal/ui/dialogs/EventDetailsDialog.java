/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.monitor.internal.ui.dialogs;

import java.io.File;
import java.io.IOException;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.zend.php.zendserver.monitor.core.EventSource;
import org.zend.php.zendserver.monitor.internal.ui.Activator;
import org.zend.php.zendserver.monitor.internal.ui.Messages;
import org.zend.sdklib.application.ZendCodeTracing;
import org.zend.sdklib.monitor.IZendIssue;

import com.zend.php.zendserver.ui.wizards.ImportEvent;

/**
 * Event details dialog. Provides basic details about event and available
 * actions.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class EventDetailsDialog extends TitleAreaDialog {

	/**
	 * Action type which can be performed for provided event.
	 */
	public enum ActionType {
		REPEAT,

		JUMP,

		TRACE;
	}

	private Text typeText;
	private Text descText;
	private Text sourceText;

	private String targetId;
	private String traceId;
	private EventSource eventSource;
	private IZendIssue zendIssue;

	private ActionType action;

	public EventDetailsDialog(Shell shell, IZendIssue zendIssue,
			String targetId, EventSource eventSource) {
		super(shell);
		this.zendIssue = zendIssue;
		this.targetId = targetId;
		this.eventSource = eventSource;
	}

	@Override
	public void create() {
		initialize();
		super.create();
		setTitle(Messages.EventDetailsDialog_Title);
		setMessage(Messages.EventDetailsDialog_Description);
	}

	/**
	 * Returns type of action which was chosen in event details dialog.
	 * 
	 * @return {@link ActionType}
	 */
	public ActionType getAction() {
		return action;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Control control = super.createDialogArea(parent);
		Composite composite = new Composite((Composite) control, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setLayout(new GridLayout(3, false));
		if (zendIssue != null) {
			typeText = createAttributeEntry(composite,
					Messages.EventDetailsDialog_TypeLabel);
			descText = createAttributeEntry(composite,
					Messages.EventDetailsDialog_DescriptionLabel);
			sourceText = createAttributeEntry(composite,
					Messages.EventDetailsDialog_SourceLabel);
		}
		createActionButtons(composite);
		initializeFields();
		return composite;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected boolean isResizable() {
		return false;
	}

	@Override
	protected void okPressed() {
		super.okPressed();
	}

	private void initialize() {
		if (zendIssue != null) {
			// TODO wait until this method will return valid group details
			// List<EventsGroupDetails> groups = zendIssue.getGroupDetails();
			// if (groups != null && groups.size() == 1) {
			// Event event = groups.get(0).getEvent();
			// this.event = event;
			// this.traceId = groups.get(0).getCodeTracing();
			// }
			this.traceId = "0.3171.1"; //$NON-NLS-1$
		}
	}

	private void initializeFields() {
		if (zendIssue != null) {
			setText(typeText, zendIssue.getIssue().getGeneralDetails()
					.getErrorType());
			setText(descText, zendIssue.getIssue().getGeneralDetails()
					.getErrorString());
			setText(sourceText, eventSource.getProjectRelativePath() + ":" //$NON-NLS-1$
					+ eventSource.getLine());
		}
	}

	private void setText(Text text, String value) {
		if (text != null && !text.isDisposed()) {
			text.setText(value);
		}
	}

	private void createActionButtons(Composite parent) {
		Group composite = new Group(parent, SWT.NONE);
		composite.setText(Messages.EventDetailsDialog_ActionsLabel);
		composite.setLayout(new GridLayout(3, true));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3,
				1));
		createRepeatButton(composite);
		createJumpButton(composite);
		createTraceButton(composite);
	}

	private void createTraceButton(Composite composite) {
		Button traceButton = createButton(composite,
				Messages.EventDetailsDialog_TraceTooltip,
				Activator.getImageDescriptor(Activator.TRACE));
		traceButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				if (traceId != null) {
					ZendCodeTracing tracing = new ZendCodeTracing(targetId);
					File codeTrace = tracing.get(traceId);
					if (codeTrace != null) {
						ImportEvent event = new ImportEvent();
						try {
							event.importEvent(codeTrace.getAbsolutePath());
						} catch (IOException e) {
							Activator.log(e);
						}
						action = ActionType.TRACE;
						EventDetailsDialog.super.okPressed();
					}
				} else {
					// Show message that code trace is unavailable
				}
			}
		});
	}

	private void createJumpButton(Composite composite) {
		Button jumpButton = createButton(composite,
				Messages.EventDetailsDialog_JumpTooltip,
				Activator.getImageDescriptor(Activator.JUMP_TO));
		jumpButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				if (zendIssue != null) {
					OpenInEditorJob job = new OpenInEditorJob(eventSource);
					job.setUser(true);
					job.schedule();
					action = ActionType.JUMP;
					EventDetailsDialog.super.okPressed();
				} else {
					// Show message that code trace is unavailable
				}
			}
		});
	}

	private void createRepeatButton(Composite composite) {
		Button repeatButton = createButton(composite,
				Messages.EventDetailsDialog_6,
				Activator.getImageDescriptor(Activator.REFRESH));
		repeatButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				// TODO handle repeat action
				action = ActionType.REPEAT;
				EventDetailsDialog.super.okPressed();
			}
		});
	}

	private Text createAttributeEntry(Composite composite, String name) {
		Label label = new Label(composite, SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		label.setText(name);
		Text text = new Text(composite, SWT.READ_ONLY | SWT.WRAP);
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		text.setBackground(composite.getBackground());
		return text;
	}

	private Button createButton(Composite composite, String text,
			ImageDescriptor image) {
		Button button = new Button(composite, SWT.PUSH);
		button.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true,
				1, 1));
		button.setToolTipText(text);
		button.setImage(image.createImage());
		button.setSize(80, 80);
		return button;
	}

}