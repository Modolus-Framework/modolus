package com.modolus.processor.command;

import com.palantir.javapoet.ClassName;
import lombok.experimental.UtilityClass;

@UtilityClass
final class Constants {

    static final String BASE_COMMANDS_PACKAGE = "com.hypixel.hytale.server.core.command.system.basecommands";
    static final String SYSTEM_PACKAGE = "com.hypixel.hytale.server.core.command.system";
    static final String STORAGE_PACKAGE = "com.hypixel.hytale.server.core.universe.world.storage";
    static final String WORLD_PACKAGE = "com.hypixel.hytale.server.core.universe.world";
    static final String UNIVERSE_PACKAGE = "com.hypixel.hytale.server.core.universe";
    static final String COMPONENT_PACKAGE = "com.hypixel.hytale.component";

    private static final String ABSTRACT_ASYNC_PLAYER_COMMAND_TYPE_NAME = "AbstractAsyncPlayerCommand";
    private static final String ABSTRACT_PLAYER_COMMAND_TYPE_NAME = "AbstractPlayerCommand";
    private static final String ABSTRACT_ASYNC_WORLD_COMMAND_TYPE_NAME = "AbstractAsyncWorldCommand";
    private static final String ABSTRACT_WORLD_COMMAND_TYPE_NAME = "AbstractWorldCommand";
    private static final String ABSTRACT_ASYNC_COMMAND_TYPE_NAME = "AbstractAsyncCommand";
    private static final String ABSTRACT_COMMAND_TYPE_NAME = "AbstractCommand";

    static final ClassName ABSTRACT_ASYNC_PLAYER_COMMAND_CLASS = ClassName.get(BASE_COMMANDS_PACKAGE, ABSTRACT_ASYNC_PLAYER_COMMAND_TYPE_NAME);
    static final ClassName ABSTRACT_PLAYER_COMMAND_CLASS = ClassName.get(BASE_COMMANDS_PACKAGE, ABSTRACT_PLAYER_COMMAND_TYPE_NAME);
    static final ClassName ABSTRACT_ASYNC_WORLD_COMMAND_CLASS = ClassName.get(BASE_COMMANDS_PACKAGE, ABSTRACT_ASYNC_WORLD_COMMAND_TYPE_NAME);
    static final ClassName ABSTRACT_WORLD_COMMAND_CLASS = ClassName.get(BASE_COMMANDS_PACKAGE, ABSTRACT_WORLD_COMMAND_TYPE_NAME);
    static final ClassName ABSTRACT_ASYNC_COMMAND_CLASS = ClassName.get(BASE_COMMANDS_PACKAGE, ABSTRACT_ASYNC_COMMAND_TYPE_NAME);
    static final ClassName ABSTRACT_COMMAND_CLASS = ClassName.get(SYSTEM_PACKAGE, ABSTRACT_COMMAND_TYPE_NAME);

    static final String STORE_TYPE_NAME = "Store";
    static final String COMMAND_CONTEXT_TYPE_NAME = "CommandContext";
    static final String ENTITY_STORE_TYPE_NAME = "EntityStore";
    static final String ENTITY_STORE_REF_TYPE_NAME = "Ref";
    static final String WORLD_TYPE_NAME = "World";
    static final String PLAYER_REF_TYPE_NAME = "PlayerRef";

    static final String COMMAND_CONTEXT_PARAM_NAME = "commandContext";
    static final String ENTITY_STORE_PARAM_NAME = "store";
    static final String ENTITY_STORE_REF_PARAM_NAME = "ref";
    static final String WORLD_PARAM_NAME = "world";
    static final String PLAYER_REF_PARAM_NAME = "playerRef";

}
