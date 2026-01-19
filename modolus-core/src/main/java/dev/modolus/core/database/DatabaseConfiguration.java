package dev.modolus.core.database;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.modolus.annotations.config.Config;
import dev.modolus.core.config.AbstractConfiguration;
import dev.modolus.core.logger.Logger;
import dev.modolus.core.logger.LoggerUtils;
import dev.modolus.util.result.Result;
import dev.modolus.util.singleton.*;
import lombok.Getter;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;

@Getter
@Config(name = "database")
public class DatabaseConfiguration extends AbstractConfiguration<DatabaseConfiguration> {

    @JsonIgnore
    private final SingletonScope scope;

    @JsonIgnore
    private final ExactScopedLazy<Database> database;

    @JsonIgnore
    private final Lazy<Logger> logger = Logger.getPluginLogger();

    @JsonIgnore
    private String migrationPath = null;

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

    protected DatabaseConfiguration(SingletonScope scope, @Nullable String databaseSingletonIdentifier) {
        super(DatabaseConfiguration.class, scope, false);

        this.scope = scope;
        this.database = databaseSingletonIdentifier == null
                ? ExactScopedLazy.of(Database.class, scope)
                : ExactScopedLazy.of(Database.class, scope, databaseSingletonIdentifier);
    }

    @Override
    protected void onConfigurationLoaded() {
        database.get().onFailure(e -> LoggerUtils.printError(logger, "No database provided: " + e.name()));
        database.get().onSuccess(db -> db.updateConfiguration(this));
    }

    public void withMigrations(String migrationPath) {
        this.migrationPath = migrationPath;
    }

    public static @NotNull @Unmodifiable Result<DatabaseConfiguration, SingletonError> provideDatabaseConfiguration() {
        return provideDatabaseConfiguration(SingletonScope.PLUGIN);
    }

    public static @NotNull @Unmodifiable Result<DatabaseConfiguration, SingletonError> provideDatabaseConfiguration(
            String databaseSingletonIdentifier) {
        return provideDatabaseConfiguration(SingletonScope.PLUGIN, databaseSingletonIdentifier);
    }

    @ApiStatus.Internal
    public static @NotNull @Unmodifiable Result<DatabaseConfiguration, SingletonError> provideDatabaseConfiguration(
            SingletonScope scope) {
        return Singletons.provideSingleton(new Database(), scope)
                .flatMap(_ -> Singletons.provideSingleton(new DatabaseConfiguration(scope, null), scope));
    }

    @ApiStatus.Internal
    public static @NotNull @Unmodifiable Result<DatabaseConfiguration, SingletonError> provideDatabaseConfiguration(
            SingletonScope scope, @NotNull String databaseSingletonIdentifier) {
        return Singletons.provideSingleton(new Database(), databaseSingletonIdentifier, scope)
                .flatMap(_ -> Singletons.provideSingleton(new DatabaseConfiguration(scope, databaseSingletonIdentifier), scope));
    }

    public static @NotNull @Unmodifiable Result<DatabaseConfiguration, SingletonError> provideDatabaseConfiguration(
            String singletonIdentifier, SingletonScope scope) {
        return Singletons.provideSingleton(new Database(), scope)
                .flatMap(_ -> Singletons.provideSingleton(new DatabaseConfiguration(scope, null), singletonIdentifier, scope));

    }

    public static @NotNull @Unmodifiable Result<DatabaseConfiguration, SingletonError> provideDatabaseConfiguration(
            @NotNull String singletonIdentifier, SingletonScope scope, @NotNull String databaseSingletonIdentifier) {
        return Singletons.provideSingleton(new Database(), databaseSingletonIdentifier, scope)
                .flatMap(_ -> Singletons.provideSingleton(new DatabaseConfiguration(scope, databaseSingletonIdentifier), singletonIdentifier, scope));
    }

}
