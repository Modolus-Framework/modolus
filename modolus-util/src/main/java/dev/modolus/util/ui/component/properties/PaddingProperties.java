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
import lombok.Builder;
import org.jetbrains.annotations.NotNull;

@Builder
public record PaddingProperties(
    Integer full,
    Integer horizonal,
    Integer vertical,
    Integer top,
    Integer bottom,
    Integer left,
    Integer right)
    implements ComponentProperty {

  @Override
  public @NotNull ComponentPropertyType getPropertyType() {
    return ComponentPropertyType.PADDING;
  }

  @Override
  public @NotNull String serializeProperties() {
    List<String> props = new ArrayList<>();

    if (full != null) {
      props.add(String.format("Full: %d", full));
    }

    if (horizonal != null) {
      props.add(String.format("Horizonal: %d", horizonal));
    }

    if (vertical != null) {
      props.add(String.format("Vertical: %d", vertical));
    }

    if (top != null) {
      props.add(String.format("Top: %d", top));
    }

    if (bottom != null) {
      props.add(String.format("Bottom: %d", bottom));
    }

    if (left != null) {
      props.add(String.format("Left: %d", left));
    }

    if (right != null) {
      props.add(String.format("Right: %d", right));
    }

    return String.format("(%s)", String.join(", ", props));
  }
}
