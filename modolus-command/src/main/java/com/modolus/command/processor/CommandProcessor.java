package com.modolus.command.processor;

import com.modolus.command.annotations.Arg;
import com.modolus.command.annotations.Args;
import com.modolus.command.annotations.Command;
import com.palantir.javapoet.*;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class CommandProcessor extends AbstractProcessor {

    private static final String BASE_COMMANDS_PACKAGE = "com.hypixel.hytale.server.core.command.system.basecommands";
    private final Map<String, Integer> variableCounter = new HashMap<>();

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_25;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(Command.class.getCanonicalName());
    }

    @SneakyThrows
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (var annotated : roundEnv.getElementsAnnotatedWith(Command.class)) {
            variableCounter.clear();
            String className = ((TypeElement) annotated).getQualifiedName().toString();

            var command = annotated.getAnnotation(Command.class);
            var args = Stream.of(annotated.getAnnotation(Args.class).value())
                    .map(this::toInternalArg)
                    .filter(Objects::nonNull)
                    .toList();


            writeCommandFile(className, args, command);
        }
        return false;
    }

    private ProcessorCommandArg toInternalArg(Arg arg) {
        var name = arg.name();

        if (variableCounter.containsKey(name)) {
            var tmpName = name;
            name += variableCounter.get(name).toString();
            variableCounter.put(tmpName, variableCounter.get(tmpName) + 1);
        } else {
            variableCounter.put(name, 1);
        }


        return switch (arg) {
            case Arg a when !a.defaultArg().ignore() -> ProcessorCommandDefaultArg.builder()
                    .name(name)
                    .description(a.description())
                    .type(getTypeMirror(a.defaultArg()::type))
                    .argTypeName(a.defaultArg().argTypeName())
                    .defaultValue(a.defaultArg().defaultValue())
                    .defaultValueDescription(a.defaultArg().defaultValueDescription())
                    .build();
            case Arg a when !a.optionalArg().ignore() -> ProcessorCommandOptionalArg.builder()
                    .name(name)
                    .description(a.description())
                    .type(getTypeMirror(a.optionalArg()::type))
                    .argTypeName(a.optionalArg().argTypeName())
                    .build();
            case Arg a when !a.requiredArg().ignore() -> ProcessorCommandRequiredArg.builder()
                    .name(name)
                    .description(a.description())
                    .type(getTypeMirror(a.requiredArg()::type))
                    .argTypeName(a.requiredArg().argTypeName())
                    .build();
            case Arg a when !a.flagArg().ignore() -> ProcessorCommandFlagArg.builder()
                    .name(name)
                    .description(a.description())
                    .build();
            default -> {
                processingEnv.getMessager().printWarning("No arg type set for " + arg.name());
                yield null;
            }
        };
    }

    private void writeCommandFile(String className, List<ProcessorCommandArg> processorCommandArgs, Command command) throws IOException {
        String packageName = "";
        int lastDot = className.lastIndexOf('.');
        if (lastDot > 0) {
            packageName = className.substring(0, lastDot);
        }

        String builderClassName = "Abstract" + className;
        String builderSimpleClassName = builderClassName
                .substring(lastDot + 1);

        var classBuilder = TypeSpec.classBuilder(builderSimpleClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .superclass(getSuperclass(command));

        processorCommandArgs.stream()
                .map(arg -> arg.toFieldSpec(processingEnv))
                .forEach(classBuilder::addField);

        var constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PROTECTED)
                .addStatement("super($S, $S)", command.name(), command.description())
                .build();

        var returnType = getOverrideMethodReturnType(command);
        var parameters = getOverrideMethodParameters(command);

        var overrideMethod = getOverrideMethod(command)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.FINAL)
                .returns(returnType)
                .addParameters(parameters);

        var methodParams = parameters.stream()
                .map(ParameterSpec::name)
                .map(CodeBlock::of);
        var argParams = processorCommandArgs.stream()
                .map(ProcessorCommandArg::toStatement);

        var params = Stream.concat(methodParams, argParams)
                .toList();

        var callAbstractMethod = CodeBlock.builder()
                .add("return execute(")
                .add(CodeBlock.join(params, ", "))
                .add(");");

        overrideMethod.addCode(callAbstractMethod.build());


        var abstractMethod = getOverrideMethod(command)
                .setName("execute")
                .addModifiers(Modifier.ABSTRACT)
                .returns(returnType)
                .addParameters(parameters)
                .addParameters(processorCommandArgs.stream().map(arg -> arg.toParameterSpec(processingEnv)).toList());

        classBuilder.addMethod(constructorBuilder);
        classBuilder.addMethod(overrideMethod.build());
        classBuilder.addMethod(abstractMethod.build());

        JavaFile clazzFile = JavaFile.builder(packageName, classBuilder.build())
                .addFileComment("Generated by Modolus Command Processor")
                .build();

        clazzFile.writeTo(processingEnv.getFiler());
    }

    private ClassName getSuperclass(Command command) {
        return switch (command) {
            case Command c when c.target() == Command.CommandTarget.PLAYER && c.async() ->
                    ClassName.get(BASE_COMMANDS_PACKAGE, "AbstractAsyncPlayerCommand");
            case Command c when c.target() == Command.CommandTarget.PLAYER ->
                    ClassName.get(BASE_COMMANDS_PACKAGE, "AbstractPlayerCommand");
            case Command c when c.target() == Command.CommandTarget.WORLD && c.async() ->
                    ClassName.get(BASE_COMMANDS_PACKAGE, "AbstractAsyncWorldCommand");
            case Command c when c.target() == Command.CommandTarget.WORLD ->
                    ClassName.get(BASE_COMMANDS_PACKAGE, "AbstractWorldCommand");
            case Command c when c.async() -> ClassName.get(BASE_COMMANDS_PACKAGE, "AbstractAsyncCommand");
            default -> ClassName.get("com.hypixel.hytale.server.core.command.system", "AbstractCommand");
        };
    }

    private MethodSpec.Builder getOverrideMethod(Command command) {
        return switch (command) {
            case Command c when c.target() == Command.CommandTarget.PLAYER && c.async() -> getAsyncCommandMethod();
            case Command c when c.target() == Command.CommandTarget.PLAYER -> getSyncCommandMethod();
            case Command c when c.target() == Command.CommandTarget.WORLD && c.async() -> getAsyncCommandMethod();
            case Command c when c.target() == Command.CommandTarget.WORLD -> getSyncCommandMethod();
            case Command c when c.async() -> getAsyncCommandMethod();
            default -> getSyncCommandMethod()
                    .returns(ParameterizedTypeName.get(CompletableFuture.class, Void.class).annotated(AnnotationSpec.builder(Nullable.class).build()));
        };
    }

    private List<ParameterSpec> getOverrideMethodParameters(Command command) {
        return switch (command) {
            case Command c when c.target() == Command.CommandTarget.PLAYER && c.async() ->
                    getAbstractPlayerCommandParameters();
            case Command c when c.target() == Command.CommandTarget.PLAYER -> getAbstractPlayerCommandParameters();
            case Command c when c.target() == Command.CommandTarget.WORLD && c.async() ->
                    getAbstractAsyncWorldCommandParameters();
            case Command c when c.target() == Command.CommandTarget.WORLD -> getAbstractWorldCommandParameters();
            case Command c when c.async() -> getAbstractAsyncCommandParameters();
            default -> getAbstractCommandParameters();
        };
    }

    private TypeName getOverrideMethodReturnType(Command command) {
        return switch (command) {
            case Command c when c.target() == Command.CommandTarget.PLAYER && c.async() ->
                    ParameterizedTypeName.get(CompletableFuture.class, Void.class).annotated(AnnotationSpec.builder(NotNull.class).build());
            case Command c when c.target() == Command.CommandTarget.PLAYER -> TypeName.VOID;
            case Command c when c.target() == Command.CommandTarget.WORLD && c.async() ->
                    ParameterizedTypeName.get(CompletableFuture.class, Void.class).annotated(AnnotationSpec.builder(NotNull.class).build());
            case Command c when c.target() == Command.CommandTarget.WORLD -> TypeName.VOID;
            case Command c when c.async() ->
                    ParameterizedTypeName.get(CompletableFuture.class, Void.class).annotated(AnnotationSpec.builder(NotNull.class).build());
            default ->
                    ParameterizedTypeName.get(CompletableFuture.class, Void.class).annotated(AnnotationSpec.builder(Nullable.class).build());
        };
    }

    private MethodSpec.Builder getAsyncCommandMethod() {
        return MethodSpec.methodBuilder("executeAsync")
                .addModifiers(Modifier.PROTECTED);
    }

    private MethodSpec.Builder getSyncCommandMethod() {
        return MethodSpec.methodBuilder("execute")
                .addModifiers(Modifier.PROTECTED);
    }

    private List<ParameterSpec> getAbstractPlayerCommandParameters() {
        return List.of(
                getCommandContextParameter(),
                getEntityStoreParameter(),
                getEntityStoreRefParameter(),
                getPlayerRefParameter(),
                getWorldParameter()
        );
    }

    private List<ParameterSpec> getAbstractAsyncWorldCommandParameters() {
        return List.of(
                getCommandContextParameter(),
                getWorldParameter()
        );
    }

    private List<ParameterSpec> getAbstractWorldCommandParameters() {
        return List.of(
                getCommandContextParameter(),
                getWorldParameter(),
                getEntityStoreParameter()
        );
    }

    private List<ParameterSpec> getAbstractAsyncCommandParameters() {
        return List.of(
                getCommandContextParameter()
        );
    }

    private List<ParameterSpec> getAbstractCommandParameters() {
        return List.of(getCommandContextParameter());
    }

    private ParameterSpec getCommandContextParameter() {
        var type = ClassName.get("com.hypixel.hytale.server.core.command.system", "CommandContext");
        return ParameterSpec.builder(type, "commandContext")
                .addAnnotation(NotNull.class)
                .build();
    }

    private ParameterSpec getEntityStoreParameter() {
        var type = ParameterizedTypeName.get(ClassName.get("com.hypixel.hytale.server.core.universe.world.storage", "EntityStore"), getEntityStoreType());
        return ParameterSpec.builder(type, "store")
                .addAnnotation(NotNull.class)
                .build();
    }

    private ParameterSpec getWorldParameter() {
        var type = ClassName.get("com.hypixel.hytale.server.core.universe.world", "World");
        return ParameterSpec.builder(type, "world")
                .addAnnotation(NotNull.class)
                .build();
    }

    private ParameterSpec getEntityStoreRefParameter() {
        var type = ParameterizedTypeName.get(ClassName.get("com.hypixel.hytale.server.core.universe", "Ref"), getEntityStoreType());
        return ParameterSpec.builder(type, "ref")
                .addAnnotation(NotNull.class)
                .build();
    }

    private ParameterSpec getPlayerRefParameter() {
        var type = ClassName.get("com.hypixel.hytale.server.core.universe", "PlayerRef");
        return ParameterSpec.builder(type, "playerRef")
                .addAnnotation(NotNull.class)
                .build();
    }

    private ClassName getEntityStoreType() {
        return ClassName.get("com.hypixel.hytale.server.core.universe.world.storage", "EntityStore");
    }

    private TypeMirror getTypeMirror(Supplier<Class<?>> call) {
        try {
            call.get();
        } catch (MirroredTypeException e) {
            return e.getTypeMirror();
        }
        return null;
    }

}
