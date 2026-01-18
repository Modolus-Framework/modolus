package com.modolus.test.events;

import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.modolus.annotations.event.EventHandler;
import com.modolus.annotations.event.EventListener;
import com.modolus.annotations.singleton.CreateOnRuntime;
import com.modolus.annotations.singleton.InjectSingleton;
import com.modolus.annotations.singleton.ProvideSingleton;
import com.modolus.core.database.Database;
import org.jetbrains.annotations.NotNull;

@CreateOnRuntime
@EventListener
@InjectSingleton(Database.class)
@ProvideSingleton(EventHandler.class)
public class JoinEventHandler extends AbstractJoinEventHandler {

    @EventHandler
    public void onPlayerConnect(final @NotNull PlayerConnectEvent event) {
    }

}
