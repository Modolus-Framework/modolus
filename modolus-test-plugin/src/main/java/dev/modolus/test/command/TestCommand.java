package dev.modolus.test.command;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import dev.modolus.annotations.command.Command;
import dev.modolus.annotations.command.SubCommand;
import dev.modolus.annotations.singleton.CreateOnRuntime;
import dev.modolus.annotations.singleton.InjectSingleton;
import dev.modolus.annotations.singleton.ProvideSingleton;
import dev.modolus.core.database.Database;
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
@InjectSingleton(value = Database.class)
@CreateOnRuntime
public class TestCommand extends AbstractTestCommand {

    @Override
    protected @Nullable CompletableFuture<Void> executeCommand(@NotNull CommandContext commandContext) {

        database.getOrThrow().doOnPreparedStatementUpdate("insert into test (id) values (?)",
                stmt -> stmt.setString(1, commandContext.sender().getDisplayName()))
                .orElseThrow();


        commandContext.sendMessage(Message.raw("Test"));
        return null;
    }
}
