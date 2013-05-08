/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies.
 * All rights reserved. This program and the accompanying materials
 * are the copyright of Zend Technologies and is protected under
 * copyright laws of the United States.
 * You must not copy, adapt or redistribute this document for 
 * any use.
 *******************************************************************************/
package org.zend.php.library.internal.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Wojciech Galanciak, 2012
 *
 */
public class CommandExecutor {

	class EnvironmentVar {

		public String name;
		public String value;

		public EnvironmentVar(String name, String value) {
			this.name = name;
			this.value = value;
		}
	}

	private Process process;

	private ILogDevice ouputLogDevice;
	private ILogDevice errorLogDevice;
	private String workingDirectory;
	private List<EnvironmentVar> environmentVars;

	private StringBuffer commandOutput;
	private StringBuffer commandError;
	private AsyncStreamReader commandOutThread;
	private AsyncStreamReader commandErrorThread;

	public void setOutputLogDevice(ILogDevice logDevice) {
		ouputLogDevice = logDevice;
	}

	public void setErrorLogDevice(ILogDevice logDevice) {
		errorLogDevice = logDevice;
	}

	public void setWorkingDirectory(String workingDirectory) {
		this.workingDirectory = workingDirectory;
	}

	public void setEnvironmentVar(String name, String value) {
		if (environmentVars == null) {
			environmentVars = new ArrayList<EnvironmentVar>();
		}
		environmentVars.add(new EnvironmentVar(name, value));
	}

	public String getCommandOutput() {
		return commandOutput.toString();
	}

	public String getCommandError() {
		return commandError.toString();
	}

	public int runCommand(String... segments) throws IOException {
		return runCommand(buildCommand(segments));
	}

	public int runCommand(String commandLine) throws IOException {
		process = runCommandHelper(commandLine);
		startOutputAndErrorReadThreads(process.getInputStream(),
				process.getErrorStream());
		int exitStatus = -1;
		try {
			exitStatus = process.waitFor();
		} catch (InterruptedException e) {
			// TODO log it
		} finally {
			notifyOutputAndErrorReadThreadsToStopReading();
		}
		return exitStatus;
	}

	private Process runCommandHelper(String commandLine) throws IOException {
		Process process = null;
		if (workingDirectory == null) {
			process = Runtime.getRuntime().exec(commandLine, getEnvTokens());
		} else {
			process = Runtime.getRuntime().exec(commandLine, getEnvTokens(),
					new File(workingDirectory));
		}
		return process;
	}

	private void startOutputAndErrorReadThreads(InputStream processOut,
			InputStream processErr) {
		commandOutput = new StringBuffer();
		commandOutThread = new AsyncStreamReader(processOut, commandOutput,
				ouputLogDevice);
		commandOutThread.start();
		commandError = new StringBuffer();
		commandErrorThread = new AsyncStreamReader(processErr, commandError,
				errorLogDevice);
		commandErrorThread.start();
	}

	private void notifyOutputAndErrorReadThreadsToStopReading() {
		commandOutThread.stopReading();
		commandErrorThread.stopReading();
	}

	private String[] getEnvTokens() {
		if (environmentVars == null) {
			return null;
		}
		String[] envTokens = new String[environmentVars.size()];
		int index = 0;
		for (EnvironmentVar var : environmentVars) {
			String envVarToken = var.name + '=' + var.value;
			envTokens[index++] = envVarToken;
		}
		return envTokens;
	}

	private String buildCommand(String... segments) {
		StringBuilder result = new StringBuilder();
		for (String segment : segments) {
			result.append(segment);
			result.append(" "); //$NON-NLS-1$
		}
		return result.toString().trim();
	}

}