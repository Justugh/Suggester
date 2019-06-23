package net.justugh.sb.manager;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.justugh.sb.Bot;
import net.justugh.sb.command.Command;
import net.justugh.sb.command.CommandInfo;
import net.justugh.sb.command.impl.*;

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
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();

        if (!event.getChannelType().isGuild() || event.getMember() == null) {
            return;
        }

        if (message.startsWith(Bot.getInstance().getConfig().getCommandIndicator())) {
            String commandName = message.substring(1).split(" ")[0];
            String[] commandArgs = getCleanArgs(message.replaceFirst(Pattern.quote(Bot.getInstance().getConfig().getCommandIndicator() + commandName), ""));

            if (commandList.stream().anyMatch(command -> command.getName().equalsIgnoreCase(commandName))) {
                event.getMessage().delete().queue();
                Command command = commandList.stream().filter(tempCommand -> tempCommand.getName().equalsIgnoreCase(commandName)).findFirst().orElse(null);

                if (command == null) {
                    return;
                }

                if (command.getPermission() == null || command.getPermission() == Permission.UNKNOWN || event.getMember().hasPermission(command.getPermission())) {
                    command.execute(new CommandInfo(event.getMember(), event.getMessage(), commandArgs, event.getTextChannel()));
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
