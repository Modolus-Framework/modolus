package com.modolus.command.processor;

import com.palantir.javapoet.*;
import lombok.AccessLevel;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import java.util.Optional;

@Builder(access = AccessLevel.PACKAGE)
record ProcessorCommandOptionalArg(String name,
                                   String description,
                                   TypeMirror type,
                                   String argTypeName) implements ProcessorCommandArg {

    @Override
    public FieldSpec toFieldSpec(ProcessingEnvironment processingEnvironment) {
        return FieldSpec.builder(getBaseType("OptionalArg", type()), name())
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .initializer("withOptionalArg($S, $S, $T.$L)", name(), description(), getArgType(), argTypeName())
                .build();
    }

    @Override
    public ParameterSpec toParameterSpec(ProcessingEnvironment processingEnvironment) {
        var optionalType = ParameterizedTypeName.get(ClassName.get(Optional.class), TypeName.get(type()));
        return ParameterSpec.builder(optionalType, name())
                .addModifiers(Modifier.FINAL)
                .addAnnotation(NotNull.class)
                .build();
    }

    @Override
    public CodeBlock toStatement() {
        return CodeBlock.of("$N.provided(commandContext) ? Optional.of($N.get(commandContext)) : Optional.empty()", name(), name());
    }

}
