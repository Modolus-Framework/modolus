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

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

public record LayoutModeProperties(@NotNull Mode mode) implements ComponentProperty {
  @Override
  public @NotNull ComponentPropertyType getPropertyType() {
    return ComponentPropertyType.LAYOUT_MODE;
  }

  @Override
  public @NotNull String serializeProperties() {
    return mode.getName();
  }

  @Getter
  @RequiredArgsConstructor
  public enum Mode {
    TOP("Top"),
    BOTTOM("Bottom"),
    LEFT("Left"),
    RIGHT("Right"),
    CENTER("Center"),
    OVERLAY("Overlay");

    private final String name;
  }
}
