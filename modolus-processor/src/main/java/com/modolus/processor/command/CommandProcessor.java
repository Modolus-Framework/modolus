package com.modolus.processor.command;

import com.modolus.annotations.command.Arg;
import com.modolus.annotations.command.Args;
import com.modolus.annotations.command.Command;
import com.modolus.processor.Processor;
import com.modolus.processor.command.args.ProcessorCommandArgConverter;
import com.modolus.util.result.Result;
import lombok.RequiredArgsConstructor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class CommandProcessor implements Processor {


    private final Map<String, Integer> variableCounter = new HashMap<>();
    private final ProcessingEnvironment processingEnv;

    @Override
    public void processSingle(Element annotated, String className) throws IOException {
        variableCounter.clear();
        var command = annotated.getAnnotation(Command.class);

        Arg[] annotatedArgList = new Arg[0];
        var annotatedArgs = annotated.getAnnotation(Args.class);
        if (annotatedArgs != null) annotatedArgList = annotatedArgs.value();

        var args = Stream.of(annotatedArgList)
                .map(arg -> ProcessorCommandArgConverter.fromArg(variableCounter, arg))
                .peek(result -> result.onFailure(processingEnv.getMessager()::printWarning))
                .filter(Result::isSuccess)
                .map(Result::get)
                .toList();


        new CommandFileWriter(className, args, command).write(processingEnv);
    }


}
