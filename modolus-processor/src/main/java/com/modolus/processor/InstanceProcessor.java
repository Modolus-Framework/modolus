package com.modolus.processor;

import com.modolus.processor.command.CommandProcessor;
import lombok.SneakyThrows;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Map;
import java.util.Set;

@SupportedAnnotationTypes("com.modolus.annotations.command.Command")
@SupportedSourceVersion(SourceVersion.RELEASE_25)
public class InstanceProcessor extends AbstractProcessor {

    @SneakyThrows
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<String, Processor> processors = Map.of(
                "com.modolus.annotations.command.Command", new CommandProcessor(processingEnv)
        );

        for (var annotation : annotations) {
            var processor = processors.get(annotation.getQualifiedName().toString());
            if (processor == null) continue;

            for (var annotated : roundEnv.getElementsAnnotatedWith(annotation)) {
                String className = ((TypeElement) annotated).getQualifiedName().toString();
                processor.processSingle(annotated, className);
            }
        }
        return false;
    }

}
