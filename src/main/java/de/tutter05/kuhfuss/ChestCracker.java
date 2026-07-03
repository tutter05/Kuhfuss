package de.tutter05.kuhfuss;

import de.tutter05.kuhfuss.utils.BlockPosition;
import de.tutter05.kuhfuss.utils.Bruteforcer;

import static de.tutter05.kuhfuss.utils.ReflectionHelper.*;

public class ChestCracker {

    public static Status bruteForcerStatus = Status.OFF;
    public static boolean isPlayerInteracting = false;

    public static String targetTitle;
    public static BlockPosition targetPosition;

    public static Bruteforcer bruteforcer = null;
    public static int packetDelay = 250;

    /**
     * Returns whether the bruteforcer is currently running
     * @return true if bruteforcer is running else if its off or paused
     */
    public static boolean isBruteforcerActive() {
        return bruteForcerStatus == Status.ON;
    }

    /**
     * Returns whether the bruteforcer is enabled (active or paused)
     * @return true if status is on or paused
     */
    public static boolean isBruteforcerEnabled() {
        return bruteForcerStatus == Status.ON || bruteForcerStatus == Status.PAUSED;
    }

    public static void stopBruteforcer() {
        bruteForcerStatus = Status.OFF;
    }

    /**
     * Starts the bruteforcer on the specified code expecting the specified chest title
     * @param code code to start from
     */
    public static void startBruteforcer(final BlockPosition targetPosition, final String code, final String targetTitle) {
        ChestCracker.targetPosition = targetPosition;
        ChestCracker.bruteforcer = new Bruteforcer(code);
        ChestCracker.bruteForcerStatus = Status.ON;
        ChestCracker.targetTitle = targetTitle;

        sendChatMessage("§a\uD83D\uDD12 Started brute-forcing chest at (%d,%d,%d) starting with \"%s\"%s..."
                .formatted(targetPosition.x(),targetPosition.y(),targetPosition.z(),code,
                        targetTitle == null ? "" : ", expecting title \"%s\"".formatted(targetTitle))
        );
    }

    /**
     * Starts the bruteforcer on the specified code
     * @param code code to start from
     */
    public static void startBruteforcer(final BlockPosition targetPosition, final String code) {
        String title = getChestTitle(targetPosition);

        if(title == null) {
            final String defaultTitles = getDefaultChestTitles().toString()
                    .replace("[", "")
                    .replace("]", "");

            sendChatMessage(("§7Target title could not be read. There is either not a chest at the specified location" +
                    "or the chunk of the target is not loaded. " +
                    "Falling to back to default chest titles (%s)").formatted(defaultTitles));
        }

        startBruteforcer(targetPosition, code, title);
    }

    /**
     * Starts the bruteforcer
     */
    public static void startBruteforcer(final BlockPosition targetPosition) {
        startBruteforcer(targetPosition, "0");
    }

    public enum Status {
        ON, OFF, PAUSED
    }

}
