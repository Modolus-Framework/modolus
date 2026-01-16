package com.modolus.processor.command;

import com.modolus.annotations.command.Command;
import com.modolus.processor.SourceFileWriter;
import com.modolus.processor.command.args.ProcessorCommandArg;
import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.CodeBlock;
import com.palantir.javapoet.ParameterSpec;
import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import java.util.List;
import java.util.stream.Stream;

import static com.modolus.processor.command.Constants.*;

public final class CommandFileWriter {

    private final SourceFileWriter sourceFileWriter;
    private final List<ProcessorCommandArg> commandArgs;
    private final Command command;

    public CommandFileWriter(@NotNull SourceFileWriter sourceFileWriter,
                             @NotNull List<ProcessorCommandArg> commandArgs,
                             @NotNull Command command) {
        this.sourceFileWriter = sourceFileWriter;
        this.commandArgs = commandArgs;
        this.command = command;

        sourceFileWriter.getClassBuilder()
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .superclass(getSuperclass(command));
    }

    public void apply(@NotNull ProcessingEnvironment processingEnvironment) {
        appendConstructor();
        appendFields(processingEnvironment);
        appendMethods(processingEnvironment);
    }

    private void appendMethods(@NotNull ProcessingEnvironment processingEnvironment) {
        var returnType = CommandMethods.getOverrideMethodReturnType(command);
        var parameters = CommandParameters.getOverrideMethodParameters(command);

        var overrideMethod = CommandMethods.getOverrideMethod(command)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.FINAL)
                .returns(returnType)
                .addParameters(parameters);

        var methodParams = parameters.stream()
                .map(ParameterSpec::name)
                .map(CodeBlock::of);
        var argParams = commandArgs.stream()
                .map(ProcessorCommandArg::toStatement);

        var params = Stream.concat(methodParams, argParams)
                .toList();

        var callAbstractMethod = CodeBlock.builder()
                .add("return executeCommand(")
                .add(CodeBlock.join(params, ", "))
                .add(");");

        overrideMethod.addCode(callAbstractMethod.build());


        var abstractMethod = CommandMethods.getOverrideMethod(command)
                .setName("executeCommand")
                .addModifiers(Modifier.ABSTRACT)
                .returns(returnType)
                .addParameters(parameters)
                .addParameters(commandArgs.stream().map(arg -> arg.toParameterSpec(processingEnvironment)).toList());


        sourceFileWriter.addMethod(overrideMethod.build());
        sourceFileWriter.addMethod(abstractMethod.build());
    }


    private void appendConstructor() {
        sourceFileWriter.getConstructor()
                .addStatement("super($S, $S)", command.name(), command.description())
                .build();
    }

    private void appendFields(@NotNull ProcessingEnvironment processingEnvironment) {
        commandArgs.stream()
                .map(arg -> arg.toFieldSpec(processingEnvironment))
                .forEach(sourceFileWriter::addField);
    }

    private ClassName getSuperclass(@NotNull Command command) {
        return switch (command) {
            case Command c when c.target() == Command.CommandTarget.PLAYER && c.async() ->
                    ABSTRACT_ASYNC_PLAYER_COMMAND_CLASS;
            case Command c when c.target() == Command.CommandTarget.PLAYER -> ABSTRACT_PLAYER_COMMAND_CLASS;
            case Command c when c.target() == Command.CommandTarget.WORLD && c.async() ->
                    ABSTRACT_ASYNC_WORLD_COMMAND_CLASS;
            case Command c when c.target() == Command.CommandTarget.WORLD -> ABSTRACT_WORLD_COMMAND_CLASS;
            case Command c when c.async() -> ABSTRACT_ASYNC_COMMAND_CLASS;
            default -> ABSTRACT_COMMAND_CLASS;
        };
    }

}
