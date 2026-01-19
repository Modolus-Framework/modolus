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
        LoggerUtils.printInfo(Logger.getPluginLogger(), String.format("Player joined %s", event.getPlayerRef().getUsername()));
    }

}
