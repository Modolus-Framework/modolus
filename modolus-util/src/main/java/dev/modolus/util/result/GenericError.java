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

package dev.modolus.util.result;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@RequiredArgsConstructor
public enum GenericError implements ErrorType<GenericError> {
  PREVIOUS_ERROR("A generic error occurred while processing the previous error."),
  EMPTY_COLLECTION("An empty collection was provided."),
  NULL_VALUE("A null value was provided."),
  OPTIONAL_EMPTY("An empty Optional was provided."),
  EXCEPTION_THROWN("An exception was thrown with the error: %s");

  private final String errorMessage;

  @Contract("_ -> new")
  @Override
  public @NotNull Error<GenericError> toError(Object... args) {
    return new Error<>(this, args, null);
  }

  @Override
  public @NotNull Error<GenericError> toErrorWithCause(@Nullable Error<?> cause, Object... args) {
    return new Error<>(this, args, cause);
  }
}
