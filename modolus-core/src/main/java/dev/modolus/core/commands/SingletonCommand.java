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

package dev.modolus.core.commands;

import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.modolus.annotations.command.Command;
import dev.modolus.annotations.singleton.CreateOnRuntime;
import dev.modolus.annotations.singleton.ProvideSingleton;
import dev.modolus.util.singleton.SingletonScope;
import dev.modolus.util.singleton.Singletons;
import org.jetbrains.annotations.NotNull;

@Command(
    name = "singletons",
    description = "Debug all singletons, which are registered",
    target = Command.CommandTarget.WORLD)
@ProvideSingleton(value = AbstractCommand.class, scope = SingletonScope.ROOT)
@CreateOnRuntime
public class SingletonCommand extends AbstractSingletonCommand {

  @Override
  protected void executeCommand(
      @NotNull CommandContext commandContext,
      @NotNull World world,
      @NotNull Store<EntityStore> store) {
    Singletons.debugSingletons(s -> commandContext.sendMessage(Message.raw(s)));
  }
}
