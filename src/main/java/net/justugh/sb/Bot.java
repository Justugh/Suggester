package net.justugh.sb;

import com.google.gson.GsonBuilder;
import lombok.Getter;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.justugh.sb.config.Config;
import net.justugh.sb.manager.BotManager;
import net.justugh.sb.manager.CommandManager;
import net.justugh.sb.manager.SuggestionManager;
import org.apache.commons.io.FileUtils;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Getter
public class Bot {

    private static Bot instance;
    private JDA jdaInstance;
    private Config config;

    private SuggestionManager suggestionManager;

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
            loadManagers();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load managers along with registering
     * them as listeners.
     */
    private void loadManagers() {
        suggestionManager = new SuggestionManager();

        jdaInstance.addEventListener(new BotManager());
        jdaInstance.addEventListener(new CommandManager());
        jdaInstance.addEventListener(suggestionManager);
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

                // We do this to make sure the config has any new fields from the Config class
                FileUtils.write(configFile, new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create().toJson(config), StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Bot getInstance() {
        return instance;
    }
}