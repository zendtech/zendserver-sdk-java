/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.debug.ui.wizards;

import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.swt.widgets.Composite;
import org.zend.php.zendserver.deployment.core.debugger.IDeploymentHelper;
import org.zend.php.zendserver.deployment.debug.ui.listeners.IStatusChangeListener;

/**
 * Implementors can provide some additional UI which will be added to default
 * Deployment Wizard.
 * 
 * @author Wojciech Galanciak, 2013
 * 
 */
public interface IDeployWizardContribution {

	void initialize(IRunnableContext context, String projectName,
			IStatusChangeListener listener, String description);

	/**
	 * Creates additional GUI provided by this extension.
	 * 
	 * @param container
	 * @param description
	 */
	void createExtraSection(Composite container);

	/**
	 * Initializes all contributed GUI components if required using
	 * {@link IDeploymentHelper}.
	 * 
	 * @param helper
	 */
	public abstract void initializeFields(IDeploymentHelper helper);

	/**
	 * Returns collection of additional values which will be passed to
	 * processing.
	 * 
	 * @return map
	 */
	Map<String, String> getExtraAttributes();

	/**
	 * Validates values of all extra fields provided by this extension.
	 * 
	 * @return status
	 */
	IStatus validate();

}
