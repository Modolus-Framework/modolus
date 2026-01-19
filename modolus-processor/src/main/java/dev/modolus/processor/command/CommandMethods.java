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

package dev.modolus.processor.command;

import com.palantir.javapoet.AnnotationSpec;
import com.palantir.javapoet.MethodSpec;
import com.palantir.javapoet.ParameterizedTypeName;
import com.palantir.javapoet.TypeName;
import dev.modolus.annotations.command.Command;
import java.util.concurrent.CompletableFuture;
import javax.lang.model.element.Modifier;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public final class CommandMethods {

  public static MethodSpec.@NotNull Builder getOverrideMethod(@NotNull Command command) {
    return switch (command) {
      case Command c when c.target() == Command.CommandTarget.PLAYER && c.async() ->
          getAsyncCommandMethod();
      case Command c when c.target() == Command.CommandTarget.PLAYER -> getSyncCommandMethod();
      case Command c when c.target() == Command.CommandTarget.WORLD && c.async() ->
          getAsyncCommandMethod();
      case Command c when c.target() == Command.CommandTarget.WORLD -> getSyncCommandMethod();
      case Command c when c.async() -> getAsyncCommandMethod();
      default ->
          getSyncCommandMethod()
              .returns(
                  ParameterizedTypeName.get(CompletableFuture.class, Void.class)
                      .annotated(AnnotationSpec.builder(Nullable.class).build()));
    };
  }

  public static @NotNull TypeName getOverrideMethodReturnType(@NotNull Command command) {
    return switch (command) {
      case Command c when c.target() == Command.CommandTarget.PLAYER && c.async() ->
          ParameterizedTypeName.get(CompletableFuture.class, Void.class)
              .annotated(AnnotationSpec.builder(NotNull.class).build());
      case Command c when c.target() == Command.CommandTarget.PLAYER -> TypeName.VOID;
      case Command c when c.target() == Command.CommandTarget.WORLD && c.async() ->
          ParameterizedTypeName.get(CompletableFuture.class, Void.class)
              .annotated(AnnotationSpec.builder(NotNull.class).build());
      case Command c when c.target() == Command.CommandTarget.WORLD -> TypeName.VOID;
      case Command c when c.async() ->
          ParameterizedTypeName.get(CompletableFuture.class, Void.class)
              .annotated(AnnotationSpec.builder(NotNull.class).build());
      default ->
          ParameterizedTypeName.get(CompletableFuture.class, Void.class)
              .annotated(AnnotationSpec.builder(Nullable.class).build());
    };
  }

  private MethodSpec.@NotNull Builder getAsyncCommandMethod() {
    return MethodSpec.methodBuilder("executeAsync").addModifiers(Modifier.PROTECTED);
  }

  private MethodSpec.@NotNull Builder getSyncCommandMethod() {
    return MethodSpec.methodBuilder("execute").addModifiers(Modifier.PROTECTED);
  }
}
