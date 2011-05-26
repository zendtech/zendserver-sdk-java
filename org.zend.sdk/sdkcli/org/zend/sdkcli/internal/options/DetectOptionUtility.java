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

	public static void addOption(Class subject, Options options) {
		addOption(subject, options, false);
	}

	public static void addOption(Class subject, Options options,
			boolean specific) {
		final Method[] methods = specific ? subject.getDeclaredMethods()
				: subject.getMethods();
		for (Method method : methods) {
			if (method.getName().startsWith(STRING_OPTION)
					&& method.getAnnotation(Option.class) != null) {
				final Option a = method.getAnnotation(Option.class);

				// create option object
				final org.apache.commons.cli.Option o = new org.apache.commons.cli.Option(
						a.opt(), a.description());
				if (a.longOpt() != null && a.longOpt().length() > 0) {
					o.setLongOpt(a.longOpt());
				}
				o.setArgs(a.numberOfArgs());
				o.setRequired(a.required());
				o.setType(a.type());

				// assign to options list
				options.addOption(o);
			} else if (method.getName().startsWith(BOOLEAN_OPTION)
					&& method.getAnnotation(Option.class) != null) {
				final Option a = method.getAnnotation(Option.class);

				// create option object
				final org.apache.commons.cli.Option o = new org.apache.commons.cli.Option(
						a.opt(), a.description());
				if (a.longOpt() != null && a.longOpt().length() > 0) {
					o.setLongOpt(a.longOpt());
				}
				o.setArgs(0);
				o.setRequired(a.required());

				// assign to options list options.addOption(o);
				options.addOption(o);
			}
		}
	}
}
