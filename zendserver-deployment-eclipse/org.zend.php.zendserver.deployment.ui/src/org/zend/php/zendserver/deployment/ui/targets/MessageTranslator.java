package org.zend.php.zendserver.deployment.ui.targets;

import org.zend.php.zendserver.deployment.ui.Messages;

/**
 * Translates cryptic error messages to user-friendlier error messages.
 *
 */
public class MessageTranslator {

	public String translate(String message) {
		if (message == null) {
			return null;
		}
		
		if ((Messages.MessageTranslator_UnknownResponseCode.equals(message)) || 
				(Messages.MessageTranslator_UnknownEOF.equals(message))) {
			return Messages.MessageTranslator_DoesntLookLikeZendServer+message;
		}
		
		return message;
	}

}
