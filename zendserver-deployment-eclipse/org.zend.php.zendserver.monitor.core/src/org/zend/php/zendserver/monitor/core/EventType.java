/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.monitor.core;

/**
 * Represents possible event types.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public enum EventType {

	SLOW_FUNCTION_EXECUTION(
			"Slow Function Execution", "slow_function_execution"), //$NON-NLS-1$ //$NON-NLS-2$

	FUNCTION_ERROR("Function Error", "function_error_"), //$NON-NLS-1$ //$NON-NLS-2$

	SLOW_QUERY_EXECUTION("Slow Query Execution", "slow_query_execution"), //$NON-NLS-1$ //$NON-NLS-2$

	SLOW_REQUEST_EXECUTION("Slow Request Execution", "slow_request_execution"), //$NON-NLS-1$ //$NON-NLS-2$

	HIGH_MEMORY_USAGE("High Memory Usage", "high_memory_usage"), //$NON-NLS-1$ //$NON-NLS-2$

	INCONSISTENT_OUTPUT_SIZE(
			"Inconsistent Output Size", "inconsistent_output_size"), //$NON-NLS-1$ //$NON-NLS-2$

	PHP_ERROR("PHP Error", "php_error"), //$NON-NLS-1$ //$NON-NLS-2$

	JAVA_EXCEPTION("Uncaught Java Exception", "java_exception"), //$NON-NLS-1$ //$NON-NLS-2$

	DATABASE_ERROR("Database Error", "database_error"), //$NON-NLS-1$ //$NON-NLS-2$

	JOB_EXECUTION_ERROR("Job Execution Error", ""), //$NON-NLS-1$ //$NON-NLS-2$

	JOB_LOGICAL_FAILURE("Job Logical Failure", ""), //$NON-NLS-1$ //$NON-NLS-2$

	JOB_EXECUTION_DELAY("Job Execution Delay", ""), //$NON-NLS-1$ //$NON-NLS-2$

	JOB_QUEUE_HIGH_CONCURRENCY_LEVEL("Job Queue High Concurrency Level", ""), //$NON-NLS-1$ //$NON-NLS-2$

	TRACER("Tracer - Failed to Write Dump File", ""), //$NON-NLS-1$ //$NON-NLS-2$

	SKEW_TIME("Skew Time", ""), //$NON-NLS-1$ //$NON-NLS-2$

	CUSTOM_EVENT(null, "custom_event"), //$NON-NLS-1$

	UNKNOWN(null, null);

	private final String rule;
	private final String link;

	private EventType(String rule, String link) {
		this.rule = rule;
		this.link = link;
	}

	public static EventType byRule(String rule) {
		if (rule == null) {
			return UNKNOWN;
		}

		EventType[] values = values();
		for (EventType type : values) {
			String r = type.getRule();
			if (r != null && rule.contains(r)) {
				return type;
			}
		}

		return UNKNOWN;
	}

	public String getRule() {
		return rule;
	}

	public String getLink() {
		return "http://files.zend.com/help/Zend-Server/zend-server.htm#" + link + ".htm"; //$NON-NLS-1$ //$NON-NLS-2$
	}

}