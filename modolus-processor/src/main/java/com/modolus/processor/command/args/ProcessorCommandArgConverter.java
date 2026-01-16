package com.modolus.processor.command.args;

import com.modolus.annotations.command.Arg;
import com.modolus.annotations.command.DefaultArg;
import com.modolus.annotations.command.OptionalArg;
import com.modolus.annotations.command.RequiredArg;
import com.modolus.util.result.Result;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;

import static com.modolus.processor.ProcessorUtils.getTypeMirror;

@UtilityClass
public class ProcessorCommandArgConverter {

    public static @NotNull @Unmodifiable Result<@Unmodifiable @NotNull ProcessorCommandArg, String> fromArg(
            @NotNull Map<String, Integer> variableCounter, @NotNull Arg arg) {
        var name = getVariableName(variableCounter, arg);

        return switch (arg) {
            case Arg a when !a.defaultArg().ignore() ->
                    Result.success(createDefaultArg(name, a.description(), a.defaultArg()));
            case Arg a when !a.optionalArg().ignore() ->
                    Result.success(createOptionalArg(name, a.description(), a.optionalArg()));
            case Arg a when !a.requiredArg().ignore() ->
                    Result.success(createRequiredArg(name, a.description(), a.requiredArg()));
            case Arg a when !a.flagArg().ignore() -> Result.success(createFlagArg(name, a.description()));
            default -> Result.failure("No arg type set for " + arg.name());
        };
    }

    private static @NotNull String getVariableName(@NotNull Map<String, Integer> variableCounter, @NotNull Arg arg) {
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

    private static @NotNull @Unmodifiable ProcessorCommandArg createDefaultArg(@NotNull String name,
                                                                               @NotNull String description,
                                                                               @NotNull DefaultArg defaultArg) {
        return ProcessorCommandDefaultArg.builder()
                .name(name)
                .description(description)
                .type(getTypeMirror(defaultArg::type))
                .argTypeName(defaultArg.argTypeName())
                .defaultValue(defaultArg.defaultValue())
                .defaultValueDescription(defaultArg.defaultValueDescription())
                .build();
    }

    private static @NotNull @Unmodifiable ProcessorCommandArg createOptionalArg(@NotNull String name,
                                                                                @NotNull String description,
                                                                                @NotNull OptionalArg optionalArg) {
        return ProcessorCommandOptionalArg.builder()
                .name(name)
                .description(description)
                .type(getTypeMirror(optionalArg::type))
                .argTypeName(optionalArg.argTypeName())
                .build();
    }

    private static @NotNull @Unmodifiable ProcessorCommandArg createRequiredArg(@NotNull String name,
                                                                                @NotNull String description,
                                                                                @NotNull RequiredArg requiredArg) {
        return ProcessorCommandRequiredArg.builder()
                .name(name)
                .description(description)
                .type(getTypeMirror(requiredArg::type))
                .argTypeName(requiredArg.argTypeName())
                .build();
    }

    private static @NotNull @Unmodifiable ProcessorCommandArg createFlagArg(@NotNull String name,
                                                                            @NotNull String description) {
        return ProcessorCommandFlagArg.builder()
                .name(name)
                .description(description)
                .build();
    }

}
