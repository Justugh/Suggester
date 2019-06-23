package net.justugh.sb.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.justugh.sb.Bot;

import java.awt.*;
import java.time.Instant;

public class EmbedUtil {

    public static void error(TextChannel channel, String content) {
        Message message = new MessageBuilder(new EmbedBuilder()
                .setDescription(content)
                .setColor(new Color(13632027))
                .setTimestamp(Instant.now())
                .setFooter("Error Tracking", Bot.getInstance().getJdaInstance().getSelfUser().getAvatarUrl())
                .setAuthor("An error occurred", null, Bot.getInstance().getJdaInstance().getSelfUser().getAvatarUrl())
                .build()).build();

        channel.sendMessage(message).queue();
    }

    public static void info(TextChannel channel, String title, String content) {
        Message message = new MessageBuilder(new EmbedBuilder()
                .setDescription(content)
                .setColor(new Color(10601844))
                .setTimestamp(Instant.now())
                .setFooter("Information", Bot.getInstance().getJdaInstance().getSelfUser().getAvatarUrl())
                .setAuthor(title, null, Bot.getInstance().getJdaInstance().getSelfUser().getAvatarUrl())
                .build()).build();

        channel.sendMessage(message).queue();
    }

    public static void info(PrivateChannel channel, String title, String content) {
        Message message = new MessageBuilder(new EmbedBuilder()
                .setDescription(content)
                .setColor(new Color(10601844))
                .setTimestamp(Instant.now())
                .setFooter("Information", Bot.getInstance().getJdaInstance().getSelfUser().getAvatarUrl())
                .setAuthor(title, null, Bot.getInstance().getJdaInstance().getSelfUser().getAvatarUrl())
                .build()).build();

        channel.sendMessage(message).queue();
    }

}
