package de.tutter05.kuhfuss.transformer;

import de.tutter05.kuhfuss.utils.CustomClassFileTransformer;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.io.ByteArrayInputStream;
import java.security.ProtectionDomain;

public class LanguageChangeTransformer extends CustomClassFileTransformer {

    public LanguageChangeTransformer() {
        super("net/minecraft/locale/Language");
    }

    @Override
    public byte[] doTransform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        System.out.println("Transforming Language...");

        try {
            ClassPool pool = ClassPool.getDefault();
            pool.appendClassPath(new javassist.LoaderClassPath(loader));

            CtClass ctClass = pool.makeClass(new ByteArrayInputStream(classfileBuffer));

            CtClass languageClass = pool.get("net.minecraft.locale.Language");
            CtMethod injectMethod = ctClass.getDeclaredMethod("inject", new CtClass[]{ languageClass });

            String injectedCode = "{" +
                    "       try {" +
                    "           ClassLoader systemLoader = ClassLoader.getSystemClassLoader();" +
                    "           Class agentClass = systemLoader.loadClass(\"de.tutter05.kuhfuss.listener.LanguageChangeListener\");" +
                    "           " +
                    "           java.lang.reflect.Method method = agentClass.getMethod(\"onLanguageChange\", new Class[0]);" +
                    "           method.invoke(null, new Object[0]);" +
                    "       } catch (Exception e) {" +
                    "           e.printStackTrace();" +
                    "       }" +
                    "}";

            injectMethod.insertAfter(injectedCode);

            byte[] transformedClass = ctClass.toBytecode();
            ctClass.detach();
            return transformedClass;

        } catch (Exception e) {
            System.err.println("Failed to transform Language class!");
            e.printStackTrace(System.out);
        }

        return null;
    }
}
