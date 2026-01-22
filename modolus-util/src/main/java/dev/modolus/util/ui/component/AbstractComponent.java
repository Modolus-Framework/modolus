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

import dev.modolus.util.result.ExceptionConsumer;
import dev.modolus.util.result.GenericError;
import dev.modolus.util.result.Result;
import dev.modolus.util.ui.component.properties.ComponentProperty;
import dev.modolus.util.ui.component.properties.ComponentPropertyType;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public abstract class AbstractComponent<T extends AbstractComponent<T>> {

  protected final String typeName;
  protected final @Nullable String id;
  protected final Set<ComponentPropertyType> allowedPropertyTypes;
  protected final Map<ComponentPropertyType, ComponentProperty> properties =
      new EnumMap<>(ComponentPropertyType.class);
  protected final List<AbstractComponent<?>> children = new ArrayList<>();
  protected final Map<String, Object> constants = new HashMap<>();

  public Result<T, GenericError> withProperties(
      ExceptionConsumer<T, IllegalStateException> consumer) {
    return Result.success(getInstance())
        .mapExceptionVoid(consumer, IllegalStateException.class)
        .map(unused -> getInstance());
  }

  public T property(@NotNull ComponentProperty property) {
    if (!allowedPropertyTypes.contains(property.getPropertyType())) {
      throw new IllegalStateException(
          String.format(
              "The property of type %s is not allowed on the component %s",
              property.getPropertyType(), this.getClass().getSimpleName()));
    }
    properties.put(property.getPropertyType(), property);
    return getInstance();
  }

  public T constant(@NotNull String name, @NotNull Object obj) {
    constants.put(name, obj);

    return getInstance();
  }

  public <O extends AbstractComponent<O>> T child(@NotNull AbstractComponent<O> child) {
    children.add(child);
    return getInstance();
  }

  public String serialize(int identLevel) {
    StringBuilder builder = new StringBuilder();
    builder.append("  ".repeat(identLevel)).append(typeName);

    if (id != null) {
      builder.append(" #").append(id);
    }

    builder.append(" {").append(System.lineSeparator());

    this.constants.forEach(
        (name, value) -> {
          builder.append("  ".repeat(identLevel + 1)).append("@").append(name).append(" = ");
          if (value instanceof ComponentProperty property) {
            builder.append(property.serializeProperties());
          } else {
            builder.append(value.toString());
          }

          builder.append(";").append(System.lineSeparator());
        });

    this.properties.values().stream()
        .map(
            property ->
                String.format(
                    "%s: %s;",
                    property.getPropertyType().getPropertyName(), property.serializeProperties()))
        .forEach(
            line ->
                builder
                    .append("  ".repeat(identLevel + 1))
                    .append(line)
                    .append(System.lineSeparator()));

    this.children.stream()
        .map(child -> child.serialize(identLevel + 1))
        .forEach(childBlock -> builder.append(childBlock).append(System.lineSeparator()));

    builder.append("  ".repeat(identLevel)).append("}").append(System.lineSeparator());

    return builder.toString();
  }

  protected abstract T getInstance();

  public @Nullable ComponentDependency getRequiredDependency() {
    return null;
  }

  public Set<ComponentDependency> getAllDependencies() {
    Set<ComponentDependency> dependencies = new HashSet<>();
    var dep = getRequiredDependency();
    if (dep != null) dependencies.add(dep);

    dependencies.addAll(
        children.stream()
            .map(AbstractComponent::getAllDependencies)
            .flatMap(Set::stream)
            .collect(Collectors.toSet()));
    return dependencies;
  }
}
