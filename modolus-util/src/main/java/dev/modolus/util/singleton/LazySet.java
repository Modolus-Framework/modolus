package dev.modolus.util.singleton;

import dev.modolus.util.result.Result;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class LazySet<T> {

    private final Class<T> clazz;
    private final SingletonScope scope;

    public @NotNull Result<Set<T>, SingletonError> get() {
        return Singletons.getSingletonsForScope(clazz, scope);
    }

    public Set<T> getOrThrow() {
        return this.get().orElseThrow();
    }

    @Contract("_ -> new")
    public static <T> @NotNull LazySet<T> ofPlugin(@NotNull Class<T> clazz) {
        return of(clazz, SingletonScope.PLUGIN);
    }

    @Contract("_ -> new")
    public static <T> @NotNull LazySet<T> ofRoot(@NotNull Class<T> clazz) {
        return of(clazz, SingletonScope.ROOT);
    }

    @Contract("_, _ -> new")
    public static <T> @NotNull LazySet<T> of(@NotNull Class<T> clazz,
                                             @NotNull SingletonScope scope) {
        return new LazySet<>(clazz, scope);
    }

}
