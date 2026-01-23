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

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record PatchStyle(
    @NotNull String texturePath,
    @Nullable Integer border,
    @Nullable Integer horizontalBorder,
    @Nullable Integer verticalBorder)
    implements ComponentProperty {
  @Override
  public @NotNull ComponentPropertyType getPropertyType() {
    return ComponentPropertyType.BACKGROUND;
  }

  @Override
  public @NotNull String serializeProperties() {
    List<String> props = new ArrayList<>();

    props.add(String.format("TexturePath: \"%s\"", texturePath));

    if (border != null) {
      props.add(String.format("Border: %d", border));
    }

    if (horizontalBorder != null) {
      props.add(String.format("HorizontalBorder: %d", horizontalBorder));
    }

    if (verticalBorder != null) {
      props.add(String.format("VerticalBorder: %d", verticalBorder));
    }

    return String.format("(%s)", String.join(", ", props));
  }

  @Contract("_ -> new")
  public static @NotNull PatchStyle of(String texturePath) {
    return new PatchStyle(texturePath, null, null, null);
  }

  @Contract("_, _ -> new")
  public static @NotNull PatchStyle ofAllSideBorder(@NotNull String texturePath, int border) {
    return new PatchStyle(texturePath, border, null, null);
  }

  @Contract("_, _ -> new")
  public static @NotNull PatchStyle ofHorizontalBorder(@NotNull String texturePath, int border) {
    return new PatchStyle(texturePath, null, border, null);
  }

  @Contract("_, _ -> new")
  public static @NotNull PatchStyle ofVerticalBorder(@NotNull String texturePath, int border) {
    return new PatchStyle(texturePath, null, null, border);
  }

  @Contract("_, _, _ -> new")
  public static @NotNull PatchStyle ofBorder(
      @NotNull String texturePath, int verticalBorder, int horizontalBorder) {
    return new PatchStyle(texturePath, null, verticalBorder, horizontalBorder);
  }
}
