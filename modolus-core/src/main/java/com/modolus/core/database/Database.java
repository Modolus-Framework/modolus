package com.modolus.core.database;

import com.modolus.util.singleton.Singleton;
import com.modolus.util.singleton.SingletonScope;
import com.modolus.util.singleton.Singletons;
import org.jetbrains.annotations.NotNull;

public class Database implements Singleton {

    private Database() {
    }

    public final void close() {
    }

    public final void open() {
    }

    public void updateConfiguration(@NotNull DatabaseConfiguration configuration) {
        this.close();


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
