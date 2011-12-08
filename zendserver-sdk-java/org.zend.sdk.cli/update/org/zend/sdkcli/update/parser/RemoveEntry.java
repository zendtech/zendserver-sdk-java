/*******************************************************************************
 * Copyright (c) Dec 7, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.update.parser;

import java.io.File;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.zend.sdkcli.update.UpdateException;

/**
 * 
 * Represents remove entry. It defines which file should be removed from current
 * Zend SDK instance.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class RemoveEntry extends AbstractDeltaEntry {

	private static final String FILE = "file";

	private String file;

	public RemoveEntry(Node node) {
		super(node);
	}

	@Override
	public boolean execute(File root) throws UpdateException {
		if (file.endsWith("*")) {
			File parent = new File(root, file.substring(0, file.length() - 1));
			File[] files = parent.listFiles();
			for (File file : files) {
				if (!delete(file)) {
					return false;
				}
			}
			return true;
		} else {
			File fileToDelete = new File(root, file);
			return delete(fileToDelete);
		}
	}

	@Override
	protected void parse(Node node) {
		NamedNodeMap attributes = node.getAttributes();
		Node attNode = attributes.getNamedItem(FILE);
		if (attNode == null) {
			throw new IllegalArgumentException(
					"Invalid add tag: missing file attribute");
		}
		file = attributes.getNamedItem(FILE).getNodeValue();
	}

}
