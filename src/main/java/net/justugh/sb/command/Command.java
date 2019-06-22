package net.justugh.sb.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.Permission;

@Getter
@Setter
@AllArgsConstructor
public abstract class Command {

    private String name;
    private Permission permission;

    public abstract void execute(CommandInfo info);

}