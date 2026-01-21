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

import dev.modolus.util.result.Error;
import dev.modolus.util.result.ErrorType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@RequiredArgsConstructor
public enum SingletonError implements ErrorType<SingletonError> {
  NO_INSTANCE_AVAILABLE("No instance of %s is available in scope %s."),
  SINGLETON_ALREADY_PROVIDED(
      "A singleton of type %s is already provided for the type %s in scope %s."),
  INSTANCE_IS_NOT_THE_REQUESTED_TYPE("The requested singleton is not of the requested type."),
  VALUE_DOES_NOT_IMPLEMENT_SINGLETON_INTERFACE(
      "The provided value of type %s does not implement Singleton."),
  FAILED_TO_GET_CALLERS_PACKAGE("Failed to get the caller's package."),
  FAILED_TO_GET_CALLERS_SCOPE("Failed to get the caller's scope from package %s."),
  SCOPE_ALREADY_INITIALIZED("The scope %s is already initialized.");

  private final String errorMessage;

  @Override
  public @NotNull Error<SingletonError> toError(Object... args) {
    return new Error<>(this, args, null);
  }

  @Override
  public @NotNull Error<SingletonError> toErrorWithCause(@Nullable Error<?> cause, Object... args) {
    return new Error<>(this, args, cause);
  }
}
