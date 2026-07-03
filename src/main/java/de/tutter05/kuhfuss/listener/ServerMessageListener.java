package de.tutter05.kuhfuss.listener;

import de.tutter05.kuhfuss.ChestCracker;
import static de.tutter05.kuhfuss.utils.ReflectionHelper.*;

public class ServerMessageListener {

    /**
     * Called whenever the server sends a message to the player
     * @param message message being sent
     * @return true if the message should be suppressed, else false
     */
    public static boolean receiveServerMessage(String message) {

        if(message == null || !ChestCracker.isBruteforcerActive()) {
            return true;
        }

        if (getCooldownMessage() != null && message.endsWith(getCooldownMessage())) {
            sendChatMessage("§e⏳ SecurityCraft rate limit detected. Increasing delay and retrying...");
            ChestCracker.packetDelay += 10;
            ChestCracker.bruteforcer.previousElement();
            return false;
        }

        return true;
    }

}
