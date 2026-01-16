package com.modolus.core;

import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.ShutdownReason;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.modolus.core.logger.Logger;
import com.modolus.core.runtime.Runtime;
import com.modolus.core.runtime.RuntimeError;
import com.modolus.util.singleton.Lazy;
import com.modolus.util.singleton.Singletons;
import org.jetbrains.annotations.NotNull;

public final class Plugin extends JavaPlugin {

    private final Lazy<Logger> logger = new Lazy<>(Logger.class);

    public Plugin(@NotNull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        Logger.provideLogger(getLogger(), "");
        Runtime.initializeRuntime()
                .onFailure(this::handleRuntimeInitializationError);
    }

    @Override
    protected void shutdown() {
        Singletons.destructSingletons();
    }

    private void handleRuntimeInitializationError(RuntimeError runtimeError) {
        logger.getOrThrow().atSevere().log("An error occured while booting modolus %s", runtimeError.name());
        HytaleServer.get().shutdownServer(new ShutdownReason(1, "An error occured while booting modolus %s".formatted(runtimeError.name())));
    }

}
