package net.justugh.sb.command.impl;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.justugh.sb.Bot;
import net.justugh.sb.command.Command;
import net.justugh.sb.command.CommandInfo;
import net.justugh.sb.guild.data.UserData;
import net.justugh.sb.util.EmbedUtil;

import java.awt.*;
import java.time.Instant;

public class UserCommand extends Command {

    public UserCommand() {
        super("user", Permission.ADMINISTRATOR);
    }

    @Override
    public void execute(CommandInfo info) {
        if(info.getArgs().length == 0) {
            EmbedUtil.error(info.getChannel(), "You must tag a user!");
        } else {
            if(info.getMessage().getMentionedMembers().isEmpty()) {
                EmbedUtil.error(info.getChannel(), "You must tag a user!");
                return;
            }

            displayUserData(info.getChannel(), info.getCaller(), info.getMessage().getMentionedMembers().get(0));
        }
    }

    private void displayUserData(TextChannel channel, Member requester, Member user) {
        UserData userData = Bot.getInstance().getUserManager().getUserData(user.getGuild().getIdLong(), user.getIdLong());

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(new Color(10601844))
                .setTimestamp(Instant.now())
                .setFooter("Requested by " + requester.getEffectiveName() + "#" + requester.getUser().getDiscriminator(), requester.getUser().getAvatarUrl())
                .setAuthor("User stats for " + user.getEffectiveName() + "#" + user.getUser().getDiscriminator(), null, user.getUser().getAvatarUrl())
                .addField("User ID", userData.getUserID() + "", true)
                .addField("Created Suggestions", userData.getSuggestions().size() + "", true)
                .addField("Accepted Suggestions", userData.getAcceptedSuggestions() + "", true)
                .addField("Rejected Suggestions", userData.getRejectedSuggestions() + "", true)
                .addField("Raw Data", Bot.getInstance().getUserManager().postToHastebin(userData), true);

        channel.sendMessage(embedBuilder.build()).queue();
    }
}
