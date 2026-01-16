package com.modolus.processor.command;

import com.modolus.annotations.command.Arg;
import com.modolus.annotations.command.Args;
import com.modolus.annotations.command.Command;
import com.modolus.processor.Processor;
import com.modolus.processor.ProcessorUtils;
import com.modolus.processor.SharedContext;
import com.modolus.processor.SourceFileWriter;
import com.modolus.processor.command.args.ProcessorCommandArgConverter;
import com.modolus.util.result.Result;
import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public final class CommandProcessor extends Processor {

    private final Map<String, Integer> variableCounter = new HashMap<>();

    public CommandProcessor(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    @Override
    public void processSingle(@NotNull Element annotated,
                              String className,
                              Map<String, SourceFileWriter> writers,
                              @NotNull SharedContext sharedContext) {
        var sourceFileWriter = ProcessorUtils.ensureBaseFileExists(writers, className, annotated);

        variableCounter.clear();
        var command = annotated.getAnnotation(Command.class);
        assert command != null;

        Arg[] annotatedArgList = new Arg[0];
        var annotatedArgs = annotated.getAnnotation(Args.class);
        if (annotatedArgs != null) annotatedArgList = annotatedArgs.value();

        var args = Stream.of(annotatedArgList)
                .map(arg -> ProcessorCommandArgConverter.fromArg(variableCounter, arg))
                .peek(result -> result.onFailure(processingEnv.getMessager()::printWarning))
                .filter(Result::isSuccess)
                .map(Result::get)
                .toList();


        new CommandFileWriter(sourceFileWriter, args, command).apply(processingEnv);
    }


}
