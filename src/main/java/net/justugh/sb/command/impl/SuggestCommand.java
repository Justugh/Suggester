package net.justugh.sb.command.impl;

import net.justugh.sb.Bot;
import net.justugh.sb.command.Command;
import net.justugh.sb.command.CommandInfo;

public class SuggestCommand extends Command {

    public SuggestCommand() {
        super("suggest", null);
    }

    @Override
    public void execute(CommandInfo info) {
        Bot.getInstance().getSuggestionManager().sendSuggestion(info.getCaller(), info.getChannel().getIdLong(), String.join(" ", info.getArgs()));
    }
}
