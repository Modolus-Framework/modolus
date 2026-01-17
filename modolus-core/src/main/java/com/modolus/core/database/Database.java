package com.modolus.core.database;

import com.modolus.core.logger.Logger;
import com.modolus.util.singleton.Lazy;
import com.modolus.util.singleton.Singleton;
import com.modolus.util.singleton.SingletonScope;
import com.modolus.util.singleton.Singletons;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;

public class Database implements Singleton {

    private final Lazy<Logger> logger = Lazy.ofPlugin(Logger.class);

    private HikariDataSource dataSource;

    private Database() {
    }

    public final void close() {
    }

    public final void open() {
    }

    public void updateConfiguration(@NotNull DatabaseConfiguration configuration) {
        this.close();

        HikariConfig config = new HikariConfig();


    }

    public static void provideDatabaseConfiguration() {
        provideDatabaseConfiguration(SingletonScope.PLUGIN);
    }

    public static void provideDatabaseConfiguration(SingletonScope scope) {
        Singletons.provideSingleton(new Database(), scope).onSuccess(Singleton::onInitialization);
    }

    public static void provideDatabaseConfiguration(String singletonIdentifier, SingletonScope scope) {
        Singletons.provideSingleton(new Database(), singletonIdentifier, scope).onSuccess(Singleton::onInitialization);
    }

}
