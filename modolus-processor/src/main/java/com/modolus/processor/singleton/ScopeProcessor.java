package com.modolus.processor.singleton;

import com.modolus.annotations.singleton.Scope;
import com.modolus.processor.Processor;
import com.modolus.processor.ProcessorUtils;
import com.modolus.processor.SharedContext;
import com.modolus.processor.SourceFileWriter;

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
