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

package dev.modolus.util.singleton;

import dev.modolus.util.result.Result;
import java.util.Set;
import java.util.function.Consumer;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class Singletons {

  private static final RootSingletonManager ROOT_SINGLETON_MANAGER = new RootSingletonManager();

  public static <T extends Singleton> Result<T, SingletonError> provideSingleton(
      @NotNull T value, @NotNull SingletonScope scope) {
    if (scope == SingletonScope.ROOT) return ROOT_SINGLETON_MANAGER.provideSingleton(value);
    return ROOT_SINGLETON_MANAGER.provideSingletonInPluginScope(value);
  }

  public static <T extends Singleton> Result<T, SingletonError> provideSingleton(
      @NotNull T value, @NotNull String singletonIdentifier, @NotNull SingletonScope scope) {
    if (scope == SingletonScope.ROOT)
      return ROOT_SINGLETON_MANAGER.provideSingleton(value, singletonIdentifier);
    return ROOT_SINGLETON_MANAGER.provideSingletonInPluginScope(value, singletonIdentifier);
  }

  public static <I, T extends I> Result<T, SingletonError> provideSingleton(
      @NotNull Class<? extends I> forType, @NotNull T value, @NotNull SingletonScope scope) {
    if (scope == SingletonScope.ROOT)
      return ROOT_SINGLETON_MANAGER.provideSingleton(forType, value);
    return ROOT_SINGLETON_MANAGER.provideSingletonInPluginScope(forType, value);
  }

  public static <I, T extends I> Result<T, SingletonError> provideSingleton(
      @NotNull Class<? extends I> forType,
      @NotNull T value,
      @NotNull String singletonIdentifier,
      @NotNull SingletonScope scope) {
    if (scope == SingletonScope.ROOT)
      return ROOT_SINGLETON_MANAGER.provideSingleton(forType, value, singletonIdentifier);
    return ROOT_SINGLETON_MANAGER.provideSingletonInPluginScope(
        forType, value, singletonIdentifier);
  }

  public static @NotNull Result<String, SingletonError> initializeSingletons() {
    return ROOT_SINGLETON_MANAGER.initializeSingletons();
  }

  public static void registerScope(@NotNull String basePackagePath) {
    ROOT_SINGLETON_MANAGER.registerScope(basePackagePath);
  }

  public static <T> @NotNull Result<T, SingletonError> getSingleton(
      @NotNull Class<T> clazz, @NotNull SingletonScope firstCheck) {
    if (firstCheck == SingletonScope.ROOT) return ROOT_SINGLETON_MANAGER.getSingleton(clazz);

    return ROOT_SINGLETON_MANAGER
        .getSingletonInPluginScope(clazz)
        .recoverFlat(_ -> ROOT_SINGLETON_MANAGER.getSingleton(clazz));
  }

  public static <T> @NotNull Result<T, SingletonError> getSingleton(
      @NotNull Class<T> clazz, @NotNull SingletonScope firstCheck, @NotNull Class<?> scopeClass) {
    if (firstCheck == SingletonScope.ROOT) return ROOT_SINGLETON_MANAGER.getSingleton(clazz);

    return ROOT_SINGLETON_MANAGER
        .getSingletonInPluginScope(clazz, scopeClass)
        .recoverFlat(_ -> ROOT_SINGLETON_MANAGER.getSingleton(clazz));
  }

  public static <T> @NotNull Result<T, SingletonError> getSingleton(
      @NotNull Class<T> clazz, @NotNull String identifier, @NotNull SingletonScope firstCheck) {
    if (firstCheck == SingletonScope.ROOT)
      return ROOT_SINGLETON_MANAGER.getSingleton(clazz, identifier);

    return ROOT_SINGLETON_MANAGER
        .getSingletonInPluginScope(clazz, identifier)
        .recoverFlat(_ -> ROOT_SINGLETON_MANAGER.getSingleton(clazz, identifier));
  }

  public static <T> @NotNull Result<T, SingletonError> getSingleton(
      @NotNull Class<T> clazz,
      @NotNull String identifier,
      @NotNull SingletonScope firstCheck,
      @NotNull Class<?> scopeClass) {
    if (firstCheck == SingletonScope.ROOT)
      return ROOT_SINGLETON_MANAGER.getSingleton(clazz, identifier);

    return ROOT_SINGLETON_MANAGER
        .getSingletonInPluginScope(clazz, identifier, scopeClass)
        .recoverFlat(_ -> ROOT_SINGLETON_MANAGER.getSingleton(clazz, identifier));
  }

  public static <T> @NotNull Result<T, SingletonError> getExactScopedSingleton(
      @NotNull Class<T> clazz, @NotNull SingletonScope firstCheck) {
    if (firstCheck == SingletonScope.ROOT) return ROOT_SINGLETON_MANAGER.getSingleton(clazz);

    return ROOT_SINGLETON_MANAGER.getSingletonInPluginScope(clazz);
  }

  public static <T> @NotNull Result<T, SingletonError> getExactScopedSingleton(
      @NotNull Class<T> clazz, @NotNull String identifier, @NotNull SingletonScope firstCheck) {
    if (firstCheck == SingletonScope.ROOT)
      return ROOT_SINGLETON_MANAGER.getSingleton(clazz, identifier);

    return ROOT_SINGLETON_MANAGER.getSingletonInPluginScope(clazz, identifier);
  }

  public static <T> @NotNull Result<Set<T>, SingletonError> getSingletonsForScope(
      @NotNull Class<T> clazz, @NotNull SingletonScope scope) {
    if (scope == SingletonScope.ROOT)
      return Result.success(ROOT_SINGLETON_MANAGER.getSingletons(clazz));
    return ROOT_SINGLETON_MANAGER.getSingletonsInPluginScope(clazz);
  }

  public static void destructSingleton(
      @NotNull Singleton singleton, @NotNull SingletonScope scope) {
    if (scope == SingletonScope.ROOT) ROOT_SINGLETON_MANAGER.destructSingleton(singleton);
    else ROOT_SINGLETON_MANAGER.destructSingletonInPluginScope(singleton).orElseThrow();
  }

  public static @NotNull Result<String, SingletonError> destructSingletons() {
    return ROOT_SINGLETON_MANAGER.destructSingletons();
  }

  public static void debugSingletons(@NotNull Consumer<String> messageSender) {
    ROOT_SINGLETON_MANAGER.debugSingletons(messageSender);
  }
}
