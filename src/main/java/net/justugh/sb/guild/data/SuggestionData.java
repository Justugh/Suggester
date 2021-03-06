package net.justugh.sb.guild.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class SuggestionData {

    private String suggestion;
    private long suggestionChannel, messageID;
    private Date suggestionDate;
    private SuggestionState suggestionState;

    public enum SuggestionState {
        OPEN,
        ACCEPTED,
        REJECTED
    }

}
