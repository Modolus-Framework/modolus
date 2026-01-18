package com.modolus.test.command;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.modolus.annotations.command.Command;
import com.modolus.annotations.command.SubCommand;
import com.modolus.annotations.singleton.CreateOnRuntime;
import com.modolus.annotations.singleton.ProvideSingleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

@Command(
        name = "test",
        description = "Test",
        subCommands = {
                @SubCommand(TestSubCommand.class)
        }
)
@ProvideSingleton(value = AbstractCommand.class)
@CreateOnRuntime
public class TestCommand extends AbstractTestCommand {
    @Override
    protected @Nullable CompletableFuture<Void> executeCommand(@NotNull CommandContext commandContext) {
        commandContext.sendMessage(Message.raw("Test"));
        return null;
    }
}
