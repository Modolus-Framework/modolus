/*
 * Copyright (C) 2026 Modolus-Framework
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package dev.modolus.core.runtime;

import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.ShutdownReason;
import dev.modolus.core.logger.Logger;
import dev.modolus.core.logger.LoggerUtils;
import dev.modolus.util.result.Error;
import dev.modolus.util.result.GenericError;
import dev.modolus.util.result.Result;
import dev.modolus.util.singleton.SingletonError;
import dev.modolus.util.singleton.Singletons;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@UtilityClass
public class Runtime {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  public static @NotNull Result<Void, RuntimeError> initializeRuntime() {
    var classLoaderResult = getDefaultClassLoader();

    return initializeScopes(classLoaderResult).flatMap(_ -> initializeClasses(classLoaderResult));
  }

  public static @NotNull Result<Void, RuntimeError> initializeCurrentScope() {
    return Singletons.initializeSingletons()
        .<RuntimeError>switchMapError()
        .caseError(
            SingletonError.SCOPE_ALREADY_INITIALIZED,
            RuntimeError.FAILED_TO_INITIALIZE_CURRENT_SCOPE::toErrorWithCause)
        .finish()
        .mapVoid(
            scope ->
                LoggerUtils.printInfo(
                    Logger.getPluginLogger(),
                    String.format("Current scope successfully initialized (Scope: %s)", scope)));
  }

  public static void requireSuccess(@NotNull Result<Void, RuntimeError> result) {
    result.onFailure(Runtime::handleRuntimeInitializationError);
  }

  private static @NotNull Result<Void, RuntimeError> initializeScopes(
      @NotNull Result<ClassLoader, RuntimeError> classLoader) {
    return classLoader
        .mapException(loader -> loader.getResources("runtime-scopes.json"), IOException.class)
        .mapError(RuntimeError.FAILED_TO_LOAD_SCOPES::toErrorWithCause)
        .map(Runtime::collectStringSet)
        .map(result -> result.mapError(RuntimeError.FAILED_TO_LOAD_SCOPES::toErrorWithCause))
        .flatMap(r -> r)
        .mapVoid(
            scopes ->
                scopes.forEach(
                    scope -> {
                      LoggerUtils.printInfo(
                          Logger.getPluginLogger(), "Initializing scope " + scope);
                      Singletons.registerScope(scope);
                    }));
  }

  private static @NotNull Result<Void, RuntimeError> initializeClasses(
      @NotNull Result<ClassLoader, RuntimeError> classLoader) {
    return classLoader
        .mapException(loader -> loader.getResources("runtime-classes.json"), IOException.class)
        .mapError(RuntimeError.FAILED_TO_LOAD_RESOURCES::toErrorWithCause)
        .map(Runtime::collectStringSet)
        .map(result -> result.mapError(RuntimeError.FAILED_TO_READ_CLASSES::toErrorWithCause))
        .flatMap(r -> r)
        .tap(
            result ->
                LoggerUtils.printInfo(
                    Logger.getPluginLogger(),
                    "Found classes to initialize: " + String.join(",", result)))
        .mapVoid(
            classes ->
                classes.parallelStream()
                    .map(Runtime::getClassByName)
                    .peek(
                        result ->
                            result.tap(
                                clazz ->
                                    LoggerUtils.printInfo(
                                        Logger.getPluginLogger(),
                                        "Found class " + clazz.getName())))
                    .map(Runtime::constructClass)
                    .forEach(Runtime::logError))
        .mapVoid(
            _ ->
                LoggerUtils.printInfo(
                    Logger.getPluginLogger(), "Runtime successfully created all classes"));
  }

  private void logError(@NotNull Result<Void, RuntimeError> result) {
    result.onFailure(s -> LoggerUtils.printError(Logger.getPluginLogger(), s));
  }

  private @NotNull Result<Void, RuntimeError> constructClass(
      @NotNull Result<Class<?>, RuntimeError> result) {
    return result
        .mapExceptionVoid(Runtime::constructClass, ReflectiveOperationException.class)
        .mapError(RuntimeError.FAILED_TO_CREATE_CLASS::toErrorWithCause);
  }

  private void constructClass(@NotNull Class<?> clazz) throws ReflectiveOperationException {
    LoggerUtils.printInfo(Logger.getPluginLogger(), "Constructing class " + clazz.getName());
    var constructor = clazz.getConstructor();
    constructor.trySetAccessible();
    constructor.newInstance();
  }

  private @NotNull Result<Class<?>, RuntimeError> getClassByName(String file) {
    return Result.<Class<?>, ClassNotFoundException>ofException(
            () -> Class.forName(file), ClassNotFoundException.class)
        .mapError(err -> RuntimeError.FAILED_TO_LOAD_CLASS.toErrorWithCause(err, file));
  }

  private Result<Set<String>, GenericError> collectStringSet(Enumeration<URL> files) {
    return Result.ofException(
        () -> {
          Set<String> classesToInitialize = new HashSet<>();

          while (files.hasMoreElements()) {
            var fileUrl = files.nextElement();

            var classes =
                OBJECT_MAPPER.readValue(fileUrl.openStream(), new TypeReference<Set<String>>() {});
            classesToInitialize.addAll(classes);
          }

          return classesToInitialize;
        },
        IOException.class);
  }

  private static @NotNull Result<ClassLoader, RuntimeError> getDefaultClassLoader() {
    return Result.ofNullableWithException(
            () -> Thread.currentThread().getContextClassLoader(), Exception.class)
        .tryRecoverNullable(_ -> Runtime.class.getClassLoader(), Exception.class)
        .recoverNullable(_ -> ClassLoader.getSystemClassLoader())
        .tryRecoverNullable(_ -> ClassLoader.getSystemClassLoader(), Exception.class)
        .mapError(_ -> RuntimeError.NO_AVAILABLE_CLASS_LOADER.toError());
  }

  private void handleRuntimeInitializationError(@NotNull Error<RuntimeError> runtimeError) {
    LoggerUtils.printError(Logger.getPluginLogger(), "An error occured while booting modolus");
    LoggerUtils.printError(Logger.getPluginLogger(), runtimeError);
    HytaleServer.get().shutdownServer(new ShutdownReason(1, runtimeError.getMessage()));
  }
}
