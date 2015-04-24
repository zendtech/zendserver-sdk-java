/*******************************************************************************
 * Copyright (c) Jun 27, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.internal.mapping;

import org.zend.sdklib.mapping.IMappingChangeEvent;
import org.zend.sdklib.mapping.IMappingEntry;

/**
 * Basic implementation of {@link IMappingChangeEvent}
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class MappingChangeEvent implements IMappingChangeEvent {

	private Kind kind;
	private IMappingEntry entry;

	public MappingChangeEvent(Kind kind, IMappingEntry entry) {
		super();
		this.kind = kind;
		this.entry = entry;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.mapping.IMappingChangeEvent#getChangeKind()
	 */
	@Override
	public Kind getChangeKind() {
		return kind;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.mapping.IMappingChangeEvent#getEntry()
	 */
	@Override
	public IMappingEntry getEntry() {
		return entry;
	}

}
