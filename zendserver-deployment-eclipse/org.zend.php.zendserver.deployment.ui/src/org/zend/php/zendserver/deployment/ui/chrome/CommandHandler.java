package org.zend.php.zendserver.deployment.ui.chrome;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;

/**
 * Executes commands and sends responses.
 * CommandHandler finds command for provided commandId, executes it, collects any errors and
 * sends them back in a JSON response.
 * 
 * JSON response contains one mandatory field 'status' with value "Error" or "Success" and
 * one optional field 'message' with the details. For example:
 * <pre>
 * { status : "Error", message: "Unknown command" }
 * </pre>
 */
public class CommandHandler {

	private static final String STATUS = "status"; //$NON-NLS-1$
	private static final String MESSAGE = "message"; //$NON-NLS-1$
	private static final String STATUS_ERROR = "Error"; //$NON-NLS-1$
	private static final String STATUS_SUCCESS = "Success"; //$NON-NLS-1$

	public void handle(HttpRequest request, HttpResponse response) throws IOException {

		Map params = request.getParameterMap();
		
		String path = request.getPath();
		String errorMessage = null;
		try {
			executeCommand(path, params, request);
		} catch (ExecutionException e) {
			errorMessage = e.getMessage();
		} catch (NotDefinedException e) {
			errorMessage = e.getMessage();
		} catch (NotEnabledException e) {
			errorMessage = e.getMessage();
		} catch (NotHandledException e) {
			errorMessage = e.getMessage();
		}

		response.setContentType("application/json;charset=utf-8"); //$NON-NLS-1$
		response.setStatus(HttpResponse.OK);
		
		Map<String, String> result = new HashMap<String, String>();
		result.put(STATUS, errorMessage == null ? STATUS_SUCCESS : STATUS_ERROR);
		if (errorMessage != null) {
			result.put(MESSAGE, errorMessage);
		}
		
		String json = toJson(result);
		response.send(json);
	}

	private String toJson(Map<String, String> result) {
		StringBuilder sb = new StringBuilder();
		sb.append("{"); //$NON-NLS-1$
		boolean isFirst = true;
		for (Entry<String, String> entry : result.entrySet()) {
			if (isFirst) {
				isFirst = false;				
			} else {
				sb.append(", "); //$NON-NLS-1$
			}
			sb.append(entry.getKey());
			sb.append(": \""); //$NON-NLS-1$
			sb.append(entry.getValue());
			sb.append("\""); //$NON-NLS-1$
		}
		sb.append("}"); //$NON-NLS-1$
		return sb.toString();
	}

	private void executeCommand(String path, Map params, HttpRequest request) throws ExecutionException, NotDefinedException, NotEnabledException, NotHandledException {
		Path cmdPath = new Path(path);
		if (cmdPath.segmentCount() == 0) {
			throw new IllegalArgumentException("Request path is missing command name."); //$NON-NLS-1$
		}
		String commandId = cmdPath.lastSegment();
		
		ICommandService cmdService = (ICommandService) PlatformUI
				.getWorkbench().getService(ICommandService.class);
		Command cmd = cmdService.getCommand(commandId);
		
		ExecutionEvent event = new ExecutionEvent(cmd, params, null, request);
		cmd.executeWithChecks(event);
	}

}
