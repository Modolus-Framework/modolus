package com.modolus.core.runtime;

import com.modolus.core.logger.Logger;
import com.modolus.util.result.Result;
import com.modolus.util.singleton.Lazy;
import com.modolus.util.singleton.SingletonScope;
import com.modolus.util.singleton.Singletons;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

@UtilityClass
public class Runtime {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Lazy<Logger> LOGGER = new Lazy<>(Logger.class, SingletonScope.ROOT);

    public static Result<Void, RuntimeError> initializeRuntime() {
        var classLoaderResult = getDefaultClassLoader();

        return initializeScopes(classLoaderResult)
                .flatMap(_ -> initializeClasses(classLoaderResult));
    }

    private static @NotNull Result<Void, RuntimeError> initializeScopes(@NotNull Result<ClassLoader, RuntimeError> classLoader) {
        return classLoader.mapException(loader -> loader.getResources("runtime-scopes.json"),
                        _ -> RuntimeError.FAILED_TO_LOAD_SCOPES, IOException.class)
                .map(Runtime::collectStringSet)
                .map(result -> result.mapError(_ -> RuntimeError.FAILED_TO_LOAD_SCOPES))
                .flatMap(r -> r)
                .mapVoid(scopes -> scopes.forEach(Singletons::registerScope));
    }

    private static @NotNull Result<Void, RuntimeError> initializeClasses(@NotNull Result<ClassLoader, RuntimeError> classLoader) {
        return classLoader.mapException(loader -> loader.getResources("runtime-classes.json"),
                        _ -> RuntimeError.FAILED_TO_LOAD_RESOURCES, IOException.class)
                .map(Runtime::collectStringSet)
                .map(result -> result.mapError(_ -> RuntimeError.FAILED_TO_READ_CLASSES))
                .flatMap(r -> r)
                .tap(result -> printInfo("Found classes to initialize: " + String.join(",", result)))
                .mapVoid(classes -> classes.parallelStream()
                        .map(Runtime::getClassByName)
                        .peek(result -> result.tap(clazz -> printInfo("Found class " + clazz.getName())))
                        .map(Runtime::constructClass)
                        .forEach(Runtime::logError))
                .mapVoid(_ -> Singletons.initializeSingletons());
    }

    private void logError(@NotNull Result<Void, RuntimeError> result) {
        result
                .mapError(RuntimeError::name)
                .onFailure(Runtime::printWarning);
    }

    private @NotNull Result<Void, RuntimeError> constructClass(@NotNull Result<Class<?>, RuntimeError> result) {
        return result.mapExceptionVoid(Runtime::constructClass, ex -> {
            printWarning("Failed to construct class " + ex.getMessage());
            return RuntimeError.FAILED_TO_CREATE_CLASS;
        }, ReflectiveOperationException.class);
    }

    private void constructClass(@NotNull Class<?> clazz) throws ReflectiveOperationException {
        printInfo("Constructing class " + clazz.getName());
        var constructor = clazz.getConstructor();
        constructor.trySetAccessible();
        constructor.newInstance();
    }

    private @NotNull Result<Class<?>, RuntimeError> getClassByName(String file) {
        return Result.<Class<?>, ClassNotFoundException>ofException(() -> Class.forName(file), ClassNotFoundException.class)
                .mapError(_ -> RuntimeError.FAILED_TO_LOAD_SCOPES);
    }

    private Result<Set<String>, IOException> collectStringSet(Enumeration<URL> files) {
        return Result.ofException(() -> {
            Set<String> classesToInitialize = new HashSet<>();

            while (files.hasMoreElements()) {
                var fileUrl = files.nextElement();

                var classes = OBJECT_MAPPER.readValue(fileUrl.openStream(), new TypeReference<Set<String>>() {
                });
                classesToInitialize.addAll(classes);
            }

            return classesToInitialize;
        }, IOException.class);
    }

    private static @NotNull Result<ClassLoader, RuntimeError> getDefaultClassLoader() {
        return Result.ofNullableWithException(() -> Thread.currentThread().getContextClassLoader(), Exception.class)
                .tryRecoverNullable(_ -> Runtime.class.getClassLoader(), Exception.class)
                .recoverNullable(_ -> ClassLoader.getSystemClassLoader())
                .tryRecoverNullable(_ -> ClassLoader.getSystemClassLoader(), Exception.class)
                .mapError(_ -> RuntimeError.NO_AVAILABLE_CLASS_LOADER);
    }

    private static void printWarning(String message) {
        LOGGER.get().onSuccess(logger -> logger.atWarning().log(message));
        LOGGER.get().onFailure(_ -> java.util.logging.Logger.getGlobal().warning(message));
    }

    private static void printInfo(String message) {
        LOGGER.get().onSuccess(logger -> logger.atInfo().log(message));
        LOGGER.get().onFailure(_ -> java.util.logging.Logger.getGlobal().info(message));
    }

}
