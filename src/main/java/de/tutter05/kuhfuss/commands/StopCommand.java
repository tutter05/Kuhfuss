package de.tutter05.kuhfuss.commands;

import de.tutter05.kuhfuss.ChestCracker;
import de.tutter05.kuhfuss.utils.Command;
import static de.tutter05.kuhfuss.utils.ReflectionHelper.*;

public class StopCommand extends Command {

    public StopCommand() {
        super("stop", "stop", "Stops the bruteforcer and returns the last attempted passcode");
    }

    @Override
    public void executeCommand(String[] args) {
        if(!ChestCracker.isBruteforcerEnabled()) {
            sendChatMessage("§cBruteforcer is not running!");
        } else {
            ChestCracker.stopBruteforcer();
            sendChatMessage("§aStopped bruteforcer at \"%s\"".formatted(ChestCracker.bruteforcer.getLastElement()));
        }
    }
}
