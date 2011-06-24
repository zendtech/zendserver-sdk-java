/*******************************************************************************
 * Copyright (c) Jun 9, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.internal.mapping;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.zend.sdklib.mapping.IMapping;
import org.zend.sdklib.mapping.IResourceMapping;

public class ResourceMapping implements IResourceMapping {

	private Map<String, Set<IMapping>> inclusion;
	private Map<String, Set<IMapping>> exclusion;
	private Set<IMapping> defaultExclusion;

	public ResourceMapping() {
		this.inclusion = new HashMap<String, Set<IMapping>>();
		this.exclusion = new HashMap<String, Set<IMapping>>();
	}

	@Override
	public Map<String, Set<IMapping>> getExclusion() {
		return exclusion;
	}

	@Override
	public Map<String, Set<IMapping>> getInclusion() {
		return inclusion;
	}

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
