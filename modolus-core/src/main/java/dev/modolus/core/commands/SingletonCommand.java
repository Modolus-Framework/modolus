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
        target = Command.CommandTarget.WORLD
)
@ProvideSingleton(value = AbstractCommand.class, scope = SingletonScope.ROOT)
@CreateOnRuntime
public class SingletonCommand extends AbstractSingletonCommand {

    @Override
    protected void executeCommand(@NotNull CommandContext commandContext, @NotNull World world, @NotNull Store<EntityStore> store) {
        Singletons.debugSingletons(s -> commandContext.sendMessage(Message.raw(s)));
    }
}
