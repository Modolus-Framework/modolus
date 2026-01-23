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

package dev.modolus.core.config;

import dev.modolus.util.result.Error;
import dev.modolus.util.result.ErrorType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@RequiredArgsConstructor
public enum ConfigError implements ErrorType<ConfigError> {
  PREVIOUS_ERROR("A config error occurred while processing the previous error."),
  CONFIG_CLASS_MISSING_CONFIG_ANNOTATION("Configuration class is missing @Config annotation"),
  FAILED_LOADING_CONFIGURATION("Failed to load configuration for path: %s"),
  FAILED_SAVING_CONFIGURATION("Failed to save configuration for path: %s");

  private final String errorMessage;

  @Override
  public @NotNull Error<ConfigError> toError(Object... args) {
    return new Error<>(this, args, null);
  }

  @Override
  public @NotNull Error<ConfigError> toErrorWithCause(@Nullable Error<?> cause, Object... args) {
    return new Error<>(this, args, cause);
  }
}
