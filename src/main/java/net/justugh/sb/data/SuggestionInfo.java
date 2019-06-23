package net.justugh.sb.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SuggestionInfo {

    private long suggester;
    private String suggestion;

}
