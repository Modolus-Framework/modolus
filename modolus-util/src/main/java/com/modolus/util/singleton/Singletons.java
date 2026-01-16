package com.modolus.util.singleton;

import com.modolus.util.result.Result;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@UtilityClass
public class Singletons {

    private static final Map<Class<?>, Singleton> SINGLETON_HOLDER = new ConcurrentHashMap<>();

    public static <T extends Singleton> Result<Void, SingletonError> registerSingleton(@NotNull T value) {
        if (SINGLETON_HOLDER.containsKey(value.getClass())) return Result.failure(SingletonError.TYPE_ALREADY_PROVIDED);
        SINGLETON_HOLDER.put(value.getClass(), value);
        return Result.success();
    }

    public static <I, T extends I> Result<Void, SingletonError> registerSingleton(Class<? extends I> forType, T value) {
        if (!(value instanceof Singleton singleton)) {
            return Result.failure(SingletonError.VALUE_DOES_NOT_IMPLEMENT_SINGLETON_INTERFACE);
        }

        if (SINGLETON_HOLDER.containsKey(forType)) return Result.failure(SingletonError.TYPE_ALREADY_PROVIDED);
        SINGLETON_HOLDER.put(forType, singleton);
        return Result.success();
    }

    public static void initializeSingletons() {
        SINGLETON_HOLDER.forEach((_, singleton) -> singleton.onInitialization());
    }

    public static <T> Result<T, SingletonError> getSingleton(Class<T> clazz) {
        if (!SINGLETON_HOLDER.containsKey(clazz)) return Result.failure(SingletonError.NO_INSTANCE_AVAILABLE);
        var value = SINGLETON_HOLDER.get(clazz);
        if (!clazz.isInstance(value)) return Result.failure(SingletonError.INSTANCE_IS_NOT_THE_REQUESTED_TYPE);
        return Result.success(clazz.cast(value));
    }

    public static void destructSingleton(Class<?> clazz) {
        SINGLETON_HOLDER.remove(clazz).onDestruction();
    }

}
