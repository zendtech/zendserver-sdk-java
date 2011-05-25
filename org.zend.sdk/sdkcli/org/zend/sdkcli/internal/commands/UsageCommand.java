package org.zend.sdkcli.internal.commands;

import java.util.Collection;

import org.apache.commons.cli.Option;
import org.zend.sdkcli.CommandFactory;
import org.zend.sdkcli.CommandType;
import org.zend.sdkcli.ICommand;

public class UsageCommand implements ICommand {
	
	@Override
	public boolean execute(CommandLine cmdLine) {

		for (CommandType type : CommandType.values()) {
			ICommand cmd = CommandFactory.createCommand(type);

			String dirObj = type.getDirectObject();
			if (dirObj == null) {
				dirObj = "";
			}
			System.out.println("zend "+type.getVerb() +" "+dirObj+" [options] - "+type.getInfo());
			CommandOptions opts = cmd.getOptions();
			if (opts != null) {
				System.out.println(" Options:");
				Collection collection = opts.getOptions();
				for (Object o : collection) {
					Option opt = (Option) o;
					System.out.printf("  -%-20s %s\n", opt.getOpt(), opt.getDescription());
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
