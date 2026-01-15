package com.modolus.processor.command.args;

import com.palantir.javapoet.CodeBlock;
import com.palantir.javapoet.FieldSpec;
import com.palantir.javapoet.ParameterSpec;
import com.palantir.javapoet.TypeName;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;

@Builder
public record ProcessorCommandRequiredArg(String name,
                                          String description,
                                          TypeMirror type,
                                          String argTypeName) implements ProcessorCommandArg {

    @Override
    public FieldSpec toFieldSpec(ProcessingEnvironment processingEnvironment) {
        return FieldSpec.builder(getBaseType("RequiredArg", type()), name())
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .initializer("withRequiredArg($S, $S, $T.$L)", name(), description(), getArgType(), argTypeName())
                .build();
    }

    @Override
    public ParameterSpec toParameterSpec(ProcessingEnvironment processingEnvironment) {
        return ParameterSpec.builder(TypeName.get(type()), name())
                .addModifiers(Modifier.FINAL)
                .addAnnotation(NotNull.class)
                .build();
    }

    @Override
    public CodeBlock toStatement() {
        return CodeBlock.of("$N.get(commandContext)", name());
    }
}
