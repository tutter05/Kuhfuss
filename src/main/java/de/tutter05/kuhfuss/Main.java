package de.tutter05.kuhfuss;

import com.sun.tools.attach.*;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {

        List<String> mainClasses = getMainClasses();
        List<VirtualMachineDescriptor> vmDescriptors = VirtualMachine.list();

        boolean attached = false;

        for (VirtualMachineDescriptor vmDescriptor : vmDescriptors) {
            System.out.println(vmDescriptor.displayName() + " -> " + vmDescriptor.id());

            for (String mainClass : mainClasses) {
                if(vmDescriptor.displayName().equals(mainClass)) {
                    System.out.println("    Attaching to game...");
                    attached = true;

                    try {
                        VirtualMachine vm = VirtualMachine.attach(vmDescriptor.id());
                        vm.loadAgent(getCurrentJarPath());
                        vm.detach();
                    } catch (AttachNotSupportedException | IOException | AgentLoadException | AgentInitializationException e) {
                        System.err.println("Unable to attach agent to path. If this error persists, try attaching the agent statically.");
                        throw new RuntimeException(e);
                    }
                }
            }


        }
        if(!attached) {
            System.out.println("\nNo Minecraft instances were found. If your game is already running and is not picked up." +
                    " please refer to the README to add a new main class and consider creating a issue on GitHub.");
        }

    }

    /**
     * Get the path of the Jar that is currently running
     * @return absolute path to the jar file
     */
    private static String getCurrentJarPath() {
        try {
            File jarFile = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            return jarFile.getAbsolutePath();
        } catch (URISyntaxException e) {
            System.err.println("Unable to find the path of this jar. If this error persists, try attaching the agent statically.");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Reads all known main classes from the bundled text file
     * @return a list of all main classes as strings
     * @throws FileNotFoundException if the file main-classes.txt does not exist
     */
    private static List<String> getMainClasses() throws FileNotFoundException {
        List<String> mainClasses = new ArrayList<>();

        InputStream is = Main.class.getResourceAsStream("/main-classes.txt");

        if(is == null) {
            throw new FileNotFoundException("main-classes.txt could not be found");
        }

        try (Scanner scanner = new Scanner(is, StandardCharsets.UTF_8)) {

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    mainClasses.add(line);
                }
            }
        }

        return mainClasses;
    }

}
