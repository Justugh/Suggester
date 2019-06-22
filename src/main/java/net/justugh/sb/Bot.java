package net.justugh.sb;

import com.google.gson.GsonBuilder;
import lombok.Getter;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.justugh.sb.config.Config;
import net.justugh.sb.manager.CommandManager;
import org.apache.commons.io.FileUtils;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Getter
public class Bot {

    private static Bot instance;
    private JDA jdaInstance;

    private Config config;

    public static void main(String[] args) {
        new Bot().launch();
    }

    public Bot() {
        instance = this;
    }

    private void launch() {
        loadConfig();

        if (config.getToken().isEmpty() || config.getToken() == null) {
            System.out.println("[ERROR LOGGING]: Cannot start the bot, auth token is invalid.");
            System.exit(0);
            return;
        }

        try {
            JDABuilder builder = new JDABuilder(AccountType.BOT);
            builder.setToken(config.getToken());
            builder.setAutoReconnect(true);
            builder.setStatus(OnlineStatus.ONLINE);
            builder.setActivity(Activity.of(config.getActivity(), config.getPlayingMessage()));

            jdaInstance = builder.build();
            loadManagers();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    private void loadManagers() {
        jdaInstance.addEventListener(new CommandManager());
    }

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