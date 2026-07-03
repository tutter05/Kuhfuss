package de.tutter05.kuhfuss.utils;

import de.tutter05.kuhfuss.AgentMain;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

public class ReflectionHelper {

    private static Object mcInstance;
    private static Object emptyPayloadArray;

    private static Field playerField;
    private static Field screenField;
    private static Field levelField;
    private static Field guiField;

    private static Method getBlockEntityMethod;
    private static Method getStringMethod;

    private static Method componentLiteralMethod;
    private static Method getChatMethod;
    private static Method addMessageMethod;
    private static Method displayClientMessageMethod;
    private static Method sendToServerMethod;

    private static Constructor<?> blockPosConstructor;
    private static Constructor<?> checkPasscodePacketConstructor;

    private static boolean reflectionInitialized = false;

    private static String cooldownMessage = null;
    private static final List<String> chestTitles = new LinkedList<>();

    /**
     * Attempts to open the chest at the specified location with the specified passcode
     * @param position position of chest to open
     * @param passcode passcode of chest
     */
    public static void sendPasscodePacket(final BlockPosition position, final String passcode) {

        try {
            initReflection();

            Object chestPosition = blockPosConstructor.newInstance(position.x(), position.y(), position.z());
            Object checkPasscodePacket = checkPasscodePacketConstructor.newInstance(chestPosition, passcode);
            sendToServerMethod.invoke(null, checkPasscodePacket, emptyPayloadArray);

        } catch (Exception e) {
            System.out.println("Unable to send passcode packet to server");
            e.printStackTrace(System.out);
        }
    }

    /**
     * Displays a message in chat (clientside)
     * @param message message to display
     */
    public static void sendChatMessage(final String message) {
        displayClientMessage(message, true);
    }

    /**
     * Displays a message over the hotbar
     * @param message message to display
     */
    public static void displayMessage(final String message) {
        displayClientMessage(message, false);
    }

    /**
     * Displays the specified message, either in chat or over the hotbar
     * @param message message to display
     * @param showInChat true if message should be displayed in the chat
     */
    private static void displayClientMessage(final String message, final boolean showInChat) {

        try {
            initReflection();

            if (mcInstance == null) {
                return;
            }

            Object componentInstance = componentLiteralMethod.invoke(null, message);

            if (showInChat) {
                Object guiInstance = guiField.get(mcInstance);
                if (guiInstance != null) {
                    Object chatComponentInstance = getChatMethod.invoke(guiInstance);
                    addMessageMethod.invoke(chatComponentInstance, componentInstance, null, null);
                }

            } else {
                Object playerObject = playerField.get(mcInstance);

                if (playerObject == null) {
                    System.out.println("Player is not initialized");
                    return;
                }

                displayClientMessageMethod.invoke(playerObject, componentInstance, true);
            }

        } catch (Exception e) {
            System.out.println("Unable to display message");
            e.printStackTrace(System.out);
        }
    }

    /**
     * Initializes reflections and caches fields for later use
     * @throws Exception if any look up failed
     */
    private static void initReflection() throws Exception {
        if (!reflectionInitialized && AgentMain.forgeClassLoader != null) {
            Class<?> mcClass = Class.forName("net.minecraft.client.Minecraft", false, AgentMain.forgeClassLoader);

            // Cache Minecraft instance
            Method getInstance = mcClass.getDeclaredMethod("getInstance");
            mcInstance = getInstance.invoke(null);

            // Cache player field
            playerField = mcClass.getDeclaredField("player");
            playerField.setAccessible(true);

            // Cache screen field
            screenField = mcClass.getDeclaredField("screen");
            screenField.setAccessible(true);

            // Cache level
            levelField = mcClass.getDeclaredField("level");
            levelField.setAccessible(true);
            Class<?> levelClass = Class.forName("net.minecraft.world.level.Level", false, AgentMain.forgeClassLoader);

            // Cache blockPos
            Class<?> blockPosClass = Class.forName("net.minecraft.core.BlockPos", false, AgentMain.forgeClassLoader);
            blockPosConstructor = blockPosClass.getConstructor(int.class, int.class, int.class);

            // Cache block entity
            getBlockEntityMethod = levelClass.getMethod("getBlockEntity", blockPosClass);

            // Cache component
            Class<?> componentClass = Class.forName("net.minecraft.network.chat.Component", false, AgentMain.forgeClassLoader);
            getStringMethod = componentClass.getMethod("getString");
            componentLiteralMethod = componentClass.getMethod("literal", String.class);

            // Cache gui
            guiField = mcClass.getDeclaredField("gui");
            guiField.setAccessible(true);
            Class<?> guiClass = Class.forName("net.minecraft.client.gui.Gui", false, AgentMain.forgeClassLoader);
            getChatMethod = guiClass.getDeclaredMethod("getChat");
            getChatMethod.setAccessible(true);

            // Cache ChatComponent
            Class<?> chatComponentClass = Class.forName("net.minecraft.client.gui.components.ChatComponent", false, AgentMain.forgeClassLoader);
            Class<?> signatureClass = Class.forName("net.minecraft.network.chat.MessageSignature", false, AgentMain.forgeClassLoader);
            Class<?> tagClass = Class.forName("net.minecraft.client.GuiMessageTag", false, AgentMain.forgeClassLoader);
            addMessageMethod = chatComponentClass.getDeclaredMethod("addMessage", componentClass, signatureClass, tagClass);
            addMessageMethod.setAccessible(true);

            // Cache displayClientMessage
            Class<?> localPlayerClass = Class.forName("net.minecraft.client.player.LocalPlayer", false, AgentMain.forgeClassLoader);
            displayClientMessageMethod = localPlayerClass.getMethod("displayClientMessage", componentClass, boolean.class);

            // Cache networking
            Class<?> checkPasscodePacketClass = Class.forName("net.geforcemods.securitycraft.network.server.CheckPasscode", false, AgentMain.forgeClassLoader);
            checkPasscodePacketConstructor = checkPasscodePacketClass.getConstructor(blockPosClass, String.class);

            Class<?> customPacketPayloadClass = Class.forName("net.minecraft.network.protocol.common.custom.CustomPacketPayload", false, AgentMain.forgeClassLoader);
            Class<?> payloadArrayClass = java.lang.reflect.Array.newInstance(customPacketPayloadClass, 0).getClass();
            emptyPayloadArray = java.lang.reflect.Array.newInstance(customPacketPayloadClass, 0);

            Class<?> clientPacketDistributorClass = Class.forName("net.neoforged.neoforge.client.network.ClientPacketDistributor", false, AgentMain.forgeClassLoader);
            sendToServerMethod = clientPacketDistributorClass.getMethod("sendToServer", customPacketPayloadClass, payloadArrayClass);

            reflectionInitialized = true;
        }
    }

