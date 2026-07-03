package de.tutter05.kuhfuss.commands;

import de.tutter05.kuhfuss.listener.ChatListener;
import de.tutter05.kuhfuss.utils.Command;
import static de.tutter05.kuhfuss.utils.ReflectionHelper.*;

import java.util.Optional;

public class HelpCommand extends Command {

    public HelpCommand() {
        super("help", "help", "Shows a list of all available commands.");
    }

    @Override
    public void executeCommand(String[] args) {
        sendChatMessage("§7§lAvailable commands: ");
        ChatListener.commands.forEach(command -> {
            String name = command.getName();
            String description = command.getDescription();

            Optional<String> usage = command.getUsage();

            sendChatMessage("§7  .%s - %s. %s".formatted(name, description, usage.map(s -> "Usage: " + s).orElse("")));
        });
        sendChatMessage("§7§lBrackets indicate optional arguments");

    }
}
