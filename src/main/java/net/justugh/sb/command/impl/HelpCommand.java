package net.justugh.sb.command.impl;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
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
        super("help", Permission.ADMINISTRATOR);
    }

    @Override
    public void execute(CommandInfo info) {
        displayHelp(info.getChannel());
    }

    private void displayHelp(TextChannel channel) {
        GuildConfig guildConfig = Bot.getInstance().getGuildConfigCache().get(channel.getGuild().getIdLong());

        Message message = new MessageBuilder(new EmbedBuilder()
                .setColor(new Color(10601844))
                .setTimestamp(Instant.now())
                .setDescription("**NOTE**: You can accept/reject suggestions using ✔ and ⛔.")
                .setFooter("Command List", Bot.getInstance().getJdaInstance().getSelfUser().getAvatarUrl())
                .setAuthor("Help", null, Bot.getInstance().getJdaInstance().getSelfUser().getAvatarUrl())
                .addField(guildConfig.getCommandIndicator() + "guildConfig", "Modify configuration values.", true)
                .addField(guildConfig.getCommandIndicator() + "suggest <suggestion>", "Suggest something!", true)
                .addField(guildConfig.getCommandIndicator() + "accept <message ID>", "Accept a suggestion.", true)
                .addField(guildConfig.getCommandIndicator() + "reject <message ID>", "Reject a suggestion.", true)
                .build()).build();

        channel.sendMessage(message).queue();
    }
}
