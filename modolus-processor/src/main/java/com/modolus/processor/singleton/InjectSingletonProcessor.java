package com.modolus.processor.singleton;

import com.modolus.annotations.singleton.InjectSingleton;
import com.modolus.processor.Processor;
import com.modolus.processor.ProcessorUtils;
import com.modolus.processor.SharedContext;
import com.modolus.processor.SourceFileWriter;
import com.modolus.util.singleton.Lazy;
import com.modolus.util.singleton.SingletonScope;
import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.FieldSpec;
import com.palantir.javapoet.ParameterizedTypeName;
import com.palantir.javapoet.TypeName;
import org.apache.commons.text.CaseUtils;
import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import java.util.Map;

public class InjectSingletonProcessor extends Processor {

    private static final ClassName LAZY_CLASS_NAME = ClassName.get(Lazy.class);
    private static final ClassName SINGLETON_SCOPE_CLASS_NAME = ClassName.get(SingletonScope.class);

    public InjectSingletonProcessor(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    @Override
    public void processSingle(@NotNull Element annotated,
                              String className,
                              Map<String, SourceFileWriter> writers,
                              @NotNull SharedContext sharedContext) {
        var singletonNeeded = annotated.getAnnotation(InjectSingleton.class);
        assert singletonNeeded != null;

        ProcessorUtils.ensureBaseFileExists(writers, className, annotated);

        writers.values()
                .forEach(sourceFileWriter -> sourceFileWriter.addField(addNeeded(singletonNeeded)));
    }

    protected FieldSpec addNeeded(@NotNull InjectSingleton singleton) {
        var type = ProcessorUtils.getTypeMirror(singleton::value);
        assert type != null;

        var name = singleton.fieldName();
        if (name.isBlank()) {
            var simpleName = ProcessorUtils.getSimpleClassName(type.toString());
            name = CaseUtils.toCamelCase(simpleName, false);
        }

        var lazyType = ParameterizedTypeName.get(LAZY_CLASS_NAME, TypeName.get(type));
        var builder = FieldSpec.builder(lazyType, name)
                .addModifiers(Modifier.PROTECTED, Modifier.FINAL);

        if (singleton.fieldName().isBlank())
            builder.initializer("$T.of($T.class, $T.$L)", lazyType.rawType(), type, SINGLETON_SCOPE_CLASS_NAME, singleton.scope().name());
        else
            builder.initializer("$T.of($T.class, $T.$L, $S)", lazyType.rawType(), type, singleton.singletonIdentifier(), SINGLETON_SCOPE_CLASS_NAME, singleton.scope().name());

        return builder.build();
    }

}
