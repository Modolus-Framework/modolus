/*
 * Copyright (C) 2026 Modolus-Framework
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package dev.modolus.processor;

import java.util.Map;
import java.util.function.Supplier;
import javax.lang.model.element.Element;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

  public static @NotNull String getPrefixedSimpleClassName(
      @NotNull String prefix, @NotNull String className) {
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

  public static @NotNull SourceFileWriter ensureBaseFileExists(
      @NotNull Map<String, SourceFileWriter> writers,
      @NotNull String className,
      @NotNull Element element) {
    var packageName = ProcessorUtils.getPackageName(className);
    var outClassName = ProcessorUtils.getPrefixedSimpleClassName("Abstract", className);

    writers.computeIfAbsent(outClassName, s -> new SourceFileWriter(element, packageName, s));

    return writers.get(outClassName);
  }
}
