/*******************************************************************************
 * Copyright (c) Dec 7, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.update.monitor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.zend.sdkcli.monitor.ProgressMonitor;

/**
 * 
 * Custom output stream which informs provided progress monitor
 * {@link ProgressMonitor} about progress of writing to the file.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class UpdateOutputStream extends FileOutputStream {

	private ProgressMonitor monitor;
	private int work;

	public UpdateOutputStream(File file, ProgressMonitor monitor, int work)
			throws FileNotFoundException {
		super(file);
		this.monitor = monitor;
		this.work = work;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.FileOutputStream#write(byte[], int, int)
	 */
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		if (work > 0) {
			int rest = work - b.length;
			if (rest > 0) {
				monitor.update(b.length);
				work = rest;
			} else {
				monitor.update(work);
				work = 0;
			}
		}
		super.write(b, off, len);
	}

}
