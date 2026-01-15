package com.modolus.command.processor;

import com.palantir.javapoet.*;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.TypeMirror;

interface ProcessorCommandArg {

    FieldSpec toFieldSpec(ProcessingEnvironment processingEnvironment);

    ParameterSpec toParameterSpec(ProcessingEnvironment processingEnvironment);

    CodeBlock toStatement();

    default String getSystemPath() {
        return "com.hypixel.hytale.server.core.command.system.arguments.system";
    }

    default String getTypesPath() {
        return "com.hypixel.hytale.server.core.command.system.arguments.types";
    }

    default ParameterizedTypeName getBaseType(String name, TypeMirror type) {
        return ParameterizedTypeName.get(ClassName.get(getSystemPath(), name), TypeName.get(type));
    }

    default TypeName getArgType() {
        return ClassName.get(getTypesPath(), "ArgTypes");
    }

}
