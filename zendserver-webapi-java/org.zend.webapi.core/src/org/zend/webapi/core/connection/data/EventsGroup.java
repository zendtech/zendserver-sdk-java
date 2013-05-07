/*******************************************************************************
 * Copyright (c) Feb 12, 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;

/**
 * Details about an issue's evensGroup. This even describes general details
 * about groups of events, unlike the �event� element which provides in-depth
 * details.
 * 
 * @author Wojciech Galanciak, 2012
 * @since 1.2
 */
public class EventsGroup extends AbstractResponseData {

	private static final String EVENTS_GROUP = "/eventsGroup";
	
	private int eventsGroupId;
	private int eventsCount;
	private String startTime;
	private int serverId;
	private String classId;
	private String userData;
	private String javaBacktrace;
	private int execTime;
	private int avgExecTime;
	private int memUsage;
	private int avgMemUsage;
	private int avgOutputSize;
	private String load;

	protected EventsGroup() {
		super(ResponseType.EVENTS_GROUP, BASE_PATH + EVENTS_GROUP, EVENTS_GROUP);
	}

	protected EventsGroup(String prefix, int occurrance) {
		super(ResponseType.EVENTS_GROUP, prefix, EVENTS_GROUP, occurrance);
	}

	public boolean accept(IResponseDataVisitor visitor) {
		if (visitor.preVisit(this)) {
			return visitor.visit(this);
		}
		return false;
	}

	/**
	 * @return Event Group's identifier
	 */
	public int getEventsGroupId() {
		return eventsGroupId;
	}

	/**
	 * @return The number of events in the current event-group
	 */
	public int getEventsCount() {
		return eventsCount;
	}

	/**
	 * @return Time (DD-MMM-YYYY HH:MM) for the first event in the current
	 *         event-group
	 */
	public String getStartTime() {
		return startTime;
	}

	/**
	 * @return Identifier of the cluster-member where the event took place. This
	 *         field will be empty if no serverId is applicable
	 */
	public int getServerId() {
		return serverId;
	}

	/**
	 * @return the classId
	 */
	public String getClassId() {
		return classId;
	}

	/**
	 * @return the userData
	 */
	public String getUserData() {
		return userData;
	}

	/**
	 * @return the javaBacktrace
	 */
	public String getJavaBacktrace() {
		return javaBacktrace;
	}

	/**
	 * @return the execTime
	 */
	public int getExecTime() {
		return execTime;
	}

	/**
	 * @return the avgExecTime
	 */
	public int getAvgExecTime() {
		return avgExecTime;
	}

	/**
	 * @return the memUsage
	 */
	public int getMemUsage() {
		return memUsage;
	}

	/**
	 * @return the avgMemUsage
	 */
	public int getAvgMemUsage() {
		return avgMemUsage;
	}

	/**
	 * @return the avgOutputSize
	 */
	public int getAvgOutputSize() {
		return avgOutputSize;
	}

	/**
	 * @return the load
	 */
	public String getLoad() {
		return load;
	}

	protected void setEventsGroupId(int eventsGroupId) {
		this.eventsGroupId = eventsGroupId;
	}

	protected void setEventsCount(int eventsCount) {
		this.eventsCount = eventsCount;
	}

	protected void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	protected void setServerId(int serverId) {
		this.serverId = serverId;
	}

	protected void setClassId(String classId) {
		this.classId = classId;
	}

	protected void setUserData(String userData) {
		this.userData = userData;
	}

	protected void setJavaBacktrace(String javaBacktrace) {
		this.javaBacktrace = javaBacktrace;
	}

	protected void setExecTime(int execTime) {
		this.execTime = execTime;
	}

	protected void setAvgExecTime(int avgExecTime) {
		this.avgExecTime = avgExecTime;
	}

	protected void setMemUsage(int memUsage) {
		this.memUsage = memUsage;
	}

	protected void setAvgMemUsage(int avgMemUsage) {
		this.avgMemUsage = avgMemUsage;
	}

	protected void setAvgOutputSize(int avgOutputSize) {
		this.avgOutputSize = avgOutputSize;
	}

	protected void setLoad(String load) {
		this.load = load;
	}

}
