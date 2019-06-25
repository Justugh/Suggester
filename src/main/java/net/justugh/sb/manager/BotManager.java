package net.justugh.sb.manager;

import com.google.gson.GsonBuilder;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.justugh.sb.Bot;
import net.justugh.sb.guild.config.GuildConfig;
import org.apache.commons.io.FileUtils;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BotManager extends ListenerAdapter {

    @Override
    public void onGuildJoin(@Nonnull GuildJoinEvent event) {
        if(event.getGuild().getDefaultChannel() == null) {
            return;
        }

        File guildFile = new File("guilds" + File.separator + event.getGuild().getIdLong() + File.separator + "config.json");
        GuildConfig guildConfig = new GuildConfig(event.getGuild().getIdLong());

        guildConfig.setDefaultSuggestionChannel(event.getGuild().getDefaultChannel().getIdLong());
        Bot.getInstance().getGuildConfigCache().put(event.getGuild().getIdLong(), guildConfig);

        try {
            FileUtils.write(guildFile, new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(guildConfig), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("[Suggester]: Error creating new Guild config.");
            e.printStackTrace();
        }
    }
}
