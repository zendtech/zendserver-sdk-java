package org.zend.php.zendserver.deployment.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * Opens AMF Code tracing event.
 * This command is called by DevCloud Google Chrome extension.
 *
 */
public class OpenCodeTracingCommand extends AbstractHandler {

	private static final String AMF_PATH = "amfPath";

	public Object execute(ExecutionEvent event) throws ExecutionException {
		String amfPath = event.getParameter(AMF_PATH);
		
		// TODO open AMF file identified by amfPath in AMF editor.
		
		return null;
	}

}
