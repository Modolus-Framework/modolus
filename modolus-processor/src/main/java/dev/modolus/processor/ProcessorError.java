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

package dev.modolus.processor;

import dev.modolus.util.result.Error;
import dev.modolus.util.result.ErrorType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@RequiredArgsConstructor
public enum ProcessorError implements ErrorType<ProcessorError> {
  NO_COMMAND_ARG_TYPE_SET("No command argument type set for command %s"),
  ELEMENT_ERROR("An error occurred while processing the previous error."),
  ;

  private final String errorMessage;

  @Override
  public @NotNull Error<ProcessorError> toError(Object... args) {
    return new Error<>(this, args, null);
  }

  @Override
  public @NotNull Error<ProcessorError> toErrorWithCause(@Nullable Error<?> cause, Object... args) {
    return new Error<>(this, args, cause);
  }
}
