package dev.modolus.processor;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

public class ProcessorException extends Exception {

    @Getter
    private final transient Element element;

    public ProcessorException(Element element, Throwable cause) {
        this.element = element;
        super(cause);
    }

    public void print(@NotNull ProcessingEnvironment processingEnvironment) {
        processingEnvironment.getMessager()
                .printError("An error occured while processing: " + getCause().getMessage(), this.element);
    }

}
