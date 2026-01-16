package com.modolus.processor;

import com.modolus.processor.command.CommandProcessor;
import com.modolus.processor.singleton.*;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import tools.jackson.databind.ObjectMapper;

import javax.annotation.Nullable;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.StandardLocation;
import java.io.OutputStream;
import java.util.*;

import static com.modolus.processor.Annotations.*;

@SupportedAnnotationTypes({
        COMMAND_ANNOTATION,
        SINGLETON_FOR_COLLECTION_ANNOTATION,
        SINGLETON_NEEDED_COLLECTION_ANNOTATION,
        SINGLETON_FOR_ANNOTATION,
        SINGLETON_NEEDED_ANNOTATION,
        CREATE_ON_RUNTIME_ANNOTATION
})
@SupportedSourceVersion(SourceVersion.RELEASE_25)
public class InstanceProcessor extends AbstractProcessor {

    private static final List<String> CREATION_ANNOTATIONS = List.of(
            COMMAND_ANNOTATION
    );

    private static final List<String> MODIFIER_ANNOTATION = List.of(
            SINGLETON_FOR_ANNOTATION,
            SINGLETON_FOR_COLLECTION_ANNOTATION,
            SINGLETON_NEEDED_ANNOTATION,
            SINGLETON_NEEDED_COLLECTION_ANNOTATION,
            CREATE_ON_RUNTIME_ANNOTATION
    );

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final Set<String> collectedClassesForRuntimeCreation = new HashSet<>();

    @SneakyThrows
    @Override
    public boolean process(@NotNull Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<String, Processor> processors = Map.of(
                COMMAND_ANNOTATION, new CommandProcessor(processingEnv),
                SINGLETON_FOR_ANNOTATION, new SingletonForProcessor(processingEnv),
                SINGLETON_FOR_COLLECTION_ANNOTATION, new SingletonForCollectionProcessor(processingEnv),
                SINGLETON_NEEDED_ANNOTATION, new SingletonNeededProcessor(processingEnv),
                SINGLETON_NEEDED_COLLECTION_ANNOTATION, new SingletonNeededCollectionProcessor(processingEnv),
                CREATE_ON_RUNTIME_ANNOTATION, new CreateOnRuntimeProcessor(processingEnv)
        );

        var sharedContext = new SharedContext(new HashMap<>(), new HashSet<>());

        annotations.stream()
                .sorted(Comparator.comparingInt(this::getAnnotationPriority).reversed())
                .forEach(annotation -> processSingle(annotation, roundEnv, processors.get(annotation.getQualifiedName().toString()), sharedContext));

        sharedContext.sourceFileWriters().values()
                .stream()
                .map(Map::values)
                .flatMap(Collection::stream)
                .map(file -> file.write(processingEnv))
                .forEach(error -> error.onFailure(e -> e.print(processingEnv)));

        collectedClassesForRuntimeCreation.addAll(sharedContext.createOnRuntimeClasses());


        if (roundEnv.processingOver()) {
            var file = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", "runtime-classes.json");
            try (OutputStream outputStream = file.openOutputStream()) {
                OBJECT_MAPPER.writeValue(outputStream, collectedClassesForRuntimeCreation);
            }
        }

        return true;
    }

    private void processSingle(TypeElement annotation, RoundEnvironment roundEnv, @Nullable Processor processor, @NotNull SharedContext sharedContext) {
        if (processor == null) return;

        for (var annotated : roundEnv.getElementsAnnotatedWith(annotation)) {
            String className = ((TypeElement) annotated).getQualifiedName().toString();

            var writers = sharedContext.sourceFileWriters().computeIfAbsent(className, s -> new HashMap<>());
            processor.processSingle(annotated, className, writers, sharedContext);
        }
    }

    private int getAnnotationPriority(@NotNull TypeElement element) {
        if (CREATION_ANNOTATIONS.contains(element.getQualifiedName().toString())) return 2;
        if (MODIFIER_ANNOTATION.contains(element.getQualifiedName().toString())) return 1;
        return 0;
    }

}
