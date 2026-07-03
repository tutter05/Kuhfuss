package de.tutter05.kuhfuss.commands;

import de.tutter05.kuhfuss.AgentMain;
import de.tutter05.kuhfuss.utils.Command;
import static de.tutter05.kuhfuss.utils.ReflectionHelper.*;

public class DetachCommand extends Command {

    public DetachCommand() {
        super("detach", "detach", "Detaches ChestCracker agent from game and revert changes");
    }

    @Override
    public void executeCommand(String[] args) {
        sendChatMessage("§aDetaching from game...");
        AgentMain.detach();
    }
}
