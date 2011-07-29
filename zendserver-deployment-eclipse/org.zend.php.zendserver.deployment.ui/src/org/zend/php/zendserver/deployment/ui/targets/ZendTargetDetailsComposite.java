package org.zend.php.zendserver.deployment.ui.targets;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.zend.php.zendserver.deployment.ui.Messages;
import org.zend.sdklib.internal.target.ZendTarget;
import org.zend.sdklib.target.IZendTarget;

public class ZendTargetDetailsComposite {

	public static final String PROP_ERROR_MESSAGE = "errorMessage"; //$NON-NLS-1$
	
	private Text idText;
	private Text hostText;
	private Text keyText;
	private Text secretText;
	
	private String errorMessage;
	
	private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
	
	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(propertyName, listener);
	}
	
	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(propertyName, listener);
	}
	
	public void setErrorMessage(String errorMessage) {
		String oldMessage = this.errorMessage;
		this.errorMessage = errorMessage;
		changeSupport.firePropertyChange(PROP_ERROR_MESSAGE, oldMessage, errorMessage);
	}
	public String getErrorMessage() {
		return errorMessage;
	}

	
	public void create(Composite composite) {
		Label label = new Label(composite, SWT.NONE);
		label.setText(Messages.TargetDialog_Id);
		idText = new Text(composite, SWT.BORDER);
		idText.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));
		idText.setToolTipText(Messages.TargetDialog_IdTooltip);
		
		label = new Label(composite, SWT.NONE);
		label.setText(Messages.TargetDialog_Host);
		hostText = new Text(composite, SWT.BORDER);
		hostText.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));
		hostText.setToolTipText(Messages.TargetDialog_HostTooltip);
		
		label = new Label(composite, SWT.NONE);
		label.setText(Messages.TargetDialog_KeyName);
		keyText = new Text(composite, SWT.BORDER);
		keyText.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));
		keyText.setToolTipText(Messages.TargetDialog_KeyTooltip);
		
		label = new Label(composite, SWT.NONE);
		label.setText(Messages.TargetDialog_KeySecret);
		secretText = new Text(composite, SWT.BORDER);
		secretText.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));
		secretText.setToolTipText(Messages.TargetDialog_SecretTooltip);
		
		Button validateButton = new Button(composite, SWT.NONE);
		validateButton.setText(Messages.TargetDialog_validate);
		validateButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				scheduleValidationJob();
			}
		});
		
	}
	
	private void scheduleValidationJob() {
		ValidateTargetJob job = new ValidateTargetJob(getTarget());
		job.setUser(true);
		job.schedule();
		job.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				String message = event.getJob().getResult().getMessage();
				setErrorMessage(message);
			}
		});
	}

	public void setDefaultTargetSettings(IZendTarget defaultTarget) {
		if (idText != null) {
			idText.setText(defaultTarget.getId());
			idText.setEnabled(false);
		}
		if (hostText != null) {
			hostText.setText(defaultTarget.getHost().toString());
		}
		if (keyText != null) {
		keyText.setText(defaultTarget.getKey());
		}
		if (secretText != null) {
			secretText.setText(defaultTarget.getSecretKey());
		}
	}

	/**
	 * Creates target details provided in the dialog controls.
	 * Target is automatically validated based on the logic in ZendTarget constructor.
	 */
	public IZendTarget getTarget() {
		URL host = null;
		try {
			host = new URL(hostText.getText());
		} catch (MalformedURLException e) {
			// should be checked earlier
		}
		
		return new ZendTarget(idText.getText(), host, keyText.getText(), secretText.getText());
	}
}
