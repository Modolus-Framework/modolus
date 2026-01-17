package com.modolus.core.logger;

import com.modolus.util.singleton.Lazy;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

@UtilityClass
public class LoggerUtils {

    public static void printInfo(@NotNull Lazy<Logger> logger,
                                 @NotNull String message) {
        print(logger, Level.INFO, message);
    }

    public static void printWarn(@NotNull Lazy<Logger> logger,
                                 @NotNull String message) {
        print(logger, Level.WARNING, message);
    }

    public static void printError(@NotNull Lazy<Logger> logger,
                                  @NotNull String message) {
        print(logger, Level.SEVERE, message);
    }

    private static void print(@NotNull Lazy<Logger> logger,
                              @NotNull Level level,
                              @NotNull String message) {
        logger.get().onSuccess(l -> l.at(level).log(message));
        logger.get().onFailure(_ -> java.util.logging.Logger.getGlobal().log(level, message));
    }

}
