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

import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import dev.modolus.core.event.EventListener;
import dev.modolus.core.logger.Logger;
import dev.modolus.core.logger.LoggerUtils;
import dev.modolus.core.runtime.Runtime;
import dev.modolus.util.singleton.LazySet;
import dev.modolus.util.singleton.Singleton;
import dev.modolus.util.singleton.SingletonScope;
import dev.modolus.util.singleton.Singletons;
import org.jetbrains.annotations.NotNull;

public abstract class BasePlugin extends JavaPlugin implements Singleton {

  protected BasePlugin(@NotNull JavaPluginInit init) {
    super(init);
  }

  protected void initializeManualDependencies() {}

  protected final void setupPlugin() {
    Logger.providePluginLogger(getLogger());
    Singletons.provideSingleton(JavaPlugin.class, this, SingletonScope.PLUGIN).orElseThrow();
    initializeManualDependencies();

    // Initialize runtime
    Runtime.requireSuccess(Runtime.initializeCurrentScope());
  }

  protected final void shutdownPlugin() {
    Singletons.destructSingletons().orElseThrow();
  }

  protected final void registerCommands() {
    LazySet.ofPlugin(AbstractCommand.class)
        .get()
        .mapVoid(commands -> commands.forEach(this::registerCommand))
        .onFailure(
            err -> {
              LoggerUtils.printError(Logger.getPluginLogger(), "Failed to get commands with error");
              LoggerUtils.printError(Logger.getPluginLogger(), err);
            });
  }

  protected final void registerEventListeners() {
    LazySet.ofPlugin(EventListener.class)
        .get()
        .mapVoid(listeners -> listeners.forEach(this::registerEventListener));
  }

  protected void registerCommand(AbstractCommand command) {
    getCommandRegistry().registerCommand(command);
  }

  protected void registerEventListener(@NotNull EventListener eventListener) {}
}
