package com.modolus.core.database;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.modolus.annotations.config.Config;
import com.modolus.core.config.AbstractConfiguration;
import com.modolus.core.logger.Logger;
import com.modolus.core.logger.LoggerUtils;
import com.modolus.util.singleton.Lazy;
import com.modolus.util.singleton.SingletonScope;
import com.modolus.util.singleton.Singletons;
import lombok.Getter;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Getter
@Config(name = "database")
public class DatabaseConfiguration extends AbstractConfiguration<DatabaseConfiguration> {

    @JsonIgnore
    private final SingletonScope scope;

    @JsonIgnore
    private final Lazy<Database> database;

    @JsonIgnore
    private final Lazy<Logger> logger = Logger.getPluginLogger();

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
        super(DatabaseConfiguration.class, scope);
        this.scope = scope;
        this.database = databaseSingletonIdentifier == null
                ? Lazy.of(Database.class, scope)
                : Lazy.of(Database.class, scope, databaseSingletonIdentifier);
    }

    @Override
    protected void onConfigurationLoaded() {
        database.get().onFailure(e -> LoggerUtils.printError(logger, "No database provided: " + e.name()));
        database.get().onSuccess(db -> db.updateConfiguration(this));
    }

    public static void provideDatabaseConfiguration() {
        provideDatabaseConfiguration(SingletonScope.PLUGIN);
    }

    public static void provideDatabaseConfiguration(String databaseSingletonIdentifier) {
        provideDatabaseConfiguration(SingletonScope.PLUGIN, databaseSingletonIdentifier);
    }

    @ApiStatus.Internal
    public static void provideDatabaseConfiguration(SingletonScope scope) {
        provideDatabaseConfiguration(scope, null);
    }

    @ApiStatus.Internal
    public static void provideDatabaseConfiguration(SingletonScope scope, String databaseSingletonIdentifier) {
        Singletons.provideSingleton(new DatabaseConfiguration(scope, databaseSingletonIdentifier), scope);
    }

    public static void provideDatabaseConfiguration(String singletonIdentifier, SingletonScope scope) {
        provideDatabaseConfiguration(singletonIdentifier, scope, null);
    }

    public static void provideDatabaseConfiguration(String singletonIdentifier, SingletonScope scope, String databaseSingletonIdentifier) {
        Singletons.provideSingleton(new DatabaseConfiguration(scope, databaseSingletonIdentifier), singletonIdentifier, scope);
    }

}
