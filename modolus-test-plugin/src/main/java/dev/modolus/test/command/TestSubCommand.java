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

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import dev.modolus.annotations.command.Command;
import dev.modolus.annotations.singleton.CreateOnRuntime;
import dev.modolus.annotations.singleton.ProvideSingleton;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Command(name = "test", description = "Test sub command")
@ProvideSingleton(value = TestSubCommand.class)
@CreateOnRuntime
public class TestSubCommand extends AbstractTestSubCommand {
  @Override
  protected @Nullable CompletableFuture<Void> executeCommand(
      @NotNull CommandContext commandContext) {
    commandContext.sendMessage(Message.raw("Test sub command"));
    return null;
  }
}
