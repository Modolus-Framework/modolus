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

package dev.modolus.test.events;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import dev.modolus.annotations.event.EventHandler;
import dev.modolus.annotations.event.EventListener;
import dev.modolus.annotations.singleton.CreateOnRuntime;
import dev.modolus.annotations.singleton.InjectSingleton;
import dev.modolus.annotations.singleton.ProvideSingleton;
import dev.modolus.core.database.Database;
import dev.modolus.core.logger.Logger;
import dev.modolus.core.logger.LoggerUtils;
import org.jetbrains.annotations.NotNull;

@CreateOnRuntime
@EventListener
@InjectSingleton(Database.class)
@ProvideSingleton(JoinEventHandler.class)
public class JoinEventHandler extends AbstractJoinEventHandler {

  @EventHandler
  public void onPlayerConnect(final @NotNull PlayerConnectEvent event) {
    event.getPlayerRef().sendMessage(Message.raw("Hello from the server!"));
    LoggerUtils.printInfo(
        Logger.getPluginLogger(),
        String.format("Player joined %s", event.getPlayerRef().getUsername()));
  }
}
