/*******************************************************************************
 * Copyright (c) May 17, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/

package org.zend.sdkcli;

import org.zend.sdkcli.internal.commands.AddRepositoryCommand;
import org.zend.sdkcli.internal.commands.AddTargetCommand;
import org.zend.sdkcli.internal.commands.CommandLine;
import org.zend.sdkcli.internal.commands.CreatePackageCommand;
import org.zend.sdkcli.internal.commands.CreateProjectCommand;
import org.zend.sdkcli.internal.commands.DeployApplicationCommand;
import org.zend.sdkcli.internal.commands.DetectTargetCommand;
import org.zend.sdkcli.internal.commands.DiscoverApplicationCommand;
import org.zend.sdkcli.internal.commands.GenerateRepositoryCommand;
import org.zend.sdkcli.internal.commands.GitCloneProjectCommand;
import org.zend.sdkcli.internal.commands.GitPushApplicationCommand;
import org.zend.sdkcli.internal.commands.ListApplicationsCommand;
import org.zend.sdkcli.internal.commands.ListRepositoriesCommand;
import org.zend.sdkcli.internal.commands.ListTargetsCommand;
import org.zend.sdkcli.internal.commands.RedeployApplicationCommand;
import org.zend.sdkcli.internal.commands.RemoveApplicationCommand;
import org.zend.sdkcli.internal.commands.RemoveRepositoryCommand;
import org.zend.sdkcli.internal.commands.RemoveTargetCommand;
import org.zend.sdkcli.internal.commands.UpdateApplicationCommand;
import org.zend.sdkcli.internal.commands.UpdateProjectCommand;
import org.zend.sdkcli.internal.commands.UpdateTargetCommand;
import org.zend.sdkcli.internal.commands.UsageCommand;

/**
 * Creates command instance.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class CommandFactory {

	/**
	 * Creates {@link ICommand} implementor instance based on a given command
	 * line.
	 * 
	 * @param line
	 *            - command line
	 * @return command instance
	 * @throws ParseError
	 */
	public static ICommand createCommand(CommandLine line) {
		CommandType type = CommandType.byCommandLine(line);

		return createCommand(type);
	}

	public static ICommand createCommand(CommandType type) {
		ICommand command = null;
		switch (type) {
		case CREATE_PROJECT:
			command = new CreateProjectCommand();
			break;
		case UPDATE_PROJECT:
			command = new UpdateProjectCommand();
			break;
		case CLONE_PROJECT:
			command = new GitCloneProjectCommand();
			break;
		case PUSH_APPLICATION:
			command = new GitPushApplicationCommand();
			break;
		case LIST_TARGETS:
			command = new ListTargetsCommand();
			break;
		case ADD_TARGET:
			command = new AddTargetCommand();
			break;
		case REMOVE_TARGET:
			command = new RemoveTargetCommand();
			break;
		case DETECT_TARGET:
			command = new DetectTargetCommand();
			break;
		case UPDATE_TARGET:
			command = new UpdateTargetCommand();
			break;
		case LIST_APPLICATIONS:
			command = new ListApplicationsCommand();
			break;
		case DEPLOY_APPLICATION:
			command = new DeployApplicationCommand();
			break;
		case REDEPLOY_APPLICATION:
			command = new RedeployApplicationCommand();
			break;
		case UPDATE_APPLICATION:
			command = new UpdateApplicationCommand();
			break;
		case REMOVE_APPLICATION:
			command = new RemoveApplicationCommand();
			break;
		case DISCOVER_APPLICATION:
			command = new DiscoverApplicationCommand();
			break;
		case CREATE_PACKAGE:
			command = new CreatePackageCommand();
			break;
		case ADD_REPOSITORY:
			command = new AddRepositoryCommand();
			break;
		case REMOVE_REPOSITORY:
			command = new RemoveRepositoryCommand();
			break;
		case LIST_REPOSITORIES:
			command = new ListRepositoriesCommand();
			break;
		case GENERATE_REPOSITORIES:
			command = new GenerateRepositoryCommand();
			break;
		default:
			command = new UsageCommand();
			break;
		}
		return command;
	}

}
