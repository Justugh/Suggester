package net.justugh.sb.command.impl;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.justugh.sb.Bot;
import net.justugh.sb.command.Command;
import net.justugh.sb.command.CommandInfo;
import net.justugh.sb.guild.config.GuildConfig;

import java.awt.*;
import java.time.Instant;

public class HelpCommand extends Command {

    public HelpCommand() {
        super("help", Permission.UNKNOWN);
    }

    @Override
    public void execute(CommandInfo info) {
        displayHelp(info.getChannel(), info.getCaller());
    }

    private void displayHelp(TextChannel channel, Member caller) {
        GuildConfig guildConfig = Bot.getInstance().getGuildConfigCache().get(channel.getGuild().getIdLong());

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(new Color(10601844))
                .setTimestamp(Instant.now())
                .setFooter("Command List", Bot.getInstance().getJdaInstance().getSelfUser().getAvatarUrl())
                .setAuthor("Help", null, Bot.getInstance().getJdaInstance().getSelfUser().getAvatarUrl())
                .addField(guildConfig.getCommandIndicator() + "suggest <suggestion>", "Suggest something!", true);

        if(caller.hasPermission(Bot.getInstance().getGuildConfigCache().get(channel.getGuild().getIdLong()).getReactionStatePermission())) {
            embedBuilder.setDescription("**NOTE**: You can accept/reject suggestions using ✔ and ⛔.");
        }

        if(caller.hasPermission(Bot.getInstance().getCommandManager().getPermission(channel.getGuild().getIdLong(), this))) {
            embedBuilder.addField(guildConfig.getCommandIndicator() + "config", "Modify configuration values.", true);
        }

        if(caller.hasPermission(Bot.getInstance().getCommandManager().getPermission(channel.getGuild().getIdLong(), this))) {
            embedBuilder.addField(guildConfig.getCommandIndicator() + "accept <message ID>", "Accept a suggestion.", true);
        }

        if(caller.hasPermission(Bot.getInstance().getCommandManager().getPermission(channel.getGuild().getIdLong(), this))) {
            embedBuilder.addField(guildConfig.getCommandIndicator() + "reject <message ID>", "Reject a suggestion.", true);
        }

        Message message = new MessageBuilder(embedBuilder.build()).build();

        channel.sendMessage(message).queue();
    }
}
