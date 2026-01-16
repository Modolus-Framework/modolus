package com.modolus.processor;

import lombok.RequiredArgsConstructor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import java.util.Map;

@RequiredArgsConstructor
public abstract class Processor {

    protected final ProcessingEnvironment processingEnv;

    public abstract void processSingle(Element annotated,
                                       String className,
                                       Map<String, SourceFileWriter> writers,
                                       SharedContext sharedContext);

}
