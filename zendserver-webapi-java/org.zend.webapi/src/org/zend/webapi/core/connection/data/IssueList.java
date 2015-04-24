/*******************************************************************************
 * Copyright (c) Feb 13, 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;

import java.util.List;

/**
 * A list of parameters.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class IssueList extends AbstractResponseData {

	private static final String ISSUES = "/issues";
	
	private List<Issue> issues;

	protected IssueList() {
		super(ResponseType.ISSUE_LIST, BASE_PATH + ISSUES, ISSUES);
	}

	protected IssueList(String prefix, int occurrance) {
		super(ResponseType.ISSUE_LIST, prefix, ISSUES, occurrance);
	}

	/**
	 * @return Issues
	 */
	public List<Issue> getIssues() {
		return issues;
	}

	public boolean accept(IResponseDataVisitor visitor) {
		boolean visit = visitor.preVisit(this);
		if (visit) {
			if (getIssues() != null) {
				for (Issue issue : getIssues()) {
					issue.accept(visitor);
				}
			}
			return visitor.visit(this);
		}
		return false;
	}

	protected void setIssues(List<Issue> issues) {
		this.issues = issues;
	}

}
