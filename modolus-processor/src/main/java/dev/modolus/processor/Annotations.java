package dev.modolus.processor;

import lombok.experimental.UtilityClass;

@UtilityClass
final class Annotations {

    static final String COMMAND_ANNOTATION = "dev.modolus.annotations.command.Command";
    static final String EVENT_LISTENER_ANNOTATION = "dev.modolus.annotations.event.EventListener";
    static final String PROVIDE_SINGLETONS_ANNOTATION = "dev.modolus.annotations.singleton.ProvideSingletons";
    static final String PROVIDE_SINGLETON_ANNOTATION = "dev.modolus.annotations.singleton.ProvideSingleton";
    static final String INJECT_SINGLETONS_ANNOTATION = "dev.modolus.annotations.singleton.InjectSingletons";
    static final String INJECT_SINGLETON_ANNOTATION = "dev.modolus.annotations.singleton.InjectSingleton";
    static final String CREATE_ON_RUNTIME_ANNOTATION = "dev.modolus.annotations.singleton.CreateOnRuntime";
    static final String SCOPE_ANNOTATION = "dev.modolus.annotations.singleton.Scope";
    static final String HYTALE_PLUGIN_ANNOTATION = "dev.modolus.annotations.plugin.HytalePlugin";

}
