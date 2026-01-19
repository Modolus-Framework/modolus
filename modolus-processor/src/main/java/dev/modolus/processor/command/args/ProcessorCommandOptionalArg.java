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

package dev.modolus.processor.command.args;

import com.palantir.javapoet.*;
import java.util.Optional;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import lombok.Builder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Builder
public record ProcessorCommandOptionalArg(
    String name, String description, TypeMirror type, String argTypeName)
    implements ProcessorCommandArg {

  @Contract("_ -> new")
  @Override
  public @NotNull FieldSpec toFieldSpec(ProcessingEnvironment processingEnvironment) {
    return FieldSpec.builder(getBaseType("OptionalArg", type()), name())
        .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
        .initializer(
            "withOptionalArg($S, $S, $T.$L)", name(), description(), getArgType(), argTypeName())
        .build();
  }

  @Override
  public @NotNull ParameterSpec toParameterSpec(ProcessingEnvironment processingEnvironment) {
    var optionalType =
        ParameterizedTypeName.get(ClassName.get(Optional.class), TypeName.get(type()));
    return ParameterSpec.builder(optionalType, name())
        .addModifiers(Modifier.FINAL)
        .addAnnotation(NotNull.class)
        .build();
  }

  @Contract(" -> new")
  @Override
  public @NotNull CodeBlock toStatement() {
    return CodeBlock.of(
        "$N.provided(commandContext) ? Optional.of($N.get(commandContext)) : Optional.empty()",
        name(),
        name());
  }
}
