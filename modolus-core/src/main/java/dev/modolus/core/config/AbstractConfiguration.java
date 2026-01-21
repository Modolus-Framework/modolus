/*
 * Copyright (C) 2026 Modolus-Framework
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package dev.modolus.core.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import dev.modolus.annotations.config.Config;
import dev.modolus.core.logger.Logger;
import dev.modolus.core.logger.LoggerUtils;
import dev.modolus.util.result.Error;
import dev.modolus.util.result.GenericError;
import dev.modolus.util.result.Result;
import dev.modolus.util.singleton.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import tools.jackson.databind.ObjectMapper;

public abstract class AbstractConfiguration<T extends AbstractConfiguration<T>>
    implements Singleton {

  @JsonIgnore private final ExactScopedLazy<JavaPlugin> plugin;

  @JsonIgnore private final Lazy<Logger> logger = Logger.getPluginLogger();

  @JsonIgnore private final ObjectMapper objectMapper = new ObjectMapper();

  @JsonIgnore private final Class<T> configurationClass;

  protected AbstractConfiguration(Class<T> configurationClass) {
    this(configurationClass, SingletonScope.PLUGIN, true);
  }

  @ApiStatus.Internal
  protected AbstractConfiguration(Class<T> configurationClass, SingletonScope scope) {
    this.configurationClass = configurationClass;
    this.plugin =
        scope == SingletonScope.PLUGIN
            ? ExactScopedLazy.ofPlugin(JavaPlugin.class)
            : ExactScopedLazy.ofRoot(JavaPlugin.class);
  }

  @ApiStatus.Internal
  protected AbstractConfiguration(
      Class<T> configurationClass, SingletonScope scope, boolean initialize) {
    this(configurationClass, scope);
    if (initialize) {
      Singletons.provideSingleton(this, scope);
    }
  }

  protected abstract void onConfigurationLoaded();

  @Override
  public final void onInitialization() {
    var result = getConfigPath().flatMap(this::initialLoadConfiguration);
    result.onFailure(this::logError);
    result.onSuccess(_ -> onConfigurationLoaded());
  }

  public final void loadConfig() {
    var result = getConfigPath().flatMap(this::loadConfiguration);
    result.onFailure(this::logError);
    result.onSuccess(_ -> onConfigurationLoaded());
  }

  public final void saveConfig() {
    getConfigPath().flatMap(this::saveConfiguration).onFailure(this::logError);
  }

  private Result<Path, ConfigError> getConfigPath() {
    var config = configurationClass.getAnnotation(Config.class);
    if (config == null) {
      return Result.failure(ConfigError.CONFIG_CLASS_MISSING_CONFIG_ANNOTATION.toError());
    }

    return plugin
        .get()
        .map(p -> p.getDataDirectory().resolve(String.format("%s.json", config.name())))
        .mapError(ConfigError.PREVIOUS_ERROR::toErrorWithCause);
  }

  private Result<Void, GenericError> ensureFileExists(Path path) {
    return Result.ofExceptionVoid(
        () -> Files.createDirectories(path.getParent()), IOException.class);
  }

  private Result<Void, ConfigError> initialLoadConfiguration(Path path) {
    if (Files.notExists(path)) {
      return saveConfiguration(path);
    }
    return loadConfiguration(path);
  }

  private @NotNull Result<Void, ConfigError> loadConfiguration(Path path) {
    return Result.ofExceptionVoid(
            () -> objectMapper.readerForUpdating(this).readValue(path.toFile()), IOException.class)
        .mapError(
            baseError ->
                ConfigError.FAILED_LOADING_CONFIGURATION.toErrorWithCause(
                    baseError, path.toString()));
  }

  private @NotNull Result<Void, ConfigError> saveConfiguration(Path path) {
    return ensureFileExists(path)
        .mapVoid(_ -> objectMapper.writerWithDefaultPrettyPrinter().writeValue(path, this))
        .mapError(
            baseError ->
                ConfigError.FAILED_SAVING_CONFIGURATION.toErrorWithCause(
                    baseError, path.toString()));
  }

  private void logError(@NotNull Error<ConfigError> error) {
    LoggerUtils.printError(logger, error.getFullMessage());
  }
}
