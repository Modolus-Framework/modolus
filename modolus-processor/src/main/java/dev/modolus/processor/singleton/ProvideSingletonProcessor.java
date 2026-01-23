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

import com.palantir.javapoet.*;
import dev.modolus.annotations.singleton.ProvideSingleton;
import dev.modolus.processor.Processor;
import dev.modolus.processor.ProcessorUtils;
import dev.modolus.processor.SharedContext;
import dev.modolus.processor.SourceFileWriter;
import dev.modolus.util.singleton.Lazy;
import dev.modolus.util.singleton.Singleton;
import dev.modolus.util.singleton.SingletonScope;
import dev.modolus.util.singleton.Singletons;
import java.util.Map;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import org.jetbrains.annotations.NotNull;

public class ProvideSingletonProcessor extends Processor {

  private static final ClassName SINGLETONS_CLASS_NAME = ClassName.get(Singletons.class);
  private static final ClassName SINGLETON_SCOPE_CLASS_NAME = ClassName.get(SingletonScope.class);
  private static final String REGISTER_SINGLETON_FUNCTION = "provideSingleton";

  public ProvideSingletonProcessor(ProcessingEnvironment processingEnv) {
    super(processingEnv);
  }

  @Override
  public void processSingle(
      @NotNull Element annotated,
      String className,
      Map<String, SourceFileWriter> writers,
      @NotNull SharedContext sharedContext) {
    var singletonFor = annotated.getAnnotation(ProvideSingleton.class);
    assert singletonFor != null;

    ProcessorUtils.ensureBaseFileExists(writers, className, annotated);

    writers
        .values()
        .forEach(
            sourceFileWriter -> {
              ensureImplementsSingleton(sourceFileWriter);
              sourceFileWriter
                  .getConstructor()
                  .addStatement(registerSingleton(annotated, singletonFor));
              addStaticAccessMethod(singletonFor, sourceFileWriter);
            });
  }

  protected void ensureImplementsSingleton(@NotNull SourceFileWriter writer) {
    writer.getClassBuilder().addSuperinterface(ClassName.get(Singleton.class));
  }

  protected void addStaticAccessMethod(
      @NotNull ProvideSingleton singleton, @NotNull SourceFileWriter writer) {
    var type = ProcessorUtils.getTypeMirror(singleton::value);
    assert type != null;

    if (singleton.singletonIdentifier().isBlank()) {
      writer.addMethod(registerSingletonMethodWithoutCustomIdentifier(type, singleton.scope()));
      return;
    }

    writer.addMethod(
        registerSingletonMethodWithCustomIdentifier(
            type, singleton.singletonIdentifier(), singleton.scope()));
  }

  protected CodeBlock registerSingleton(
      @NotNull Element element, @NotNull ProvideSingleton singleton) {
    var type = ProcessorUtils.getTypeMirror(singleton::value);
    assert type != null;

    if (singleton.singletonIdentifier().isBlank())
      return registerSingletonCodeBlockWithoutCustomIdentifier(type, element, singleton.scope());
    return registerSingletonCodeBlockWithCustomIdentifier(
        type, element, singleton.singletonIdentifier(), singleton.scope());
  }

  private @NotNull CodeBlock registerSingletonCodeBlockWithCustomIdentifier(
      @NotNull TypeMirror type,
      @NotNull Element element,
      @NotNull String identifier,
      @NotNull SingletonScope scope) {
    if (processingEnv.getTypeUtils().isSameType(type, element.asType()))
      return CodeBlock.of(
          "$T.$L(this, $S, $T.$L).orElseThrow()",
          SINGLETONS_CLASS_NAME,
          REGISTER_SINGLETON_FUNCTION,
          identifier,
          SINGLETON_SCOPE_CLASS_NAME,
          scope.name());

    return CodeBlock.of(
        "$T.$L($T.class, this, $S, $T.$L).orElseThrow()",
        SINGLETONS_CLASS_NAME,
        REGISTER_SINGLETON_FUNCTION,
        type,
        identifier,
        SINGLETON_SCOPE_CLASS_NAME,
        scope.name());
  }

  private @NotNull CodeBlock registerSingletonCodeBlockWithoutCustomIdentifier(
      @NotNull TypeMirror type, @NotNull Element element, @NotNull SingletonScope scope) {
    if (processingEnv.getTypeUtils().isSameType(type, element.asType()))
      return CodeBlock.of(
          "$T.$L(this, $T.$L).orElseThrow()",
          SINGLETONS_CLASS_NAME,
          REGISTER_SINGLETON_FUNCTION,
          SINGLETON_SCOPE_CLASS_NAME,
          scope.name());

    return CodeBlock.of(
        "$T.$L($T.class, this, $T.$L).orElseThrow()",
        SINGLETONS_CLASS_NAME,
        REGISTER_SINGLETON_FUNCTION,
        type,
        SINGLETON_SCOPE_CLASS_NAME,
        scope.name());
  }

  private @NotNull MethodSpec registerSingletonMethodWithCustomIdentifier(
      @NotNull TypeMirror type, @NotNull String identifier, @NotNull SingletonScope scope) {
    // TODO add safer version
    return MethodSpec.methodBuilder("getLazy" + ((DeclaredType) type).asElement().getSimpleName())
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .returns(ParameterizedTypeName.get(ClassName.get(Lazy.class), TypeName.get(type)))
        .addStatement(
            "return $T.of($T.class, $T.$L, $S)",
            ClassName.get(Lazy.class),
            type,
            ClassName.get(SingletonScope.class),
            scope.name(),
            identifier)
        .build();
  }

  private @NotNull MethodSpec registerSingletonMethodWithoutCustomIdentifier(
      @NotNull TypeMirror type, @NotNull SingletonScope scope) {
    // TODO add safer version
    return MethodSpec.methodBuilder("getLazy" + ((DeclaredType) type).asElement().getSimpleName())
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .returns(ParameterizedTypeName.get(ClassName.get(Lazy.class), TypeName.get(type)))
        .addStatement(
            "return $T.of($T.class, $T.$L)",
            ClassName.get(Lazy.class),
            type,
            ClassName.get(SingletonScope.class),
            scope.name())
        .build();
  }
}
