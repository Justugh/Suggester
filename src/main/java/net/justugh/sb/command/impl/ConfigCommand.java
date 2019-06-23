package net.justugh.sb.command.impl;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.justugh.sb.Bot;
import net.justugh.sb.command.Command;
import net.justugh.sb.command.CommandInfo;
import net.justugh.sb.config.Config;
import net.justugh.sb.util.EmbedUtil;
import net.justugh.sb.util.StringUtil;

import java.awt.*;
import java.time.Instant;
import java.util.Arrays;

public class ConfigCommand extends Command {

    public ConfigCommand() {
        super("config", Permission.ADMINISTRATOR);
    }

    @Override
    public void execute(CommandInfo info) {
        if (info.getArgs().length == 0) {
            displayHelp(info.getChannel());
        } else {
            if (info.getArgs()[0].equalsIgnoreCase("default-channel")) {
                if (info.getArgs().length >= 2) {
                    if (info.getMessage().getMentionedChannels().isEmpty()) {
                        EmbedUtil.error(info.getChannel(), "You have to tag a channel!");
                        return;
                    }

                    TextChannel taggedChannel = info.getMessage().getMentionedChannels().get(0);
                    Bot.getInstance().getConfig().setDefaultSuggestionChannel(taggedChannel.getIdLong());
                    Bot.getInstance().getConfig().save();
                    displayUpdate(info.getChannel(), info.getCaller(), "Successfully set " + taggedChannel.getAsMention() + " as the default suggestion channel!");
                } else {
                    TextChannel taggedChannel = info.getChannel();
                    Bot.getInstance().getConfig().setDefaultSuggestionChannel(taggedChannel.getIdLong());
                    Bot.getInstance().getConfig().save();
                    displayUpdate(info.getChannel(), info.getCaller(), "Successfully set " + taggedChannel.getAsMention() + " as the default suggestion channel!");
                }
            } else if (info.getArgs()[0].equalsIgnoreCase("link-channel")) {
                if (info.getArgs().length >= 3) {
                    if (info.getMessage().getMentionedChannels().isEmpty() || info.getMessage().getMentionedChannels().size() < 2) {
                        EmbedUtil.error(info.getChannel(), "You have to tag at least two channels!");
                        return;
                    }

                    TextChannel mainChannel = info.getMessage().getMentionedChannels().get(0);
                    TextChannel suggestionChannel = info.getMessage().getMentionedChannels().get(1);

                    Bot.getInstance().getConfig().getSuggestionChannels().put(mainChannel.getIdLong(), suggestionChannel.getIdLong());
                    Bot.getInstance().getConfig().save();
                    displayUpdate(info.getChannel(), info.getCaller(), "Successfully linked " + suggestionChannel.getAsMention() + " as " + mainChannel.getAsMention() + "'s suggestion channel!");
                } else {
                    EmbedUtil.error(info.getChannel(), "You must provide two channels to link! `>config link-channel <channel> <channel>`!");
                }
            } else if (info.getArgs()[0].equalsIgnoreCase("prefix")) {
                if (info.getArgs().length >= 2) {
                    Bot.getInstance().getConfig().setCommandIndicator(info.getArgs()[1]);
                    Bot.getInstance().getConfig().save();
                    displayUpdate(info.getChannel(), info.getCaller(), "Successfully set " + info.getArgs()[1] + " as the command prefix!");
                } else {
                    EmbedUtil.error(info.getChannel(), "You have to provide a new prefix! `>config prefix <string>`!");
                }
            } else if (info.getArgs()[0].equalsIgnoreCase("status")) {
                if (info.getArgs().length >= 2) {
                    OnlineStatus status = OnlineStatus.fromKey(info.getArgs()[1]);

                    if(status == OnlineStatus.UNKNOWN) {
                        EmbedUtil.error(info.getChannel(), "Invalid status! Valid statuses: `ONLINE, IDLE, DO_NOT_DISTURB, INVISIBLE, OFFLINE`");
                        return;
                    }

                    Bot.getInstance().getConfig().setOnlineStatus(status);
                    Bot.getInstance().getConfig().save();
                    Bot.getInstance().getJdaInstance().getPresence().setStatus(status);
                    displayUpdate(info.getChannel(), info.getCaller(), "Successfully set " + status.getKey() + " as the online status (this will not update instantly)!");
                } else {
                    EmbedUtil.error(info.getChannel(), "You have to provide a new status! `>config status <status>`! Valid statuses: `ONLINE, IDLE, DO_NOT_DISTURB, INVISIBLE, OFFLINE`");
                }
            } else if (info.getArgs()[0].equalsIgnoreCase("playing")) {
                if (info.getArgs().length >= 2) {
                    Bot.getInstance().getConfig().setPlayingMessage(StringUtil.join(1, info.getArgs()));
                    Bot.getInstance().getConfig().save();
                    Bot.getInstance().getJdaInstance().getPresence().setActivity(Activity.of(Bot.getInstance().getConfig().getActivity(), Bot.getInstance().getConfig().getPlayingMessage()));
                    displayUpdate(info.getChannel(), info.getCaller(), "Successfully set " + StringUtil.join(1, info.getArgs()) + " as the playing message!");
                } else {
                    EmbedUtil.error(info.getChannel(), "You have to provide a new status! `>config playing <string>`!");
                }
            } else if (info.getArgs()[0].equalsIgnoreCase("activity")) {
                if (info.getArgs().length >= 2) {
                    Activity.ActivityType activity = Arrays.stream(Activity.ActivityType.values()).filter(activityType -> activityType.name().equalsIgnoreCase(info.getArgs()[1])).findFirst().orElse(null);

                    if(activity == null) {
                        EmbedUtil.error(info.getChannel(), "You have to provide a new activity! `>config activity <activity>`! Valid activities: `DEFAULT, STREAMING, LISTENING, WATCHING`");
                        return;
                    }

                    Bot.getInstance().getConfig().setActivity(activity);
                    Bot.getInstance().getConfig().save();
                    Bot.getInstance().getJdaInstance().getPresence().setActivity(Activity.of(Bot.getInstance().getConfig().getActivity(), Bot.getInstance().getConfig().getPlayingMessage()));
                    displayUpdate(info.getChannel(), info.getCaller(), "Successfully set " + activity.name() + " as the online status!");
                } else {
                    EmbedUtil.error(info.getChannel(), "You have to provide a new activity! `>config activity <activity>`! Valid activities: `DEFAULT, STREAMING, LISTENING, WATCHING`");
                }
            } else if (info.getArgs()[0].equalsIgnoreCase("linked")) {
                if (info.getArgs().length >= 2) {
                    if (info.getMessage().getMentionedChannels().isEmpty()) {
                        EmbedUtil.error(info.getChannel(), "You have to tag a channel!");
                        return;
                    }

                    TextChannel mainChannel = info.getMessage().getMentionedChannels().get(0);

                    if(Bot.getInstance().getConfig().getSuggestionChannels().get(mainChannel.getIdLong()) == null) {
                        EmbedUtil.error(info.getChannel(), mainChannel.getAsMention() + " doesn't have a linked channel! It uses the default channel.");
                        return;
                    }

                    TextChannel linkedChannel = Bot.getInstance().getJdaInstance().getTextChannelById(Bot.getInstance().getConfig().getSuggestionChannels().get(mainChannel.getIdLong()));

                    if(linkedChannel == null) {
                        Bot.getInstance().getConfig().getSuggestionChannels().remove(mainChannel.getIdLong());
                        EmbedUtil.error(info.getChannel(), mainChannel.getAsMention() + " doesn't have a linked channel! It uses the default channel.");
                        return;
                    }

                    EmbedUtil.info(info.getChannel(), mainChannel.getName() + "'s Linked Channel", mainChannel.getAsMention() + "'s linked channel is " + linkedChannel.getAsMention() + ".");
                } else {
                    if(Bot.getInstance().getConfig().getSuggestionChannels().get(info.getChannel().getIdLong()) == null) {
                        EmbedUtil.error(info.getChannel(), info.getChannel().getAsMention() + " doesn't have a linked channel! It uses the default channel.");
                        return;
                    }

                    TextChannel linkedChannel = Bot.getInstance().getJdaInstance().getTextChannelById(Bot.getInstance().getConfig().getSuggestionChannels().get(info.getChannel().getIdLong()));

                    if(linkedChannel == null) {
                        Bot.getInstance().getConfig().getSuggestionChannels().remove(info.getChannel().getIdLong());
                        EmbedUtil.error(info.getChannel(), info.getChannel().getAsMention() + " doesn't have a linked channel! It uses the default channel.");
                        return;
                    }

                    EmbedUtil.info(info.getChannel(), info.getChannel().getName() + "'s Linked Channel", info.getChannel().getAsMention() + "'s linked channel is " + linkedChannel.getAsMention() + ".");                }
            } else if(info.getArgs()[0].equalsIgnoreCase("display")) {
                displayConfig(info.getChannel());
            }
        }
    }

