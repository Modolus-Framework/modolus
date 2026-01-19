package dev.modolus.processor.command;

import dev.modolus.annotations.command.Command;
import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.ParameterSpec;
import com.palantir.javapoet.ParameterizedTypeName;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

import static dev.modolus.processor.command.Constants.*;

@UtilityClass
public final class CommandParameters {

    public static List<ParameterSpec> getOverrideMethodParameters(@NotNull Command command) {
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

    @Contract(" -> new")
    private static @NotNull @Unmodifiable List<ParameterSpec> getAbstractPlayerCommandParameters() {
        return List.of(
                getCommandContextParameter(),
                getEntityStoreParameter(),
                getEntityStoreRefParameter(),
                getPlayerRefParameter(),
                getWorldParameter()
        );
    }

    @Contract(" -> new")
    private static @NotNull @Unmodifiable List<ParameterSpec> getAbstractAsyncWorldCommandParameters() {
        return List.of(
                getCommandContextParameter(),
                getWorldParameter()
        );
    }

    @Contract(" -> new")
    private static @NotNull @Unmodifiable List<ParameterSpec> getAbstractWorldCommandParameters() {
        return List.of(
                getCommandContextParameter(),
                getWorldParameter(),
                getEntityStoreParameter()
        );
    }

    @Contract(" -> new")
    private static @NotNull @Unmodifiable List<ParameterSpec> getAbstractAsyncCommandParameters() {
        return List.of(
                getCommandContextParameter()
        );
    }

    @Contract(" -> new")
    private static @NotNull @Unmodifiable List<ParameterSpec> getAbstractCommandParameters() {
        return List.of(getCommandContextParameter());
    }

    private static @NotNull ParameterSpec getCommandContextParameter() {
        var type = ClassName.get(SYSTEM_PACKAGE, COMMAND_CONTEXT_TYPE_NAME);
        return ParameterSpec.builder(type, COMMAND_CONTEXT_PARAM_NAME)
                .addAnnotation(NotNull.class)
                .build();
    }

    private static @NotNull ParameterSpec getEntityStoreParameter() {
        var type = ParameterizedTypeName.get(ClassName.get(COMPONENT_PACKAGE, STORE_TYPE_NAME), getEntityStoreType());
        return ParameterSpec.builder(type, ENTITY_STORE_PARAM_NAME)
                .addAnnotation(NotNull.class)
                .build();
    }

    private static @NotNull ParameterSpec getWorldParameter() {
        var type = ClassName.get(WORLD_PACKAGE, WORLD_TYPE_NAME);
        return ParameterSpec.builder(type, WORLD_PARAM_NAME)
                .addAnnotation(NotNull.class)
                .build();
    }

    private static @NotNull ParameterSpec getEntityStoreRefParameter() {
        var type = ParameterizedTypeName.get(ClassName.get(COMPONENT_PACKAGE, ENTITY_STORE_REF_TYPE_NAME), getEntityStoreType());
        return ParameterSpec.builder(type, ENTITY_STORE_REF_PARAM_NAME)
                .addAnnotation(NotNull.class)
                .build();
    }

    private static @NotNull ParameterSpec getPlayerRefParameter() {
        var type = ClassName.get(UNIVERSE_PACKAGE, PLAYER_REF_TYPE_NAME);
        return ParameterSpec.builder(type, PLAYER_REF_PARAM_NAME)
                .addAnnotation(NotNull.class)
                .build();
    }

    private static ClassName getEntityStoreType() {
        return ClassName.get(STORAGE_PACKAGE, ENTITY_STORE_TYPE_NAME);
    }

}
