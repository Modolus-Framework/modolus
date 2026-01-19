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

package dev.modolus.processor.singleton;

import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.FieldSpec;
import com.palantir.javapoet.ParameterizedTypeName;
import com.palantir.javapoet.TypeName;
import dev.modolus.annotations.singleton.InjectSingleton;
import dev.modolus.processor.Processor;
import dev.modolus.processor.ProcessorUtils;
import dev.modolus.processor.SharedContext;
import dev.modolus.processor.SourceFileWriter;
import dev.modolus.util.singleton.Lazy;
import dev.modolus.util.singleton.SingletonScope;
import java.util.Map;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import org.apache.commons.text.CaseUtils;
import org.jetbrains.annotations.NotNull;

public class InjectSingletonProcessor extends Processor {

  private static final ClassName LAZY_CLASS_NAME = ClassName.get(Lazy.class);
  private static final ClassName SINGLETON_SCOPE_CLASS_NAME = ClassName.get(SingletonScope.class);

  public InjectSingletonProcessor(ProcessingEnvironment processingEnv) {
    super(processingEnv);
  }

  @Override
  public void processSingle(
      @NotNull Element annotated,
      String className,
      Map<String, SourceFileWriter> writers,
      @NotNull SharedContext sharedContext) {
    var singletonNeeded = annotated.getAnnotation(InjectSingleton.class);
    assert singletonNeeded != null;

    ProcessorUtils.ensureBaseFileExists(writers, className, annotated);

    writers
        .values()
        .forEach(sourceFileWriter -> sourceFileWriter.addField(addNeeded(singletonNeeded)));
  }

  protected FieldSpec addNeeded(@NotNull InjectSingleton singleton) {
    var type = ProcessorUtils.getTypeMirror(singleton::value);
    assert type != null;

    var name = singleton.fieldName();
    if (name.isBlank()) {
      var simpleName = ProcessorUtils.getSimpleClassName(type.toString());
      name = CaseUtils.toCamelCase(simpleName, false);
    }

    var lazyType = ParameterizedTypeName.get(LAZY_CLASS_NAME, TypeName.get(type));
    var builder =
        FieldSpec.builder(lazyType, name).addModifiers(Modifier.PROTECTED, Modifier.FINAL);

    if (singleton.fieldName().isBlank())
      builder.initializer(
          "$T.of($T.class, $T.$L)",
          lazyType.rawType(),
          type,
          SINGLETON_SCOPE_CLASS_NAME,
          singleton.scope().name());
    else
      builder.initializer(
          "$T.of($T.class, $T.$L, $S)",
          lazyType.rawType(),
          type,
          singleton.singletonIdentifier(),
          SINGLETON_SCOPE_CLASS_NAME,
          singleton.scope().name());

    return builder.build();
  }
}
