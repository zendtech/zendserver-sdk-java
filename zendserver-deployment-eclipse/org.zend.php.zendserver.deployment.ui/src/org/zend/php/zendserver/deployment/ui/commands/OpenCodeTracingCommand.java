package org.zend.php.zendserver.deployment.ui.commands;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.internal.preferences.Base64;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.zend.php.zendserver.deployment.ui.chrome.HttpRequest;

/**
 * Opens AMF Code tracing event.
 * This command is called by DevCloud Google Chrome extension.
 *
 */
public class OpenCodeTracingCommand extends AbstractHandler {

	private static final String OPEN_TRACE_VIEW_COMMAND = "com.zend.php.zendserver.ui.openTracerView"; //$NON-NLS-1$
	private static final String FILE_NAME = "fileName"; //$NON-NLS-1$

	public Object execute(ExecutionEvent event) throws ExecutionException {
		Object request = event.getApplicationContext();
		
		byte[] data = ((HttpRequest)request).getBody().toString().getBytes();
		
		byte[] decoded = Base64.decode(data);
		
		File file = null;
		try {
			file = File.createTempFile("amf", "amf"); //$NON-NLS-1$ //$NON-NLS-2$
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(decoded);
			fos.close();
		} catch (IOException e) {
			throw new ExecutionException("An error occured while storing Zend Server Event trace file: "+e.getMessage(), e); //$NON-NLS-1$
		}

		ICommandService cmdService = (ICommandService) PlatformUI
				.getWorkbench().getService(ICommandService.class);
		Command cmd = cmdService.getCommand(OPEN_TRACE_VIEW_COMMAND);
		
		Map<String, String> params = new HashMap<String, String>();
		params.put(FILE_NAME, file.getAbsolutePath());
		ExecutionEvent event2 = new ExecutionEvent(cmd, params, null, null);
		try {
			cmd.executeWithChecks(event2);
		} catch (NotDefinedException e) {
			throw new ExecutionException("Zend Server Event Tracing feature could not be found", e); //$NON-NLS-1$
		} catch (NotEnabledException e) {
			throw new ExecutionException(e.getMessage(), e);
		} catch (NotHandledException e) {
			throw new ExecutionException(e.getMessage(), e);
		}
		
		return null;
	}
	
}
