/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/

package org.zend.sdklib.internal.event;

import org.zend.webapi.core.progress.IStatus;
import org.zend.webapi.core.progress.IStatusChangeEvent;

public class StatusChangeEvent implements IStatusChangeEvent {

	private IStatus status;

	public StatusChangeEvent(IStatus status) {
		super();
		this.status = status;
	}

	@Override
	public IStatus getStatus() {
		return status;
	}

}
