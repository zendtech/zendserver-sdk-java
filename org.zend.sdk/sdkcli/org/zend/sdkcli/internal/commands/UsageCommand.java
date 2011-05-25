package org.zend.sdkcli.internal.commands;

import java.util.Collection;

import org.zend.sdkcli.CommandFactory;
import org.zend.sdkcli.CommandType;
import org.zend.sdkcli.ICommand;

public class UsageCommand implements ICommand {
	
	@Override
	public boolean execute(CommandLine cmdLine) {

		for (CommandType type : CommandType.values()) {
			ICommand cmd = CommandFactory.createCommand(type);

			System.out.println(type);
			CommandOptions opts = cmd.getOptions();
			if (opts != null) {
				System.out.println("  Options:");
				Collection collection = opts.getOptions();
				for (Object o : collection) {
					System.out.println("    " + o);
				}
			}

			System.out.println();
		}

		return true;
	}

	public CommandOptions getOptions() {
		return null;
	}
}
