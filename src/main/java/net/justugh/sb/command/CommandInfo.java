package net.justugh.sb.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

@Getter
@Setter
@AllArgsConstructor
public class CommandInfo {

    private Member caller;
    private String[] args;
    private TextChannel channel;

}