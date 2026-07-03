package de.tutter05.kuhfuss;

import de.tutter05.kuhfuss.transformer.*;
import de.tutter05.kuhfuss.transformer.ChatComponentTransformer;
import de.tutter05.kuhfuss.transformer.ClientPacketListenerTransformer;
import de.tutter05.kuhfuss.transformer.LanguageChangeTransformer;
import de.tutter05.kuhfuss.transformer.MinecraftTransformer;
import de.tutter05.kuhfuss.utils.CustomClassFileTransformer;
import de.tutter05.kuhfuss.utils.Logger;

import static de.tutter05.kuhfuss.utils.ReflectionHelper.*;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Handles agent attaching and detaching
 */
public class AgentMain {

    public static ClassLoader forgeClassLoader = null;
    private static Instrumentation instrumentation = null;

    public static List<CustomClassFileTransformer> transformers = Arrays.asList(
            new ClientPacketListenerTransformer(),
            new MinecraftTransformer(),
            new LanguageChangeTransformer(),
            new ChatComponentTransformer()
    );

    /**
     * Called upon static attachment of the agent
     * @param agentArgs provided args, empty by default
     * @param inst the VMs instrumentation
     */
    public static void premain(String agentArgs, Instrumentation inst) {
        Logger.init();

        System.out.println("Statically attached to game.");
        initTransformers(inst);
    }

    /**
     * Called upon dynamic attachment of the agent
     * @param agentArgs provided args, empty by default
     * @param inst the VMs instrumentation
     */
    public static void agentmain(String agentArgs, Instrumentation inst) {
        Logger.init();

        System.out.println("Dynamically attached to game.");
        initTransformers(inst);
        retransformLoadedClasses(inst);
    }

    /**
     * Initialize our transformers
     * @param inst the VMs instrumentation
     */
    private static void initTransformers(Instrumentation inst) {
        instrumentation = inst;
        transformers.forEach(transformer -> inst.addTransformer(transformer, true));
    }

    /**
     * Retransforms modified classes
     * @param inst the VMs instrumentation
     */
    private static void retransformLoadedClasses(Instrumentation inst) {
        try {
            List<Class<?>> classesToTransform = new ArrayList<>(transformers.size());

            for (Class<?> loadedClass : inst.getAllLoadedClasses()) {
                String loadedClassName = loadedClass.getName().strip();

                for (CustomClassFileTransformer transformer : transformers) {
                    String transformerTargetName = transformer.getTargetClass().replace('/', '.');

                    if(loadedClassName.equals(transformerTargetName)) {
                        classesToTransform.add(loadedClass);
                        break;
                    }

                }

            }

            System.out.printf("Retransforming %d classes%n", classesToTransform.size());
            instrumentation.retransformClasses(classesToTransform.toArray(new Class<?>[0]));
            System.out.println("Successfully retransformed classes");

        } catch (UnmodifiableClassException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Detaches agent from game, reverting all changes
     */
    public static void detach() {
        if(instrumentation == null || forgeClassLoader == null) {
            return;
        }

        ChestCracker.isPlayerInteracting = false;
        ChestCracker.stopBruteforcer();
        ChestCracker.packetDelay = 250;

        List<ClassDefinition> originalClasses = new LinkedList<>();

        try {

            for (CustomClassFileTransformer transformer : transformers) {
                instrumentation.removeTransformer(transformer);

                String className = transformer.getTargetClass().replace('/', '.');

                byte[] originalClassFileBuffer = transformer.getOriginalClassFileBuffer();
                Class<?> transformedClass = Class.forName(className, false, forgeClassLoader);

                originalClasses.add(new ClassDefinition(transformedClass, originalClassFileBuffer));
            }

            instrumentation.redefineClasses(originalClasses.toArray(new ClassDefinition[0]));

        } catch (ClassNotFoundException | UnmodifiableClassException e) {
            displayMessage("§cUnable to detach from game");
            e.printStackTrace(System.out);
        }

        System.out.println("Retransformed all classes.");
    }


}
