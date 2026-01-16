package com.modolus.processor.singleton;

import com.modolus.annotations.singleton.InjectSingletons;
import com.modolus.processor.ProcessorUtils;
import com.modolus.processor.SharedContext;
import com.modolus.processor.SourceFileWriter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import java.util.Arrays;
import java.util.Map;

public class InjectSingletonsProcessor extends InjectSingletonProcessor {

    public InjectSingletonsProcessor(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    @Override
    public void processSingle(@NotNull Element annotated,
                              String className,
                              Map<String, SourceFileWriter> writers,
                              @NotNull SharedContext sharedContext) {
        var singletonNeededCollection = annotated.getAnnotation(InjectSingletons.class);
        assert singletonNeededCollection != null;

        ProcessorUtils.ensureBaseFileExists(writers, className, annotated);

        writers.values()
                .forEach(sourceFileWriter -> Arrays.stream(singletonNeededCollection.value())
                        .distinct()
                        .map(this::addNeeded)
                        .forEach(sourceFileWriter::addField));
    }

}
