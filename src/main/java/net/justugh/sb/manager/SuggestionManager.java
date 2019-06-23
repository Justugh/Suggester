package net.justugh.sb.manager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.justugh.sb.Bot;
import net.justugh.sb.config.Config;
import net.justugh.sb.data.SuggestionData;

import javax.annotation.Nonnull;
import java.awt.*;
import java.time.Instant;
import java.util.Date;

public class SuggestionManager extends ListenerAdapter {

    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        if(!isSuggestionChannel(event.getChannel().getIdLong()) || event.getChannel().retrieveMessageById(event.getMessageId()).complete().getAuthor() != event.getJDA().getSelfUser()) {
            return;
        }

        if(event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();

            if(event.getReactionEmote().getEmoji().equalsIgnoreCase("⛔")) {
                changeSuggestionState(message, event.getMember(), "Rejected");
                Bot.getInstance().getUserManager().getUserData(event.getUser().getIdLong()).getSuggestionByMessage(message.getIdLong()).setSuggestionState(SuggestionData.SuggestionState.REJECTED);
            } else if(event.getReactionEmote().getEmoji().equalsIgnoreCase("✔")) {
                changeSuggestionState(message, event.getMember(), "Accepted");
                Bot.getInstance().getUserManager().getUserData(event.getUser().getIdLong()).getSuggestionByMessage(message.getIdLong()).setSuggestionState(SuggestionData.SuggestionState.ACCEPTED);
            }

            Bot.getInstance().getUserManager().getUserData(event.getUser().getIdLong()).save();
        }
    }

    /**
     * Send a suggestion.
     *
     * @param suggester The Member suggesting.
     * @param channelID The channel ID where the suggestion was sent.
     * @param suggestion The raw string of the suggestion.
     * @return The suggestion channel
     */
    public TextChannel sendSuggestion(Member suggester, long channelID, String suggestion) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(Color.GREEN)
                .setAuthor("Suggestion from " + suggester.getEffectiveName() + "#" + suggester.getUser().getDiscriminator(), null, suggester.getUser().getAvatarUrl())
                .setDescription("**Suggestion**: \n \n"+ suggestion + "\n \n_ _")
                .setFooter("Status: Open")
                .setTimestamp(Instant.now());
        Message message = new MessageBuilder(embedBuilder.build()).build();

        TextChannel suggestionChannel = getSuggestionChannel(channelID);

        if(suggestionChannel == null) {
            System.out.println("[Suggester]: Default suggestion channel is null.");
            Bot.getInstance().getJdaInstance().getTextChannelById(channelID).sendMessage(suggester.getAsMention() + " The default suggestion channel isn't properly configured. Fix this using `>config default-channel`!").queue();
            return null;
        }

        Message sentMessage = suggestionChannel.sendMessage(message).complete();
        sentMessage.addReaction("✅").queue();
        sentMessage.addReaction("❎").queue();

        Bot.getInstance().getUserManager().getUserData(suggester.getIdLong()).getSuggestions().add(new SuggestionData(suggestion, suggestionChannel.getIdLong(), sentMessage.getIdLong(), new Date(), SuggestionData.SuggestionState.OPEN));
        Bot.getInstance().getUserManager().getUserData(suggester.getIdLong()).save();

        return suggestionChannel;
    }

    /**
     * Change the suggestion state
     * of a suggestion.
     *
     * @param message The message instance of the suggestion.
     * @param modifier The member who modified the suggestion.
     * @param state The new "state" of the suggestion.
     */
    public void changeSuggestionState(Message message, Member modifier, String state) {
        if(message.getEmbeds().isEmpty()) {
            return;
        }

        if(message.getEmbeds().get(0) == null || message.getEmbeds().get(0).getAuthor() == null) {
            return;
        }

        EmbedBuilder embedBuilder = new EmbedBuilder(message.getEmbeds().get(0))
                .setAuthor(state + " " + message.getEmbeds().get(0).getAuthor().getName(), null, message.getEmbeds().get(0).getAuthor().getIconUrl())
                .setFooter(state + " by " + modifier.getEffectiveName() + "#" + modifier.getUser().getDiscriminator());
        message.editMessage(embedBuilder.build()).queue();
        message.clearReactions().queue();
    }

    /**
     * Return whether or not a channel
     * is a registered suggestion channel.
     *
     * @param channelID The channel ID.
     * @return If the channel is a suggestion Channel.
     */
    public boolean isSuggestionChannel(long channelID) {
        return Bot.getInstance().getConfig().getSuggestionChannels().values().contains(channelID) || Bot.getInstance().getConfig().getDefaultSuggestionChannel() == channelID;
    }

    /**
     * Get parallel suggestion channel by
     * channel ID. Returns default suggestion
     * channel if a parallel suggestion
     * channel cannot be found.
     *
     * @param channelID The specified channel ID
     * @return An instance of the parallel suggestion channel.
     */
    public TextChannel getSuggestionChannel(long channelID) {
        Config config = Bot.getInstance().getConfig();

        if(!config.getSuggestionChannels().containsKey(channelID)) {
            return Bot.getInstance().getJdaInstance().getTextChannelById(config.getDefaultSuggestionChannel());
        } else {
            TextChannel suggestionChannel = Bot.getInstance().getJdaInstance().getTextChannelById(config.getSuggestionChannels().get(channelID));

            if(suggestionChannel != null) {
                return suggestionChannel;
            } else {
                return Bot.getInstance().getJdaInstance().getTextChannelById(config.getDefaultSuggestionChannel());
            }
        }
    }

}