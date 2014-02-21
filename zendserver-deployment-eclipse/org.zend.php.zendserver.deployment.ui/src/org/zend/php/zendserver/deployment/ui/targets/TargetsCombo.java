package org.zend.php.zendserver.deployment.ui.targets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;
import org.zend.webapi.core.connection.data.values.ZendServerVersion;

public class TargetsCombo {

	public enum Type {
		ALL,

		PHPCLOUD,

		OPENSHIFT,

		ZEND_SERVER_6;
	}

	private TargetsManager targetsManager = TargetsManagerService.INSTANCE
			.getTargetManager();

	private Combo targetsCombo;
	
	private Button addTargetButton;
	
	private IZendTarget[] targetsList = new IZendTarget[0];

	private String labelText;

	private String tooltip;

	private Type type;
	
	private boolean addTarget;
	
	public TargetsCombo() {
		this(Type.ALL, false);
	}
	
	public TargetsCombo(Type type) {
		this(type, false);
	}

	public TargetsCombo(boolean addTarget) {
		this(Type.ALL, addTarget);
	}

	public TargetsCombo(Type type, boolean addTarget) {
		this.type = type;
		this.addTarget = addTarget;
	}

	public void select(String targetId) {
		for (int i = 0; i < targetsList.length; i++) {
			if (targetsList[i].getId().equals(targetId)) {
				targetsCombo.select(i);
				return;
			}
		}
	}

	public void setEnabled(boolean value) {
		targetsCombo.setEnabled(value);
		if (addTarget) {
			addTargetButton.setEnabled(value);
		}
	}

	public IZendTarget getSelected() {
		int idx = targetsCombo.getSelectionIndex();
		if (idx <= -1) {
			return null;
		}

		IZendTarget target = targetsList[idx];
		return targetsManager.getTargetById(target.getId());
	}

	public void updateItems() {
		targetsList = filterTargets(targetsManager.getTargets());
		targetsCombo.removeAll();
		String defaultId = targetsManager.getDefaultTargetId();
		int defaultNo = 0;

		if (targetsList.length != 0) {
			int i = 0;
			for (IZendTarget target : targetsList) {
				if (target.getId().equals(defaultId)) {
					defaultNo = i;
				}
				targetsCombo.add(target.getHost()
						+ " (Id: " + target.getId() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
				i++;
			}
		}
		if (targetsCombo.getItemCount() > 0) {
			targetsCombo.select(defaultNo);
		}
	}

	public Combo getCombo() {
		return targetsCombo;
	}

	public void createControl(Composite container) {
		Label label = new Label(container, SWT.NONE);
		label.setText(labelText);
		Composite comboContainer = new Composite(container, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.verticalSpacing = 0;
		comboContainer.setLayout(layout);
		comboContainer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		targetsCombo = new Combo(comboContainer, SWT.SIMPLE | SWT.DROP_DOWN
				| SWT.READ_ONLY);
		targetsCombo.setToolTipText(tooltip);
		targetsCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		if (addTarget) {
			addTargetButton = new Button(comboContainer, SWT.PUSH);
			addTargetButton.setText("Add Target"); //$NON-NLS-1$
			addTargetButton.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL,
					false, false));
		}
		updateItems();
	}

	public void setLabel(String text) {
		this.labelText = text;
	}

	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}
	
	public void setAddTargetListener(SelectionListener listener) {
		if (addTargetButton != null && !addTargetButton.isDisposed()) {
			addTargetButton.addSelectionListener(listener);
		}
	}

	private IZendTarget[] filterTargets(IZendTarget[] targets) {
		List<IZendTarget> result = new ArrayList<IZendTarget>();
		if (targets != null && targets.length > 0) {
			for (IZendTarget target : targets) {
				if (type == Type.PHPCLOUD && !TargetsManager.isPhpcloud(target)) {
					continue;
				} else if (type == Type.OPENSHIFT
						&& !TargetsManager.isOpenShift(target)) {
					continue;
				} else if (type == Type.ZEND_SERVER_6) {
					if (!TargetsManager.checkExactVersion(target,
							ZendServerVersion.v6_X_X)) {
						continue;
					}
				}
				result.add(target);
			}
		}
		return result.toArray(new IZendTarget[0]);
	}

}
