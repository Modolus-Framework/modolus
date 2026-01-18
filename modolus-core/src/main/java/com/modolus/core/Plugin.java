package com.modolus.core;

import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.ShutdownReason;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.modolus.annotations.plugin.HytalePlugin;
import com.modolus.annotations.plugin.PluginAuthor;
import com.modolus.core.database.DatabaseConfiguration;
import com.modolus.core.logger.Logger;
import com.modolus.core.logger.LoggerUtils;
import com.modolus.core.runtime.Runtime;
import com.modolus.core.runtime.RuntimeError;
import com.modolus.util.singleton.*;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

@HytalePlugin(
        group = "com.modolus",
        name = "modolus-core",
        description = "A library plugin, which tries to reduce boilerplate and improve the developer experience",
        authors = {
                @PluginAuthor(
                        name = "Louis Schmieder",
                        email = "dev@louis-schmieder.de",
                        url = "https://github.com/LouisSchmieder"
                )
        },
        website = "https://github.com/Modolus-Framework/modolus"
)
public final class Plugin extends JavaPlugin implements Singleton {

    private final Lazy<Logger> logger = Logger.getRootLogger();

    public Plugin(@NotNull JavaPluginInit init) {
        super(init);
    }

    @Override
    public @NotNull CompletableFuture<Void> preLoad() {
        var before = super.preLoad();

        var scopeProvider = CompletableFuture.runAsync(() -> {
            Logger.provideRootLogger(getLogger());
            Runtime.initializeRuntime().onFailure(this::handleRuntimeInitializationError);
        });

        return before != null ? CompletableFuture.allOf(before, scopeProvider) : scopeProvider;
    }

    @Override
    protected void setup() {
        Singletons.provideSingleton(JavaPlugin.class, this, SingletonScope.ROOT).orElseThrow();
        DatabaseConfiguration.provideDatabaseConfiguration(SingletonScope.ROOT);

        Runtime.requireSuccess(Runtime.initializeCurrentScope());


        LazySet.ofRoot(AbstractCommand.class).get()
                .mapVoid(commands -> commands.forEach(command -> {
                    getCommandRegistry().registerCommand(command);
                    LoggerUtils.printInfo(logger, String.format("Registered command %s", command.getClass().getSimpleName()));
                }))
                .onFailure(err -> LoggerUtils.printError(logger, String.format("Failed to get commands with error: %s", err.name())));
    }

    @Override
    protected void shutdown() {
        Singletons.destructSingletons();
    }

    private void handleRuntimeInitializationError(@NotNull RuntimeError runtimeError) {
        logger.getOrThrow().atSevere().log("An error occured while booting modolus %s", runtimeError.name());
        HytaleServer.get().shutdownServer(new ShutdownReason(1, "An error occured while booting modolus %s".formatted(runtimeError.name())));
    }

}
