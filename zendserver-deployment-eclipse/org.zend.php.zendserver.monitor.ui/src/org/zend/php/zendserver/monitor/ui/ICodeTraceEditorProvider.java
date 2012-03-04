/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.monitor.ui;

/**
 * Implementor should provide user interface part responsible for displaying
 * code trace stored locally.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public interface ICodeTraceEditorProvider {

	boolean openInEditor(String path);

}
