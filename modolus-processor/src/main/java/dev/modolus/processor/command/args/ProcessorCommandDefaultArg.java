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

import com.palantir.javapoet.CodeBlock;
import com.palantir.javapoet.FieldSpec;
import com.palantir.javapoet.ParameterSpec;
import com.palantir.javapoet.TypeName;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import lombok.Builder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Builder
public record ProcessorCommandDefaultArg(
    String name,
    String description,
    TypeMirror type,
    String argTypeName,
    String defaultValue,
    String defaultValueDescription)
    implements ProcessorCommandArg {

  @Contract("_ -> new")
  @Override
  public @NotNull FieldSpec toFieldSpec(ProcessingEnvironment processingEnvironment) {
    return FieldSpec.builder(getBaseType("DefaultArg", type()), name())
        .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
        .initializer(
            "withDefaultArg($S, $S, $T.$L, $L, $S)",
            name(),
            description(),
            getArgType(),
            argTypeName(),
            defaultValue(),
            defaultValueDescription())
        .build();
  }

  @Contract("_ -> new")
  @Override
  public @NotNull ParameterSpec toParameterSpec(ProcessingEnvironment processingEnvironment) {
    return ParameterSpec.builder(TypeName.get(type()), name())
        .addModifiers(Modifier.FINAL)
        .addAnnotation(NotNull.class)
        .build();
  }

  @Contract(" -> new")
  @Override
  public @NotNull CodeBlock toStatement() {
    return CodeBlock.of(
        "$N.provided(commandContext) ? $N.get(commandContext) : $L",
        name(),
        name(),
        defaultValue());
  }
}
