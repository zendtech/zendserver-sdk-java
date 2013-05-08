/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies.
 * All rights reserved. This program and the accompanying materials
 * are the copyright of Zend Technologies and is protected under
 * copyright laws of the United States.
 * You must not copy, adapt or redistribute this document for 
 * any use.
 *******************************************************************************/
package org.zend.php.library.internal.ui.preferences;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.zend.php.library.internal.core.ILogDevice;

/**
 * Implementation of {@link ILogDevice}. It can be used to open Console view and
 * provide output of a certain process.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class EclipseConsoleLog implements ILogDevice {

	private MessageConsoleStream stream;
	private String name;
	private boolean initialized;

	public EclipseConsoleLog(String name) {
		super();
		this.name = name;
		this.initialized = false;
	}

	/**
	 * Open and initialize Console view.
	 */
	public void init() {
		if (!initialized) {
			MessageConsole console = new MessageConsole(name, null);
			console.activate();
			ConsolePlugin.getDefault().getConsoleManager()
					.addConsoles(new IConsole[] { console });
			stream = console.newMessageStream();
			initialized = true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.zend.php.ccm.core.cmd.ILogDevice#log(java.lang.String)
	 */
	public void log(String output) {
		stream.println(output);
	}

}
