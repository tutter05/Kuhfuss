package de.tutter05.kuhfuss.listener;

import de.tutter05.kuhfuss.commands.*;
import de.tutter05.kuhfuss.commands.*;
import de.tutter05.kuhfuss.utils.Command;

import static de.tutter05.kuhfuss.utils.ReflectionHelper.*;

import java.util.Arrays;
import java.util.List;

public class ChatListener {

    public static List<Command> commands = Arrays.asList(
            new HelpCommand(),
            new DetachCommand(),
            new BruteforceCommand(),
            new StopCommand(),
            new DelayCommand()
    );

    /**
     * Called when a chat message is sent. Injected by ClientPacketTransformer
     */
    public static void onSendChat(final String text) {
        for (Command cmd : commands) {
            if(text.toLowerCase().startsWith(cmd.getName())) {
                String[] args = text.split(" ");
                String[] restArgs = Arrays.copyOfRange(args, 1, args.length);
                cmd.executeCommand(restArgs);
                return;
            }
        }

        sendChatMessage(
                "§cUnknown command \"%s\". Type .help for a list of all available commands."
                        .formatted(text.split(" ")[0])
        );
    }

}

