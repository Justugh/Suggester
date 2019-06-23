package net.justugh.sb.command.impl;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.justugh.sb.Bot;
import net.justugh.sb.command.Command;
import net.justugh.sb.command.CommandInfo;
import net.justugh.sb.util.EmbedUtil;

public class RejectCommand extends Command {

    public RejectCommand() {
        super("reject", Permission.ADMINISTRATOR);
    }

    @Override
    public void execute(CommandInfo info) {
        if(info.getArgs().length == 0) {
            EmbedUtil.error(info.getChannel(), "You must provide a suggestion to reject! `>reject <message ID>`");
        } else {
            try {
                if(Bot.getInstance().getGuildConfigCache().get(info.getGuild().getIdLong()).getSuggestionChannels().keySet().contains(info.getChannel().getIdLong())) {
                    Message message = Bot.getInstance().getJdaInstance().getTextChannelById(Bot.getInstance().getGuildConfigCache().get(info.getGuild().getIdLong()).getSuggestionChannels().get(info.getChannel().getIdLong()))
                            .retrieveMessageById(info.getArgs()[0]).complete();

                    Bot.getInstance().getSuggestionManager().changeSuggestionState(message, info.getCaller(), "Rejected");
                }

                if(!Bot.getInstance().getSuggestionManager().isSuggestionChannel(info.getGuild().getIdLong(), info.getChannel().getIdLong())) {
                    Message message = Bot.getInstance().getJdaInstance().getTextChannelById(Bot.getInstance().getGuildConfigCache().get(info.getGuild().getIdLong()).getDefaultSuggestionChannel()).retrieveMessageById(info.getArgs()[0]).complete();

                    Bot.getInstance().getSuggestionManager().changeSuggestionState(message, info.getCaller(), "Rejected");
                    return;
                }

                Message message = info.getChannel().retrieveMessageById(info.getArgs()[0]).complete();

                Bot.getInstance().getSuggestionManager().changeSuggestionState(message, info.getCaller(), "Rejected");
            } catch (IllegalArgumentException e) {
                EmbedUtil.error(info.getChannel(), "You must provide a valid message ID!");
            }
        }
    }
}
