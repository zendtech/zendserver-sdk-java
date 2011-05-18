/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/

package org.zend.sdklib.internal.library;

import java.util.ArrayList;
import java.util.List;

import org.zend.sdklib.event.IStatusChangeEvent;
import org.zend.sdklib.event.IStatusChangeListener;
import org.zend.sdklib.internal.event.StatusChangeEvent;
import org.zend.sdklib.library.ILibrary;
import org.zend.sdklib.library.IStatus;

/**
 * Abstract class which implement {@link ILibrary} interface. It is intended
 * that it should be extended by all library classes. It consists common logic
 * related to spreading of status changes.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public abstract class AbstractLibrary implements ILibrary {

	private List<IStatusChangeListener> listeners;

	public AbstractLibrary() {
		this.listeners = new ArrayList<IStatusChangeListener>();
	}

	@Override
	public void addStatusChangeListener(IStatusChangeListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeStatusChangeListener(IStatusChangeListener listener) {
		listeners.remove(listener);
	}

	protected void statusChanged(IStatus status) {
		IStatusChangeEvent event = new StatusChangeEvent(status);
		for (IStatusChangeListener listener : listeners) {
			listener.statusChanged(event);
		}
	}

}
