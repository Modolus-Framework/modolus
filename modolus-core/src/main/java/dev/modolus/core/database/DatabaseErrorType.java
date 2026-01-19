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

package dev.modolus.core.database;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
public enum DatabaseErrorType {
  DATABASE_NOT_INITIALIZED("Database is not initialized"),
  GENERIC_SQL_EXCEPTION("SQL Exception: %s"),
  FAILED_TO_CONNECT_TO_DATABASE("Failed to connect to database with error: %s"),
  FAILED_TO_RECEIVE_CONNECTION("Failed to receive connection with error: %s");

  private final String message;

  @Contract("_ -> new")
  public @NotNull DatabaseError toError(Object... args) {
    return new DatabaseError(this, List.of(args));
  }
}
