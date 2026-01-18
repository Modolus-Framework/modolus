package com.modolus.core.runtime;

import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.ShutdownReason;
import com.modolus.core.logger.Logger;
import com.modolus.core.logger.LoggerUtils;
import com.modolus.util.result.Result;
import com.modolus.util.singleton.SingletonError;
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

    public static @NotNull Result<Void, RuntimeError> initializeRuntime() {
        var classLoaderResult = getDefaultClassLoader();

        return initializeScopes(classLoaderResult)
                .flatMap(_ -> initializeClasses(classLoaderResult));
    }

    public static @NotNull Result<Void, RuntimeError> initializeCurrentScope() {
        return Singletons.initializeSingletons()
                .<RuntimeError>switchMapError()
                .caseError(SingletonError.SCOPE_ALREADY_INITIALIZED, RuntimeError.FAILED_TO_INITIALIZE_CURRENT_SCOPE)
                .finish()
                .mapVoid(scope -> LoggerUtils.printInfo(Logger.getPluginLogger(), String.format("Current scope successfully initialized (Scope: %s)", scope)));
    }

    public static void requireSuccess(@NotNull Result<Void, RuntimeError> result) {
        result.onFailure(Runtime::handleRuntimeInitializationError);
    }

    private static @NotNull Result<Void, RuntimeError> initializeScopes(@NotNull Result<ClassLoader, RuntimeError> classLoader) {
        return classLoader.mapException(loader -> loader.getResources("runtime-scopes.json"),
                        _ -> RuntimeError.FAILED_TO_LOAD_SCOPES, IOException.class)
                .map(Runtime::collectStringSet)
                .map(result -> result.mapError(_ -> RuntimeError.FAILED_TO_LOAD_SCOPES))
                .flatMap(r -> r)
                .mapVoid(scopes -> scopes.forEach(scope -> {
                    LoggerUtils.printInfo(Logger.getPluginLogger(), "Initializing scope " + scope);
                    Singletons.registerScope(scope);
                }));
    }

    private static @NotNull Result<Void, RuntimeError> initializeClasses(@NotNull Result<ClassLoader, RuntimeError> classLoader) {
        return classLoader.mapException(loader -> loader.getResources("runtime-classes.json"),
                        _ -> RuntimeError.FAILED_TO_LOAD_RESOURCES, IOException.class)
                .map(Runtime::collectStringSet)
                .map(result -> result.mapError(_ -> RuntimeError.FAILED_TO_READ_CLASSES))
                .flatMap(r -> r)
                .tap(result -> LoggerUtils.printInfo(Logger.getPluginLogger(), "Found classes to initialize: " + String.join(",", result)))
                .mapVoid(classes -> classes.parallelStream()
                        .map(Runtime::getClassByName)
                        .peek(result -> result.tap(clazz -> LoggerUtils.printInfo(Logger.getPluginLogger(), "Found class " + clazz.getName())))
                        .map(Runtime::constructClass)
                        .forEach(Runtime::logError))
                .mapVoid(_ -> LoggerUtils.printInfo(Logger.getPluginLogger(), "Runtime successfully created all classes"));
    }

    private void logError(@NotNull Result<Void, RuntimeError> result) {
        result
                .mapError(RuntimeError::name)
                .onFailure(s -> LoggerUtils.printError(Logger.getPluginLogger(), s));
    }

    private @NotNull Result<Void, RuntimeError> constructClass(@NotNull Result<Class<?>, RuntimeError> result) {
        return result.mapExceptionVoid(Runtime::constructClass, ex -> {
            LoggerUtils.printWarn(Logger.getPluginLogger(), "Failed to construct class " + ex.getMessage());
            return RuntimeError.FAILED_TO_CREATE_CLASS;
        }, ReflectiveOperationException.class);
    }

    private void constructClass(@NotNull Class<?> clazz) throws ReflectiveOperationException {
        LoggerUtils.printInfo(Logger.getPluginLogger(), "Constructing class " + clazz.getName());
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

    private void handleRuntimeInitializationError(@NotNull RuntimeError runtimeError) {
        var message = String.format("An error occured while booting modolus %s", runtimeError.name());
        LoggerUtils.printError(Logger.getPluginLogger(), message);
        HytaleServer.get().shutdownServer(new ShutdownReason(1, message));
    }

}
