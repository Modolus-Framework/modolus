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

package dev.modolus.processor.command.args;

import static dev.modolus.processor.ProcessorUtils.getTypeMirror;

import dev.modolus.annotations.command.Arg;
import dev.modolus.annotations.command.DefaultArg;
import dev.modolus.annotations.command.OptionalArg;
import dev.modolus.annotations.command.RequiredArg;
import dev.modolus.processor.ProcessorError;
import dev.modolus.util.result.Result;
import java.util.Map;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

@UtilityClass
public class ProcessorCommandArgConverter {

  public static @NotNull @Unmodifiable Result<
          @Unmodifiable @NotNull ProcessorCommandArg, ProcessorError>
      fromArg(@NotNull Map<String, Integer> variableCounter, @NotNull Arg arg) {
    var name = getVariableName(variableCounter, arg);

    return switch (arg) {
      case Arg a when !a.defaultArg().ignore() ->
          Result.success(createDefaultArg(name, a.description(), a.defaultArg()));
      case Arg a when !a.optionalArg().ignore() ->
          Result.success(createOptionalArg(name, a.description(), a.optionalArg()));
      case Arg a when !a.requiredArg().ignore() ->
          Result.success(createRequiredArg(name, a.description(), a.requiredArg()));
      case Arg a when !a.flagArg().ignore() -> Result.success(createFlagArg(name, a.description()));
      default -> Result.failure(ProcessorError.NO_COMMAND_ARG_TYPE_SET.toError(arg.name()));
    };
  }

  private static @NotNull String getVariableName(
      @NotNull Map<String, Integer> variableCounter, @NotNull Arg arg) {
    var name = arg.name();

    if (variableCounter.containsKey(name)) {
      var tmpName = name;
      name += variableCounter.get(name).toString();
      variableCounter.put(tmpName, variableCounter.get(tmpName) + 1);
    } else {
      variableCounter.put(name, 1);
    }

    return name;
  }

  private static @NotNull @Unmodifiable ProcessorCommandArg createDefaultArg(
      @NotNull String name, @NotNull String description, @NotNull DefaultArg defaultArg) {
    return ProcessorCommandDefaultArg.builder()
        .name(name)
        .description(description)
        .type(getTypeMirror(defaultArg::type))
        .argTypeName(defaultArg.argTypeName())
        .defaultValue(defaultArg.defaultValue())
        .defaultValueDescription(defaultArg.defaultValueDescription())
        .build();
  }

  private static @NotNull @Unmodifiable ProcessorCommandArg createOptionalArg(
      @NotNull String name, @NotNull String description, @NotNull OptionalArg optionalArg) {
    return ProcessorCommandOptionalArg.builder()
        .name(name)
        .description(description)
        .type(getTypeMirror(optionalArg::type))
        .argTypeName(optionalArg.argTypeName())
        .build();
  }

  private static @NotNull @Unmodifiable ProcessorCommandArg createRequiredArg(
      @NotNull String name, @NotNull String description, @NotNull RequiredArg requiredArg) {
    return ProcessorCommandRequiredArg.builder()
        .name(name)
        .description(description)
        .type(getTypeMirror(requiredArg::type))
        .argTypeName(requiredArg.argTypeName())
        .build();
  }

  private static @NotNull @Unmodifiable ProcessorCommandArg createFlagArg(
      @NotNull String name, @NotNull String description) {
    return ProcessorCommandFlagArg.builder().name(name).description(description).build();
  }
}
