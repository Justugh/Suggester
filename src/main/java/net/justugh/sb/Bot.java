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

    private CommandManager commandManager;
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
            // We need to do this to make sure we can get the bots guilds to load the configs.
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
        commandManager = new CommandManager();
        suggestionManager = new SuggestionManager();
        userManager = new UserManager();

        jdaInstance.addEventListener(new BotManager());
        jdaInstance.addEventListener(commandManager);
        jdaInstance.addEventListener(suggestionManager);
        jdaInstance.addEventListener(userManager);
    }

    /**
     * Load the configuration
     */
    private void loadConfig() {
        File configFile = new File("config.json");

        try {
            if (!configFile.exists()) {
                config = new Config();

                FileUtils.write(configFile, new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(config), StandardCharsets.UTF_8);
            } else {
                config = new GsonBuilder().create().fromJson(new FileReader(configFile), Config.class);

                // We do this to make sure the config has any new fields from the GuildConfig class
                FileUtils.write(configFile, new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create().toJson(config), StandardCharsets.UTF_8);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load Guild configuration files.
     */
    private void loadGuildConfigs() {
        for (Guild guild : jdaInstance.getSelfUser().getMutualGuilds()) {
            File guildFile = new File("guilds" + File.separator + guild.getIdLong() + File.separator + "config.json");

            try {
                if (!guildFile.exists()) {
                    GuildConfig guildConfig = new GuildConfig(guild.getIdLong());

                    if(guild.getDefaultChannel() != null) {
                        guildConfig.setDefaultSuggestionChannel(guild.getDefaultChannel().getIdLong());
                    }

                    guildConfigCache.put(guild.getIdLong(), guildConfig);

                    FileUtils.write(guildFile, new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(guildConfig), StandardCharsets.UTF_8);
                } else {
                    GuildConfig guildConfig = new GsonBuilder().create().fromJson(new FileReader(guildFile), GuildConfig.class);

                    if(guildConfig.getDefaultSuggestionChannel() == 0) {
                        if(guild.getDefaultChannel() != null) {
                            guildConfig.setDefaultSuggestionChannel(guild.getDefaultChannel().getIdLong());
                        }
                    }

                    guildConfigCache.put(guild.getIdLong(), guildConfig);

                    // We do this to make sure the config has any new fields from the GuildConfig class
                    FileUtils.write(guildFile, new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create().toJson(guildConfig), StandardCharsets.UTF_8);
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get the Guild configuration.
     *
     * @param guildID The Guild's ID.
     * @return The instance of the Guild Config.
     */
    public GuildConfig getGuildConfig(long guildID) {
        return guildConfigCache.get(guildID);
    }

    /**
     * Get the Bot instance.
     *
     * @return The instance of the Bot.
     */
    public static Bot getInstance() {
        return instance;
    }
}