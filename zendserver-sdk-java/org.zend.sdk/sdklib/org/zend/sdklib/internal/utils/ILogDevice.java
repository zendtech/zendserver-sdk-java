/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies.
 * All rights reserved. This program and the accompanying materials
 * are the copyright of Zend Technologies and is protected under
 * copyright laws of the United States.
 * You must not copy, adapt or redistribute this document for 
 * any use.
 *******************************************************************************/
package org.zend.sdklib.internal.utils;

import java.io.OutputStream;

/**
 * 
 */
public interface ILogDevice {

	public void log(String str);

	public void logError(String str);

	public void updateOutputStream(OutputStream outputStream);

	public void clear();

}