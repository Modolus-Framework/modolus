package com.modolus.util.singleton;

import com.modolus.util.result.Result;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public sealed class Lazy<T> permits ExactScopedLazy {

    protected final @NotNull Class<T> clazz;
    protected final @NotNull SingletonScope scope;
    protected @Nullable String singletonIdentifier = null;


    public @NotNull Result<T, SingletonError> get() {
        return singletonIdentifier == null
                ? Singletons.getSingleton(clazz, scope)
                : Singletons.getSingleton(clazz, singletonIdentifier, scope);
    }

    public T getOrThrow() {
        return this.get().orElseThrow();
    }

    @Contract("_ -> new")
    public static <T> @NotNull Lazy<T> ofPlugin(@NotNull Class<T> clazz) {
        return new Lazy<>(clazz, SingletonScope.PLUGIN);
    }

    @Contract("_, _ -> new")
    public static <T> @NotNull Lazy<T> ofPlugin(@NotNull Class<T> clazz,
                                                @NotNull String singletonIdentifier) {
        return new Lazy<>(clazz, SingletonScope.PLUGIN, singletonIdentifier);
    }

    @Contract("_ -> new")
    public static <T> @NotNull Lazy<T> ofRoot(@NotNull Class<T> clazz) {
        return new Lazy<>(clazz, SingletonScope.ROOT);
    }

    @Contract("_, _ -> new")
    public static <T> @NotNull Lazy<T> ofRoot(@NotNull Class<T> clazz,
                                              @NotNull String singletonIdentifier) {
        return new Lazy<>(clazz, SingletonScope.ROOT, singletonIdentifier);
    }

    @Contract("_, _ -> new")
    public static <T> @NotNull Lazy<T> of(@NotNull Class<T> clazz,
                                          @NotNull SingletonScope scope) {
        return new Lazy<>(clazz, scope);
    }

    @Contract("_, _, _ -> new")
    public static <T> @NotNull Lazy<T> of(@NotNull Class<T> clazz,
                                          @NotNull SingletonScope scope,
                                          @NotNull String singletonIdentifier) {
        return new Lazy<>(clazz, scope, singletonIdentifier);
    }

}
