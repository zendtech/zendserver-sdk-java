/*******************************************************************************
 * Copyright (c) Dec 7, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.update.parser;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.zend.sdkcli.update.UpdateException;

/**
 * 
 * Represents add entry. It defines which file should be added to current Zend
 * SDK instance. Optionally, it also defines where this file should be added. If
 * destination is not defined, then file is added to Zend SDK root location.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class AddEntry extends AbstractDeltaEntry {

	private static final String FILE = "file";
	private static final String DEST = "dest";

	private String toAdd;
	private String destination;
	private File temp;

	public AddEntry(Node node, File temp) {
		super(node);
		this.temp = temp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.sdkcli.update.parser.AbstractDeltaEntry#execute(java.io.File)
	 */
	@Override
	public boolean execute(File root) throws UpdateException {
		File dest = destination != null ? new File(root, destination) : root;
		File origin = new File(temp, toAdd);
		try {
			copy(origin, dest, temp.getAbsolutePath());
			return true;
		} catch (IOException e) {
			throw new UpdateException(e);
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
		toAdd = attributes.getNamedItem(FILE).getNodeValue();
		attNode = attributes.getNamedItem(DEST);
		if (attNode != null) {
			destination = attributes.getNamedItem(DEST).getNodeValue();
		}
	}

	private void copy(File file, File dest, String root) throws IOException {
		if (file.isDirectory()) {
			String absolutePath = file.getAbsolutePath();
			String newPath = absolutePath.substring(root.length());
			new File(dest, newPath).mkdir();
			File[] children = file.listFiles();
			for (File child : children) {
				copy(child, dest, root);
			}
		} else {
			String absolutePath = file.getAbsolutePath();
			String newPath = absolutePath.substring(root.length());
			copyFile(file, new File(dest, newPath));
		}
	}

	private void createFile(File out) throws IOException {
		File parent = out.getParentFile();
		if (!parent.exists()) {
			parent.mkdirs();
		}
		out.createNewFile();
	}

	private void copyFile(File in, File out) throws IOException {
		if (out.exists()) {
			return;
		}
		createFile(out);
		FileInputStream fis = new FileInputStream(in);
		FileOutputStream fos = new FileOutputStream(out);
		try {
			byte[] buf = new byte[4096];
			int i = 0;
			while ((i = fis.read(buf)) != -1) {
				fos.write(buf, 0, i);
			}
		} finally {
			closeStream(fis);
			closeStream(fos);
		}
	}

	private void closeStream(Closeable stream) throws IOException {
		if (stream != null) {
			stream.close();
		}
	}

}
