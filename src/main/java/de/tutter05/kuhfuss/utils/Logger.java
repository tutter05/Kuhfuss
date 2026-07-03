package de.tutter05.kuhfuss.utils;

import java.io.OutputStream;
import java.io.PrintStream;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Logger {

    private final static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final PrintStream underlyingOut = System.out;

    /**
     * Initializes logging
     */
    public static void init() {
        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {
                underlyingOut.write(b);
            }
        }) {
            @Override
            public void println(String message) {
                log(message);
            }

            @Override
            public void println(Object message) {
                log(String.valueOf(message));
            }

            @Override
            public void print(String message) {
                log(message);
            }

            @Override
            public PrintStream printf(String format, Object... args) {
                String formatted = String.format(format, args);
                log(formatted.replaceAll("[\\r\\n]+$", ""));
                return this;
            }

            @Override
            public PrintStream printf(Locale l, String format, Object... args) {
                String formatted = String.format(l, format, args);
                log(formatted.replaceAll("[\\r\\n]+$", ""));
                return this;
            }

            @Override
            public PrintStream format(String format, Object... args) {
                return printf(format, args);
            }

            @Override
            public PrintStream format(java.util.Locale l, String format, Object... args) {
                return printf(l, format, args);
            }
        });
    }

    /**
     * Format the specified message and logs it to the console
     * @param message message to log to console
     */
    private static void log(String message) {
        String time = LocalTime.now().format(timeFormatter);

        String formattedMessage = String.format("[%s] [%s] %s",
                time,
                "Kuhfuss",
                message
        );
        underlyingOut.println(formattedMessage);
    }

}
