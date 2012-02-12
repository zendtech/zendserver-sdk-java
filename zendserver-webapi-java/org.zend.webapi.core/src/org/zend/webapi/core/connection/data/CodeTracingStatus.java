/*******************************************************************************
 * Copyright (c) Feb 12, 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;

/**
 * A list of indicators for code tracing activity and operations.
 * 
 * @author Wojciech Galanciak, 2012
 * @since 1.2
 */
public class CodeTracingStatus extends AbstractResponseData {

	private String componentStatus;
	private String alwaysDump;
	private String traceEnabled;
	private String developerMode;
	private int awaitsRestart;

	protected CodeTracingStatus() {
		super(ResponseType.CODE_TRACING_STATUS, BASE_PATH
				+ "/codeTracingStatus");
	}

	protected CodeTracingStatus(String prefix, int occurrance) {
		super(ResponseType.CODE_TRACING_STATUS, prefix, occurrance);
	}

	public boolean accept(IResponseDataVisitor visitor) {
		if (visitor.preVisit(this)) {
			return visitor.visit(this);
		}
		return false;
	}

	/**
	 * @return Current activity status of the component: Active | Inactive
	 */
	public String getComponentStatus() {
		return componentStatus;
	}

	/**
	 * @return Current always_dump directive value (On|Off)
	 */
	public String getAlwaysDump() {
		return alwaysDump;
	}

	/**
	 * @return Current trace_enabled directive value (On|Off)
	 */
	public String getTraceEnabled() {
		return traceEnabled;
	}

	/**
	 * @return Current developer_mode directive value (On|Off)
	 */
	public String getDeveloperMode() {
		return developerMode;
	}

	/**
	 * @return If true, ZendServer is waiting for a restart which may affect
	 *         these settings
	 */
	public int getAwaitsRestart() {
		return awaitsRestart;
	}

	protected void setComponentStatus(String componentStatus) {
		this.componentStatus = componentStatus;
	}

	protected void setAlwaysDump(String alwaysDump) {
		this.alwaysDump = alwaysDump;
	}

	protected void setTraceEnabled(String traceEnabled) {
		this.traceEnabled = traceEnabled;
	}

	protected void setDeveloperMode(String developerMode) {
		this.developerMode = developerMode;
	}

	protected void setAwaitsRestart(int awaitsRestart) {
		this.awaitsRestart = awaitsRestart;
	}

}
