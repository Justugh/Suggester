package net.justugh.sb;

import com.google.gson.GsonBuilder;
import lombok.Getter;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.justugh.sb.config.Config;
import net.justugh.sb.guild.config.GuildConfig;
import net.justugh.sb.manager.BotManager;
import net.justugh.sb.manager.CommandManager;
import net.justugh.sb.manager.SuggestionManager;
import net.justugh.sb.manager.UserManager;
import org.apache.commons.io.FileUtils;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

@Getter
public class Bot {

    private static Bot instance;
    private JDA jdaInstance;
    private Config config;
    private HashMap<Long, GuildConfig> guildConfigCache = new HashMap<>();

    private SuggestionManager suggestionManager;
    private UserManager userManager;

    public static void main(String[] args) {
        new Bot().launch();
    }

    private Bot() {
        instance = this;
    }

    /**
     * Do all the essentials to
     * launch the Discord bot
     */
    private void launch() {
        loadConfig();

        if (config.getToken().isEmpty() || config.getToken() == null) {
            System.out.println("[Suggester]: Cannot start the bot, auth token is invalid.");
            System.exit(0);
            return;
        }

        try {
            JDABuilder builder = new JDABuilder(AccountType.BOT);
            builder.setToken(config.getToken());
            builder.setAutoReconnect(true);
            builder.setStatus(config.getOnlineStatus());
            builder.setActivity(Activity.of(config.getActivity(), config.getPlayingMessage()));

            jdaInstance = builder.build();
            jdaInstance.awaitReady();
            loadGuildConfigs();
            loadManagers();
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load managers along with registering
     * them as listeners.
     */
    private void loadManagers() {
        suggestionManager = new SuggestionManager();
        userManager = new UserManager();

        jdaInstance.addEventListener(new BotManager());
        jdaInstance.addEventListener(new CommandManager());
        jdaInstance.addEventListener(suggestionManager);
        jdaInstance.addEventListener(userManager);
    }

    /**
     * Load the configuration
     */
    private void loadConfig() {
        File configFile = new File("config.json");

        if (!configFile.exists()) {
            config = new Config();

            try {
                FileUtils.write(configFile, new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(config), StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                config = new GsonBuilder().create().fromJson(new FileReader(configFile), Config.class);

                // We do this to make sure the config has any new fields from the GuildConfig class
                FileUtils.write(configFile, new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create().toJson(config), StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadGuildConfigs() {
        for (Guild guild : jdaInstance.getSelfUser().getMutualGuilds()) {
            File guildConfigFile = new File("guilds" + File.separator + guild.getIdLong() + File.separator + "config.json");

            if (!guildConfigFile.exists()) {
                GuildConfig guildConfig = new GuildConfig(guild.getIdLong());
                guildConfig.setDefaultSuggestionChannel(guild.getDefaultChannel().getIdLong());

                guildConfigCache.put(guild.getIdLong(), guildConfig);

                try {
                    FileUtils.write(guildConfigFile, new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(guildConfig), StandardCharsets.UTF_8);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    GuildConfig guildConfig = new GsonBuilder().create().fromJson(new FileReader(guildConfigFile), GuildConfig.class);

                    if(guildConfig.getDefaultSuggestionChannel() == 0) {
                        guildConfig.setDefaultSuggestionChannel(guild.getDefaultChannel().getIdLong());
                    }

                    guildConfigCache.put(guild.getIdLong(), guildConfig);

                    // We do this to make sure the config has any new fields from the GuildConfig class
                    FileUtils.write(guildConfigFile, new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create().toJson(guildConfig), StandardCharsets.UTF_8);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Bot getInstance() {
        return instance;
    }
}