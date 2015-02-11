package org.zend.php.zendserver.deployment.ui.zendserver;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.zend.php.zendserver.deployment.ui.zendserver.messages"; //$NON-NLS-1$
	public static String LocalZendServerCompositeFragment_BaseUrlConflictError;
	public static String LocalZendServerCompositeFragment_CannotDetectError;
	public static String LocalZendServerCompositeFragment_Desc;
	public static String LocalZendServerCompositeFragment_DetectTitle;
	public static String LocalZendServerCompositeFragment_EmptyNameError;
	public static String LocalZendServerCompositeFragment_Name;
	public static String LocalZendServerCompositeFragment_NameConflictError;
	public static String LocalZendServerCompositeFragment_NameLabel;
	public static String LocalZendServerCompositeFragment_NameTooltip;
	public static String LocalTargetDetector_CompleteMessage;
	public static String LocalTargetDetector_CompleteTitle;
	public static String LocalTargetDetector_InvalidCredentialsMessage;
	public static String LocalTargetDetector_SeeDocMessage;
	public static String LocalTargetDetector_UnsupportedMessage;
	public static String LocalTargetDetector_UnsupportedTitle;
	public static String LocalTargetDetector_WindowsPriviligesMessage;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
