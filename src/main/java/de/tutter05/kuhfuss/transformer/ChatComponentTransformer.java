package de.tutter05.kuhfuss.transformer;

import de.tutter05.kuhfuss.utils.CustomClassFileTransformer;
import javassist.*;

import java.io.IOException;
import java.security.ProtectionDomain;

public class ChatComponentTransformer extends CustomClassFileTransformer {

    public ChatComponentTransformer() {
        super("net/minecraft/client/gui/components/ChatComponent");
    }

    @Override
    public byte[] doTransform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        System.out.println("Transforming ChatComponent...");

        try {

            ClassPool classPool = ClassPool.getDefault();
            classPool.appendClassPath(new javassist.LoaderClassPath(loader));

            CtClass listenerClass = classPool.makeClass(new java.io.ByteArrayInputStream(classfileBuffer));

            CtClass componentTargetClass = classPool.get("net.minecraft.network.chat.Component");
            CtMethod addMessageMethod = listenerClass.getDeclaredMethod("addMessage", new CtClass[]{ componentTargetClass });

            String injectedChatCode = "{" +
                    "    try {" +
                    "        java.lang.Class compClass = $1.getClass();" +
                    "        java.lang.reflect.Method getStringMethod = compClass.getMethod(\"getString\", new java.lang.Class[0]);" +
                    "        java.lang.String messageString = (java.lang.String) getStringMethod.invoke($1, new java.lang.Object[0]);" +

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
            addMessageMethod.insertBefore(injectedChatCode);
        } catch (CannotCompileException | NotFoundException | IOException e) {
            System.out.println("Unable to transform ChatComponent");
            e.printStackTrace(System.out);
        }

        return null;
    }
}
