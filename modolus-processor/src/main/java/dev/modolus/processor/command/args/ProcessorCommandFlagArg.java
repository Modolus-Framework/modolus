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
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import lombok.Builder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Builder
public record ProcessorCommandFlagArg(String name, String description)
    implements ProcessorCommandArg {

  @Contract("_ -> new")
  @Override
  public @NotNull FieldSpec toFieldSpec(ProcessingEnvironment processingEnvironment) {
    var type = ClassName.get(getSystemPath(), "DefaultArg");

    return FieldSpec.builder(type, name())
        .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
        .initializer("withFlagArg($S, $S)", name(), description())
        .build();
  }

  @Contract("_ -> new")
  @Override
  public @NotNull ParameterSpec toParameterSpec(ProcessingEnvironment processingEnvironment) {
    return ParameterSpec.builder(TypeName.BOOLEAN, name()).addModifiers(Modifier.FINAL).build();
  }

  @Contract(" -> new")
  @Override
  public @NotNull CodeBlock toStatement() {
    return CodeBlock.of("commandContext.hasFlag($S)", name());
  }
}
