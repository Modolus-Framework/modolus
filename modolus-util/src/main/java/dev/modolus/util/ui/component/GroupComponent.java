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

package dev.modolus.util.ui.component;

import dev.modolus.util.ui.Color;
import dev.modolus.util.ui.component.properties.*;
import java.util.Set;
import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

public final class GroupComponent extends AbstractComponent<GroupComponent> {

  public GroupComponent() {
    this(null);
  }

  public GroupComponent(@Nullable String id) {
    super(
        "Group",
        id,
        Set.of(
            ComponentPropertyType.ANCHOR,
            ComponentPropertyType.BACKGROUND,
            ComponentPropertyType.LAYOUT_MODE,
            ComponentPropertyType.PADDING,
            ComponentPropertyType.FLEX_WEIGHT,
            ComponentPropertyType.VISIBLE,
            ComponentPropertyType.ENABLED,
            ComponentPropertyType.OPACITY));
  }

  public GroupComponent anchor(@NotNull AnchorProperties anchorProperties) {
    return property(anchorProperties);
  }

  public GroupComponent anchor(int width, int height) {
    return property(AnchorProperties.builder().width(width).height(height).build());
  }

  public GroupComponent anchor(int top, int bottom, int left, int right) {
    return property(
        AnchorProperties.builder().left(left).right(right).top(top).bottom(bottom).build());
  }

  public GroupComponent anchorWithHorizonal(int horizonal, int vertical) {
    return property(AnchorProperties.builder().horizonal(horizonal).vertical(vertical).build());
  }

  public GroupComponent anchorNamed(@NotNull String name) {
    return property(new NamedProperty(name, ComponentPropertyType.ANCHOR));
  }

  public GroupComponent background(@NotNull Color color) {
    return property(new BackgroundProperties(color));
  }

  public GroupComponent backgroundNamed(@NotNull String name) {
    return property(new NamedProperty(name, ComponentPropertyType.BACKGROUND));
  }

  public GroupComponent layoutMode(@NotNull LayoutModeProperties.Mode mode) {
    return property(new LayoutModeProperties(mode));
  }

  public GroupComponent layoutModeNamed(@NotNull String name) {
    return property(new NamedProperty(name, ComponentPropertyType.LAYOUT_MODE));
  }

  @Override
  protected GroupComponent getInstance() {
    return this;
  }
}
