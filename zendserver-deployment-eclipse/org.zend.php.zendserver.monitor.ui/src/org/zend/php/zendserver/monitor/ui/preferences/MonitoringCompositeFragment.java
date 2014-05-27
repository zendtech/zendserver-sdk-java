/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies.
 * All rights reserved. This program and the accompanying materials
 * are the copyright of Zend Technologies and is protected under
 * copyright laws of the United States.
 * You must not copy, adapt or redistribute this document for 
 * any use.
 *******************************************************************************/
package org.zend.php.zendserver.monitor.ui.preferences;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.ui.wizards.IControlHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.prefs.BackingStoreException;
import org.zend.php.server.ui.fragments.AbstractCompositeFragment;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.monitor.core.MonitorManager;
import org.zend.php.zendserver.monitor.internal.ui.Activator;
import org.zend.php.zendserver.monitor.internal.ui.Messages;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;
import org.zend.webapi.core.connection.data.values.IssueSeverity;

/**
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public class MonitoringCompositeFragment extends AbstractCompositeFragment {

	private class URLInputValidator implements IInputValidator {

		public String isValid(String newText) {
			try {
				new URL(newText);
			} catch (MalformedURLException e) {
				return Messages.MonitoringCompositeFragment_InvalidUrlMessage;
			}
			return null;
		}

	}

	public static String ID = "org.zend.php.zendserver.monitor.ui.preferences.MonitoringCompositeFragment"; //$NON-NLS-1$

	private static final String EMPTY_STRING = ""; //$NON-NLS-1$

	private TableViewer viewer;
	private Button removeButton;
	private Button modifyButton;
	private Button addButton;
	private Button[] severityButtons;
	private Button hideButton;
	private Text delayText;

	private List<String> input;

	private boolean[] severities;
	private boolean hide;
	private int delay;

	private IZendTarget target;

	/**
	 * PlatformCompositeFragment constructor
	 * 
	 * @param parent
	 * @param handler
	 * @param isForEditing
	 */
	public MonitoringCompositeFragment(Composite parent,
			IControlHandler handler, boolean isForEditing) {
		super(parent, handler, isForEditing,
				Messages.MonitoringCompositeFragment_Title,
				Messages.MonitoringCompositeFragment_Description);
		createControl(isForEditing);
	}

	/**
	 * Saves the page's state
	 */
	public void saveValues() {
		hide = hideButton.getSelection();
		try {
			delay = Integer.valueOf(delayText.getText());
		} catch (NumberFormatException ex) {
			// just keep the old value
		}
		for (int i = 0; i < severityButtons.length; i++) {
			severities[i] = severityButtons[i].getSelection();
		}
	}

	@Override
	public boolean performOk() {
		saveValues();
		IEclipsePreferences prefs = MonitorManager.getPreferences();
		IZendTarget target = getTarget();
		if (target != null) {
			String id = target.getId();
			String oldValue = getValue(MonitorManager.getFilters(id));
			String newValue = getValue(input);
			if (oldValue == null || !oldValue.equals(newValue)) {
				prefs.put(MonitorManager.getFiltersKey(id), newValue);
				MonitorManager.updateFilters(id);
			}
			for (int i = 0; i < severities.length; i++) {
				String nodeName = getNodeName(severityButtons[i]);
				prefs.putBoolean(id + '.' + nodeName, severities[i]);
			}
			prefs.putBoolean(MonitorManager.getHideKey(id), hide);
			prefs.putInt(MonitorManager.getHideTimeKey(id), delay);
			try {
				prefs.flush();
			} catch (BackingStoreException e) {
				Activator.log(e);
			}
		}
		return true;
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public void validate() {
		try {
			String val = delayText.getText();
			delay = Integer.valueOf(val);
		} catch (NumberFormatException ex) {
			setMessage(Messages.MonitoringCompositeFragment_DelayNaNMessage,
					IMessageProvider.ERROR);
			return;
		}
		setMessage(getDescription(), IMessageProvider.NONE);
	}

	@Override
	public boolean isComplete() {
		return true;
	}

	@Override
	protected void createControl(Composite parent) {
		createFiltersSection(parent);
		createSeveritySection(parent);
		createDelaySection(parent);
	}

	@Override
	protected void init() {
		target = getTarget();
		severities = new boolean[] { true, true, true };
		input = new ArrayList<String>();
		if (target != null) {
			String id = target.getId();
			IEclipsePreferences prefs = MonitorManager.getPreferences();
			List<String> value = MonitorManager.getFilters(id);
			if (value != null && !value.isEmpty()) {
				input = value;
			} else {
				input = new ArrayList<String>();
			}
			String key = MonitorManager.getHideKey(id);
			hide = prefs.getBoolean(key, false);
			key = MonitorManager.getHideTimeKey(id);
			delay = prefs.getInt(key, 10);
			severities = new boolean[3];
			for (int i = 0; i < severityButtons.length; i++) {
				String nodeName = getNodeName(severityButtons[i]);
				key = id + '.' + nodeName;
				severities[i] = prefs.getBoolean(key, true);
			}
			hideButton.setSelection(hide);
			delayText.setEnabled(hideButton.getSelection());
			delayText.setText(String.valueOf(delay));
		}
		for (int i = 0; i < severityButtons.length; i++) {
			severityButtons[i].setSelection(severities[i]);
		}
		viewer.setInput(input);
		viewer.refresh();
		validate();
	}

	private IZendTarget getTarget() {
		TargetsManager manager = TargetsManagerService.INSTANCE
				.getTargetManager();
		Server server = getServer();
		if (server != null) {
			String serverName = server.getName();
			IZendTarget[] targets = manager.getTargets();
			for (IZendTarget target : targets) {
				if (serverName.equals(target.getServerName())) {
					return target;
				}
			}
		}
		return null;
	}

	private String getNodeName(Button button) {
		return button.getText();
	}

	private String getValue(List<String> list) {
		StringBuilder builder = new StringBuilder();
		if (list != null && !list.isEmpty()) {
			for (String val : list) {
				builder.append(val).append(MonitorManager.FILTER_SEPARATOR);
			}
			return builder.substring(0, builder.length() - 1);
		}
		return ""; //$NON-NLS-1$
	}

	private void createSeveritySection(Composite composite) {
		Composite container = new Composite(composite, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,
				3, 1));
		container.setLayout(new GridLayout(1, true));
		Label label = new Label(container, SWT.WRAP);
		label.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		label.setText(Messages.MonitoringCompositeFragment_SeverityDesc);
		Group group = new Group(container, SWT.NONE);
		group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		group.setLayout(new GridLayout(1, false));
		IssueSeverity[] severityValues = IssueSeverity.values();
		severityButtons = new Button[severityValues.length];
		for (int i = 0; i < severityValues.length; i++) {
			severityButtons[i] = createCheckBox(group, severityValues[i]);
			severityButtons[i].addSelectionListener(new SelectionAdapter() {

				public void widgetSelected(SelectionEvent e) {
					for (int i = 0; i < severityButtons.length; i++) {
						severities[i] = severityButtons[i].getSelection();
					}
				}
			});
		}
	}

	private Button createCheckBox(Composite parent, IssueSeverity severity) {
		Button button = new Button(parent, SWT.CHECK | SWT.LEFT);
		button.setText(severity.getName());
		button.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		return button;
	}

	private void createDelaySection(Composite composite) {
		Composite container = new Composite(composite, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,
				3, 1));
		container.setLayout(new GridLayout(1, true));
		hideButton = new Button(container, SWT.CHECK);
		hideButton.setText(Messages.MonitoringCompositeFragment_HideLabel);
		hideButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				hide = hideButton.getSelection();
				delayText.setEnabled(hide);
			}
		});
		hideButton.setLayoutData(new GridData(SWT.TOP, SWT.FILL, false, false));
		Group group = new Group(container, SWT.NONE);
		group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
		group.setLayout(new GridLayout(1, false));
		Composite labelComposite = new Composite(group, SWT.NONE);
		labelComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false));
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		labelComposite.setLayout(layout);
		Label label = new Label(labelComposite, SWT.NONE);
		label.setText(Messages.MonitoringCompositeFragment_DelayLabel);
		GridData gd = new GridData(SWT.LEFT, SWT.CENTER, true, true);
		gd.minimumWidth = 100;
		label.setLayoutData(gd);
		delayText = new Text(labelComposite, SWT.BORDER | SWT.SINGLE);
		delayText.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				validate();
			}
		});
		delayText.setText(String.valueOf(MonitorManager.DELAY_DEFAULT));
		delayText
				.setToolTipText(Messages.MonitoringCompositeFragment_DelayDesc);
		GridData data = new GridData(SWT.LEFT, SWT.CENTER, true, true);
		data.minimumWidth = 100;
		delayText.setLayoutData(data);
	}

	private void createFiltersSection(Composite parent) {
		Composite filtersSection = new Composite(parent, SWT.NONE);
		filtersSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 3, 1));
		GridLayout gl = new GridLayout(2, false);
		gl.marginWidth = 0;
		gl.marginHeight = 0;
		filtersSection.setLayout(gl);
		Label filtersLabel = new Label(filtersSection, SWT.NONE);
		filtersLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,
				2, 1));
		filtersLabel.setText(Messages.MonitoringCompositeFragment_FiltersDesc);
		viewer = new TableViewer(filtersSection, SWT.SINGLE | SWT.BORDER);
		viewer.getTable().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = viewer.getSelection();
				if (selection != null && !selection.isEmpty()) {
					removeButton.setEnabled(true);
					modifyButton.setEnabled(true);
				} else {
					removeButton.setEnabled(false);
					modifyButton.setEnabled(false);
				}
			}
		});
		viewer.setContentProvider(new FiltersContentProvider());
		viewer.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				if (element instanceof String) {
					return (String) element;
				}
				return super.getText(element);
			}
		});

		Composite buttonsSection = new Composite(filtersSection, SWT.NONE);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, false, true);
		gd.widthHint = 90;
		buttonsSection.setLayoutData(gd);
		GridLayout layout = new GridLayout(1, true);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		buttonsSection.setLayout(layout);
		addButton = new Button(buttonsSection, SWT.PUSH);
		addButton.setText(Messages.MonitoringCompositeFragment_AddLabel);
		addButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		addButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				InputDialog dlg = new InputDialog(getShell(),
						Messages.MonitoringCompositeFragment_AddTitle,
						Messages.MonitoringCompositeFragment_AddDesc,
						EMPTY_STRING, new URLInputValidator());
				if (dlg.open() == Window.OK) {
					input.add(dlg.getValue());
					viewer.refresh();
				}
			}
		});
		modifyButton = new Button(buttonsSection, SWT.PUSH);
		modifyButton.setText(Messages.MonitoringCompositeFragment_ModifyLabel);
		modifyButton
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		modifyButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection sel = (IStructuredSelection) viewer
						.getSelection();
				Object[] selected = sel.toArray();
				if (selected.length > 0) {
					InputDialog dlg = new InputDialog(getShell(),
							Messages.MonitoringCompositeFragment_ModifyTitle,
							Messages.MonitoringCompositeFragment_ModifyDesc,
							(String) selected[0], new URLInputValidator());
					if (dlg.open() == Window.OK) {
						int index = input.indexOf(selected[0]);
						input.remove(selected[0]);
						input.add(index, dlg.getValue());
						viewer.refresh();
					}
				}
			}
		});
		removeButton = new Button(buttonsSection, SWT.PUSH);
		removeButton.setText(Messages.MonitoringCompositeFragment_RemoveLabel);
		removeButton
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		removeButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				removeElement(viewer.getSelection());
			}
		});
		removeButton.setEnabled(false);
		modifyButton.setEnabled(false);
	}

	private void removeElement(ISelection selection) {
		IStructuredSelection sel = (IStructuredSelection) selection;
		Object[] toRemove = sel.toArray();
		for (Object elem : toRemove) {
			if (elem == null) {
				return;
			}
			input.remove((String) elem);
		}
		viewer.refresh();
	}

}
