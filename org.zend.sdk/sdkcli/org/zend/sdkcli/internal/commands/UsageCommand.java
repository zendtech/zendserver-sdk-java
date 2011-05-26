package org.zend.sdkcli.internal.commands;

import java.util.Collection;

import org.apache.commons.cli.Option;
import org.zend.sdkcli.CommandFactory;
import org.zend.sdkcli.CommandType;
import org.zend.sdkcli.ICommand;
import org.zend.sdkcli.ParseError;

public class UsageCommand implements ICommand {
	
	@Override
	public boolean execute(CommandLine cmdLine) {

		String verb = cmdLine.getArgument(1);
		String dirObj = cmdLine.getArgument(2);
		CommandLine helpCmd = null;
		ICommand cmd = null;
		try {
			helpCmd = new CommandLine(new String[] {verb, dirObj});
			cmd = CommandFactory.createCommand(helpCmd);
		} catch (ParseError e) {
			// ignore
		}
		
		if (helpCmd != null && cmd != null && (! (cmd instanceof UsageCommand))) {
			CommandType type = CommandType.byCommandLine(helpCmd);
			printCommandUsage(type);
			printCommandOptions(cmd);
			return true;
		}
		
		printAvailableCommands();
		return true;
	}

	private void printAvailableCommands() {
		for (CommandType type : CommandType.values()) {
			ICommand cmd = CommandFactory.createCommand(type);

			printCommandUsage(type);
		}
	}

	private void printCommandUsage(CommandType type) {
		String dirObj = type.getDirectObject();
		if (dirObj == null) {
			dirObj = "";
		}
		System.out.println("zend "+type.getVerb() +" "+dirObj+" [options] - "+type.getInfo());
	}
	
	private void printCommandOptions(ICommand cmd) {
		CommandOptions opts = cmd.getOptions();
		if (opts != null) {
			System.out.println(" Options:");
			Collection collection = opts.getOptions();
			for (Object o : collection) {
				Option opt = (Option) o;
				System.out.printf("  -%-3s %s\n", opt.getOpt(), opt.getDescription());
			}				
			System.out.println();
		}
	}

	public CommandOptions getOptions() {
		return null;
	}
}
