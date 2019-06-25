package net.justugh.sb.manager;

import lombok.Getter;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.justugh.sb.Bot;
import net.justugh.sb.command.Command;
import net.justugh.sb.command.CommandInfo;
import net.justugh.sb.command.impl.*;
import net.justugh.sb.guild.config.GuildConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Getter
public class CommandManager extends ListenerAdapter {

    private List<Command> commandList = new ArrayList<>();

    public CommandManager() {
        commandList.add(new ConfigCommand());
        commandList.add(new SuggestCommand());
        commandList.add(new HelpCommand());
        commandList.add(new AcceptCommand());
        commandList.add(new RejectCommand());
        commandList.add(new UserCommand());
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();

        if (event.getMember() == null) {
            return;
        }

        if (message.startsWith(Bot.getInstance().getGuildConfigCache().get(event.getGuild().getIdLong()).getCommandIndicator())) {
            String commandName = message.substring(1).split(" ")[0];
            String[] commandArgs = getCleanArgs(message.replaceFirst(Pattern.quote(Bot.getInstance().getGuildConfigCache().get(event.getGuild().getIdLong()).getCommandIndicator() + commandName), ""));

            if (commandList.stream().anyMatch(command -> command.getName().equalsIgnoreCase(commandName))) {
                event.getMessage().delete().queue();
                Command command = commandList.stream().filter(tempCommand -> tempCommand.getName().equalsIgnoreCase(commandName)).findFirst().orElse(null);

                if (command == null) {
                    return;
                }

                Permission permission = command.getPermission(event.getGuild().getIdLong());

                if (permission == Permission.UNKNOWN || event.getMember().hasPermission(permission)) {
                    command.execute(new CommandInfo(event.getGuild(), event.getMember(), event.getMessage(), commandArgs, event.getChannel()));
                }
            }
        }
    }

    /**
     * Checks to determine if a string is a valid command.
     *
     * @param name The command name.
     * @return Whether or not it's a valid command.
     */
    public boolean isValidCommand(String name) {
        return commandList.stream().anyMatch(command -> command.getName().equalsIgnoreCase(name));
    }

    /**
     * Get the command's guild permission.
     *
     * @param guildID The command's guild.
     * @param command The command.
     * @return The command's permission from the Guild config.
     */
    public Permission getPermission(long guildID, Command command) {
        GuildConfig guildConfig = Bot.getInstance().getGuildConfigCache().get(guildID);

        if(!guildConfig.getCommandPermissions().containsKey(command.getName())) {
            guildConfig.getCommandPermissions().put(command.getName(), command.getDefaultPermission());
            guildConfig.save();
            return command.getDefaultPermission();
        }

        return guildConfig.getCommandPermissions().get(command.getName());
    }

    /**
     * Gets clean args from a provided command string.
     *
     * @param rawArgs The raw arguments.
     * @return Clean arguments.
     */
    private String[] getCleanArgs(String rawArgs) {
        return rawArgs.isEmpty() ? new String[]{} : rawArgs.trim().isEmpty() ? new String[]{} : rawArgs.trim().split(" ");
    }

}
