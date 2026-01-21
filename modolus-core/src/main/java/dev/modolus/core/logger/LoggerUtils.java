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

package dev.modolus.core.logger;

import dev.modolus.util.result.Error;
import dev.modolus.util.singleton.Lazy;
import java.util.logging.Level;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class LoggerUtils {

  public static void printInfo(@NotNull Lazy<Logger> logger, @NotNull String message) {
    print(logger, Level.INFO, message);
  }

  public static void printWarn(@NotNull Lazy<Logger> logger, @NotNull String message) {
    print(logger, Level.WARNING, message);
  }

  public static void printError(@NotNull Lazy<Logger> logger, @NotNull String message) {
    print(logger, Level.SEVERE, message);
  }

  public static void printError(@NotNull Lazy<Logger> logger, @NotNull Error<?> error) {
    printError(logger, error.getFullMessage());
  }

  private static void print(
      @NotNull Lazy<Logger> logger, @NotNull Level level, @NotNull String message) {
    logger.get().onSuccess(l -> l.at(level).log(message));
    logger.get().onFailure(_ -> java.util.logging.Logger.getGlobal().log(level, message));
  }
}
