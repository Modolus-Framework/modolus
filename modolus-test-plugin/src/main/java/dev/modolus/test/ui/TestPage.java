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

package dev.modolus.test.ui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

public class TestPage extends InteractiveCustomUIPage<TestPage.Data> {

  public TestPage(@NotNull PlayerRef playerRef) {
    super(playerRef, CustomPageLifetime.CanDismiss, Data.CODEC);
  }

  @Override
  public void build(
      @NotNull Ref<EntityStore> ref,
      @NotNull UICommandBuilder uiCommandBuilder,
      @NotNull UIEventBuilder uiEventBuilder,
      @NotNull Store<EntityStore> store) {

    uiCommandBuilder.append(AbstractTestUI.PATH);
  }

  @NoArgsConstructor
  public static class Data {
    public static final BuilderCodec<Data> CODEC =
        BuilderCodec.builder(Data.class, Data::new)
            .append(new KeyedCodec<>("Action", Codec.STRING), (e, v) -> e.action = v, e -> e.action)
            .add()
            .build();

    private String action;
  }
}
