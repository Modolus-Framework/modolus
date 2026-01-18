package com.modolus.core.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.modolus.annotations.config.Config;
import com.modolus.core.logger.Logger;
import com.modolus.core.logger.LoggerUtils;
import com.modolus.util.result.Result;
import com.modolus.util.singleton.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class AbstractConfiguration<T extends AbstractConfiguration<T>> implements Singleton {

    @JsonIgnore
    private final ExactScopedLazy<JavaPlugin> plugin;

    @JsonIgnore
    private final Lazy<Logger> logger = Logger.getPluginLogger();

    @JsonIgnore
    private final ObjectMapper objectMapper = new ObjectMapper();

    @JsonIgnore
    private final Class<T> configurationClass;

    protected AbstractConfiguration(Class<T> configurationClass) {
        this(configurationClass, SingletonScope.PLUGIN);
    }

    @ApiStatus.Internal
    protected AbstractConfiguration(Class<T> configurationClass, SingletonScope scope) {
        this.configurationClass = configurationClass;
        this.plugin = scope == SingletonScope.PLUGIN
                ? ExactScopedLazy.ofPlugin(JavaPlugin.class)
                : ExactScopedLazy.ofRoot(JavaPlugin.class);
        Singletons.provideSingleton(this, scope);
    }

    protected abstract void onConfigurationLoaded();

    @Override
    public final void onInitialization() {
        var result = getConfigPath()
                .flatMap(this::initialLoadConfiguration);
        result.onFailure(this::logError);
        result.onSuccess(_ -> onConfigurationLoaded());
    }

    public final void loadConfig() {
        var result = getConfigPath()
                .flatMap(this::loadConfiguration);
        result.onFailure(this::logError);
        result.onSuccess(_ -> onConfigurationLoaded());
    }

    public final void saveConfig() {
        getConfigPath()
                .flatMap(this::saveConfiguration)
                .onFailure(this::logError);
    }

    private Result<Path, IOException> getConfigPath() {
        var config = configurationClass.getAnnotation(Config.class);
        if (config == null) {
            return Result.failure(new IOException("Configuration class is missing @Config annotation"));
        }

        return plugin.get()
                .map(p -> p.getDataDirectory().resolve(String.format("%s.json", config.name())))
                .mapError(err -> new IOException(err.name()));
    }

    private Result<Void, IOException> ensureFileExists(Path path) {
        return Result.ofExceptionVoid(() -> Files.createDirectories(path.getParent()), IOException.class);
    }

    private Result<Void, IOException> initialLoadConfiguration(Path path) {
        if (Files.notExists(path)) {
            return saveConfiguration(path);
        }
        return loadConfiguration(path);
    }

    private @NotNull Result<Void, IOException> loadConfiguration(Path path) {
        return Result.ofExceptionVoid(() -> objectMapper.readerForUpdating(this).readValue(path.toFile()), IOException.class);
    }

    private @NotNull Result<Void, IOException> saveConfiguration(Path path) {
        return ensureFileExists(path)
                .mapVoid(_ -> objectMapper.writerWithDefaultPrettyPrinter().writeValue(path, this));
    }

    private void logError(@NotNull IOException exception) {
        LoggerUtils.printError(logger,
                String.format("An error occured while loading configuration: %s (Configuration: %s)",
                        exception.getMessage(),
                        getClass().getCanonicalName()));
    }

}
