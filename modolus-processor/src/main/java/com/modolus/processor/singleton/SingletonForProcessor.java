package com.modolus.processor.singleton;

import com.modolus.annotations.singleton.SingletonFor;
import com.modolus.processor.Processor;
import com.modolus.processor.ProcessorUtils;
import com.modolus.processor.SharedContext;
import com.modolus.processor.SourceFileWriter;
import com.modolus.util.singleton.Singleton;
import com.modolus.util.singleton.Singletons;
import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.CodeBlock;
import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import java.util.Map;

public class SingletonForProcessor extends Processor {

    private static final ClassName SINGLETONS_CLASS_NAME = ClassName.get(Singletons.class);

    public SingletonForProcessor(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    @Override
    public void processSingle(@NotNull Element annotated,
                              String className,
                              Map<String, SourceFileWriter> writers,
                              @NotNull SharedContext sharedContext) {
        var singletonFor = annotated.getAnnotation(SingletonFor.class);
        assert singletonFor != null;

        ProcessorUtils.ensureBaseFileExists(writers, className, annotated);

        writers.values()
                .forEach(sourceFileWriter -> {
                    ensureImplementsSingleton(sourceFileWriter);
                    sourceFileWriter.getConstructor()
                            .addStatement(registerSingleton(annotated, singletonFor));
                });
    }

    protected void ensureImplementsSingleton(SourceFileWriter writer) {
        writer.getClassBuilder()
                .addSuperinterface(ClassName.get(Singleton.class));
    }

    protected CodeBlock registerSingleton(@NotNull Element element, @NotNull SingletonFor singleton) {
        var type = ProcessorUtils.getTypeMirror(singleton::value);
        assert type != null;

        if (!processingEnv.getTypeUtils().isSubtype(element.asType(), type)) {
            processingEnv.getMessager().printError("Is not a subtype of " + type, element);
        }

        if (processingEnv.getTypeUtils().isSameType(type, element.asType()))
            return CodeBlock.of("$T.$L(this).orElseThrow()", SINGLETONS_CLASS_NAME, "registerSingleton");


        return CodeBlock.of("$T.$L($T.class, this).orElseThrow()", SINGLETONS_CLASS_NAME, "registerSingleton", type);
    }

}
