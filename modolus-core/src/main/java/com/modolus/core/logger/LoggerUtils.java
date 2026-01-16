package com.modolus.core.logger;

import com.modolus.util.singleton.Lazy;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class LoggerUtils {

    public static void printInfo(@NotNull Lazy<Logger> logger, String message) {
        logger.get().onSuccess(l -> l.atInfo().log(message));
        logger.get().onFailure(_ -> java.util.logging.Logger.getGlobal().info(message));
    }

    public static void printWarn(@NotNull Lazy<Logger> logger, String message) {
        logger.get().onSuccess(l -> l.atWarning().log(message));
        logger.get().onFailure(_ -> java.util.logging.Logger.getGlobal().warning(message));
    }

    public static void printError(@NotNull Lazy<Logger> logger, String message) {
        logger.get().onSuccess(l -> l.atSevere().log(message));
        logger.get().onFailure(_ -> java.util.logging.Logger.getGlobal().severe(message));
    }

}
