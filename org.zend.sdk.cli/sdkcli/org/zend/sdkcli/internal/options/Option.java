/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.internal.options;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Roy, 2011
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD})
public @interface Option {

	/**
	 * @see org.apache.commons.cli.Option#getOpt()
	 */
	String opt();

	/**
	 * @see org.apache.commons.cli.Option#getDescription()
	 */
	String description();

	/**
	 * @see org.apache.commons.cli.Option#getDescription()
	 */
	boolean required();

	/**
	 * @see org.apache.commons.cli.Option#getLongOpt()
	 */
	String longOpt() default "";

	/**
	 * @see org.apache.commons.cli.Option#getLongOpt()
	 */
	Class type() default String.class;
	
	/**
	 * @see org.apache.commons.cli.Option#getDescription()
	 */
	int numberOfArgs() default 1;
	
	String argName() default "arg";
	
}
