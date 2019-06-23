package net.justugh.sb.guild.data;

import com.google.gson.GsonBuilder;
import lombok.Getter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Getter
public class UserData {

    private long userID;
    private long guildID;

    private List<SuggestionData> suggestions = new ArrayList<>();

    public UserData(long userID, long guildID) {
        this.userID = userID;
        this.guildID = guildID;
    }

    public void save() {
        try {
            FileUtils.write(getUserFile(), new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(this), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getUserFile() {
        return new File("guilds" + File.separator + guildID + File.separator + "users" + File.separator + userID + ".json");
    }

    public SuggestionData getSuggestionByMessage(long messageID) {
        return suggestions.stream().filter(suggestionData -> suggestionData.getMessageID() == messageID).findFirst().orElse(null);
    }

    public long getAcceptedSuggestions() {
        return suggestions.stream().filter(suggestionData -> suggestionData.getSuggestionState() == SuggestionData.SuggestionState.ACCEPTED).count();
    }

    public long getRejectedSuggestions() {
        return suggestions.stream().filter(suggestionData -> suggestionData.getSuggestionState() == SuggestionData.SuggestionState.REJECTED).count();
    }

}
