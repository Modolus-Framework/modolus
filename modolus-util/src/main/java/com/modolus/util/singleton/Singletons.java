package com.modolus.util.singleton;

import com.modolus.util.result.Result;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class Singletons {

    private static final RootSingletonManager ROOT_SINGLETON_MANAGER = new RootSingletonManager();

    public static <T extends Singleton> Result<Singleton, SingletonError> provideSingleton(@NotNull T value,
                                                                                           @NotNull SingletonScope scope) {
        if (scope == SingletonScope.ROOT) return ROOT_SINGLETON_MANAGER.provideSingleton(value);
        return ROOT_SINGLETON_MANAGER.provideSingletonInPluginScope(value);
    }

    public static <T extends Singleton> Result<Singleton, SingletonError> provideSingleton(@NotNull T value,
                                                                                           @NotNull String singletonIdentifier,
                                                                                           @NotNull SingletonScope scope) {
        if (scope == SingletonScope.ROOT) return ROOT_SINGLETON_MANAGER.provideSingleton(value, singletonIdentifier);
        return ROOT_SINGLETON_MANAGER.provideSingletonInPluginScope(value, singletonIdentifier);
    }

    public static <I, T extends I> Result<Singleton, SingletonError> provideSingleton(@NotNull Class<? extends I> forType,
                                                                                      @NotNull T value,
                                                                                      @NotNull SingletonScope scope) {
        if (scope == SingletonScope.ROOT) return ROOT_SINGLETON_MANAGER.provideSingleton(forType, value);
        return ROOT_SINGLETON_MANAGER.provideSingletonInPluginScope(forType, value);
    }

    public static <I, T extends I> Result<Singleton, SingletonError> provideSingleton(@NotNull Class<? extends I> forType,
                                                                                      @NotNull T value,
                                                                                      @NotNull String singletonIdentifier,
                                                                                      @NotNull SingletonScope scope) {
        if (scope == SingletonScope.ROOT)
            return ROOT_SINGLETON_MANAGER.provideSingleton(forType, value, singletonIdentifier);
        return ROOT_SINGLETON_MANAGER.provideSingletonInPluginScope(forType, value, singletonIdentifier);
    }

    public static void initializeSingletons() {
        ROOT_SINGLETON_MANAGER.initializeSingletons();
    }

    public static void registerScope(@NotNull String basePackagePath, @NotNull String scopeName) {
        ROOT_SINGLETON_MANAGER.registerScope(basePackagePath, scopeName);
    }

    public static <T> Result<T, SingletonError> getSingleton(@NotNull Class<T> clazz, @NotNull SingletonScope firstCheck) {
        if (firstCheck == SingletonScope.ROOT)
            return ROOT_SINGLETON_MANAGER.getSingleton(clazz)
                    .recoverFlat(_ -> ROOT_SINGLETON_MANAGER.getSingletonInPluginScope(clazz));

        return ROOT_SINGLETON_MANAGER.getSingletonInPluginScope(clazz)
                .recoverFlat(_ -> ROOT_SINGLETON_MANAGER.getSingleton(clazz));
    }

    public static <T> Result<T, SingletonError> getSingleton(@NotNull Class<T> clazz,
                                                             @NotNull String identifier,
                                                             @NotNull SingletonScope firstCheck) {
        if (firstCheck == SingletonScope.ROOT)
            return ROOT_SINGLETON_MANAGER.getSingleton(clazz)
                    .recoverFlat(_ -> ROOT_SINGLETON_MANAGER.getSingletonInPluginScope(clazz, identifier));

        return ROOT_SINGLETON_MANAGER.getSingletonInPluginScope(clazz)
                .recoverFlat(_ -> ROOT_SINGLETON_MANAGER.getSingleton(clazz, identifier));
    }

    public static void destructSingleton(@NotNull Singleton singleton, @NotNull SingletonScope scope) {
        if (scope == SingletonScope.ROOT) ROOT_SINGLETON_MANAGER.destructSingleton(singleton);
        else ROOT_SINGLETON_MANAGER.destructSingletonInPluginScope(singleton).orElseThrow();
    }

    public static void destructSingletons() {
        ROOT_SINGLETON_MANAGER.destructSingletons();
    }
}
