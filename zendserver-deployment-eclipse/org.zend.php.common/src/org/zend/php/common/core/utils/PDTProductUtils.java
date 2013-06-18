package org.zend.php.common.core.utils;

import org.eclipse.core.runtime.Platform;

public class PDTProductUtils {
	public static final String PDT_PRODUCT_ID = "org.zend.php.product";

	public static boolean isPDtProduct() {
		return PDT_PRODUCT_ID.equals(Platform.getProduct().getId());
	}
}
