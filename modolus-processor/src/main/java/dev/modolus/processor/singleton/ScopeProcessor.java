package dev.modolus.processor.singleton;

import dev.modolus.annotations.singleton.Scope;
import dev.modolus.processor.Processor;
import dev.modolus.processor.ProcessorUtils;
import dev.modolus.processor.SharedContext;
import dev.modolus.processor.SourceFileWriter;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import java.util.Map;

public class ScopeProcessor extends Processor {

    public ScopeProcessor(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    @Override
    public void processSingle(Element annotated,
                              String className,
                              Map<String, SourceFileWriter> writers,
                              SharedContext sharedContext) {
        var scope = annotated.getAnnotation(Scope.class);
        assert scope != null;

        sharedContext.scopePackages().add(ProcessorUtils.getPackageName(className));
    }
}
