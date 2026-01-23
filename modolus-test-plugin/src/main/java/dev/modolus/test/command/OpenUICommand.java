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

package dev.modolus.test.command;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.modolus.annotations.command.Command;
import dev.modolus.annotations.singleton.CreateOnRuntime;
import dev.modolus.annotations.singleton.ProvideSingleton;
import dev.modolus.test.ui.TestPage;
import org.jetbrains.annotations.NotNull;

@Command(name = "open-ui", description = "Opens the UI", target = Command.CommandTarget.PLAYER)
@ProvideSingleton(AbstractCommand.class)
@CreateOnRuntime
public class OpenUICommand extends AbstractOpenUICommand {
  @Override
  protected void executeCommand(
      @NotNull CommandContext commandContext,
      @NotNull Store<EntityStore> store,
      @NotNull Ref<EntityStore> ref,
      @NotNull PlayerRef playerRef,
      @NotNull World world) {
    Player player = store.getComponent(ref, Player.getComponentType());
    if (player == null) {
      commandContext.sendMessage(Message.raw("ERROR: Could not get player component!"));
      return;
    }

    TestPage page = new TestPage(playerRef);
    player.getPageManager().openCustomPage(ref, store, page);
  }
}
