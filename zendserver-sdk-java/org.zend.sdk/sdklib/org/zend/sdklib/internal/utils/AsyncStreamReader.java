/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies.
 * All rights reserved. This program and the accompanying materials
 * are the copyright of Zend Technologies and is protected under
 * copyright laws of the United States.
 * You must not copy, adapt or redistribute this document for 
 * any use.
 *******************************************************************************/
package org.zend.sdklib.internal.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 
 */
class AsyncStreamReader extends Thread {

	protected ILogDevice logDevice;
	private StringBuffer buffer;
	private InputStream inputStream;
	private boolean stop;

	public AsyncStreamReader(InputStream inputStream, StringBuffer buffer,
			ILogDevice logDevice) {
		this.inputStream = inputStream;
		this.buffer = buffer;
		this.logDevice = logDevice;
	}

	public String getBuffer() {
		return buffer.toString();
	}

	public void run() {
		try {
			readCommandOutput();
		} catch (Exception e) {
			logDevice.logError(e.getMessage());
		}
	}

	public void stopReading() {
		stop = true;
	}

	protected void printToDisplayDevice(String line) {
		if (logDevice != null) {
			logDevice.log(line);
		}
	}

	private void readCommandOutput() throws IOException {
		BufferedReader out = new BufferedReader(
				new InputStreamReader(inputStream));
		String line = null;
		int read = 0;
		char[] charBuff = new char[512];
		try {
			while ((stop == false) && ((read = out.read(charBuff)) != -1)) {
				line = new String(charBuff, 0, read);
				buffer.append(line);
				printToDisplayDevice(line);
			}
		} finally {
			out.close();
		}
	}

}