package net.justugh.sb.manager;

import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.justugh.sb.Bot;

import javax.annotation.Nonnull;

public class BotManager extends ListenerAdapter {

    @Override
    public void onGuildJoin(@Nonnull GuildJoinEvent event) {
        if(event.getGuild().getDefaultChannel() == null) {
            return;
        }

        Bot.getInstance().getGuildConfigCache().get(event.getGuild().getIdLong()).setDefaultSuggestionChannel(event.getGuild().getDefaultChannel().getIdLong());
        Bot.getInstance().getGuildConfigCache().get(event.getGuild().getIdLong()).save();
    }

    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        if(event.getGuild().getIdLong() != 592031551777406977L) {
            return;
        }

        event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById(592846541082066953L)).queue();
    }
}
