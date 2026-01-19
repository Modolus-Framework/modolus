package dev.modolus.processor.singleton;

import dev.modolus.annotations.singleton.ProvideSingletons;
import dev.modolus.processor.ProcessorUtils;
import dev.modolus.processor.SharedContext;
import dev.modolus.processor.SourceFileWriter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import java.util.Arrays;
import java.util.Map;

public class ProvideSingletonsProcessor extends ProvideSingletonProcessor {

    public ProvideSingletonsProcessor(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    @Override
    public void processSingle(@NotNull Element annotated,
                              String className,
                              Map<String, SourceFileWriter> writers,
                              @NotNull SharedContext sharedContext) {
        var singletonForCollection = annotated.getAnnotation(ProvideSingletons.class);
        assert singletonForCollection != null;

        ProcessorUtils.ensureBaseFileExists(writers, className, annotated);

        writers.values()
                .forEach(sourceFileWriter -> {
                    ensureImplementsSingleton(sourceFileWriter);
                    Arrays.stream(singletonForCollection.value())
                            .distinct()
                            .map(singleton -> registerSingleton(annotated, singleton))
                            .forEach(sourceFileWriter.getConstructor()::addStatement);
                });
    }

}
