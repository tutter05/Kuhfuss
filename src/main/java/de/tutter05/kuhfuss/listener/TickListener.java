package de.tutter05.kuhfuss.listener;

import de.tutter05.kuhfuss.ChestCracker;
import de.tutter05.kuhfuss.utils.Timer;

import static de.tutter05.kuhfuss.utils.ReflectionHelper.*;

public class TickListener {

    private static final Timer timer = new Timer();

    /**
     * Called every game tick, whether player is in a level or not. Is injected by MinecraftTransformer
     */
    public static void onTick() {

        if(!isPlayerInGame())
            return;

        updateStatus();

        switch (ChestCracker.bruteForcerStatus) {

            case OFF -> displayMessage("§eBruteforcer is inactive...");
            case PAUSED -> displayMessage("§e(%s)".formatted(ChestCracker.bruteforcer.getLastElement()));
            case ON -> timer.onTimeout(ChestCracker.packetDelay, () -> {
                String nextCode = ChestCracker.bruteforcer.nextCode();

                displayMessage("§e"+nextCode);
                sendPasscodePacket(ChestCracker.targetPosition, nextCode);
            });

        }

    }

    /**
     * Turns the bruteforcer back on when the player closed the inventory
     */
    private static void updateStatus() {
        if(ChestCracker.bruteForcerStatus == ChestCracker.Status.OFF) return;
        ChestCracker.bruteForcerStatus = hasScreenOpen() ? ChestCracker.Status.PAUSED : ChestCracker.Status.ON;
    }


}

