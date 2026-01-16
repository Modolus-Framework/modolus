package com.modolus.processor.singleton;

import com.modolus.processor.Processor;
import com.modolus.processor.SharedContext;
import com.modolus.processor.SourceFileWriter;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import java.util.Map;

public class CreateOnRuntimeProcessor extends Processor {

    public CreateOnRuntimeProcessor(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    @Override
    public void processSingle(Element annotated, String className, Map<String, SourceFileWriter> writers, SharedContext sharedContext) {
        sharedContext.createOnRuntimeClasses().add(className);
    }
}
