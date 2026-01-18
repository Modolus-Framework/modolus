package com.modolus.util.singleton;

import com.modolus.util.result.Result;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class RootSingletonManager extends ScopedSingletonManager {

    static final List<String> ROOT_PACKAGE_NAMES = List.of(
            "com.modolus.util",
            "com.modolus.core",
            "com.modolus.annotations",
            "java"
    );

    private final Map<String, ScopedSingletonManager> scopes = new ConcurrentHashMap<>();

    RootSingletonManager() {
        super("ROOT");
    }

    public <T extends Singleton> @NotNull Result<T, SingletonError> provideSingletonInPluginScope(@NotNull T value) {
        return getCallersScopeManager()
                .flatMap(manager -> manager.provideSingleton(value));
    }

    public <T extends Singleton> @NotNull Result<T, SingletonError> provideSingletonInPluginScope(@NotNull T value,
                                                                                                          @NotNull String singletonIdentifier) {
        return getCallersScopeManager()
                .flatMap(manager -> manager.provideSingleton(value, singletonIdentifier));
    }

    public <I, T extends I> @NotNull Result<T, SingletonError> provideSingletonInPluginScope(Class<? extends I> forType,
                                                                                                     T value) {
        return getCallersScopeManager()
                .flatMap(manager -> manager.provideSingleton(forType, value));
    }

    public <I, T extends I> @NotNull Result<T, SingletonError> provideSingletonInPluginScope(Class<? extends I> forType,
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


    public @NotNull <T> Result<Set<T>, SingletonError> getSingletonsInPluginScope(@NotNull Class<T> clazz) {
        return getCallersScopeManager()
                .map(manager -> manager.getSingletons(clazz));
    }

    public @NotNull Result<Void, SingletonError> destructSingletonInPluginScope(@NotNull Singleton singleton) {
        return getCallersScopeManager()
                .mapVoid(manager ->
                        manager.destructSingleton(singleton));
    }

    public void registerScope(@NotNull String scopeBasePackage) {
        if (ROOT_PACKAGE_NAMES.stream().anyMatch(scopeBasePackage::startsWith)) return;
        scopes.put(scopeBasePackage, new ScopedSingletonManager(scopeBasePackage));
    }

    @Override
    public @NotNull Result<String, SingletonError> initializeSingletons() {
        return getCallersScopeManager()
                .flatMap(ScopedSingletonManager::initializeSingletons)
                .recoverFlat(_ -> super.initializeSingletons());
    }

    @Override
    public @NotNull Result<String, SingletonError> destructSingletons() {
        return getCallersScopeManager()
                .flatMap(ScopedSingletonManager::destructSingletons)
                .recoverFlat(_ -> super.destructSingletons());
    }

    @Override
    public void debugSingletons(@NotNull Consumer<String> messageSender) {
        messageSender.accept("Scope (root):");
        super.debugSingletons(messageSender);

        this.scopes.forEach((packageName, manager) -> {
            messageSender.accept("Scope (" + packageName + "):");
            manager.debugSingletons(messageSender);
        });
    }

    private @NotNull Result<ScopedSingletonManager, SingletonError> getCallersScopeManager() {
        return getCallersPackageName()
                .flatMap(this::findScopeNameByPackageName);
    }

    private @NotNull Result<String, SingletonError> getCallersPackageName() {
        StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
        return walker.walk(stream -> Result.ofOptional(stream
                        .map(frame -> frame.getDeclaringClass().getPackageName())
                        .filter(name -> ROOT_PACKAGE_NAMES.stream().noneMatch(name::startsWith))
                        .findFirst()))
                .mapError(_ -> SingletonError.FAILED_TO_GET_CALLERS_SCOPE);
    }

    private @NotNull Result<ScopedSingletonManager, SingletonError> findScopeNameByPackageName(String packageName) {
        return Result.ofOptional(scopes.entrySet().stream()
                        .filter(entry -> packageName.startsWith(entry.getKey()))
                        .findFirst()
                        .map(Map.Entry::getValue))
                .mapError(_ -> SingletonError.FAILED_TO_GET_CALLERS_SCOPE);
    }
}
