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

import dev.modolus.annotations.command.Arg;
import dev.modolus.annotations.command.Args;
import dev.modolus.annotations.command.Command;
import dev.modolus.processor.Processor;
import dev.modolus.processor.ProcessorUtils;
import dev.modolus.processor.SharedContext;
import dev.modolus.processor.SourceFileWriter;
import dev.modolus.processor.command.args.ProcessorCommandArgConverter;
import dev.modolus.util.result.Result;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import org.jetbrains.annotations.NotNull;

public final class CommandProcessor extends Processor {

  private final Map<String, Integer> variableCounter = new HashMap<>();

  public CommandProcessor(ProcessingEnvironment processingEnv) {
    super(processingEnv);
  }

  @Override
  public void processSingle(
      @NotNull Element annotated,
      String className,
      Map<String, SourceFileWriter> writers,
      @NotNull SharedContext sharedContext) {
    var sourceFileWriter = ProcessorUtils.ensureBaseFileExists(writers, className, annotated);

    variableCounter.clear();
    var command = annotated.getAnnotation(Command.class);
    assert command != null;

    Arg[] annotatedArgList = new Arg[0];
    var annotatedArgs = annotated.getAnnotation(Args.class);

    if (annotatedArgs == null) {
      var annotatedArg = annotated.getAnnotation(Arg.class);
      if (annotatedArg != null) annotatedArgList = new Arg[] {annotatedArg};
    } else {
      annotatedArgList = annotatedArgs.value();
    }

    var args =
        Stream.of(annotatedArgList)
            .map(arg -> ProcessorCommandArgConverter.fromArg(variableCounter, arg))
            .peek(
                result ->
                    result.onFailure(
                        error ->
                            processingEnv
                                .getMessager()
                                .printWarning(error.getFullMessage(), annotated)))
            .filter(Result::isSuccess)
            .map(Result::get)
            .toList();

    new CommandFileWriter(sourceFileWriter, args, command).apply(processingEnv);
  }
}
