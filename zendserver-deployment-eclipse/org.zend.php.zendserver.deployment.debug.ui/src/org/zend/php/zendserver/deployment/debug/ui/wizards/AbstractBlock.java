package org.zend.php.zendserver.deployment.debug.ui.wizards;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.zend.php.zendserver.deployment.debug.core.config.IDeploymentHelper;


public abstract class AbstractBlock {

	private Composite container;

	protected IStatusChangeListener listener;

	public AbstractBlock(IStatusChangeListener listener) {
		this.listener = listener;
	}

	public abstract IStatus validatePage();

	public abstract void initializeFields(IDeploymentHelper helper);

	public abstract IDeploymentHelper getHelper();

	public Composite createContents(Composite parent) {
		container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		container.setLayout(layout);
		return container;
	}

	protected Composite getContainer() {
		return container;
	}

	protected IStatusChangeListener getContext() {
		return listener;
	}

	protected Combo createLabelWithCombo(String labelText, String tooltip, Composite container) {
		Label label = new Label(container, SWT.NULL);
		label.setText(labelText);
		Combo combo = new Combo(container, SWT.SIMPLE | SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				listener.statusChanged(validatePage());
			}
		});
		combo.setToolTipText(tooltip);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return combo;
	}

	protected Text createLabelWithText(String labelText, String tooltip, Composite container) {
		Label label = new Label(container, SWT.NULL);
		label.setText(labelText);
		Text text = new Text(container, SWT.BORDER | SWT.SINGLE);
		text.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				listener.statusChanged(validatePage());
			}
		});
		text.setToolTipText(tooltip);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return text;
	}

	protected Button createLabelWithCheckbox(String desc, String tooltip, Composite composite) {
		Button button = new Button(composite, SWT.CHECK);
		button.setText(desc);
		button.setToolTipText(tooltip);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				listener.statusChanged(validatePage());
			}
		});
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		button.setLayoutData(gd);
		return button;
	}

}
