package com.modolus.processor.event;

import com.modolus.annotations.event.EventHandler;
import com.modolus.processor.Processor;
import com.modolus.processor.ProcessorUtils;
import com.modolus.processor.SharedContext;
import com.modolus.processor.SourceFileWriter;
import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.MethodSpec;
import com.palantir.javapoet.ParameterSpec;
import com.palantir.javapoet.TypeName;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class EventProcessor extends Processor {

    private static final ClassName JAVA_PLUGIN_CLASS_NAME = ClassName.get("com.hypixel.hytale.server.core.plugin", "JavaPlugin");
    private static final ClassName LAZY_CLASS_NAME = ClassName.get("com.modolus.util.singleton", "Lazy");
    private static final ClassName EVENT_LISTENER_CLASS_NAME = ClassName.get("com.modolus.core.event", "EventListener");

    private final TypeMirror objectType = processingEnv.getElementUtils().getTypeElement("java.lang.Object").asType();
    private final TypeMirror iBaseEventType = processingEnv.getElementUtils().getTypeElement("com.hypixel.hytale.event.IBaseEvent").asType();
    private final TypeMirror iAsyncEventType = processingEnv.getElementUtils().getTypeElement("com.hypixel.hytale.event.IAsyncEvent").asType();
    private final TypeMirror voidType = processingEnv.getElementUtils().getTypeElement("java.lang.Void").asType();

    private final MethodSpec.Builder initializationBuilder = MethodSpec
            .methodBuilder("onInitialization")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .addStatement("var plugin = $T.ofPlugin($T.class).getOrThrow();", LAZY_CLASS_NAME, JAVA_PLUGIN_CLASS_NAME);

    public EventProcessor(ProcessingEnvironment processingEnv) {
        super(processingEnv);

    }

    @Override
    public void processSingle(@NotNull Element annotated,
                              String className,
                              Map<String, SourceFileWriter> writers,
                              SharedContext sharedContext) {
        var sourceFileWriter = ProcessorUtils.ensureBaseFileExists(writers, className, annotated);

        sourceFileWriter.getClassBuilder()
                .addModifiers(Modifier.ABSTRACT)
                .addSuperinterface(EVENT_LISTENER_CLASS_NAME);

        annotated.getEnclosedElements()
                .stream()
                .filter(this::isEventHandler)
                .map(ExecutableElement.class::cast)
                .forEach(method -> {
                    addAbstractMethod(method, sourceFileWriter);
                    addInitializationMethod(method);
                });

        sourceFileWriter.addMethod(initializationBuilder.build());
    }

    private boolean isEventHandler(@NotNull Element enclosed) {
        if (enclosed.getKind() != ElementKind.METHOD) return false;
        var method = (ExecutableElement) enclosed;
        if (enclosed.getAnnotation(EventHandler.class) == null) return false;

        if (method.getParameters().size() != 1) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Event handler method must have exactly one parameter");
            return false;
        }

        var param = method.getParameters().getFirst();

        var superTypes = getAllSuperclasses(param.asType());

        return superTypes.stream()
                .anyMatch(type -> processingEnv.getTypeUtils().isSameType(
                        processingEnv.getTypeUtils().erasure(type),
                        processingEnv.getTypeUtils().erasure(iBaseEventType)));
    }

    private void addAbstractMethod(@NotNull ExecutableElement method, @NotNull SourceFileWriter writer) {
        var abstractMethod = MethodSpec.methodBuilder(method.getSimpleName().toString())
                .addModifiers(Modifier.PROTECTED, Modifier.ABSTRACT)
                .returns(TypeName.get(method.getReturnType()))
                .addParameters(method.getParameters().stream()
                        .map(this::toParameterSpec)
                        .toList())
                .build();

        writer.addMethod(abstractMethod);
    }

    private void addInitializationMethod(@NotNull ExecutableElement method) {
        var async = isAsync(method) ? "Async" : "";
        var paramType = method.getParameters().getFirst().asType();
        if (isGlobal(method))
            initializationBuilder.addStatement("plugin.getEventRegistry().register$LGlobal($T.class, this::$L)", async, paramType, method.getSimpleName());
        else
            initializationBuilder.addStatement("plugin.getEventRegistry().register$L($T.class, this::$L)", async, paramType, method.getSimpleName());

    }

    private boolean isAsync(@NotNull ExecutableElement method) {
        return getAllSuperclasses(method.getParameters().getFirst().asType()).stream()
                .anyMatch(type -> processingEnv.getTypeUtils().isSameType(
                        processingEnv.getTypeUtils().erasure(type),
                        processingEnv.getTypeUtils().erasure(iAsyncEventType)));
    }

    private boolean isGlobal(@NotNull ExecutableElement method) {
        return getAllSuperclasses(method.getParameters().getFirst().asType()).stream()
                .filter(type -> processingEnv.getTypeUtils().isSameType(
                        processingEnv.getTypeUtils().erasure(type),
                        processingEnv.getTypeUtils().erasure(iBaseEventType)))
                .findAny()
                .filter(typeMirror -> typeMirror.getKind() == TypeKind.DECLARED)
                .map(DeclaredType.class::cast)
                .map(DeclaredType::getTypeArguments)
                .filter(Predicate.not(List::isEmpty))
                .map(List::getFirst)
                .filter(type -> processingEnv.getTypeUtils().isSameType(type, voidType))
                .isPresent();
    }

    @Contract("_ -> new")
    private @NotNull ParameterSpec toParameterSpec(@NotNull VariableElement variableElement) {
        return ParameterSpec.builder(TypeName.get(variableElement.asType()), variableElement.getSimpleName().toString())
                .build();
    }

    private @NotNull @Unmodifiable List<TypeMirror> getAllSuperclasses(TypeMirror typeMirror) {
        var superTypes = processingEnv.getTypeUtils().directSupertypes(typeMirror);

        return superTypes.stream()
                .filter(Predicate.not(type -> processingEnv.getTypeUtils().isSameType(type, objectType)))
                .flatMap(type -> Stream.concat(Stream.of(type), getAllSuperclasses(type).stream()))
                .distinct()
                .toList();
    }

}
