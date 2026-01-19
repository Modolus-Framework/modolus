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
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class LazySet<T> {

  private final Class<T> clazz;
  private final SingletonScope scope;

  public @NotNull Result<Set<T>, SingletonError> get() {
    return Singletons.getSingletonsForScope(clazz, scope);
  }

  public Set<T> getOrThrow() {
    return this.get().orElseThrow();
  }

  @Contract("_ -> new")
  public static <T> @NotNull LazySet<T> ofPlugin(@NotNull Class<T> clazz) {
    return of(clazz, SingletonScope.PLUGIN);
  }

  @Contract("_ -> new")
  public static <T> @NotNull LazySet<T> ofRoot(@NotNull Class<T> clazz) {
    return of(clazz, SingletonScope.ROOT);
  }

  @Contract("_, _ -> new")
  public static <T> @NotNull LazySet<T> of(@NotNull Class<T> clazz, @NotNull SingletonScope scope) {
    return new LazySet<>(clazz, scope);
  }
}
