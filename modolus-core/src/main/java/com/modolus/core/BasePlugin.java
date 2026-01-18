package com.modolus.core;

import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.modolus.core.event.EventListener;
import com.modolus.core.logger.Logger;
import com.modolus.core.logger.LoggerUtils;
import com.modolus.core.runtime.Runtime;
import com.modolus.util.singleton.LazySet;
import com.modolus.util.singleton.Singleton;
import com.modolus.util.singleton.SingletonScope;
import com.modolus.util.singleton.Singletons;
import org.jetbrains.annotations.NotNull;


public abstract class BasePlugin extends JavaPlugin implements Singleton {

    protected BasePlugin(@NotNull JavaPluginInit init) {
        super(init);
    }

    protected void initializeManualDependencies() {
    }

    protected final void setupPlugin() {
        Logger.providePluginLogger(getLogger());
        Singletons.provideSingleton(JavaPlugin.class, this, SingletonScope.PLUGIN).orElseThrow();
        initializeManualDependencies();

        // Initialize runtime
        Runtime.requireSuccess(Runtime.initializeCurrentScope());
    }

    protected final void shutdownPlugin() {
        Singletons.destructSingletons()
                .orElseThrow();
    }

    protected final void registerCommands() {
        LazySet.ofPlugin(AbstractCommand.class).get()
                .mapVoid(commands -> commands.forEach(this::registerCommand))
                .onFailure(err -> LoggerUtils.printError(Logger.getPluginLogger(), String.format("Failed to get commands with error: %s", err.name())));
    }

    protected final void registerEventListeners() {
        LazySet.ofPlugin(EventListener.class).get()
                .mapVoid(listeners -> listeners.forEach(this::registerEventListener));
    }

    protected void registerCommand(AbstractCommand command) {
        getCommandRegistry().registerCommand(command);
    }

    protected void registerEventListener(@NotNull EventListener eventListener) {

    }

}
