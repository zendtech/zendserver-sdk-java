/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.internal.commands;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.zend.sdkcli.ParseError;
import org.zend.sdklib.internal.target.ZendTargetAutoDetect;
import org.zend.sdklib.target.IZendTarget;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.response.ResponseCode;

public class CreateTargetCommandLine extends TargetAwareCommand {

	private static final String LOCALHOST = "localhost";
	private static final String ID = "t";

	public CreateTargetCommandLine(CommandLine commandLine) throws ParseError {
		super(commandLine);
	}

	@Override
	public boolean execute() {
		if (hasOption(LOCALHOST)) {
			final ZendTargetAutoDetect autoDetect = new ZendTargetAutoDetect();
			final IZendTarget target = autoDetect
					.getLocalZendServer(getValue(ID));
			if (autoDetect != null) {
				try {
					final IZendTarget add = getTargetManager().add(target);
				} catch (WebApiException e) {
					getLogger()
							.error("Coudn't connect to local host server, please make "
									+ "sure you the server is up and its version is 5.5 and up.");
					getLogger()
							.error("More information provided by localhost:");

					final ResponseCode responseCode = e.getResponseCode();
					if (responseCode != null) {
						getLogger().error("\tError code: " + responseCode);
					}
					final String message = e.getMessage();
					if (message != null) {
						getLogger().error("\tError message: " + message);
					}
				}
			}
		}

		return false;
	}

	@Override
	protected void setupOptions() {
		// auto detection mode
		final Option option = OptionBuilder.withDescription(
				"auto detect the localhost target").create(LOCALHOST);

		//
		options.addOption(option);
	}

}
