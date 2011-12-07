/*******************************************************************************
 * Copyright (c) Dec 7, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.update.parser;

import java.io.File;

import org.w3c.dom.Node;
import org.zend.sdkcli.update.UpdateException;

/**
 * 
 * Represents abstract entry from delta file.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public abstract class AbstractDeltaEntry {

	public AbstractDeltaEntry(Node node) {
		parse(node);
	}

	/**
	 * Executes actions specific to its {@link ImplementationType#}
	 * 
	 * @param root
	 *            - root file of Zend SDK
	 * @return true if execution was performed successfully; otherwise returns
	 *         false
	 * @throws UpdateException
	 */
	public abstract boolean execute(File root) throws UpdateException;

	protected abstract void parse(Node node);

	protected boolean delete(File file) {
		if (file == null || !file.exists()) {
			return true;
		}
		if (file.isDirectory()) {
			String[] children = file.list();
			for (int i = 0; i < children.length; i++) {
				boolean result = delete(new File(file, children[i]));
				if (!result) {
					return false;
				}
			}
		}
		return file.delete();
	}

}
