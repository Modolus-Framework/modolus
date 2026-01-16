package com.modolus.processor.singleton;

import com.modolus.annotations.singleton.SingletonForCollection;
import com.modolus.processor.ProcessorUtils;
import com.modolus.processor.SharedContext;
import com.modolus.processor.SourceFileWriter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import java.util.Arrays;
import java.util.Map;

public class SingletonForCollectionProcessor extends SingletonForProcessor {

    public SingletonForCollectionProcessor(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    @Override
    public void processSingle(@NotNull Element annotated,
                              String className,
                              Map<String, SourceFileWriter> writers,
                              @NotNull SharedContext sharedContext) {
        var singletonForCollection = annotated.getAnnotation(SingletonForCollection.class);
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
