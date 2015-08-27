/*******************************************************************************
 * Copyright (c) 2015 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;

import java.util.ArrayList;
import java.util.List;

/**
 * A list of 0 or more directives info.
 * 
 * @author Bartlomiej Laczkowski
 */
public class DirectivesList extends AbstractResponseData {

	private static final String DIRECTIVES = "/directives"; //$NON-NLS-1$

	private List<DirectiveInfo> directivesInfo = new ArrayList<DirectiveInfo>();

	public DirectivesList() {
		super(ResponseType.CONFIGURATION_DIRECTIVES_LIST, BASE_PATH + DIRECTIVES, DIRECTIVES);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.webapi.core.connection.data.IResponseData#accept(org.zend.webapi
	 * .core.connection.data.IResponseDataVisitor)
	 */
	@Override
	public boolean accept(IResponseDataVisitor visitor) {
		if (visitor.preVisit(this)) {
			if (getDirectivesInfo() != null) {
				for (DirectiveInfo info : getDirectivesInfo()) {
					info.accept(visitor);
				}
			}
			return visitor.visit(this);
		}
		return false;
	}

	/**
	 * Returns directives info list
	 * 
	 * @return directives info list
	 */
	public List<DirectiveInfo> getDirectivesInfo() {
		return directivesInfo;
	}

	protected void setDirectivesInfo(List<DirectiveInfo> directivesInfo) {
		this.directivesInfo = directivesInfo;
	}

}
