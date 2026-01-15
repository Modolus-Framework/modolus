package com.modolus.processor.command;

import com.modolus.annotations.command.Command;
import com.palantir.javapoet.AnnotationSpec;
import com.palantir.javapoet.MethodSpec;
import com.palantir.javapoet.ParameterizedTypeName;
import com.palantir.javapoet.TypeName;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.lang.model.element.Modifier;
import java.util.concurrent.CompletableFuture;

@UtilityClass
public final class CommandMethods {

    public static MethodSpec.@NotNull Builder getOverrideMethod(@NotNull Command command) {
        return switch (command) {
            case Command c when c.target() == Command.CommandTarget.PLAYER && c.async() -> getAsyncCommandMethod();
            case Command c when c.target() == Command.CommandTarget.PLAYER -> getSyncCommandMethod();
            case Command c when c.target() == Command.CommandTarget.WORLD && c.async() -> getAsyncCommandMethod();
            case Command c when c.target() == Command.CommandTarget.WORLD -> getSyncCommandMethod();
            case Command c when c.async() -> getAsyncCommandMethod();
            default -> getSyncCommandMethod()
                    .returns(ParameterizedTypeName.get(CompletableFuture.class, Void.class).annotated(AnnotationSpec.builder(Nullable.class).build()));
        };
    }

    public static @NotNull TypeName getOverrideMethodReturnType(@NotNull Command command) {
        return switch (command) {
            case Command c when c.target() == Command.CommandTarget.PLAYER && c.async() ->
                    ParameterizedTypeName.get(CompletableFuture.class, Void.class).annotated(AnnotationSpec.builder(NotNull.class).build());
            case Command c when c.target() == Command.CommandTarget.PLAYER -> TypeName.VOID;
            case Command c when c.target() == Command.CommandTarget.WORLD && c.async() ->
                    ParameterizedTypeName.get(CompletableFuture.class, Void.class).annotated(AnnotationSpec.builder(NotNull.class).build());
            case Command c when c.target() == Command.CommandTarget.WORLD -> TypeName.VOID;
            case Command c when c.async() ->
                    ParameterizedTypeName.get(CompletableFuture.class, Void.class).annotated(AnnotationSpec.builder(NotNull.class).build());
            default ->
                    ParameterizedTypeName.get(CompletableFuture.class, Void.class).annotated(AnnotationSpec.builder(Nullable.class).build());
        };
    }


    private MethodSpec.@NotNull Builder getAsyncCommandMethod() {
        return MethodSpec.methodBuilder("executeAsync")
                .addModifiers(Modifier.PROTECTED);
    }

    private MethodSpec.@NotNull Builder getSyncCommandMethod() {
        return MethodSpec.methodBuilder("execute")
                .addModifiers(Modifier.PROTECTED);
    }

}
