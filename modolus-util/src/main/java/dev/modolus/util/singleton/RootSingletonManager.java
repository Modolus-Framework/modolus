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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

public final class RootSingletonManager extends ScopedSingletonManager {

  static final List<String> ROOT_PACKAGE_NAMES =
      List.of(
          "dev.modolus.util", "dev.modolus.core", "dev.modolus.annotations", "java", "com.hypixel");

  private final Map<String, ScopedSingletonManager> scopes = new ConcurrentHashMap<>();

  RootSingletonManager() {
    super("ROOT");
  }

  public <T extends Singleton> @NotNull Result<T, SingletonError> provideSingletonInPluginScope(
      @NotNull T value) {
    return getCallersScopeManager().flatMap(manager -> manager.provideSingleton(value));
  }

  public <T extends Singleton> @NotNull Result<T, SingletonError> provideSingletonInPluginScope(
      @NotNull T value, @NotNull String singletonIdentifier) {
    return getCallersScopeManager()
        .flatMap(manager -> manager.provideSingleton(value, singletonIdentifier));
  }

  public <I, T extends I> @NotNull Result<T, SingletonError> provideSingletonInPluginScope(
      Class<? extends I> forType, T value) {
    return getCallersScopeManager().flatMap(manager -> manager.provideSingleton(forType, value));
  }

  public <I, T extends I> @NotNull Result<T, SingletonError> provideSingletonInPluginScope(
      Class<? extends I> forType, T value, String singletonIdentifier) {
    return getCallersScopeManager()
        .flatMap(manager -> manager.provideSingleton(forType, value, singletonIdentifier));
  }

  public <T> @NotNull Result<T, SingletonError> getSingletonInPluginScope(Class<T> clazz) {
    return getCallersScopeManager().flatMap(manager -> manager.getSingleton(clazz));
  }

  public <T> @NotNull Result<T, SingletonError> getSingletonInPluginScope(
      Class<T> clazz, String identifier) {
    return getCallersScopeManager().flatMap(manager -> manager.getSingleton(clazz, identifier));
  }

  public @NotNull <T> Result<Set<T>, SingletonError> getSingletonsInPluginScope(
      @NotNull Class<T> clazz) {
    return getCallersScopeManager().map(manager -> manager.getSingletons(clazz));
  }

  public @NotNull Result<Void, SingletonError> destructSingletonInPluginScope(
      @NotNull Singleton singleton) {
    return getCallersScopeManager().mapVoid(manager -> manager.destructSingleton(singleton));
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

    this.scopes.forEach(
        (packageName, manager) -> {
          messageSender.accept("Scope (" + packageName + "):");
          manager.debugSingletons(messageSender);
        });
  }

  private @NotNull Result<ScopedSingletonManager, SingletonError> getCallersScopeManager() {
    return getCallersPackageName().flatMap(this::findScopeNameByPackageName);
  }

  private @NotNull Result<String, SingletonError> getCallersPackageName() {
    StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
    return walker
        .walk(
            stream ->
                Result.ofOptional(
                    stream
                        .map(frame -> frame.getDeclaringClass().getPackageName())
                        .filter(name -> ROOT_PACKAGE_NAMES.stream().noneMatch(name::startsWith))
                        .findFirst()))
        .mapError(_ -> SingletonError.FAILED_TO_GET_CALLERS_PACKAGE.toError());
  }

  private @NotNull Result<ScopedSingletonManager, SingletonError> findScopeNameByPackageName(
      String packageName) {
    return Result.ofOptional(
            scopes.entrySet().stream()
                .filter(entry -> packageName.startsWith(entry.getKey()))
                .findFirst()
                .map(Map.Entry::getValue))
        .mapError(_ -> SingletonError.FAILED_TO_GET_CALLERS_SCOPE.toError(packageName));
  }
}
