/*******************************************************************************
 * Copyright (c) Jul 3, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.internal.mapping;

import java.util.List;

import org.zend.sdklib.mapping.IMapping;
import org.zend.sdklib.mapping.IMappingEntry;

/**
 * Default implementation of {@link IMappingEntry}.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class MappingEntry implements IMappingEntry {

	private String folder;
	private List<IMapping> mappings;
	private Type type;

	public MappingEntry(String folder, List<IMapping> mappings, Type type) {
		super();
		this.folder = folder;
		this.mappings = mappings;
		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.mapping.IMappingEntry#getFolder()
	 */
	@Override
	public String getFolder() {
		return folder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.mapping.IMappingEntry#getMappings()
	 */
	@Override
	public List<IMapping> getMappings() {
		return mappings;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.mapping.IMappingEntry#getType()
	 */
	@Override
	public Type getType() {
		return type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IMappingEntry) {
			IMappingEntry objMapping = (IMappingEntry) obj;
			if (getFolder().equals(objMapping.getFolder())
					&& getType() == objMapping.getType()) {
				return true;
			}
		}
		return false;
	}

}
