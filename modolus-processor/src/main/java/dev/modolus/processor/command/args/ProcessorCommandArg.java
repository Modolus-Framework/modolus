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
import javax.lang.model.type.TypeMirror;

public interface ProcessorCommandArg {

  FieldSpec toFieldSpec(ProcessingEnvironment processingEnvironment);

  ParameterSpec toParameterSpec(ProcessingEnvironment processingEnvironment);

  CodeBlock toStatement();

  default String getSystemPath() {
    return "com.hypixel.hytale.server.core.command.system.arguments.system";
  }

  default String getTypesPath() {
    return "com.hypixel.hytale.server.core.command.system.arguments.types";
  }

  default ParameterizedTypeName getBaseType(String name, TypeMirror type) {
    return ParameterizedTypeName.get(ClassName.get(getSystemPath(), name), TypeName.get(type));
  }

  default TypeName getArgType() {
    return ClassName.get(getTypesPath(), "ArgTypes");
  }
}
