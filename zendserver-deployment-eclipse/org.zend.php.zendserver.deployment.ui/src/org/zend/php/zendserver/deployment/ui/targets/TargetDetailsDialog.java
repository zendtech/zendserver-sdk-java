package org.zend.php.zendserver.deployment.ui.targets;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.zend.php.zendserver.deployment.ui.Messages;
import org.zend.sdklib.target.IZendTarget;

/**
 * Dialog to edit deployment targetComposite details.
 */
public class TargetDetailsDialog extends Dialog {

	private static final int DEFAULT_WIDTH = 300;
	
	private String message;

	private AbstractTargetDetailsComposite targetComposite = new ZendTargetDetailsComposite();
	
	private Label errorLabel;

	private String title;

	private IZendTarget defaultTarget;

	private IZendTarget createdTarget;

	public TargetDetailsDialog(Shell parentShell) {
		super(parentShell);
	}
	
	/**
	 * Sets message to be shown at the top of the dialog.
	 * 
	 * @param message Message to be shown at the top of the dialog
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * Sets title to be shown at the dialog title bar.
	 * 
	 * @param title Title to be shown at the dialog title bar.
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		
		if (title != null) {
			newShell.setText(title);
		}
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		// create a composite with standard margins and spacing
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.widthHint = DEFAULT_WIDTH;
		composite.setLayoutData(gd);
		
		Label label;
		if (message != null) {
			label = new Label(composite, SWT.NONE);
			label.setText(message);
			gd = new GridData(SWT.FILL, SWT.DEFAULT, true, false);
			gd.horizontalSpan = 2;
			label.setLayoutData(gd);
		}
		
		targetComposite.create(composite);
		targetComposite.addPropertyChangeListener(ZendTargetDetailsComposite.PROP_ERROR_MESSAGE, new PropertyChangeListener() {
			
			public void propertyChange(PropertyChangeEvent evt) {
				final Object newVal = evt.getNewValue();
				if ((errorLabel == null) || (errorLabel.isDisposed())) {
					return;
				}
				
				errorLabel.getDisplay().syncExec(new Runnable() {
					public void run() {
						errorLabel.setText((String)newVal);
					}
				});
				
			}
		});
		
		Button validateButton = new Button(composite, SWT.NONE);
		validateButton.setText(Messages.TargetDialog_validate);
		validateButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				targetComposite.validate();
			}
		});
		
		
		errorLabel = new Label(composite, SWT.NONE);
		errorLabel.setLayoutData(new GridData(GridData.FILL_BOTH));
		applyDialogFont(composite);
		
		if (defaultTarget != null) {
			targetComposite.setDefaultTargetSettings(defaultTarget);
		}
		
		return composite;
	}
	
	@Override
	protected void okPressed() {
		Job job = targetComposite.validate();
		try {
			job.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.okPressed();
	}
	
	
	
	/**
	 * Returns targetComposite specified by the user
	 * 
	 * @return
	 */
	public IZendTarget getTarget() {
		return targetComposite.getTarget();
	}

	/**
	 * Sets default values for the fields.
	 * It also disables idField, because usually we don't want to change id.
	 * 
	 * @param targetComposite
	 */
	public void setDefaultTarget(IZendTarget target) {
		this.defaultTarget = target;
		if (targetComposite != null) {
			targetComposite.setDefaultTargetSettings(target);
		}
	}

}
