package net.justugh.sb.command.impl;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.justugh.sb.Bot;
import net.justugh.sb.command.Command;
import net.justugh.sb.command.CommandInfo;

public class ConfigCommand extends Command {

    public ConfigCommand() {
        super("config", Permission.ADMINISTRATOR);
    }

    @Override
    public void execute(CommandInfo info) {
        if(info.getArgs().length == 0) {
            displayHelp();
        } else {
            if(info.getArgs()[0].equalsIgnoreCase("default-channel")) {
                if(info.getArgs().length >= 2) {
                    if(info.getMessage().getMentionedChannels().isEmpty()) {
                        info.getChannel().sendMessage(info.getCaller().getAsMention() + " You have to tag a channel!").queue();
                        return;
                    }

                    TextChannel taggedChannel = info.getMessage().getMentionedChannels().get(0);
                    Bot.getInstance().getConfig().setDefaultSuggestionChannel(taggedChannel.getIdLong());
                    Bot.getInstance().getConfig().save();
                    info.getChannel().sendMessage(info.getCaller().getAsMention() + " Successfully set " + taggedChannel.getAsMention() + " as the default suggestion channel!").queue();
                } else {
                    TextChannel taggedChannel = info.getChannel();
                    Bot.getInstance().getConfig().setDefaultSuggestionChannel(taggedChannel.getIdLong());
                    Bot.getInstance().getConfig().save();
                    info.getChannel().sendMessage(info.getCaller().getAsMention() + " Successfully set " + taggedChannel.getAsMention() + " as the default suggestion channel!").queue();
                }
            }
        }
    }

    private void displayHelp() {

    }
}
