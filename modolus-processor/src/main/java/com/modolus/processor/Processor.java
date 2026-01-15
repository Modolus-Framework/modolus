package com.modolus.processor;

import javax.lang.model.element.Element;
import java.io.IOException;

public interface Processor {

    void processSingle(Element annotated, String className) throws IOException;

}
