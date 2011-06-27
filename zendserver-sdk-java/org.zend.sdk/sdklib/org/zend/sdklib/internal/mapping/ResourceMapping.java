/*******************************************************************************
 * Copyright (c) Jun 9, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.internal.mapping;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.zend.sdklib.mapping.IMapping;
import org.zend.sdklib.mapping.IResourceMapping;

public class ResourceMapping implements IResourceMapping {

	private Map<String, Set<IMapping>> inclusion;
	private Map<String, Set<IMapping>> exclusion;
	private Set<IMapping> defaultExclusion;

	public ResourceMapping() {
		this.inclusion = new TreeMap<String, Set<IMapping>>();
		this.exclusion = new TreeMap<String, Set<IMapping>>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.mapping.IResourceMapping#getExclusion()
	 */
	@Override
	public Map<String, Set<IMapping>> getExclusion() {
		return exclusion;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.mapping.IResourceMapping#getInclusion()
	 */
	@Override
	public Map<String, Set<IMapping>> getInclusion() {
		return inclusion;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.mapping.IResourceMapping#getDefaultExclusion()
	 */
	@Override
	public Set<IMapping> getDefaultExclusion() {
		return defaultExclusion;
	}

	public void setInclusion(Map<String, Set<IMapping>> inclusion) {
		this.inclusion = inclusion;
	}

	public void setExclusion(Map<String, Set<IMapping>> exclusion) {
		this.exclusion = exclusion;
	}

	public void setDefaultExclusion(Set<IMapping> defaultExclusion) {
		this.defaultExclusion = defaultExclusion;
	}

}
