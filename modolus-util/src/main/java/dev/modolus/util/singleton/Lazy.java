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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public sealed class Lazy<T> permits ExactScopedLazy {

  protected final @NotNull Class<T> clazz;
  protected final @NotNull SingletonScope scope;
  protected @Nullable String singletonIdentifier = null;

  public @NotNull Result<T, SingletonError> get() {
    return singletonIdentifier == null
        ? Singletons.getSingleton(clazz, scope)
        : Singletons.getSingleton(clazz, singletonIdentifier, scope);
  }

  public T getOrThrow() {
    return this.get().orElseThrow();
  }

  @Contract("_ -> new")
  public static <T> @NotNull Lazy<T> ofPlugin(@NotNull Class<T> clazz) {
    return new Lazy<>(clazz, SingletonScope.PLUGIN);
  }

  @Contract("_, _ -> new")
  public static <T> @NotNull Lazy<T> ofPlugin(
      @NotNull Class<T> clazz, @NotNull String singletonIdentifier) {
    return new Lazy<>(clazz, SingletonScope.PLUGIN, singletonIdentifier);
  }

  @Contract("_ -> new")
  public static <T> @NotNull Lazy<T> ofRoot(@NotNull Class<T> clazz) {
    return new Lazy<>(clazz, SingletonScope.ROOT);
  }

  @Contract("_, _ -> new")
  public static <T> @NotNull Lazy<T> ofRoot(
      @NotNull Class<T> clazz, @NotNull String singletonIdentifier) {
    return new Lazy<>(clazz, SingletonScope.ROOT, singletonIdentifier);
  }

  @Contract("_, _ -> new")
  public static <T> @NotNull Lazy<T> of(@NotNull Class<T> clazz, @NotNull SingletonScope scope) {
    return new Lazy<>(clazz, scope);
  }

  @Contract("_, _, _ -> new")
  public static <T> @NotNull Lazy<T> of(
      @NotNull Class<T> clazz, @NotNull SingletonScope scope, @NotNull String singletonIdentifier) {
    return new Lazy<>(clazz, scope, singletonIdentifier);
  }
}
