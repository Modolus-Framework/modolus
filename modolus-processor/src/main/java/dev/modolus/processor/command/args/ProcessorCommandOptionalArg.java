package dev.modolus.processor.command.args;

import com.palantir.javapoet.*;
import lombok.Builder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import java.util.Optional;

@Builder
public record ProcessorCommandOptionalArg(String name,
                                          String description,
                                          TypeMirror type,
                                          String argTypeName) implements ProcessorCommandArg {

    @Contract("_ -> new")
    @Override
    public @NotNull FieldSpec toFieldSpec(ProcessingEnvironment processingEnvironment) {
        return FieldSpec.builder(getBaseType("OptionalArg", type()), name())
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .initializer("withOptionalArg($S, $S, $T.$L)", name(), description(), getArgType(), argTypeName())
                .build();
    }

    @Override
    public @NotNull ParameterSpec toParameterSpec(ProcessingEnvironment processingEnvironment) {
        var optionalType = ParameterizedTypeName.get(ClassName.get(Optional.class), TypeName.get(type()));
        return ParameterSpec.builder(optionalType, name())
                .addModifiers(Modifier.FINAL)
                .addAnnotation(NotNull.class)
                .build();
    }

    @Contract(" -> new")
    @Override
    public @NotNull CodeBlock toStatement() {
        return CodeBlock.of("$N.provided(commandContext) ? Optional.of($N.get(commandContext)) : Optional.empty()", name(), name());
    }

}
