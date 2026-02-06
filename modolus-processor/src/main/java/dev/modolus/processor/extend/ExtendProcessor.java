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

package dev.modolus.processor.extend;

import com.palantir.javapoet.*;
import dev.modolus.annotations.extend.Extends;
import dev.modolus.processor.Processor;
import dev.modolus.processor.ProcessorUtils;
import dev.modolus.processor.SharedContext;
import dev.modolus.processor.SourceFileWriter;
import java.util.Map;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.util.ElementFilter;
import org.jetbrains.annotations.NotNull;

public class ExtendProcessor extends Processor {

  public ExtendProcessor(ProcessingEnvironment processingEnv) {
    super(processingEnv);
  }

  @Override
  public void processSingle(
      Element annotated,
      String className,
      Map<String, SourceFileWriter> writers,
      SharedContext sharedContext) {
    var extendType = annotated.getAnnotation(Extends.class);
    assert extendType != null;

    ProcessorUtils.ensureBaseFileExists(writers, className, annotated);

    writers.values().forEach(writer -> addExtend(writer, extendType));
  }

  private void addExtend(@NotNull SourceFileWriter sourceFileWriter, @NotNull Extends extendType) {
    var type = ProcessorUtils.getTypeMirror(extendType::value);
    assert type != null;

    var element = processingEnv.getElementUtils().getTypeElement(TypeName.get(type).toString());
    var typeParams =
        element.getTypeParameters().stream()
            .peek(
                typeName ->
                    sourceFileWriter
                        .getClassBuilder()
                        .addTypeVariable(TypeVariableName.get(typeName)))
            .map(Element::asType)
            .map(TypeName::get)
            .toArray(TypeName[]::new);

    sourceFileWriter.getClassBuilder().addModifiers(Modifier.ABSTRACT);

    TypeName typeName = TypeName.get(type);

    if (typeParams.length != 0) {
      typeName = ParameterizedTypeName.get((ClassName) TypeName.get(type), typeParams);
    }

    sourceFileWriter.getClassBuilder().superclass(typeName);

    var constructor = ElementFilter.constructorsIn(element.getEnclosedElements()).getFirst();

    constructor
        .getParameters()
        .forEach(
            param ->
                sourceFileWriter
                    .getConstructor()
                    .addParameter(TypeName.get(param.asType()), param.getSimpleName().toString()));

    var parameters =
        constructor.getParameters().stream()
            .map(param -> CodeBlock.of("$L", param.getSimpleName().toString()))
            .toList();

    sourceFileWriter.getConstructor().addStatement("super($L)", CodeBlock.join(parameters, ", "));
  }
}
