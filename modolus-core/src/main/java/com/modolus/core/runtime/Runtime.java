package com.modolus.core.runtime;

import com.modolus.util.result.Result;
import com.modolus.util.singleton.Singletons;
import lombok.experimental.UtilityClass;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

@UtilityClass
public class Runtime {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static Result<Void, RuntimeError> initializeRuntime() {
        var classLoaderResult = getDefaultClassLoader();
        if (classLoaderResult.isFailure()) {
            return Result.failure(classLoaderResult.getError());
        }

        var classLoader = classLoaderResult.orElseThrow();
        var filesResult = Result.ofException(() -> classLoader.getResources("runtime-classes.json"), IOException.class)
                .mapError(error -> RuntimeError.FAILED_TO_LOAD_RESOURCES);

        if (filesResult.isFailure()) {
            return Result.failure(filesResult.getError());
        }

        var files = filesResult.orElseThrow();
        assert files != null;

        var classes = collectClassesToInitialize(files);

        if (classes.isFailure()) {
            return Result.failure(RuntimeError.FAILED_TO_READ_CLASSES);
        }

        classes.orElseThrow().parallelStream()
                .map(file -> Result.ofException(() -> Class.forName(file), ClassNotFoundException.class))
                .map(result -> result.mapError(err -> RuntimeError.FAILED_TO_LOAD_CLASS))
                .map(result -> result.mapException(Runtime::constructClass, err -> RuntimeError.FAILED_TO_CREATE_CLASS, ReflectiveOperationException.class))
                .forEach(result -> result.onFailure(System.out::println));

        Singletons.initializeSingletons();

        return Result.success();
    }

    private void constructClass(Class<?> clazz) throws ReflectiveOperationException {
        clazz.getConstructor().newInstance();
    }

    private Result<Set<String>, IOException> collectClassesToInitialize(Enumeration<URL> files) {
        return Result.ofException(() -> {
            Set<String> classesToInitialize = new HashSet<>();

            while (files.hasMoreElements()) {
                var fileUrl = files.nextElement();

                var classes = OBJECT_MAPPER.readValue(fileUrl.openStream(), new TypeReference<Set<String>>() {
                });
                classesToInitialize.addAll(classes);
            }

            return classesToInitialize;
        }, IOException.class);
    }

    private static Result<ClassLoader, RuntimeError> getDefaultClassLoader() {
        return Result.ofNullableWithException(() -> Thread.currentThread().getContextClassLoader(), Exception.class)
                .tryRecoverNullable(error -> Runtime.class.getClassLoader(), Exception.class)
                .recoverNullable(error -> ClassLoader.getSystemClassLoader())
                .tryRecoverNullable(error -> ClassLoader.getSystemClassLoader(), Exception.class)
                .mapError(genericError -> RuntimeError.NO_AVAILABLE_CLASS_LOADER);
    }

}
