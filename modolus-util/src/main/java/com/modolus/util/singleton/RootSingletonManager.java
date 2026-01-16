package com.modolus.util.singleton;

import com.modolus.util.result.Result;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class RootSingletonManager extends ScopedSingletonManager {

    private static final List<String> ROOT_PACKAGE_NAMES = List.of(
            "com.modolus.util",
            "com.modolus.core",
            "com.modolus.annotations"
    );

    private final Map<String, String> scopeBasePackageToScopeNameMap = new HashMap<>();
    private final Map<String, ScopedSingletonManager> scopes = new ConcurrentHashMap<>();

    public <T extends Singleton> @NotNull Result<Singleton, SingletonError> provideSingletonInPluginScope(@NotNull T value) {
        return getCallersScopeManager()
                .flatMap(manager -> manager.provideSingleton(value));
    }

    public <T extends Singleton> @NotNull Result<Singleton, SingletonError> provideSingletonInPluginScope(@NotNull T value,
                                                                                                          @NotNull String singletonIdentifier) {
        return getCallersScopeManager()
                .flatMap(manager -> manager.provideSingleton(value, singletonIdentifier));
    }

    public <I, T extends I> @NotNull Result<Singleton, SingletonError> provideSingletonInPluginScope(Class<? extends I> forType,
                                                                                                     T value) {
        return getCallersScopeManager()
                .flatMap(manager -> manager.provideSingleton(forType, value));
    }

    public <I, T extends I> @NotNull Result<Singleton, SingletonError> provideSingletonInPluginScope(Class<? extends I> forType,
                                                                                                     T value,
                                                                                                     String singletonIdentifier) {
        return getCallersScopeManager()
                .flatMap(manager -> manager.provideSingleton(forType, value, singletonIdentifier));
    }

    public <T> @NotNull Result<T, SingletonError> getSingletonInPluginScope(Class<T> clazz) {
        return getCallersScopeManager()
                .flatMap(manager -> manager.getSingleton(clazz, getDefaultSingletonNameFor(clazz)));
    }

    public <T> @NotNull Result<T, SingletonError> getSingletonInPluginScope(Class<T> clazz,
                                                                            String identifier) {
        return getCallersScopeManager()
                .flatMap(manager -> manager.getSingleton(clazz, identifier));
    }

    public @NotNull Result<Void, SingletonError> destructSingletonInPluginScope(@NotNull Singleton singleton) {
        return getCallersScopeManager()
                .map((Consumer<ScopedSingletonManager>) manager ->
                        manager.destructSingleton(singleton));
    }

    public void registerScope(String scopeBasePackage, String scopeName) {
        scopeBasePackageToScopeNameMap.put(scopeBasePackage, scopeName);
        scopes.put(scopeName, new ScopedSingletonManager());
    }

    @Override
    public void initializeSingletons() {
        scopes.forEach((_, manager) -> manager.initializeSingletons());
        super.initializeSingletons();
    }

    @Override
    public void destructSingletons() {
        scopes.forEach((_, manager) -> manager.destructSingletons());
        super.destructSingletons();
    }

    private @NotNull Result<ScopedSingletonManager, SingletonError> getCallersScopeManager() {
        return getCallersPackageName()
                .flatMap(this::findScopeNameByPackageName)
                .map(scopes::get);
    }

    private @NotNull Result<String, SingletonError> getCallersPackageName() {
        StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
        return walker.walk(stream -> Result.ofOptional(stream
                        .map(frame -> frame.getDeclaringClass().getPackageName())
                        .filter(name -> ROOT_PACKAGE_NAMES.stream().noneMatch(name::startsWith))
                        .findFirst()))
                .mapError(_ -> SingletonError.FAILED_TO_GET_CALLERS_SCOPE);
    }

    private @NotNull Result<String, SingletonError> findScopeNameByPackageName(String packageName) {
        return Result.ofOptional(scopeBasePackageToScopeNameMap.entrySet().stream()
                        .filter(entry -> packageName.startsWith(entry.getKey()))
                        .findFirst()
                        .map(Map.Entry::getValue))
                .mapError(_ -> SingletonError.FAILED_TO_GET_CALLERS_SCOPE);
    }

}
