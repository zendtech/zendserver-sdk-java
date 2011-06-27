/*******************************************************************************
 * Copyright (c) Jun 27, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.internal.mapping;

import org.zend.sdklib.mapping.IMapping;
import org.zend.sdklib.mapping.IMappingChangeEvent;

/**
 * Basic implementation of {@link IMappingChangeEvent}
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class MappingChangeEvent implements IMappingChangeEvent {

	private Kind kind;
	private IMapping mapping;
	private String folder;

	public MappingChangeEvent(Kind kind, IMapping mapping, String folder) {
		super();
		this.kind = kind;
		this.mapping = mapping;
		this.folder = folder;
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
	 * @see org.zend.sdklib.mapping.IMappingChangeEvent#getMapping()
	 */
	@Override
	public IMapping getMapping() {
		return mapping;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.mapping.IMappingChangeEvent#getFolder()
	 */
	@Override
	public String getFolder() {
		return folder;
	}

}
