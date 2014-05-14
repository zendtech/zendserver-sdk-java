package org.zend.php.zendserver.deployment.ui.zendserver;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.zend.php.zendserver.deployment.ui.zendserver.messages"; //$NON-NLS-1$
	public static String LocalZendServerCompositeFragment_CannotDetectError;
	public static String LocalZendServerCompositeFragment_Desc;
	public static String LocalZendServerCompositeFragment_DetectTitle;
	public static String LocalZendServerCompositeFragment_EmptyNameError;
	public static String LocalZendServerCompositeFragment_Name;
	public static String LocalZendServerCompositeFragment_NameLabel;
	public static String LocalZendServerCompositeFragment_NameTooltip;
	public static String LocalZendServerType_Name;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
