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

        if(!configFile.exists()) {
            config = new Config();

            try {
                FileUtils.write(configFile, new GsonBuilder().setPrettyPrinting().create().toJson(config), Charset.defaultCharset());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                config = new GsonBuilder().create().fromJson(new FileReader(configFile), Config.class);

                // We do this to make sure the config has any new fields from the Config class
                FileUtils.write(configFile, new GsonBuilder().setPrettyPrinting().create().toJson(config), Charset.defaultCharset());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Bot getInstance() {
        return instance;
    }
}