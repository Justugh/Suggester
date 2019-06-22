package net.justugh.sb.command.impl;

import net.justugh.sb.command.Command;
import net.justugh.sb.command.CommandInfo;

public class ExampleCommand extends Command {

    public ExampleCommand() {
        super("example", null);
    }

    @Override
    public void execute(CommandInfo info) {
        info.getChannel().sendMessage("Testing " + info.getCaller().getAsMention()).queue();
    }
}
