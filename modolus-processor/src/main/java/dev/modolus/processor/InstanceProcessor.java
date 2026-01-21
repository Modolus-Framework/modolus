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

package dev.modolus.processor;

import static dev.modolus.processor.Annotations.*;

import dev.modolus.processor.command.CommandProcessor;
import dev.modolus.processor.event.EventProcessor;
import dev.modolus.processor.manifest.HytalePluginProcessor;
import dev.modolus.processor.manifest.PluginManifest;
import dev.modolus.processor.singleton.*;
import dev.modolus.util.result.Error;
import java.io.OutputStream;
import java.util.*;
import javax.annotation.Nullable;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.StandardLocation;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;

@SupportedAnnotationTypes({
  COMMAND_ANNOTATION,
  EVENT_LISTENER_ANNOTATION,
  PROVIDE_SINGLETONS_ANNOTATION,
  INJECT_SINGLETONS_ANNOTATION,
  PROVIDE_SINGLETON_ANNOTATION,
  INJECT_SINGLETON_ANNOTATION,
  CREATE_ON_RUNTIME_ANNOTATION,
  SCOPE_ANNOTATION,
  HYTALE_PLUGIN_ANNOTATION
})
@SupportedSourceVersion(SourceVersion.RELEASE_25)
@SupportedOptions({"projectVersion"})
public class InstanceProcessor extends AbstractProcessor {

  private static final List<String> CREATION_ANNOTATIONS =
      List.of(COMMAND_ANNOTATION, EVENT_LISTENER_ANNOTATION);

  private static final List<String> MODIFIER_ANNOTATION =
      List.of(
          PROVIDE_SINGLETON_ANNOTATION,
          PROVIDE_SINGLETONS_ANNOTATION,
          INJECT_SINGLETON_ANNOTATION,
          INJECT_SINGLETONS_ANNOTATION,
          CREATE_ON_RUNTIME_ANNOTATION,
          SCOPE_ANNOTATION,
          HYTALE_PLUGIN_ANNOTATION);

  private static final ObjectWriter OBJECT_WRITER =
      new ObjectMapper().writerWithDefaultPrettyPrinter();

  private final Set<String> collectedClassesForRuntimeCreation = new HashSet<>();
  private final Set<String> collectedScopes = new HashSet<>();
  private PluginManifest pluginManifest = null;

  @SneakyThrows
  @Override
  public boolean process(
      @NotNull Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    Map<String, Processor> processors =
        Map.of(
            COMMAND_ANNOTATION, new CommandProcessor(processingEnv),
            EVENT_LISTENER_ANNOTATION, new EventProcessor(processingEnv),
            PROVIDE_SINGLETON_ANNOTATION, new ProvideSingletonProcessor(processingEnv),
            PROVIDE_SINGLETONS_ANNOTATION, new ProvideSingletonsProcessor(processingEnv),
            INJECT_SINGLETON_ANNOTATION, new InjectSingletonProcessor(processingEnv),
            INJECT_SINGLETONS_ANNOTATION, new InjectSingletonsProcessor(processingEnv),
            CREATE_ON_RUNTIME_ANNOTATION, new CreateOnRuntimeProcessor(processingEnv),
            SCOPE_ANNOTATION, new ScopeProcessor(processingEnv),
            HYTALE_PLUGIN_ANNOTATION, new HytalePluginProcessor(processingEnv));

    var sharedContext =
        new SharedContext(new HashMap<>(), new HashSet<>(), new HashSet<>(), new Holder<>());

    annotations.stream()
        .sorted(Comparator.comparingInt(this::getAnnotationPriority).reversed())
        .forEach(
            annotation ->
                processSingle(
                    annotation,
                    roundEnv,
                    processors.get(annotation.getQualifiedName().toString()),
                    sharedContext));

    sharedContext.sourceFileWriters().values().stream()
        .map(Map::values)
        .flatMap(Collection::stream)
        .map(file -> file.write(processingEnv))
        .forEach(error -> error.onFailure(this::logError));

    collectedClassesForRuntimeCreation.addAll(sharedContext.createOnRuntimeClasses());
    collectedScopes.addAll(sharedContext.scopePackages());
    if (sharedContext.pluginManifest().getValue() != null) {
      pluginManifest = sharedContext.pluginManifest().getValue();
    }

    if (roundEnv.processingOver()) {
      var runtimeClasses =
          processingEnv
              .getFiler()
              .createResource(StandardLocation.CLASS_OUTPUT, "", "runtime-classes.json");
      try (OutputStream outputStream = runtimeClasses.openOutputStream()) {
        OBJECT_WRITER.writeValue(outputStream, collectedClassesForRuntimeCreation);
      }

      var runtimeScopes =
          processingEnv
              .getFiler()
              .createResource(StandardLocation.CLASS_OUTPUT, "", "runtime-scopes.json");
      try (OutputStream outputStream = runtimeScopes.openOutputStream()) {
        OBJECT_WRITER.writeValue(outputStream, collectedScopes);
      }

      if (pluginManifest != null) {
        var pluginManifestFile =
            processingEnv
                .getFiler()
                .createResource(StandardLocation.CLASS_OUTPUT, "", "manifest.json");
        try (OutputStream outputStream = pluginManifestFile.openOutputStream()) {
          OBJECT_WRITER.writeValue(outputStream, pluginManifest);
        }
      }
    }

    return true;
  }

  private void processSingle(
      TypeElement annotation,
      RoundEnvironment roundEnv,
      @Nullable Processor processor,
      @NotNull SharedContext sharedContext) {
    if (processor == null) return;

    for (var annotated : roundEnv.getElementsAnnotatedWith(annotation)) {
      String className = ((TypeElement) annotated).getQualifiedName().toString();

      var writers =
          sharedContext.sourceFileWriters().computeIfAbsent(className, _ -> new HashMap<>());
      processor.processSingle(annotated, className, writers, sharedContext);
    }
  }

  private int getAnnotationPriority(@NotNull TypeElement element) {
    if (CREATION_ANNOTATIONS.contains(element.getQualifiedName().toString())) return 2;
    if (MODIFIER_ANNOTATION.contains(element.getQualifiedName().toString())) return 1;
    return 0;
  }

  private void logError(@NotNull Error<ProcessorError> error) {
    var logger = processingEnv.getMessager();
    if (error.getErrorType() == ProcessorError.ELEMENT_ERROR
        && error.getArgs().length == 1
        && error.getArgs()[0] instanceof Element elem) {
      logger.printError(error.getFullMessage(), elem);
      return;
    }

    logger.printError(error.getFullMessage());
  }
}
