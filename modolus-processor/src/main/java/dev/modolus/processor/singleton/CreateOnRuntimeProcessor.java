package dev.modolus.processor.singleton;

import dev.modolus.processor.Processor;
import dev.modolus.processor.SharedContext;
import dev.modolus.processor.SourceFileWriter;

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