    private void displayUpdate(TextChannel displayChannel, Member caller, String content) {
        Message message = new MessageBuilder(new EmbedBuilder()
                .setDescription(content)
                .setColor(new Color(10601844))
                .setTimestamp(Instant.now())
                .setFooter("Updated by " + caller.getEffectiveName() + "#" + caller.getUser().getDiscriminator(), caller.getUser().getAvatarUrl())
                .setAuthor("Configuration Updated", null, Bot.getInstance().getJdaInstance().getSelfUser().getAvatarUrl())
                .build()).build();

        displayChannel.sendMessage(message).queue();
    }

    private void displayConfig(TextChannel channel) {
        Config config = Bot.getInstance().getConfig();

        Message message = new MessageBuilder(new EmbedBuilder()
                .setDescription("**Note**: All values can be changed using " + Bot.getInstance().getConfig().getCommandIndicator() +  "config")
                .setColor(new Color(10601844))
                .setTimestamp(Instant.now())
                .setFooter("Configuration", Bot.getInstance().getJdaInstance().getSelfUser().getAvatarUrl())
                .setAuthor("Configuration", null, Bot.getInstance().getJdaInstance().getSelfUser().getAvatarUrl())
                .addField("Command Prefix", config.getCommandIndicator(), true)
                .addField("Online Status", config.getOnlineStatus().name(), true)
                .addField("Playing Message", config.getPlayingMessage(), true)
                .addField("Activity", config.getActivity().name(), true)
                .addField("Default Suggestion Channel", Bot.getInstance().getJdaInstance().getTextChannelById(config.getDefaultSuggestionChannel()).getAsMention(), true)
                .addField("Linked Channels", "Use >config linked to see linked channels.", true)
                .build()).build();

        channel.sendMessage(message).queue();
    }

    private void displayHelp(TextChannel channel) {
        Message message = new MessageBuilder(new EmbedBuilder()
                .setDescription("**Note**: All commands start with " + Bot.getInstance().getConfig().getCommandIndicator() + "config")
                .setColor(new Color(10601844))
                .setTimestamp(Instant.now())
                .setFooter("Command List", Bot.getInstance().getJdaInstance().getSelfUser().getAvatarUrl())
                .setAuthor("Configuration Commands", null, Bot.getInstance().getJdaInstance().getSelfUser().getAvatarUrl())
                .addField("display", "Display the configuration values.", true)
                .addField("default-channel (channel)", "Set the default suggestion channel.", true)
                .addField("link-channel <channel> <channel>", "Link a channel to another channel, channel \nones suggestions will be sent to channel two.", true)
                .addField("prefix <string>", "Set the command prefix.", true)
                .addField("status <status>", "Set the online status of the bot.", true)
                .addField("playing <string>", "Set the playing message of the bot.", true)
                .addField("activity <activity>", "Set the playing message activity type.", true)
                .addField("linked (channel)", "See the channels linked channel.", true)
                .build()).build();

        channel.sendMessage(message).queue();
    }
}
