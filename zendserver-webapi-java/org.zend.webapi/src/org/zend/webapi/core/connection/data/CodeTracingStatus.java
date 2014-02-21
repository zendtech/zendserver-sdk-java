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

	
	private static final String CODE_TRACING_STATUS = "/codeTracingStatus";

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
	
	/**
	 * Represents set of possible code tracing statuses.
	 */
	public enum Status {
		
		ACTIVE("Active"),
		
		INACTIVE("Inactive"),
		
		UNKNOWN(null);
		
		private final String value;

		private Status(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
		
		public static Status byValue(String value) {
			if (value == null) {
				return UNKNOWN;
			}
			Status[] values = values();
			for (Status status : values) {
				if (value.equals(status.getValue())) {
					return status;
				}
			}
			return UNKNOWN;
		}
		
	}
	
	private Status componentStatus;
	private State traceEnabled;
	private State developerMode;
	private State awaitsRestart;

	protected CodeTracingStatus() {
		super(ResponseType.CODE_TRACING_STATUS,
				BASE_PATH + CODE_TRACING_STATUS, CODE_TRACING_STATUS);
	}

	protected CodeTracingStatus(String prefix, int occurrance) {
		super(ResponseType.CODE_TRACING_STATUS, prefix, CODE_TRACING_STATUS,
				occurrance);
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
	public Status getComponentStatus() {
		return componentStatus;
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

	protected void setComponentStatus(Status componentStatus) {
		this.componentStatus = componentStatus;
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
