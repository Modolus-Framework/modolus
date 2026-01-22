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

package dev.modolus.util.ui;

import dev.modolus.util.ui.component.AbstractComponent;
import dev.modolus.util.ui.component.properties.ComponentProperty;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public class UIRoot<T extends AbstractComponent<T>> extends AbstractComponent<UIRoot<T>> {

  private String cached;

  public UIRoot(@NotNull T rootElement) {
    super(null, null, Set.of());
    child(rootElement);
    cached = serialize(0);
  }

  @Override
  public final <O extends AbstractComponent<O>> UIRoot<T> child(
      @NotNull AbstractComponent<O> child) {
    if (!children.isEmpty()) throw new IllegalStateException("Root can only contain one child!");
    return super.child(child);
  }

  public final String getInlineUI() {
    return cached;
  }

  public final void updateInlineUICache() {
    cached = serialize(0);
  }

  @Override
  public final @NotNull String serialize(int identLevel) {
    StringBuilder builder = new StringBuilder();

    this.getAllDependencies().stream()
        .map(dep -> String.format("$%s = \"%s\";", dep.identifierName(), dep.path()))
        .forEach(line -> builder.append(line).append(System.lineSeparator()));

    this.constants.forEach(
        (name, value) -> {
          builder.append("@").append(name).append(" = ");
          if (value instanceof ComponentProperty property) {
            builder.append(property.serializeProperties());
          } else {
            builder.append(value.toString());
          }

          builder.append(";").append(System.lineSeparator());
        });

    this.children.stream()
        .map(child -> child.serialize(identLevel))
        .forEach(childBlock -> builder.append(childBlock).append(System.lineSeparator()));

    return builder.toString();
  }

  @Override
  protected UIRoot<T> getInstance() {
    return this;
  }
}
