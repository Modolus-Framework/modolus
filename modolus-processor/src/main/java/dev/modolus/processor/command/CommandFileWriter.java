/*
 * Copyright (C) 2026 Modolus-Framework
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package dev.modolus.processor.command;

import static dev.modolus.processor.command.Constants.*;

import com.palantir.javapoet.*;
import dev.modolus.annotations.command.Command;
import dev.modolus.processor.ProcessorUtils;
import dev.modolus.processor.SourceFileWriter;
import dev.modolus.processor.command.args.ProcessorCommandArg;
import dev.modolus.util.singleton.Lazy;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import org.jetbrains.annotations.NotNull;

public final class CommandFileWriter {

  private static final ClassName LAZY_CLASS_NAME = ClassName.get(Lazy.class);

  private final SourceFileWriter sourceFileWriter;
  private final List<ProcessorCommandArg> commandArgs;
  private final Command command;

  public CommandFileWriter(
      @NotNull SourceFileWriter sourceFileWriter,
      @NotNull List<ProcessorCommandArg> commandArgs,
      @NotNull Command command) {
    this.sourceFileWriter = sourceFileWriter;
    this.commandArgs = commandArgs;
    this.command = command;

    sourceFileWriter
        .getClassBuilder()
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
        .superclass(getSuperclass(command));
  }

  public void apply(@NotNull ProcessingEnvironment processingEnvironment) {
    appendConstructor();
    appendSubCommands();
    appendFields(processingEnvironment);
    appendMethods(processingEnvironment);
  }

  private void appendMethods(@NotNull ProcessingEnvironment processingEnvironment) {
    var returnType = CommandMethods.getOverrideMethodReturnType(command);
    var parameters = CommandParameters.getOverrideMethodParameters(command);

    var overrideMethod =
        CommandMethods.getOverrideMethod(command)
            .addAnnotation(Override.class)
            .addModifiers(Modifier.FINAL)
            .returns(returnType)
            .addParameters(parameters);

    var methodParams = parameters.stream().map(ParameterSpec::name).map(CodeBlock::of);
    var argParams = commandArgs.stream().map(ProcessorCommandArg::toStatement);

    var params = Stream.concat(methodParams, argParams).toList();

    var callAbstractMethod = CodeBlock.builder();

    if (!returnType.equals(TypeName.VOID)) callAbstractMethod.add("return ");

    callAbstractMethod.add("executeCommand(").add(CodeBlock.join(params, ", ")).add(");");

    overrideMethod.addCode(callAbstractMethod.build());

    var abstractMethod =
        CommandMethods.getOverrideMethod(command)
            .setName("executeCommand")
            .addModifiers(Modifier.ABSTRACT)
            .returns(returnType)
            .addParameters(parameters)
            .addParameters(
                commandArgs.stream()
                    .map(arg -> arg.toParameterSpec(processingEnvironment))
                    .toList());

    sourceFileWriter.addMethod(overrideMethod.build());
    sourceFileWriter.addMethod(abstractMethod.build());
  }

  private void appendConstructor() {
    sourceFileWriter
        .getConstructor()
        .addStatement("super($S, $S)", command.name(), command.description())
        .build();
  }

  private void appendFields(@NotNull ProcessingEnvironment processingEnvironment) {
    commandArgs.stream()
        .map(arg -> arg.toFieldSpec(processingEnvironment))
        .forEach(sourceFileWriter::addField);
  }

  private void appendSubCommands() {
    var builder =
        MethodSpec.methodBuilder("onInitialization")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override.class);

    for (var subCommand : command.subCommands()) {
      var typeMirror = ProcessorUtils.getTypeMirror(subCommand::value);

      if (subCommand.singletonIdentifier().isBlank())
        builder.addStatement(
            "$T.ofPlugin($T.class).get().onSuccess(this::addSubCommand)",
            LAZY_CLASS_NAME,
            typeMirror);
      else
        builder.addStatement(
            "$T.ofPlugin($T.class, $S).get().onSuccess(this::addSubCommand)",
            LAZY_CLASS_NAME,
            typeMirror,
            subCommand.singletonIdentifier());
    }

    sourceFileWriter.addMethod(builder.build());
  }

  private ClassName getSuperclass(@NotNull Command command) {
    return switch (command) {
      case Command c when c.target() == Command.CommandTarget.PLAYER && c.async() ->
          ABSTRACT_ASYNC_PLAYER_COMMAND_CLASS;
      case Command c when c.target() == Command.CommandTarget.PLAYER ->
          ABSTRACT_PLAYER_COMMAND_CLASS;
      case Command c when c.target() == Command.CommandTarget.WORLD && c.async() ->
          ABSTRACT_ASYNC_WORLD_COMMAND_CLASS;
      case Command c when c.target() == Command.CommandTarget.WORLD -> ABSTRACT_WORLD_COMMAND_CLASS;
      case Command c when c.async() -> ABSTRACT_ASYNC_COMMAND_CLASS;
      default -> ABSTRACT_COMMAND_CLASS;
    };
  }
}
