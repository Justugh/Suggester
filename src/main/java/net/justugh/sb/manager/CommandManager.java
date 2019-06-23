package net.justugh.sb.manager;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.justugh.sb.Bot;
import net.justugh.sb.command.Command;
import net.justugh.sb.command.CommandInfo;
import net.justugh.sb.command.impl.*;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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

                if (command.getPermission() == null || command.getPermission() == Permission.UNKNOWN || event.getMember().hasPermission(command.getPermission())) {
                    command.execute(new CommandInfo(event.getGuild(), event.getMember(), event.getMessage(), commandArgs, event.getChannel()));
                }
            }
        }
    }

    /**
     * Gets clean args from a provided command string.
     *
     * @param rawArgs The raw arguments.
     * @return Clean arguments.
     */
    public String[] getCleanArgs(String rawArgs) {
        return rawArgs.isEmpty() ? new String[]{} : rawArgs.trim().isEmpty() ? new String[]{} : rawArgs.trim().split(" ");
    }

}
