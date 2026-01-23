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

package dev.modolus.util.ui.component.properties;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record NamedProperty(@NotNull String name, @NotNull ComponentPropertyType type)
    implements ComponentProperty {
  @Override
  public @NotNull ComponentPropertyType getPropertyType() {
    return type;
  }

  @Override
  public @NotNull String serializeProperties() {
    return String.format("@%s", name);
  }

  @Contract("_,_ -> new")
  public static @NotNull NamedProperty of(
      @NotNull String name, @NotNull ComponentPropertyType type) {
    return new NamedProperty(name, type);
  }
}
