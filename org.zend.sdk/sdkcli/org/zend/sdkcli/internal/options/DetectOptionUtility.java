/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.internal.options;

import java.lang.reflect.Method;

import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

/**
 * detects options in command
 * 
 * @author Roy, 2011
 */
public class DetectOptionUtility {

	private static final String BOOLEAN_OPTION = "is";
	private static final String STRING_OPTION = "get";

	public static void addOption(Class subject,
			Options options) {
		final Method[] methods = subject.getMethods();
		for (Method method : methods) {
			if (method.getName().startsWith(STRING_OPTION)
					&& method.getAnnotation(Option.class) != null) {
				final Option a = method.getAnnotation(Option.class);
				final org.apache.commons.cli.Option o = OptionBuilder
						.withArgName(a.opt()).withDescription(a.description())
						.withLongOpt(a.longOpt()).withType(String.class)
						.create(a.opt());
				options.addOption(o);
			} else if (method.getName().startsWith(BOOLEAN_OPTION)
					&& method.getAnnotation(Option.class) != null) {
				final Option a = method.getAnnotation(Option.class);
				final org.apache.commons.cli.Option o = OptionBuilder
						.withArgName(a.opt()).withDescription(a.description())
						.withLongOpt(a.longOpt()).withType(Boolean.class)
						.create(a.opt());
				options.addOption(o);
			}
		}
	}
}
