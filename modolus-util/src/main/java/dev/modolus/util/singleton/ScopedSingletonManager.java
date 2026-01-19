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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.CaseUtils;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public sealed class ScopedSingletonManager permits RootSingletonManager {

  private final Map<Class<?>, Map<String, Singleton>> singletonHolder = new ConcurrentHashMap<>();

  private final String scopeName;
  private final AtomicBoolean initialized = new AtomicBoolean(false);

  public <T extends Singleton> Result<T, SingletonError> provideSingleton(@NotNull T value) {
    return provideSingleton(value, getDefaultSingletonNameFor(value.getClass()));
  }

  public <T extends Singleton> Result<T, SingletonError> provideSingleton(
      @NotNull T value, @NotNull String singletonIdentifier) {
    var map = singletonHolder.computeIfAbsent(value.getClass(), _ -> new HashMap<>());
    if (map.containsKey(singletonIdentifier))
      return Result.failure(SingletonError.SINGLETON_ALREADY_PROVIDED);
    map.put(singletonIdentifier, value);
    return Result.success(value);
  }

  public <I, T extends I> Result<T, SingletonError> provideSingleton(
      Class<? extends I> forType, T value) {
    return provideSingleton(forType, value, getDefaultSingletonNameFor(forType));
  }

  public <I, T extends I> Result<T, SingletonError> provideSingleton(
      Class<? extends I> forType, T value, String singletonIdentifier) {
    if (!(value instanceof Singleton singleton)) {
      return Result.failure(SingletonError.VALUE_DOES_NOT_IMPLEMENT_SINGLETON_INTERFACE);
    }

    var map = singletonHolder.computeIfAbsent(forType, _ -> new HashMap<>());
    if (map.containsKey(singletonIdentifier))
      return Result.failure(SingletonError.SINGLETON_ALREADY_PROVIDED);
    map.put(singletonIdentifier, singleton);
    return Result.success(value);
  }

  public Result<String, SingletonError> initializeSingletons() {
    if (initialized.get()) return Result.failure(SingletonError.SCOPE_ALREADY_INITIALIZED);

    singletonHolder.values().stream()
        .map(Map::values)
        .flatMap(Collection::stream)
        .forEach(Singleton::onInitialization);
    initialized.set(true);
    return Result.success(scopeName);
  }

  public <T> Result<T, SingletonError> getSingleton(Class<T> clazz) {
    return getSingleton(clazz, getDefaultSingletonNameFor(clazz));
  }

  public <T> Result<T, SingletonError> getSingleton(Class<T> clazz, String identifier) {
    if (!singletonHolder.containsKey(clazz))
      return Result.failure(SingletonError.NO_INSTANCE_AVAILABLE);
    var map = singletonHolder.get(clazz);
    if (!map.containsKey(identifier)) return Result.failure(SingletonError.NO_INSTANCE_AVAILABLE);
    var value = map.get(identifier);
    if (!clazz.isInstance(value))
      return Result.failure(SingletonError.INSTANCE_IS_NOT_THE_REQUESTED_TYPE);
    return Result.success(clazz.cast(value));
  }

  public <T> Set<T> getSingletons(Class<T> clazz) {
    if (!singletonHolder.containsKey(clazz)) return Set.of();
    return singletonHolder.get(clazz).values().stream()
        .filter(clazz::isInstance)
        .map(clazz::cast)
        .collect(Collectors.toSet());
  }

  public void destructSingleton(@NotNull Singleton singleton) {
    singleton.onDestruction();
    singletonHolder.values().stream()
        .filter(map -> map.containsValue(singleton))
        .forEach(map -> map.entrySet().removeIf(entry -> entry.getValue().equals(singleton)));
  }

  public String getDefaultSingletonNameFor(@NotNull Class<?> clazz) {
    return CaseUtils.toCamelCase(clazz.getSimpleName(), false);
  }

  public Result<String, SingletonError> destructSingletons() {
    singletonHolder.values().stream()
        .map(Map::values)
        .flatMap(Collection::stream)
        .forEach(Singleton::onDestruction);
    singletonHolder.clear();
    return Result.success(scopeName);
  }

  public void debugSingletons(@NotNull Consumer<String> messageSender) {
    singletonHolder.forEach(
        (clazz, map) -> {
          messageSender.accept("  Provided for (" + shortClassName(clazz) + "):");
          map.forEach(
              (identifier, singleton) ->
                  messageSender.accept(
                      "    " + identifier + ": " + shortClassName(singleton.getClass())));
        });
  }

  private @NotNull String shortClassName(@NotNull Class<?> clazz) {
    String packageName =
        Stream.of(clazz.getPackageName().split("\\."))
            .map(part -> String.valueOf(part.charAt(0)))
            .collect(Collectors.joining("."));

    return packageName + "." + clazz.getSimpleName();
  }
}
