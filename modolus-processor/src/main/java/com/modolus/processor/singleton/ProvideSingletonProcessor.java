package com.modolus.processor.singleton;

import com.modolus.annotations.singleton.ProvideSingleton;
import com.modolus.processor.Processor;
import com.modolus.processor.ProcessorUtils;
import com.modolus.processor.SharedContext;
import com.modolus.processor.SourceFileWriter;
import com.modolus.util.singleton.Singleton;
import com.modolus.util.singleton.SingletonScope;
import com.modolus.util.singleton.Singletons;
import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.CodeBlock;
import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import java.util.Map;

public class ProvideSingletonProcessor extends Processor {

    private static final ClassName SINGLETONS_CLASS_NAME = ClassName.get(Singletons.class);
    private static final ClassName SINGLETON_SCOPE_CLASS_NAME = ClassName.get(SingletonScope.class);
    private static final String REGISTER_SINGLETON_FUNCTION = "provideSingleton";

    public ProvideSingletonProcessor(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    @Override
    public void processSingle(@NotNull Element annotated,
                              String className,
                              Map<String, SourceFileWriter> writers,
                              @NotNull SharedContext sharedContext) {
        var singletonFor = annotated.getAnnotation(ProvideSingleton.class);
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

    protected CodeBlock registerSingleton(@NotNull Element element, @NotNull ProvideSingleton singleton) {
        var type = ProcessorUtils.getTypeMirror(singleton::value);
        assert type != null;

        if (singleton.singletonIdentifier().isBlank())
            return registerSingletonCodeBlockWithoutCustomIdentifier(type, element, singleton.scope());
        return registerSingletonCodeBlockWithCustomIdentifier(type, element, singleton.singletonIdentifier(), singleton.scope());
    }

    private @NotNull CodeBlock registerSingletonCodeBlockWithCustomIdentifier(@NotNull TypeMirror type,
                                                                              @NotNull Element element,
                                                                              @NotNull String identifier,
                                                                              @NotNull SingletonScope scope) {
        if (processingEnv.getTypeUtils().isSameType(type, element.asType()))
            return CodeBlock.of("$T.$L(this, $S, $T.$L).orElseThrow()", SINGLETONS_CLASS_NAME, REGISTER_SINGLETON_FUNCTION, identifier, SINGLETON_SCOPE_CLASS_NAME, scope.name());


        return CodeBlock.of("$T.$L($T.class, this, $S, $T.$L).orElseThrow()", SINGLETONS_CLASS_NAME, REGISTER_SINGLETON_FUNCTION, type, identifier, SINGLETON_SCOPE_CLASS_NAME, scope.name());
    }

    private @NotNull CodeBlock registerSingletonCodeBlockWithoutCustomIdentifier(@NotNull TypeMirror type,
                                                                                 @NotNull Element element,
                                                                                 @NotNull SingletonScope scope) {
        if (processingEnv.getTypeUtils().isSameType(type, element.asType()))
            return CodeBlock.of("$T.$L(this, $T.$L).orElseThrow()", SINGLETONS_CLASS_NAME, REGISTER_SINGLETON_FUNCTION, SINGLETON_SCOPE_CLASS_NAME, scope.name());


        return CodeBlock.of("$T.$L($T.class, this, $T.$L).orElseThrow()", SINGLETONS_CLASS_NAME, REGISTER_SINGLETON_FUNCTION, type, SINGLETON_SCOPE_CLASS_NAME, scope.name());
    }

}
