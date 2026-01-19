package dev.modolus.processor.command.args;

import com.palantir.javapoet.CodeBlock;
import com.palantir.javapoet.FieldSpec;
import com.palantir.javapoet.ParameterSpec;
import com.palantir.javapoet.TypeName;
import lombok.Builder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;

@Builder
public record ProcessorCommandDefaultArg(String name,
                                         String description,
                                         TypeMirror type,
                                         String argTypeName,
                                         String defaultValue,
                                         String defaultValueDescription) implements ProcessorCommandArg {

    @Contract("_ -> new")
    @Override
    public @NotNull FieldSpec toFieldSpec(ProcessingEnvironment processingEnvironment) {
        return FieldSpec.builder(getBaseType("DefaultArg", type()), name())
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .initializer("withDefaultArg($S, $S, $T.$L, $L, $S)", name(), description(), getArgType(), argTypeName(), defaultValue(), defaultValueDescription())
                .build();
    }

    @Contract("_ -> new")
    @Override
    public @NotNull ParameterSpec toParameterSpec(ProcessingEnvironment processingEnvironment) {
        return ParameterSpec.builder(TypeName.get(type()), name())
                .addModifiers(Modifier.FINAL)
                .addAnnotation(NotNull.class)
                .build();
    }

    @Contract(" -> new")
    @Override
    public @NotNull CodeBlock toStatement() {
        return CodeBlock.of("$N.provided(commandContext) ? $N.get(commandContext) : $L", name(), name(), defaultValue());
    }
}
