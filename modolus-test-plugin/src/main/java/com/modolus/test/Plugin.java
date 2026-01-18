package com.modolus.test;

import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.modolus.annotations.plugin.HytalePlugin;
import com.modolus.annotations.plugin.PluginAuthor;
import com.modolus.annotations.plugin.PluginDependency;
import com.modolus.annotations.singleton.Scope;
import com.modolus.core.logger.Logger;
import com.modolus.core.logger.LoggerUtils;
import com.modolus.core.runtime.Runtime;
import com.modolus.util.singleton.LazySet;
import com.modolus.util.singleton.Singleton;
import com.modolus.util.singleton.SingletonScope;
import com.modolus.util.singleton.Singletons;
import org.jetbrains.annotations.NotNull;

@HytalePlugin(
        group = "com.modolus",
        name = "modolus-test-plugin",
        description = "A test plugin, to test features from modolus",
        authors = {
                @PluginAuthor(
                        name = "Louis Schmieder",
                        email = "dev@louis-schmieder.de",
                        url = "https://github.com/LouisSchmieder"
                )
        },
        website = "https://github.com/Modolus-Framework/modolus",
        dependencies = {
                @PluginDependency(
                        name = "com.modolus:modolus-core",
                        version = "*"
                )
        }
)
@Scope
public class Plugin extends JavaPlugin implements Singleton {

    public Plugin(@NotNull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        Logger.providePluginLogger(getLogger());
        Singletons.provideSingleton(JavaPlugin.class, this, SingletonScope.PLUGIN).orElseThrow();

        Runtime.requireSuccess(Runtime.initializeCurrentScope());

        LoggerUtils.printInfo(Logger.getPluginLogger(), "Test Plugin loaded");

        LazySet.ofPlugin(AbstractCommand.class).get()
                .mapVoid(commands -> commands.forEach(command -> {
                    getCommandRegistry().registerCommand(command);
                    LoggerUtils.printInfo(Logger.getPluginLogger(), String.format("Registered command %s", command.getClass().getSimpleName()));
                }))
                .onFailure(err -> LoggerUtils.printError(Logger.getPluginLogger(), String.format("Failed to get commands with error: %s", err.name())));

    }

}
