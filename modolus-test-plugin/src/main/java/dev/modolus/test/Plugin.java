package dev.modolus.test;

import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import dev.modolus.annotations.plugin.HytalePlugin;
import dev.modolus.annotations.plugin.PluginAuthor;
import dev.modolus.annotations.plugin.PluginDependency;
import dev.modolus.annotations.singleton.Scope;
import dev.modolus.core.BasePlugin;
import dev.modolus.core.database.DatabaseConfiguration;
import dev.modolus.core.logger.Logger;
import dev.modolus.core.logger.LoggerUtils;
import org.jetbrains.annotations.NotNull;

@HytalePlugin(
        group = "dev.modolus",
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
                        name = "dev.modolus:modolus-core",
                        version = "*"
                )
        }
)
@Scope
public class Plugin extends BasePlugin {

    public Plugin(@NotNull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void initializeManualDependencies() {
        var data = DatabaseConfiguration.provideDatabaseConfiguration();
        data.onFailure(e -> LoggerUtils.printError(Logger.getPluginLogger(), "Failed to initialize database: " + e.name()));
        data
                .onSuccess(config -> config.withMigrations("test/migrations"));
    }

    @Override
    protected void setup() {
        setupPlugin();
        registerCommands();
        registerEventListeners();
    }

    @Override
    protected void shutdown() {
        shutdownPlugin();
    }

    @Override
    protected void registerCommand(AbstractCommand command) {
        super.registerCommand(command);
        LoggerUtils.printInfo(Logger.getPluginLogger(), String.format("Registered command %s", command.getClass().getSimpleName()));
    }
}
