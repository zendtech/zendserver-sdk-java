/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Zend Technologies Ltd. - initial API and implementation
 *******************************************************************************/
package org.zend.php.server.internal.ui.apache;

import org.eclipse.osgi.util.NLS;

/**
 * @author Wojciech Galanciak, 2014
 *
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.zend.php.server.internal.ui.apache.messages"; //$NON-NLS-1$
	public static String LocalApacheCompositeFragment_BrowseLabel;
	public static String LocalApacheCompositeFragment_Desc;
	public static String LocalApacheCompositeFragment_ExamplePathUnix;
	public static String LocalApacheCompositeFragment_ExamplePathWin32;
	public static String LocalApacheCompositeFragment_LocationInvalidMessage;
	public static String LocalApacheCompositeFragment_LocationLabel;
	public static String LocalApacheCompositeFragment_LocationTooltip;
	public static String LocalApacheCompositeFragment_NameConflictMessage;
	public static String LocalApacheCompositeFragment_NameEmptyMessage;
	public static String LocalApacheCompositeFragment_NameLabel;
	public static String LocalApacheWizardFragment_CompositeNoinitMessage;
	public static String RefreshApacheAction_JobDesc;
	public static String RefreshApacheAction_JobName;
	public static String RefreshApacheAction_RefreshLabel;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
