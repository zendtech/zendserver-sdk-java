/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies.
 * All rights reserved. This program and the accompanying materials
 * are the copyright of Zend Technologies and is protected under
 * copyright laws of the United States.
 * You must not copy, adapt or redistribute this document for 
 * any use.
 *******************************************************************************/
package org.zend.sdklib.internal.utils;

import java.io.InputStream;

/**
 * @author Wojciech Galanciak, 2013
 * 
 */
class ErrorAsyncStreamReader extends AsyncStreamReader {

	public ErrorAsyncStreamReader(InputStream inputStream, StringBuffer buffer,
			ILogDevice logDevice) {
		super(inputStream, buffer, logDevice);
	}

	@Override
	protected void printToDisplayDevice(String line) {
		if (logDevice != null) {
			logDevice.logError(line);
		}
	}

}