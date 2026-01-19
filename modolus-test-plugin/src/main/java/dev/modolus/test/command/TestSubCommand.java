package dev.modolus.test.command;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import dev.modolus.annotations.command.Command;
import dev.modolus.annotations.singleton.CreateOnRuntime;
import dev.modolus.annotations.singleton.ProvideSingleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

@Command(
        name = "test",
        description = "Test sub command"
)
@ProvideSingleton(value = TestSubCommand.class)
@CreateOnRuntime
public class TestSubCommand extends AbstractTestSubCommand {
    @Override
    protected @Nullable CompletableFuture<Void> executeCommand(@NotNull CommandContext commandContext) {
        commandContext.sendMessage(Message.raw("Test sub command"));
        return null;
    }
}
