package net.justugh.sb.guild.config;

import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

@Getter
@Setter
public class GuildConfig {

    private Long guildID;
    private String commandIndicator = ">";

    private long defaultSuggestionChannel = 0;
    private HashMap<Long, Long> suggestionChannels = new HashMap<>();

    public GuildConfig(long guildID) {
        this.guildID = guildID;
    }

    /**
     * Save the configuration.
     */
    public void save() {
        try {
            FileUtils.write(new File("guilds" + File.separator + guildID + File.separator + "config.json"), new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(this), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
