package net.justugh.sb.config;

import com.google.gson.GsonBuilder;
import lombok.Getter;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Getter
public class Config {

    private String token = "";
    private OnlineStatus onlineStatus = OnlineStatus.ONLINE;
    private String playingMessage = "your suggestions.";
    private Activity.ActivityType activity = Activity.ActivityType.LISTENING;

    /**
     * Save the configuration.
     */
    public void save() {
        try {
            FileUtils.write(new File( "config.json"), new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(this), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
