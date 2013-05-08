/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies.
 * All rights reserved. This program and the accompanying materials
 * are the copyright of Zend Technologies and is protected under
 * copyright laws of the United States.
 * You must not copy, adapt or redistribute this document for 
 * any use.
 *******************************************************************************/
package org.zend.php.library.internal.core;

/**
 * @author Wojciech Galanciak, 2012
 *
 */
public interface ILogDevice {
	
	public void log(String str);
	
	public void init();
	
}