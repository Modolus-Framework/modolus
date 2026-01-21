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

package dev.modolus.core.database;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.modolus.annotations.config.Config;
import dev.modolus.core.config.AbstractConfiguration;
import dev.modolus.core.logger.Logger;
import dev.modolus.core.logger.LoggerUtils;
import dev.modolus.util.result.Result;
import dev.modolus.util.singleton.*;
import java.util.Map;
import lombok.Getter;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

@Getter
@Config(name = "database")
public class DatabaseConfiguration extends AbstractConfiguration<DatabaseConfiguration> {

  @JsonIgnore private final SingletonScope scope;

  @JsonIgnore private final ExactScopedLazy<Database> database;

  @JsonIgnore private final Lazy<Logger> logger = Logger.getPluginLogger();

  @JsonIgnore private String migrationPath = null;

  private String jdbcUrl = "jdbc:sqlite:test.db";

  private String username = "";

  private String password = "";

  private Map<String, String> dataSourceProperties = Map.of();

  private int connectionTimeout = 30_000;

  private int idleTimeout = 600_000;

  private int keepAliveTime = 300_000;

  private int maxLifetime = 1_800_000;

  private int minimumIdle = 10;

  private int maximumPoolSize = 10;

  protected DatabaseConfiguration(
      SingletonScope scope, @Nullable String databaseSingletonIdentifier) {
    super(DatabaseConfiguration.class, scope, false);

    this.scope = scope;
    this.database =
        databaseSingletonIdentifier == null
            ? ExactScopedLazy.of(Database.class, scope)
            : ExactScopedLazy.of(Database.class, scope, databaseSingletonIdentifier);
  }

  @Override
  protected void onConfigurationLoaded() {
    database.get().onFailure(e -> LoggerUtils.printError(logger, e));
    database.get().onSuccess(db -> db.updateConfiguration(this));
  }

  public void withMigrations(String migrationPath) {
    this.migrationPath = migrationPath;
  }

  public static @NotNull @Unmodifiable Result<DatabaseConfiguration, SingletonError>
      provideDatabaseConfiguration() {
    return provideDatabaseConfiguration(SingletonScope.PLUGIN);
  }

  public static @NotNull @Unmodifiable Result<DatabaseConfiguration, SingletonError>
      provideDatabaseConfiguration(String databaseSingletonIdentifier) {
    return provideDatabaseConfiguration(SingletonScope.PLUGIN, databaseSingletonIdentifier);
  }

  @ApiStatus.Internal
  public static @NotNull @Unmodifiable Result<DatabaseConfiguration, SingletonError>
      provideDatabaseConfiguration(SingletonScope scope) {
    return Singletons.provideSingleton(new Database(), scope)
        .flatMap(_ -> Singletons.provideSingleton(new DatabaseConfiguration(scope, null), scope));
  }

  @ApiStatus.Internal
  public static @NotNull @Unmodifiable Result<DatabaseConfiguration, SingletonError>
      provideDatabaseConfiguration(
          SingletonScope scope, @NotNull String databaseSingletonIdentifier) {
    return Singletons.provideSingleton(new Database(), databaseSingletonIdentifier, scope)
        .flatMap(
            _ ->
                Singletons.provideSingleton(
                    new DatabaseConfiguration(scope, databaseSingletonIdentifier), scope));
  }

  public static @NotNull @Unmodifiable Result<DatabaseConfiguration, SingletonError>
      provideDatabaseConfiguration(String singletonIdentifier, SingletonScope scope) {
    return Singletons.provideSingleton(new Database(), scope)
        .flatMap(
            _ ->
                Singletons.provideSingleton(
                    new DatabaseConfiguration(scope, null), singletonIdentifier, scope));
  }

  public static @NotNull @Unmodifiable Result<DatabaseConfiguration, SingletonError>
      provideDatabaseConfiguration(
          @NotNull String singletonIdentifier,
          SingletonScope scope,
          @NotNull String databaseSingletonIdentifier) {
    return Singletons.provideSingleton(new Database(), databaseSingletonIdentifier, scope)
        .flatMap(
            _ ->
                Singletons.provideSingleton(
                    new DatabaseConfiguration(scope, databaseSingletonIdentifier),
                    singletonIdentifier,
                    scope));
  }
}
