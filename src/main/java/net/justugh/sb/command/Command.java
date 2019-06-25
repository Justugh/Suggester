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

    /**
     * Execute the command.
     *
     * @param info Command information.
     */
    public abstract void execute(CommandInfo info);

    /**
     * Get the Command's Guild permission.
     *
     * @param guildID The Commands Guild ID.
     * @return The Command's Guild permission.
     */
    public Permission getPermission(long guildID) {
        return Bot.getInstance().getCommandManager().getPermission(guildID, this);
    }

}