package de.tutter05.kuhfuss.transformer;

import de.tutter05.kuhfuss.utils.CustomClassFileTransformer;
import javassist.*;

import java.io.IOException;
import java.security.ProtectionDomain;

/**
 * Hooks into Minecrafts ClientPackerListener to intercept chat messages and hook into the function called when a player
 * opens a chest.
 */
public class ClientPacketListenerTransformer extends CustomClassFileTransformer {

    public ClientPacketListenerTransformer() {
        super("net/minecraft/client/multiplayer/ClientPacketListener");
    }

    @Override
    public byte[] doTransform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
         System.out.println("Transforming ClientPacketListener...");

            try {
                ClassPool classPool = ClassPool.getDefault();
                classPool.appendClassPath(new javassist.LoaderClassPath(loader));

                CtClass listenerClass = classPool.makeClass(new java.io.ByteArrayInputStream(classfileBuffer));

                // Hook into sendChat
                CtMethod sendChatMethod = listenerClass.getDeclaredMethod("sendChat", new CtClass[]{classPool.get("java.lang.String")});
                String injectedChatCode = "{ if ($1.startsWith(\".\")) { try { java.lang.ClassLoader systemLoader = java.lang.ClassLoader.getSystemClassLoader(); java.lang.Class agentClass = systemLoader.loadClass(\"de.tutter05.kuhfuss.listener.ChatListener\"); java.lang.Class[] paramTypes = new java.lang.Class[1]; paramTypes[0] = java.lang.String.class; java.lang.reflect.Method processMethod = agentClass.getMethod(\"onSendChat\", paramTypes); java.lang.Object[] invokeArgs = new java.lang.Object[1]; invokeArgs[0] = $1.substring(1); processMethod.invoke(null, invokeArgs); } catch (java.lang.Exception e) { e.printStackTrace(); } return; } }";
                sendChatMethod.insertBefore(injectedChatCode);

                // Hook into handleOpenScreen
                CtClass packetClass = classPool.get("net.minecraft.network.protocol.game.ClientboundOpenScreenPacket");
                CtMethod handleOpenScreenMethod = listenerClass.getDeclaredMethod("handleOpenScreen", new CtClass[] { packetClass });
                String injectedScreenCode = "{" +
                        "       try {" +
                        "           String screenTitle = $1.getTitle().getString();" +

                        "           ClassLoader systemLoader = ClassLoader.getSystemClassLoader();" +
                        "           Class agentClass = systemLoader.loadClass(\"de.tutter05.kuhfuss.listener.ChestOpenListener\");" +
                        "           java.lang.reflect.Method method = agentClass.getMethod(\"onChestOpened\", new Class[] { String.class });" +
                        "           method.invoke(null, new Object[] { screenTitle });" +
                        "       } catch (Exception e) {" +
                        "           e.printStackTrace();" +
                        "       }" +
                        "}";
                handleOpenScreenMethod.insertBefore(injectedScreenCode);

                CtClass systemChatClass = classPool.get("net.minecraft.network.protocol.game.ClientboundSystemChatPacket");
                CtMethod systemChatMethod = listenerClass.getDeclaredMethod("handleSystemChat", new CtClass[]{ systemChatClass });

                String systemChatCode = "{" +
                        "    try {" +
                        "        java.lang.Class packetClass = $1.getClass();" +
                        "        java.lang.reflect.Method contentMethod = packetClass.getMethod(\"content\", new java.lang.Class[0]);" +
                        "        java.lang.Object component = contentMethod.invoke($1, new java.lang.Object[0]);" +

                        "        java.lang.Class componentClass = component.getClass();" +
                        "        java.lang.reflect.Method getStringMethod = componentClass.getMethod(\"getString\", new java.lang.Class[0]);" +
                        "        java.lang.String messageString = (java.lang.String) getStringMethod.invoke(component, new java.lang.Object[0]);" +

                        "        java.lang.ClassLoader systemLoader = java.lang.ClassLoader.getSystemClassLoader();" +
                        "        java.lang.Class agentClass = systemLoader.loadClass(\"de.tutter05.kuhfuss.listener.ServerMessageListener\");" +
                        "        java.lang.reflect.Method method = agentClass.getMethod(\"receiveServerMessage\", new java.lang.Class[] { java.lang.String.class });" +

                        "        java.lang.Boolean allowMessage = (java.lang.Boolean) method.invoke(null, new java.lang.Object[] { messageString });" +
                        "        if (!allowMessage.booleanValue()) {" +
                        "            return;" +
                        "        }" +
                        "    } catch (Exception e) {" +
                        "        e.printStackTrace();" +
                        "    }" +
                        "}";

                systemChatMethod.insertBefore(systemChatCode);

                byte[] transformedClass = listenerClass.toBytecode();
                listenerClass.detach();
                return transformedClass;

            } catch (NotFoundException e) {
                System.out.println("Unable to find class");
                e.printStackTrace(System.out);
            } catch (IOException e) {
                System.out.println("Encountered IOException during transform");
                e.printStackTrace(System.out);
            } catch (CannotCompileException e) {
                System.out.println("Unable to compile modified code");
                e.printStackTrace(System.out);
            }

        return null;
    }
}
