/*******************************************************************************
 * Copyright (c) Dec 7, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.update.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
	private static final String EXCLUDE = "exclude";

	private String file;
	private List<String> exclude;

	public RemoveEntry(Node node) {
		super(node);
	}

	@Override
	public boolean execute(File root) throws UpdateException {
		if (file.endsWith("*")) {
			File parent = new File(root, file.substring(0, file.length() - 1));
			File[] files = parent.listFiles();
			for (File file : files) {
				delete(file, exclude);
			}
		} else {
			File fileToDelete = new File(root, file);
			delete(fileToDelete, exclude);
		}
		return true;
	}

	@Override
	protected void parse(Node node) {
		NamedNodeMap attributes = node.getAttributes();
		Node attNode = attributes.getNamedItem(FILE);
		if (attNode == null) {
			throw new IllegalArgumentException(
					"Invalid add tag: missing file attribute");
		}
		file = attNode.getNodeValue();
		attNode = attributes.getNamedItem(EXCLUDE);
		if (attNode == null) {
			exclude = new ArrayList<String>();
		} else {
			exclude = Arrays.asList(attNode.getNodeValue().split("\\|"));
		}
	}

}
