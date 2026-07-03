package de.tutter05.kuhfuss.commands;

import de.tutter05.kuhfuss.ChestCracker;
import de.tutter05.kuhfuss.utils.Command;
import static de.tutter05.kuhfuss.utils.ReflectionHelper.*;

public class DelayCommand extends Command {

    public DelayCommand() {
        super("delay", "delay (amount_of_ms)", "Sets the delay (in milliseconds) between opening attempts. By default this value is 250ms");
    }

    @Override
    public void executeCommand(String[] args) {

        try {

            if(args.length == 0) {
                sendChatMessage("§aCurrent delay is %d".formatted(ChestCracker.packetDelay));
            } else {
                int delay = Integer.parseInt(args[0]);
                ChestCracker.packetDelay = delay;
                sendChatMessage("§aSet delay to %d".formatted(delay));
            }

        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            sendChatMessage("§cInvalid arguments! Type .help for help");

        }


    }
}
