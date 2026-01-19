package dev.modolus.core.logger;

import com.hypixel.hytale.logger.HytaleLogger;
import dev.modolus.util.singleton.Lazy;
import dev.modolus.util.singleton.Singleton;
import dev.modolus.util.singleton.SingletonScope;
import dev.modolus.util.singleton.Singletons;
import io.sentry.IScopes;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Logger implements Singleton {

    public static Lazy<Logger> getPluginLogger() {
        return Lazy.ofPlugin(Logger.class);
    }

    public static Lazy<Logger> getRootLogger() {
        return Lazy.ofRoot(Logger.class);
    }

    private final HytaleLogger hytaleLogger;

    private Logger(HytaleLogger hytaleLogger, String singletonIdentifier, SingletonScope scope) {
        this(hytaleLogger);
        if (singletonIdentifier == null) Singletons.provideSingleton(this, scope).orElseThrow();
        else Singletons.provideSingleton(this, singletonIdentifier, scope).orElseThrow();
    }

    public static void providePluginLogger(@NotNull HytaleLogger logger) {
        providePluginLogger(logger, null);
    }

    public static void providePluginLogger(@NotNull HytaleLogger logger, String loggerIdentifier) {
        new Logger(logger, loggerIdentifier, SingletonScope.PLUGIN);
    }

    public static void provideRootLogger(@NotNull HytaleLogger logger) {
        provideRootLogger(logger, null);
    }

    public static void provideRootLogger(@NotNull HytaleLogger logger, String loggerIdentifier) {
        new Logger(logger, loggerIdentifier, SingletonScope.ROOT);
    }

    public HytaleLogger.Api at(Level level) {
        return hytaleLogger.at(level);
    }

    public HytaleLogger.Api atSevere() {
        return hytaleLogger.atSevere();
    }

    public HytaleLogger.Api atWarning() {
        return hytaleLogger.atWarning();
    }

    public HytaleLogger.Api atInfo() {
        return hytaleLogger.atInfo();
    }

    public HytaleLogger.Api atConfig() {
        return hytaleLogger.atConfig();
    }

    public HytaleLogger.Api atFine() {
        return hytaleLogger.atFine();
    }

    public HytaleLogger.Api atFiner() {
        return hytaleLogger.atFiner();
    }

    public HytaleLogger.Api atFinest() {
        return hytaleLogger.atFinest();
    }

    public @NotNull Level getLevel() {
        return hytaleLogger.getLevel();
    }

    public void setLevel(Level level) {
        hytaleLogger.setLevel(level);
    }

    public String getName() {
        return hytaleLogger.getName();
    }

    public @NotNull HytaleLogger getSubLogger(String name) {
        return hytaleLogger.getSubLogger(name);
    }

    public void setPropagatesSentryToParent(boolean propagate) {
        hytaleLogger.setPropagatesSentryToParent(propagate);
    }

    public void setSentryClient(IScopes scopes) {
        hytaleLogger.setSentryClient(scopes);
    }

}
