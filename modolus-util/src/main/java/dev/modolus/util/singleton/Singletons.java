package dev.modolus.util.singleton;

import dev.modolus.util.result.Result;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.function.Consumer;

@UtilityClass
public class Singletons {

    private static final RootSingletonManager ROOT_SINGLETON_MANAGER = new RootSingletonManager();

    public static <T extends Singleton> Result<T, SingletonError> provideSingleton(@NotNull T value,
                                                                                           @NotNull SingletonScope scope) {
        if (scope == SingletonScope.ROOT) return ROOT_SINGLETON_MANAGER.provideSingleton(value);
        return ROOT_SINGLETON_MANAGER.provideSingletonInPluginScope(value);
    }

    public static <T extends Singleton> Result<T, SingletonError> provideSingleton(@NotNull T value,
                                                                                           @NotNull String singletonIdentifier,
                                                                                           @NotNull SingletonScope scope) {
        if (scope == SingletonScope.ROOT) return ROOT_SINGLETON_MANAGER.provideSingleton(value, singletonIdentifier);
        return ROOT_SINGLETON_MANAGER.provideSingletonInPluginScope(value, singletonIdentifier);
    }

    public static <I, T extends I> Result<T, SingletonError> provideSingleton(@NotNull Class<? extends I> forType,
                                                                                      @NotNull T value,
                                                                                      @NotNull SingletonScope scope) {
        if (scope == SingletonScope.ROOT) return ROOT_SINGLETON_MANAGER.provideSingleton(forType, value);
        return ROOT_SINGLETON_MANAGER.provideSingletonInPluginScope(forType, value);
    }

    public static <I, T extends I> Result<T, SingletonError> provideSingleton(@NotNull Class<? extends I> forType,
                                                                                      @NotNull T value,
                                                                                      @NotNull String singletonIdentifier,
                                                                                      @NotNull SingletonScope scope) {
        if (scope == SingletonScope.ROOT)
            return ROOT_SINGLETON_MANAGER.provideSingleton(forType, value, singletonIdentifier);
        return ROOT_SINGLETON_MANAGER.provideSingletonInPluginScope(forType, value, singletonIdentifier);
    }

    public static @NotNull Result<String, SingletonError> initializeSingletons() {
        return ROOT_SINGLETON_MANAGER.initializeSingletons();
    }

    public static void registerScope(@NotNull String basePackagePath) {
        ROOT_SINGLETON_MANAGER.registerScope(basePackagePath);
    }

    public static <T> @NotNull Result<T, SingletonError> getSingleton(@NotNull Class<T> clazz,
                                                                      @NotNull SingletonScope firstCheck) {
        if (firstCheck == SingletonScope.ROOT)
            return ROOT_SINGLETON_MANAGER.getSingleton(clazz);

        return ROOT_SINGLETON_MANAGER.getSingletonInPluginScope(clazz)
                .recoverFlat(_ -> ROOT_SINGLETON_MANAGER.getSingleton(clazz));
    }

    public static <T> @NotNull Result<T, SingletonError> getSingleton(@NotNull Class<T> clazz,
                                                                      @NotNull String identifier,
                                                                      @NotNull SingletonScope firstCheck) {
        if (firstCheck == SingletonScope.ROOT)
            return ROOT_SINGLETON_MANAGER.getSingleton(clazz, identifier);

        return ROOT_SINGLETON_MANAGER.getSingletonInPluginScope(clazz, identifier)
                .recoverFlat(_ -> ROOT_SINGLETON_MANAGER.getSingleton(clazz, identifier));
    }

    public static <T> @NotNull Result<T, SingletonError> getExactScopedSingleton(@NotNull Class<T> clazz,
                                                                                 @NotNull SingletonScope firstCheck) {
        if (firstCheck == SingletonScope.ROOT)
            return ROOT_SINGLETON_MANAGER.getSingleton(clazz);

        return ROOT_SINGLETON_MANAGER.getSingletonInPluginScope(clazz);
    }

    public static <T> @NotNull Result<T, SingletonError> getExactScopedSingleton(@NotNull Class<T> clazz,
                                                                                 @NotNull String identifier,
                                                                                 @NotNull SingletonScope firstCheck) {
        if (firstCheck == SingletonScope.ROOT)
            return ROOT_SINGLETON_MANAGER.getSingleton(clazz, identifier);

        return ROOT_SINGLETON_MANAGER.getSingletonInPluginScope(clazz, identifier);
    }

    public static <T> @NotNull Result<Set<T>, SingletonError> getSingletonsForScope(@NotNull Class<T> clazz,
                                                                                    @NotNull SingletonScope scope) {
        if (scope == SingletonScope.ROOT) return Result.success(ROOT_SINGLETON_MANAGER.getSingletons(clazz));
        return ROOT_SINGLETON_MANAGER.getSingletonsInPluginScope(clazz);
    }

    public static void destructSingleton(@NotNull Singleton singleton, @NotNull SingletonScope scope) {
        if (scope == SingletonScope.ROOT) ROOT_SINGLETON_MANAGER.destructSingleton(singleton);
        else ROOT_SINGLETON_MANAGER.destructSingletonInPluginScope(singleton).orElseThrow();
    }

    public static @NotNull Result<String, SingletonError> destructSingletons() {
        return ROOT_SINGLETON_MANAGER.destructSingletons();
    }

    public static void debugSingletons(@NotNull Consumer<String> messageSender) {
        ROOT_SINGLETON_MANAGER.debugSingletons(messageSender);
    }

}
