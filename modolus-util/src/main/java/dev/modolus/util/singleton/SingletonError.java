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

public enum SingletonError {
  NO_INSTANCE_AVAILABLE,
  SINGLETON_ALREADY_PROVIDED,
  INSTANCE_IS_NOT_THE_REQUESTED_TYPE,
  VALUE_DOES_NOT_IMPLEMENT_SINGLETON_INTERFACE,
  FAILED_TO_GET_CALLERS_SCOPE,
  SCOPE_ALREADY_INITIALIZED
}
