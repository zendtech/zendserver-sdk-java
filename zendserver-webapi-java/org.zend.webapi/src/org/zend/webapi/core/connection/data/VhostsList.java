/*******************************************************************************
 * Copyright (c) 2015 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;

import java.util.List;

/**
 * A list of 0 or more vhosts.
 * 
 * @author Wojciech Galanciak, 2015
 * 
 */
public class VhostsList extends AbstractResponseData {

	private static final String VHOSTS_LIST = "/vhostList";

	private List<VhostInfo> vhosts;

	protected VhostsList() {
		super(ResponseType.VHOSTS_LIST, BASE_PATH + VHOSTS_LIST, VHOSTS_LIST);
	}

	protected VhostsList(String prefix) {
		super(ResponseType.VHOSTS_LIST, prefix, VHOSTS_LIST);
	}

	/**
	 * @return Vhosts information. May appear 0 or more times.
	 */
	public List<VhostInfo> getVhosts() {
		return vhosts;
	}

	public boolean accept(IResponseDataVisitor visitor) {
		boolean visit = visitor.preVisit(this);
		if (visit) {
			if (getVhosts() != null) {
				for (VhostInfo info : getVhosts()) {
					info.accept(visitor);
				}
			}
			return visitor.visit(this);
		}
		return false;
	}

	protected void setVhosts(List<VhostInfo> vhosts) {
		this.vhosts = vhosts;
	}

}
