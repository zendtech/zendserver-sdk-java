/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies.
 * All rights reserved. This program and the accompanying materials
 * are the copyright of Zend Technologies and is protected under
 * copyright laws of the United States.
 * You must not copy, adapt or redistribute this document for 
 * any use.
 *******************************************************************************/
package org.zend.php.library.internal.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Wojciech Galanciak, 2013
 *
 */
class AsyncStreamReader extends Thread {

	private StringBuffer buffer;
	private InputStream inputStream;
	private boolean stop;
	private ILogDevice logDevice;

	private String newLine;

	public AsyncStreamReader(InputStream inputStream, StringBuffer buffer,
			ILogDevice logDevice) {
		this.inputStream = inputStream;
		this.buffer = buffer;
		this.logDevice = logDevice;
		this.newLine = System.getProperty("line.separator"); //$NON-NLS-1$
	}

	public String getBuffer() {
		return buffer.toString();
	}

	public void run() {
		try {
			readCommandOutput();
		} catch (Exception e) {
			// TODO log it
		}
	}

	public void stopReading() {
		stop = true;
	}

	private void readCommandOutput() throws IOException {
		BufferedReader out = new BufferedReader(new InputStreamReader(
				inputStream));
		String line = null;
		while ((stop == false) && ((line = out.readLine()) != null)) {
			buffer.append(line + newLine);
			printToDisplayDevice(line);
		}
		out.close();
	}

	private void printToDisplayDevice(String line) {
		if (logDevice != null) {
			logDevice.log(line);
		}
	}
	
}