package com.modolus.processor.command.args;

import com.palantir.javapoet.*;
import lombok.Builder;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;

@Builder
public record ProcessorCommandFlagArg(String name,
                                      String description) implements ProcessorCommandArg {


    @Override
    public FieldSpec toFieldSpec(ProcessingEnvironment processingEnvironment) {
        var type = ClassName.get(getSystemPath(), "DefaultArg");

        return FieldSpec.builder(type, name())
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .initializer("withFlagArg($S, $S)", name(), description())
                .build();
    }

    @Override
    public ParameterSpec toParameterSpec(ProcessingEnvironment processingEnvironment) {
        return ParameterSpec.builder(TypeName.BOOLEAN, name())
                .addModifiers(Modifier.FINAL)
                .build();
    }

    @Override
    public CodeBlock toStatement() {
        return CodeBlock.of("commandContext.hasFlag($S)", name());
    }
}