    /**
     * Checks if the player is logged in
     * @return true if player is in a level else false
     */
    public static boolean isPlayerInGame() {
        try {
            initReflection();
            Object playerInstance = playerField.get(mcInstance);
            return playerInstance != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks whether the player has a screen open
     * @return true if a gui is opened
     */
    public static boolean hasScreenOpen() {
        try {
            initReflection();
            Object currentScreen = screenField.get(mcInstance);
            return currentScreen != null;
        } catch (Exception e) {
            System.out.println("Unable to lookup open screen");
        }
        return true;
    }

    /**
     * Returns the SecurityCraft passcode cooldown message for the currently selected language
     * @return cooldown message or null when look up failed
     */
    public static String getCooldownMessage() {
        if(cooldownMessage != null) {
            return cooldownMessage;
        } else {
            return cooldownMessage = getTranslation("messages.securitycraft:passcodeProtected.onCooldown");
        }
    }

    /**
     * Returns a list of the title of both single and double keypad chests
     * @return list of names
     */
    public static List<String> getDefaultChestTitles() {
        if (chestTitles.isEmpty()) {
            chestTitles.addFirst(getTranslation("block.securitycraft.keypad_chest"));
            chestTitles.addFirst(getTranslation("block.securitycraft.keypad_chest_double"));
        }
        return chestTitles;
    }

    /**
     * Translates the given key to the currently selected language
     * @return cooldown message or null when look up failed
     */
    private static String getTranslation(final String key) {

            try {
                Class<?> languageClass = Class.forName("net.minecraft.locale.Language", false, AgentMain.forgeClassLoader);

                Method getInstanceMethod = languageClass.getMethod("getInstance");
                Object languageInstance = getInstanceMethod.invoke(null);

                Method getOrDefaultMethod = languageClass.getMethod("getOrDefault", String.class);
                String translated = (String) getOrDefaultMethod.invoke(languageInstance, key);

                if (translated != null && translated.equals(key)) {
                    return null;
                }

                return translated;

            } catch (Exception e) {
                System.out.println("Unable to look up translation");
                e.printStackTrace(System.out);
            }

        return null;
    }

    /**
     * Clears the cached translation texts
     */
    public static void clearTranslationCache() {
        cooldownMessage = null;
        chestTitles.clear();
    }

    /**
     * Returns the title of the chest at the specified position
     * @param blockPosition position of the chest
     * @return title of the chest or null if the chunk the chest is in is not loaded
     */
    public static String getChestTitle(final BlockPosition blockPosition) {
        try {
            initReflection();
            if (mcInstance == null) return null;

            Object level = levelField.get(mcInstance);
            if (level == null) return null;

            Object blockPos = blockPosConstructor.newInstance(blockPosition.x(), blockPosition.y(), blockPosition.z());

            Object blockEntity = getBlockEntityMethod.invoke(level, blockPos);

            if (blockEntity != null) {
                Class<?> beClass = blockEntity.getClass();
                java.lang.reflect.Method getDisplayNameMethod;

                try {
                    getDisplayNameMethod = beClass.getMethod("getDisplayName");
                } catch (NoSuchMethodException e) {
                    return null;
                }

                Object component = getDisplayNameMethod.invoke(blockEntity);

                if (component != null) {
                    return (String) getStringMethod.invoke(component);
                }
            }

        } catch (Exception e) {
            System.out.println("Unable to read chest title");
            e.printStackTrace(System.out);
        }

        return null;
    }



}
