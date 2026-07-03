package de.tutter05.kuhfuss.listener;

import de.tutter05.kuhfuss.ChestCracker;
import static de.tutter05.kuhfuss.utils.ReflectionHelper.*;

public class ChestOpenListener {

    /**
     * Called whenever a player opens a chest, used to detect the correct code.
     * Is injected by ClientPacketListenerTransformer
     * @param title title of the chest that's being opened
     */
    public static void onChestOpened(String title) {

        if(ChestCracker.bruteForcerStatus != ChestCracker.Status.ON) return;

        if(title == null) {
            System.out.println("Chest was opened but the title was invalid. Skipping checks");
            return;
        }

        boolean chestTitleMatching = (ChestCracker.targetTitle == null && getDefaultChestTitles().contains(title))
                || title.equals(ChestCracker.targetTitle);

        if(chestTitleMatching) {
            String passcode = ChestCracker.bruteforcer.getLastElement();

            sendChatMessage("§a\uD83D\uDD13 Cracked chest at %s! Passcode=%s".formatted(ChestCracker.targetPosition, passcode));
            ChestCracker.stopBruteforcer();
        }

    }

}
