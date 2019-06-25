package net.justugh.sb.command.impl;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.justugh.sb.Bot;
import net.justugh.sb.command.Command;
import net.justugh.sb.command.CommandInfo;
import net.justugh.sb.util.EmbedUtil;

public class SuggestCommand extends Command {

    public SuggestCommand() {
        super("suggest", Permission.UNKNOWN);
    }

    @Override
    public void execute(CommandInfo info) {
        if(info.getArgs().length == 0) {
            EmbedUtil.error(info.getChannel(), "You must provide something to suggest!");
        } else {
            TextChannel channel = Bot.getInstance().getSuggestionManager().sendSuggestion(info.getCaller(), info.getChannel().getIdLong(), String.join(" ", info.getArgs()));
            EmbedUtil.info(info.getCaller().getUser().openPrivateChannel().complete(), "Suggestion Submitted", info.getCaller().getAsMention() + " Your suggestion has been submitted, view it in " + channel.getAsMention() + "!");
        }
    }
}
