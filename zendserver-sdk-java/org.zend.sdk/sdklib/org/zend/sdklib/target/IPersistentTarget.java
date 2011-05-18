/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.target;

import java.io.InputStream;
import java.io.OutputStream;

public interface IPersistentTarget extends ITarget {
	
	/**
	 * @param outputStream
	 * @return boolean true when write operation success
	 */
	public boolean write(OutputStream outputStream);
	
	/**
	 * @param outputStream
	 * @return boolean true when read operaion success
	 */
	public boolean read(InputStream outputStream);
	
}
