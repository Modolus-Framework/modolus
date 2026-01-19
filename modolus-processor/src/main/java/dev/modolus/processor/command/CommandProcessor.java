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

        if (annotatedArgs == null) {
            var annotatedArg = annotated.getAnnotation(Arg.class);
            if (annotatedArg != null) annotatedArgList = new Arg[]{annotatedArg};
        } else {
            annotatedArgList = annotatedArgs.value();
        }

        var args = Stream.of(annotatedArgList)
                .map(arg -> ProcessorCommandArgConverter.fromArg(variableCounter, arg))
                .peek(result -> result.onFailure(processingEnv.getMessager()::printWarning))
                .filter(Result::isSuccess)
                .map(Result::get)
                .toList();


        new CommandFileWriter(sourceFileWriter, args, command).apply(processingEnv);
    }


}
