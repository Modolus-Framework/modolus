package com.modolus.test.events;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.modolus.annotations.event.EventHandler;
import com.modolus.annotations.event.EventListener;
import com.modolus.annotations.singleton.CreateOnRuntime;
import com.modolus.annotations.singleton.InjectSingleton;
import com.modolus.annotations.singleton.ProvideSingleton;
import com.modolus.core.database.Database;
import com.modolus.core.logger.Logger;
import com.modolus.core.logger.LoggerUtils;
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
