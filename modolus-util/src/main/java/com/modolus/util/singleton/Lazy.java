package com.modolus.util.singleton;

import com.modolus.util.result.Result;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Lazy<T> {

    private final Class<T> clazz;
    private final SingletonScope scope;
    private String singletonIdentifier = null;

    public Result<T, SingletonError> get() {
        return singletonIdentifier == null ? Singletons.getSingleton(clazz, scope) : Singletons.getSingleton(clazz, singletonIdentifier, scope);
    }

    public T getOrThrow() {
        return this.get().orElseThrow();
    }

    public static <T> Lazy<T> ofPlugin(@NotNull Class<T> clazz) {
        return new Lazy<>(clazz, SingletonScope.PLUGIN, null);
    }

    public static <T> Lazy<T> ofPlugin(@NotNull Class<T> clazz, @NotNull String singletonIdentifier) {
        return new Lazy<>(clazz, SingletonScope.PLUGIN, singletonIdentifier);
    }

    public static <T> Lazy<T> ofRoot(@NotNull Class<T> clazz) {
        return new Lazy<>(clazz, SingletonScope.ROOT, null);
    }

    public static <T> Lazy<T> ofRoot(@NotNull Class<T> clazz, @NotNull String singletonIdentifier) {
        return new Lazy<>(clazz, SingletonScope.ROOT, singletonIdentifier);
    }

    public static <T> Lazy<T> of(@NotNull Class<T> clazz, @NotNull SingletonScope scope) {
        return new Lazy<>(clazz, scope, null);
    }

    public static <T> Lazy<T> of(@NotNull Class<T> clazz, @NotNull SingletonScope scope, @NotNull String singletonIdentifier) {
        return new Lazy<>(clazz, scope, singletonIdentifier);
    }

}
