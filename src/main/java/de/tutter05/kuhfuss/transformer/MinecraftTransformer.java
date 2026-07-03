package de.tutter05.kuhfuss.transformer;

import de.tutter05.kuhfuss.AgentMain;
import de.tutter05.kuhfuss.ChestCracker;
import de.tutter05.kuhfuss.utils.CustomClassFileTransformer;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.security.ProtectionDomain;

/**
 * Hooks into SecurityCrafts ClientHandler to set Forge classloader and hook into tick()
 */
public class MinecraftTransformer extends CustomClassFileTransformer {

    public MinecraftTransformer() {
        super("net/minecraft/client/Minecraft");
    }

    @Override
    public byte[] doTransform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
         System.out.println("Transforming Minecraft...");

            AgentMain.forgeClassLoader = loader;

            try {
                ClassPool classPool = ClassPool.getDefault();
                classPool.appendClassPath(new javassist.LoaderClassPath(loader));
                classPool.insertClassPath(new ClassClassPath(ChestCracker.class));
                classPool.importPackage("de.niecklikescode.chestcracker");

                CtClass minecraftClass = classPool.makeClass(new java.io.ByteArrayInputStream(classfileBuffer));

                // Hook tick code
                CtMethod tickMethod = minecraftClass.getDeclaredMethod("tick");
                String injectedTickCode = "{" +
                        "       try {" +
                        "           ClassLoader systemLoader = ClassLoader.getSystemClassLoader();" +
                        "           Class agentClass = systemLoader.loadClass(\"de.tutter05.kuhfuss.listener.TickListener\");" +
                        "           " +
                        "           java.lang.reflect.Method method = agentClass.getMethod(\"onTick\", new Class[0]);" +
                        "           method.invoke(null, new Object[0]);" +
                        "       } catch (Exception e) {" +
                        "           e.printStackTrace();" +
                        "       }" +
                        "}";
                tickMethod.insertBefore(injectedTickCode);

                byte[] transformedClass = minecraftClass.toBytecode();
                minecraftClass.detach();
                return transformedClass;

            } catch (Exception e) {
                e.printStackTrace(System.out);
            }

        return null;
    }
}
