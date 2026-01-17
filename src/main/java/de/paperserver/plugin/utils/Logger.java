package de.paperserver.plugin.utils;

import de.paperserver.plugin.PaperPluginSuite;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void info(String message) {
        PaperPluginSuite.getInstance().getLogger()
                .info(formatMessage("[INFO]", message));
    }

    public static void warn(String message) {
        PaperPluginSuite.getInstance().getLogger()
                .warning(formatMessage("[WARN]", message));
    }

    public static void error(String message) {
        PaperPluginSuite.getInstance().getLogger()
                .severe(formatMessage("[ERROR]", message));
    }

    public static void debug(String message) {
        if (PaperPluginSuite.getInstance().getConfig().getBoolean("debug", false)) {
            PaperPluginSuite.getInstance().getLogger()
                    .info(formatMessage("[DEBUG]", message));
        }
    }

    public static void log(String category, String message) {
        PaperPluginSuite.getInstance().getLogger()
                .info(String.format("[%s] %s", category, message));
    }

    private static String formatMessage(String level, String message) {
        return String.format("%s %s - %s", formatter.format(LocalDateTime.now()), level, message);
    }
}
