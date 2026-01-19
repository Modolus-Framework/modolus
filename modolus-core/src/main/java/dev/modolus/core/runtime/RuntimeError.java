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

package dev.modolus.core.runtime;

public enum RuntimeError {
  NO_AVAILABLE_CLASS_LOADER,
  FAILED_TO_LOAD_RESOURCES,
  FAILED_TO_LOAD_SCOPES,
  FAILED_TO_LOAD_CLASS,
  FAILED_TO_READ_CLASSES,
  FAILED_TO_CREATE_CLASS,
  FAILED_TO_INITIALIZE_CURRENT_SCOPE
}
