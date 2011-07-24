/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.ui.contentassist;

/**
 * Interface for all proposals providers of content assist
 * 
 * @author Roy, 2011
 *
 */
public interface IProposalProvider {

	/**
	 * Initialize list
	 */
	public abstract void init();

	/**
	 * @return the list of all proposals
	 */
	public abstract String[] getNames();

}
