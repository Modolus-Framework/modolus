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

package dev.modolus.core;

import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.ShutdownReason;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import dev.modolus.annotations.plugin.HytalePlugin;
import dev.modolus.annotations.plugin.PluginAuthor;
import dev.modolus.core.logger.Logger;
import dev.modolus.core.logger.LoggerUtils;
import dev.modolus.core.runtime.Runtime;
import dev.modolus.core.runtime.RuntimeError;
import dev.modolus.util.singleton.*;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;

@HytalePlugin(
    group = "dev.modolus",
    name = "modolus-core",
    description =
        "A library plugin, which tries to reduce boilerplate and improve the developer experience",
    authors = {
      @PluginAuthor(
          name = "Louis Schmieder",
          email = "dev@louis-schmieder.de",
          url = "https://github.com/LouisSchmieder")
    },
    website = "https://github.com/Modolus-Framework/modolus")
public final class Plugin extends JavaPlugin implements Singleton {

  private final Lazy<Logger> logger = Logger.getRootLogger();

  public Plugin(@NotNull JavaPluginInit init) {
    super(init);
  }

  @Override
  public @NotNull CompletableFuture<Void> preLoad() {
    var before = super.preLoad();

    var scopeProvider =
        CompletableFuture.runAsync(
            () -> {
              Logger.provideRootLogger(getLogger());
              Runtime.initializeRuntime().onFailure(this::handleRuntimeInitializationError);
            });

    return before != null ? CompletableFuture.allOf(before, scopeProvider) : scopeProvider;
  }

  @Override
  protected void setup() {
    Singletons.provideSingleton(JavaPlugin.class, this, SingletonScope.ROOT).orElseThrow();

    Runtime.requireSuccess(Runtime.initializeCurrentScope());

    LazySet.ofRoot(AbstractCommand.class)
        .get()
        .mapVoid(
            commands ->
                commands.forEach(
                    command -> {
                      getCommandRegistry().registerCommand(command);
                      LoggerUtils.printInfo(
                          logger,
                          String.format(
                              "Registered command %s", command.getClass().getSimpleName()));
                    }))
        .onFailure(
            err ->
                LoggerUtils.printError(
                    logger, String.format("Failed to get commands with error: %s", err.name())));
  }

  @Override
  protected void shutdown() {
    Singletons.destructSingletons();
  }

  private void handleRuntimeInitializationError(@NotNull RuntimeError runtimeError) {
    logger
        .getOrThrow()
        .atSevere()
        .log("An error occured while booting modolus %s", runtimeError.name());
    HytaleServer.get()
        .shutdownServer(
            new ShutdownReason(
                1, "An error occured while booting modolus %s".formatted(runtimeError.name())));
  }
}
