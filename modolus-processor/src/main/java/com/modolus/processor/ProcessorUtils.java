package com.modolus.processor;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.lang.model.element.Element;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import java.util.Map;
import java.util.function.Supplier;

@UtilityClass
public class ProcessorUtils {

    public static String getPackageName(@NotNull String className) {
        String packageName = "";
        int lastDot = className.lastIndexOf('.');
        if (lastDot > 0) {
            packageName = className.substring(0, lastDot);
        }
        return packageName;
    }

    public static @NotNull String getSimpleClassName(@NotNull String className) {
        return className.substring(className.lastIndexOf('.') + 1);
    }

    public static @NotNull String getPrefixedSimpleClassName(@NotNull String prefix, @NotNull String className) {
        return prefix + getSimpleClassName(className);
    }

    public static @Nullable TypeMirror getTypeMirror(@NotNull Supplier<Class<?>> call) {
        try {
            call.get();
        } catch (MirroredTypeException e) {
            return e.getTypeMirror();
        }
        return null;
    }

    public static @NotNull SourceFileWriter ensureBaseFileExists(@NotNull Map<String, SourceFileWriter> writers,
                                                                 @NotNull String className,
                                                                 @NotNull Element element) {
        var packageName = ProcessorUtils.getPackageName(className);
        var outClassName = ProcessorUtils.getPrefixedSimpleClassName("Abstract", className);

        writers.computeIfAbsent(outClassName, s -> new SourceFileWriter(element, packageName, s));

        return writers.get(outClassName);
    }

}
