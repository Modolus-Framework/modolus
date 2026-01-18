package com.modolus.core.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.modolus.annotations.command.Command;
import com.modolus.annotations.singleton.CreateOnRuntime;
import com.modolus.annotations.singleton.ProvideSingleton;
import com.modolus.util.singleton.SingletonScope;
import com.modolus.util.singleton.Singletons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

@Command(
        name = "singletons",
        description = "Debug all singletons, which are registered"
)
@ProvideSingleton(value = AbstractCommand.class, scope = SingletonScope.ROOT)
@CreateOnRuntime
public class SingletonCommand extends AbstractSingletonCommand {

    @Override
    protected @Nullable CompletableFuture<Void> executeCommand(@NotNull CommandContext commandContext) {
        Singletons.debugSingletons(s -> commandContext.sendMessage(Message.raw(s)));
        return null;
    }

}
