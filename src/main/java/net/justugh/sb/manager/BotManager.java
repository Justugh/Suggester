package net.justugh.sb.manager;

import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.justugh.sb.Bot;

import javax.annotation.Nonnull;

public class BotManager extends ListenerAdapter {

    @Override
    public void onGuildJoin(@Nonnull GuildJoinEvent event) {
        if(event.getGuild().getDefaultChannel() == null) {
            return;
        }

        Bot.getInstance().getConfig().setDefaultSuggestionChannel(event.getGuild().getDefaultChannel().getIdLong());
        Bot.getInstance().getConfig().save();
    }
}
