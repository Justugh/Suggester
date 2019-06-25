package net.justugh.sb.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.Permission;
import net.justugh.sb.Bot;

@Getter
@Setter
@AllArgsConstructor
public abstract class Command {

    private String name;
    private Permission defaultPermission;

    public abstract void execute(CommandInfo info);

    public Permission getPermission(long guildID) {
        return Bot.getInstance().getCommandManager().getPermission(guildID, this);
    }

}