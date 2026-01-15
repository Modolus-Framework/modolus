package com.modolus.processor;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
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

    public static String getSimpleClassName(@NotNull String className) {
        return className.substring(className.lastIndexOf('.') + 1);
    }

    public static @Nullable TypeMirror getTypeMirror(@NotNull Supplier<Class<?>> call) {
        try {
            call.get();
        } catch (MirroredTypeException e) {
            return e.getTypeMirror();
        }
        return null;
    }

}
