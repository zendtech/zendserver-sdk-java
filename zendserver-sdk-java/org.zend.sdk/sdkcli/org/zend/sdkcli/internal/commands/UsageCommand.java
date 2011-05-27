package org.zend.sdkcli.internal.commands;

import java.util.Collection;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.zend.sdkcli.CommandFactory;
import org.zend.sdkcli.CommandType;
import org.zend.sdkcli.ICommand;
import org.zend.sdkcli.internal.options.DetectOptionUtility;

public class UsageCommand implements ICommand {

	@Override
	public boolean execute(CommandLine cmdLine) {

		String verb;
		String dirObj;
		if ("help".equals(cmdLine.getArgument(0))) {
			verb = cmdLine.getArgument(1);
			dirObj = cmdLine.getArgument(2);
		} else {
			verb = cmdLine.getArgument(0);
			dirObj = cmdLine.getArgument(1);
		}
		CommandLine helpCmd = null;
		ICommand cmd = null;

		helpCmd = new CommandLine(new String[] { verb, dirObj });
		cmd = CommandFactory.createCommand(helpCmd);

		if (helpCmd != null && cmd != null && (!(cmd instanceof UsageCommand))) {
			CommandType type = CommandType.byCommandLine(helpCmd);
			printCommandUsage(type);
			printCommandOptions(type);
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
		System.out.println("zend " + type.getVerb() + " " + dirObj
				+ " [options] - " + type.getInfo());
	}

	private void printCommandOptions(CommandType type) {
		Options opts = new Options();
		
		// get command specific options
		DetectOptionUtility.addOption(CommandFactory.createCommand(type)
				.getClass(), opts, true);

		if (opts.getOptions().size() > 0) {
			System.out.println(" Options:");
			Collection collection = opts.getOptions();
			for (Object o : collection) {
				Option opt = (Option) o;
				System.out.printf("  -%-3s %s%s\n", opt.getOpt(), opt
						.getDescription(), opt.isRequired() ? ""
						: " (Optional)");
			}
			System.out.println();
		}
	}

	public Options getOptions() {
		return null;
	}
}
