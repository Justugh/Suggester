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
import net.justugh.sb.util.EmbedUtil;

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
            GuildConfig guildConfig = Bot.getInstance().getGuildConfig(info.getGuild().getIdLong());

            if (info.getArgs()[0].equalsIgnoreCase("default-channel")) {
                if (info.getArgs().length >= 2) {
                    if (info.getMessage().getMentionedChannels().isEmpty()) {
                        EmbedUtil.error(info.getChannel(), "You have to tag a channel!");
                        return;
                    }

                    TextChannel taggedChannel = info.getMessage().getMentionedChannels().get(0);
                    guildConfig.setDefaultSuggestionChannel(taggedChannel.getIdLong());
                    guildConfig.save();
                    displayUpdate(info.getChannel(), info.getCaller(), "Successfully set " + taggedChannel.getAsMention() + " as the default suggestion channel!");
                } else {
                    TextChannel taggedChannel = info.getChannel();
                    guildConfig.setDefaultSuggestionChannel(taggedChannel.getIdLong());
                    guildConfig.save();
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

                    guildConfig.getSuggestionChannels().put(mainChannel.getIdLong(), suggestionChannel.getIdLong());
                    guildConfig.save();
                    displayUpdate(info.getChannel(), info.getCaller(), "Successfully linked " + suggestionChannel.getAsMention() + " as " + mainChannel.getAsMention() + "'s suggestion channel!");
                } else {
                    EmbedUtil.error(info.getChannel(), "You must provide two channels to link! `>config link-channel <channel> <channel>`!");
                }
            } else if (info.getArgs()[0].equalsIgnoreCase("prefix")) {
                if (info.getArgs().length >= 2) {
                    guildConfig.setCommandIndicator(info.getArgs()[1]);
                    guildConfig.save();
                    displayUpdate(info.getChannel(), info.getCaller(), "Successfully set " + info.getArgs()[1] + " as the command prefix!");
                } else {
                    EmbedUtil.error(info.getChannel(), "You have to provide a new prefix! `>config prefix <string>`!");
                }
            }  else if (info.getArgs()[0].equalsIgnoreCase("linked")) {
                if (info.getArgs().length >= 2) {
                    if (info.getMessage().getMentionedChannels().isEmpty()) {
                        EmbedUtil.error(info.getChannel(), "You have to tag a channel!");
                        return;
                    }

                    TextChannel mainChannel = info.getMessage().getMentionedChannels().get(0);

                    if(guildConfig.getSuggestionChannels().get(mainChannel.getIdLong()) == null) {
                        EmbedUtil.error(info.getChannel(), mainChannel.getAsMention() + " doesn't have a linked channel! It uses the default channel.");
                        return;
                    }

                    TextChannel linkedChannel = Bot.getInstance().getJdaInstance().getTextChannelById(guildConfig.getSuggestionChannels().get(mainChannel.getIdLong()));

                    if(linkedChannel == null) {
                        guildConfig.getSuggestionChannels().remove(mainChannel.getIdLong());
                        EmbedUtil.error(info.getChannel(), mainChannel.getAsMention() + " doesn't have a linked channel! It uses the default channel.");
                        return;
                    }

                    EmbedUtil.info(info.getChannel(), mainChannel.getName() + "'s Linked Channel", mainChannel.getAsMention() + "'s linked channel is " + linkedChannel.getAsMention() + ".");
                } else {
                    if(guildConfig.getSuggestionChannels().get(info.getChannel().getIdLong()) == null) {
                        EmbedUtil.error(info.getChannel(), info.getChannel().getAsMention() + " doesn't have a linked channel! It uses the default channel.");
                        return;
                    }

                    TextChannel linkedChannel = Bot.getInstance().getJdaInstance().getTextChannelById(guildConfig.getSuggestionChannels().get(info.getChannel().getIdLong()));

                    if(linkedChannel == null) {
                        guildConfig.getSuggestionChannels().remove(info.getChannel().getIdLong());
                        EmbedUtil.error(info.getChannel(), info.getChannel().getAsMention() + " doesn't have a linked channel! It uses the default channel.");
                        return;
                    }

                    EmbedUtil.info(info.getChannel(), info.getChannel().getName() + "'s Linked Channel", info.getChannel().getAsMention() + "'s linked channel is " + linkedChannel.getAsMention() + ".");                }
            } else if(info.getArgs()[0].equalsIgnoreCase("display")) {
                displayConfig(info.getChannel());
            } else if(info.getArgs()[0].equalsIgnoreCase("reaction-permission")) {
                if(info.getArgs().length >= 2) {
                    Permission permission = Arrays.stream(Permission.values()).filter(perm -> perm.name().equalsIgnoreCase(info.getArgs()[1])).findFirst().orElse(null);

                    if(permission == null) {
                        EmbedUtil.error(info.getChannel(), "You must provide a permission! Valid Permissions: `CREATE_INSTANT_INVITE, " +
                                "KICK_MEMBERS, BAN_MEMBERS, ADMINISTRATOR, MANAGE_CHANNEL, MANAGE_SERVER, MESSAGE_ADD_REACTION, VIEW_AUDIT_LOGS, PRIORITY_SPEAKER, " +
                                "VIEW_CHANNEL, MESSAGE_READ, MESSAGE_WRITE, MESSAGE_TTS, MESSAGE_MANAGE, MESSAGE_EMBED_LINKS, MESSAGE_ATTACH_FILES, MESSAGE_HISTORY, " +
                                "MESSAGE_MENTION_EVERYONE, MESSAGE_EXT_EMOJI, VOICE_CONNECT, VOICE_SPEAK, VOICE_MUTE_OTHERS, VOICE_DEAF_OTHERS, VOICE_MOVE_OTHERS, " +
                                "VOICE_USE_VAD, NICKNAME_CHANGE, NICKNAME_MANAGE, MANAGE_ROLES, MANAGE_PERMISSIONS, MANAGE_WEBHOOKS, MANAGE_EMOTES, UNKNOWN`");
                        return;
                    }

                    guildConfig.setReactionStatePermission(permission);
                    guildConfig.save();
                    EmbedUtil.info(info.getChannel(), "Permission Updated", "Successfully set reaction permission to " + permission.name() + "!");
                } else {
                    EmbedUtil.error(info.getChannel(), "You must provide a permission! Valid Permissions: `CREATE_INSTANT_INVITE, " +
                            "KICK_MEMBERS, BAN_MEMBERS, ADMINISTRATOR, MANAGE_CHANNEL, MANAGE_SERVER, MESSAGE_ADD_REACTION, VIEW_AUDIT_LOGS, PRIORITY_SPEAKER, " +
                            "VIEW_CHANNEL, MESSAGE_READ, MESSAGE_WRITE, MESSAGE_TTS, MESSAGE_MANAGE, MESSAGE_EMBED_LINKS, MESSAGE_ATTACH_FILES, MESSAGE_HISTORY, " +
                            "MESSAGE_MENTION_EVERYONE, MESSAGE_EXT_EMOJI, VOICE_CONNECT, VOICE_SPEAK, VOICE_MUTE_OTHERS, VOICE_DEAF_OTHERS, VOICE_MOVE_OTHERS, " +
                            "VOICE_USE_VAD, NICKNAME_CHANGE, NICKNAME_MANAGE, MANAGE_ROLES, MANAGE_PERMISSIONS, MANAGE_WEBHOOKS, MANAGE_EMOTES, UNKNOWN`");
                }
            } else if(info.getArgs()[0].equalsIgnoreCase("permission")) {
                if(info.getArgs().length >= 3) {
                    if(!Bot.getInstance().getCommandManager().isValidCommand(info.getArgs()[1])) {
                        EmbedUtil.error(info.getChannel(), "You must provide a valid command (do not include the prefix)!");
                        return;
                    }

                    Permission permission = Arrays.stream(Permission.values()).filter(perm -> perm.name().equalsIgnoreCase(info.getArgs()[2])).findFirst().orElse(null);

                    if(permission == null) {
                        EmbedUtil.error(info.getChannel(), "You must provide a permission! Valid Permissions: `CREATE_INSTANT_INVITE, " +
                                "KICK_MEMBERS, BAN_MEMBERS, ADMINISTRATOR, MANAGE_CHANNEL, MANAGE_SERVER, MESSAGE_ADD_REACTION, VIEW_AUDIT_LOGS, PRIORITY_SPEAKER, " +
                                "VIEW_CHANNEL, MESSAGE_READ, MESSAGE_WRITE, MESSAGE_TTS, MESSAGE_MANAGE, MESSAGE_EMBED_LINKS, MESSAGE_ATTACH_FILES, MESSAGE_HISTORY, " +
                                "MESSAGE_MENTION_EVERYONE, MESSAGE_EXT_EMOJI, VOICE_CONNECT, VOICE_SPEAK, VOICE_MUTE_OTHERS, VOICE_DEAF_OTHERS, VOICE_MOVE_OTHERS, " +
                                "VOICE_USE_VAD, NICKNAME_CHANGE, NICKNAME_MANAGE, MANAGE_ROLES, MANAGE_PERMISSIONS, MANAGE_WEBHOOKS, MANAGE_EMOTES, UNKNOWN`");
                        return;
                    }

                    guildConfig.getCommandPermissions().put(info.getArgs()[1], permission);
                    guildConfig.save();
                    EmbedUtil.info(info.getChannel(), "Permission Updated", "Successfully set " + info.getArgs()[1] + " command permission to " + permission.name() + "!");
                } else {
                    EmbedUtil.error(info.getChannel(), "You must provide a valid command and permission!");
                }
            } else if(info.getArgs()[0].equalsIgnoreCase("permissions")) {
                displayCommandPermissions(info.getChannel());
            } else if(info.getArgs()[0].equalsIgnoreCase("dmnotification")) {
                if(info.getArgs().length >= 2) {
                    Bot.getInstance().getGuildConfig(info.getGuild().getIdLong()).setDmNotification(Boolean.parseBoolean(info.getArgs()[1]));
                    Bot.getInstance().getGuildConfig(info.getGuild().getIdLong()).save();

                    EmbedUtil.info(info.getChannel(), "DM Notifactions Updated", "DM notifactions have been set to: " + Boolean.parseBoolean(info.getArgs()[1]));
                } else {
                    EmbedUtil.error(info.getChannel(), "You must put true or false!");
                }
            }
        }
    }

    /**
     * Display command permissions embed.
     *
     * @param channel The channel to send it to.
     */
    private void displayCommandPermissions(TextChannel channel) {
        GuildConfig guildConfig = Bot.getInstance().getGuildConfigCache().get(channel.getGuild().getIdLong());

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setDescription("**Note**: All values can be changed using " + guildConfig.getCommandIndicator() +  "config permission <command> <permission>")
                .setColor(new Color(10601844))
                .setTimestamp(Instant.now())
                .setFooter("Configuration", Bot.getInstance().getJdaInstance().getSelfUser().getAvatarUrl())
                .setAuthor("Configuration", null, Bot.getInstance().getJdaInstance().getSelfUser().getAvatarUrl());

        for (Command command : Bot.getInstance().getCommandManager().getCommandList()) {
            embedBuilder.addField(guildConfig.getCommandIndicator() + command.getName(), Bot.getInstance().getCommandManager().getPermission(channel.getGuild().getIdLong(), command).name(), true);
        }

        Message message = new MessageBuilder(embedBuilder.build()).build();

        channel.sendMessage(message).queue();
    }

    /**
     * Send update embed.
     *
     * @param displayChannel The channel to send it to.
     * @param caller The person creating the embed.
     * @param content The content of the embed.
     */
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

    /**
     * Display configuration values in an embed.
     *
     * @param channel The channel to send the embed to.
     */
    private void displayConfig(TextChannel channel) {
        GuildConfig guildConfig = Bot.getInstance().getGuildConfigCache().get(channel.getGuild().getIdLong());

        Message message = new MessageBuilder(new EmbedBuilder()
                .setDescription("**Note**: All values can be changed using " + guildConfig.getCommandIndicator() +  "config")
                .setColor(new Color(10601844))
                .setTimestamp(Instant.now())
                .setFooter("Configuration", Bot.getInstance().getJdaInstance().getSelfUser().getAvatarUrl())
                .setAuthor("Configuration", null, Bot.getInstance().getJdaInstance().getSelfUser().getAvatarUrl())
                .addField("Command Prefix", guildConfig.getCommandIndicator(), true)
                .addField("DM Notification", guildConfig.isDmNotification() + "", true)
                .addField("Reaction State Permission", guildConfig.getReactionStatePermission().name(), true)
                .addField("Default Suggestion Channel", Bot.getInstance().getJdaInstance().getTextChannelById(guildConfig.getDefaultSuggestionChannel()).getAsMention(), true)
                .addField("Linked Channels", "Use >config linked to see linked channels.", true)
                .addField("Command Permissions", "Use >config permissions to see command permissions.", true)
                .build()).build();

        channel.sendMessage(message).queue();
    }

    /**
     * Display command help information.
     *
     * @param channel The channel to send it to.
     */
    private void displayHelp(TextChannel channel) {
        Message message = new MessageBuilder(new EmbedBuilder()
                .setDescription("**Note**: All commands start with " + Bot.getInstance().getGuildConfigCache().get(channel.getGuild().getIdLong()).getCommandIndicator() + "config")
                .setColor(new Color(10601844))
                .setTimestamp(Instant.now())
                .setFooter("Command List", Bot.getInstance().getJdaInstance().getSelfUser().getAvatarUrl())
                .setAuthor("Configuration Commands", null, Bot.getInstance().getJdaInstance().getSelfUser().getAvatarUrl())
                .addField("display", "Display the configuration values.", true)
                .addField("permissions", "Display the command permissions.", true)
                .addField("dmnotification <boolean>", "Set whether or not to DM about submitted suggestions.", true)
                .addField("prefix <string>", "Set the command prefix.", true)
                .addField("linked (channel)", "See the channels linked channel.", true)
                .addField("default-channel (channel)", "Set the default suggestion channel.", true)
                .addField("reaction-permission <permission>", "Set the reaction accept/reject permission.", true)
                .addField("link-channel <channel> <channel>", "Link a channel to another channel, channel \nones suggestions will be sent to channel two.", true)
                .addField("permission <command> <permission>", "Set a commands permission requirement.", true)
                .build()).build();

        channel.sendMessage(message).queue();
    }
}
