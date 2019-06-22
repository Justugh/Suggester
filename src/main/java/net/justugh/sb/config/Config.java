package net.justugh.sb.config;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Activity;

import java.util.HashMap;

@Getter
public class Config {

    private String token = "";
    private String commandIndicator = ">";
    private String playingMessage = "to your suggestions.";
    private Activity.ActivityType activity = Activity.ActivityType.LISTENING;

    private HashMap<Long, Long> suggestionChannels = new HashMap<>();

}
