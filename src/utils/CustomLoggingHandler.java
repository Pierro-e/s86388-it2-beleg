package utils;

import java.util.logging.Handler;
import java.util.logging.Formatter;
import java.util.logging.Logger;
import java.util.logging.LogRecord;

public class CustomLoggingHandler extends Handler {

    public static final String ANSI_RESET  = "\u001B[0m";
    public static final String ANSI_BLACK  = "\u001B[30m";
    public static final String ANSI_RED    = "\u001B[31m";
    public static final String ANSI_GREEN  = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE   = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN   = "\u001B[36m";
    public static final String ANSI_WHITE  = "\u001B[37m";

    static public void prepareLogger(Logger logger) {
        logger.setUseParentHandlers(false);
        for (Handler h : logger.getHandlers()) {
            logger.removeHandler(h);
        }
        logger.addHandler(new CustomLoggingHandler());
    }

    public CustomLoggingHandler() {
        Formatter formatter = new Formatter() {
            @Override
            public String format(LogRecord record) {
                String out = "";
                // may be useful for checking logging levels:
                // out += record.getLevel() + ": ";
                // set color messages depending on the log level
                switch (record.getLevel().toString()) {
                  case "SEVERE":  out += ANSI_RED; break;
                  case "WARNING": out += ANSI_GREEN; break;
                  case "CONFIG":  out += ANSI_CYAN; break;
                  default:
                }
                out += record.getMessage();
                out += ANSI_RESET;
                return out;
            }
        };
        this.setFormatter(formatter);
    }

    @Override
    public void publish(LogRecord record) {
        Formatter formatter = getFormatter();
        System.out.println(formatter.format(record));
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
    }
}

