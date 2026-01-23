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

import dev.modolus.util.ui.Color;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Builder
public record LabelStyle(
    @Nullable String fontSize,
    @Nullable Color fontColor,
    @Nullable Boolean renderBold,
    @Nullable Boolean renderItalic,
    @Nullable Boolean renderUppercase,
    @Nullable HorizontalAlignment horizontalAlignment,
    @Nullable VerticalAlignment verticalAlignment,
    @Nullable String fontName,
    @Nullable Integer letterSpacing,
    @Nullable Float lineSpacing,
    @Nullable Boolean wrap,
    @Nullable Overflow overflow)
    implements ComponentProperty, ComponentStyle {

  @Override
  public @NotNull ComponentPropertyType getPropertyType() {
    return ComponentPropertyType.STYLE;
  }

  @Override
  public @NotNull String serializeProperties() {
    List<String> props = new ArrayList<>();

    if (fontSize != null) {
      props.add(String.format("FontSize: %s", fontSize));
    }

    if (fontColor != null) {
      props.add(String.format("FontColor: %s", fontColor.serialize()));
    }

    if (renderBold != null) {
      props.add(String.format("RenderBold: %b", renderBold));
    }

    if (renderItalic != null) {
      props.add(String.format("RenderItalic: %b", renderItalic));
    }

    if (renderUppercase != null) {
      props.add(String.format("RenderUppercase: %b", renderUppercase));
    }

    if (horizontalAlignment != null) {
      props.add(String.format("HorizontalAlignment: %s", horizontalAlignment.name()));
    }

    if (verticalAlignment != null) {
      props.add(String.format("VerticalAlignment: %s", verticalAlignment.name()));
    }

    if (fontName != null) {
      props.add(String.format("FontName: %s", fontName));
    }

    if (letterSpacing != null) {
      props.add(String.format("LetterSpacing: %d", letterSpacing));
    }

    if (lineSpacing != null) {
      props.add(String.format("LineSpacing: %f", lineSpacing));
    }

    if (wrap != null) {
      props.add(String.format("Wrap: %b", wrap));
    }

    if (overflow != null) {
      props.add(String.format("Overflow: %s", overflow.name()));
    }

    return String.format("(%s)", String.join(", ", props));
  }

  @Override
  public @NotNull String getStyleName() {
    return "LabelStyle";
  }

  @Getter
  @RequiredArgsConstructor
  enum HorizontalAlignment {
    START("Start"),
    CENTER("Center"),
    END("End");

    private final String name;
  }

  @Getter
  @RequiredArgsConstructor
  enum VerticalAlignment {
    TOP("Top"),
    CENTER("Center"),
    BOTTOM("Bottom");

    private final String name;
  }

  @Getter
  @RequiredArgsConstructor
  enum Overflow {
    ELLIPSIS("Ellipsis"),
    CLIP("Clip"),
    VISIBLE("Visible");

    private final String name;
  }
}
