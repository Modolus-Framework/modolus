package com.modolus.processor.command.args;

import com.palantir.javapoet.*;
import lombok.Builder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;

@Builder
public record ProcessorCommandFlagArg(String name,
                                      String description) implements ProcessorCommandArg {


    @Contract("_ -> new")
    @Override
    public @NotNull FieldSpec toFieldSpec(ProcessingEnvironment processingEnvironment) {
        var type = ClassName.get(getSystemPath(), "DefaultArg");

        return FieldSpec.builder(type, name())
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .initializer("withFlagArg($S, $S)", name(), description())
                .build();
    }

    @Contract("_ -> new")
    @Override
    public @NotNull ParameterSpec toParameterSpec(ProcessingEnvironment processingEnvironment) {
        return ParameterSpec.builder(TypeName.BOOLEAN, name())
                .addModifiers(Modifier.FINAL)
                .build();
    }

    @Contract(" -> new")
    @Override
    public @NotNull CodeBlock toStatement() {
        return CodeBlock.of("commandContext.hasFlag($S)", name());
    }
}
