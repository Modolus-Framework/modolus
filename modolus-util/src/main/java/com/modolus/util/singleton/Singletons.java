package com.modolus.util.singleton;

import com.modolus.util.result.Result;
import lombok.experimental.UtilityClass;
import org.apache.commons.text.CaseUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@UtilityClass
public class Singletons {

    private static final Map<Class<?>, Map<String, Singleton>> SINGLETON_HOLDER = new ConcurrentHashMap<>();

    public static <T extends Singleton> Result<Singleton, SingletonError> provideSingleton(@NotNull T value) {
        return provideSingleton(value, getDefaultSingletonNameFor(value.getClass()));
    }

    public static <T extends Singleton> Result<Singleton, SingletonError> provideSingleton(@NotNull T value,
                                                                                           @NotNull String singletonIdentifier) {
        var map = SINGLETON_HOLDER.computeIfAbsent(value.getClass(), _ -> new HashMap<>());
        if (map.containsKey(singletonIdentifier))
            return Result.failure(SingletonError.SINGLETON_ALREADY_PROVIDED);
        map.put(singletonIdentifier, value);
        return Result.success(value);
    }

    public static <I, T extends I> Result<Singleton, SingletonError> provideSingleton(Class<? extends I> forType,
                                                                                      T value) {
        return provideSingleton(forType, value, getDefaultSingletonNameFor(forType));
    }

    public static <I, T extends I> Result<Singleton, SingletonError> provideSingleton(Class<? extends I> forType,
                                                                                      T value,
                                                                                      String singletonIdentifier) {
        if (!(value instanceof Singleton singleton)) {
            return Result.failure(SingletonError.VALUE_DOES_NOT_IMPLEMENT_SINGLETON_INTERFACE);
        }

        var map = SINGLETON_HOLDER.computeIfAbsent(forType, _ -> new HashMap<>());
        if (map.containsKey(singletonIdentifier))
            return Result.failure(SingletonError.SINGLETON_ALREADY_PROVIDED);
        map.put(singletonIdentifier, singleton);
        return Result.success(singleton);
    }

    public static void initializeSingletons() {
        SINGLETON_HOLDER.values().stream()
                .map(Map::values)
                .flatMap(Collection::stream)
                .forEach(Singleton::onInitialization);
    }

    public static <T> Result<T, SingletonError> getSingleton(Class<T> clazz) {
        return getSingleton(clazz, getDefaultSingletonNameFor(clazz));
    }

    public static <T> Result<T, SingletonError> getSingleton(Class<T> clazz,
                                                             String identifier) {
        if (!SINGLETON_HOLDER.containsKey(clazz)) return Result.failure(SingletonError.NO_INSTANCE_AVAILABLE);
        var map = SINGLETON_HOLDER.get(clazz);
        if (!map.containsKey(identifier)) return Result.failure(SingletonError.NO_INSTANCE_AVAILABLE);
        var value = map.get(identifier);
        if (!clazz.isInstance(value)) return Result.failure(SingletonError.INSTANCE_IS_NOT_THE_REQUESTED_TYPE);
        return Result.success(clazz.cast(value));
    }

    public static void destructSingleton(@NotNull Singleton singleton) {
        singleton.onDestruction();
        SINGLETON_HOLDER.values().stream()
                .filter(map -> map.containsValue(singleton))
                .forEach(map -> map.entrySet().removeIf(entry -> entry.getValue().equals(singleton)));
    }

    public static String getDefaultSingletonNameFor(@NotNull Class<?> clazz) {
        return CaseUtils.toCamelCase(clazz.getSimpleName(), false);
    }

    public static void destructSingletons() {
        SINGLETON_HOLDER.values().stream()
                .map(Map::values)
                .flatMap(Collection::stream)
                .forEach(Singleton::destruct);
        SINGLETON_HOLDER.clear();
    }
}
