package org.zend.php.common.welcome;

import java.net.URL;

import org.zend.php.common.Messages;

public class PdtWelcomePageEditorInput extends WelcomePageEditorInput {

	public PdtWelcomePageEditorInput(URL fileURL, int persistent, String string) {
		super(fileURL, persistent, string, "/pdt_directory.xml", true, Messages.UpdatingZendEclispePDT, false, false);
	}
}
