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

	
	/**
	 * Represents set of possible states for CodeTracingStatus fields.
	 */
	public enum State {
		
		ON(1),
		
		OFF(0),
		
		UNKNOWN(-1);
		
		private final int value;

		private State(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
		
		public static State byValue(int value) {
			if (value < 0) {
				return UNKNOWN;
			}
			State[] values = values();
			for (State state : values) {
				if (state.getValue() == value) {
					return state;
				}
			}
			return UNKNOWN;
		}
		
	}
	
	private String componentStatus;
	private State alwaysDump;
	private State traceEnabled;
	private State developerMode;
	private State awaitsRestart;

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
	public State getAlwaysDump() {
		return alwaysDump;
	}

	/**
	 * @return Current trace_enabled directive value (On|Off)
	 */
	public State getTraceEnabled() {
		return traceEnabled;
	}

	/**
	 * @return Current developer_mode directive value (On|Off)
	 */
	public State getDeveloperMode() {
		return developerMode;
	}

	/**
	 * @return If true, ZendServer is waiting for a restart which may affect
	 *         these settings
	 */
	public State getAwaitsRestart() {
		return awaitsRestart;
	}

	protected void setComponentStatus(String componentStatus) {
		this.componentStatus = componentStatus;
	}

	protected void setAlwaysDump(State alwaysDump) {
		this.alwaysDump = alwaysDump;
	}

	protected void setTraceEnabled(State traceEnabled) {
		this.traceEnabled = traceEnabled;
	}

	protected void setDeveloperMode(State developerMode) {
		this.developerMode = developerMode;
	}

	protected void setAwaitsRestart(State awaitsRestart) {
		this.awaitsRestart = awaitsRestart;
	}

}
