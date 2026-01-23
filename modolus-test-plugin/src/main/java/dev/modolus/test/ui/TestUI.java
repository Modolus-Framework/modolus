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

import static dev.modolus.util.ui.component.Components.group;
import static dev.modolus.util.ui.component.Components.textButton;

import dev.modolus.annotations.singleton.CreateOnRuntime;
import dev.modolus.annotations.singleton.ProvideSingleton;
import dev.modolus.annotations.ui.UI;
import dev.modolus.util.ui.component.GroupComponent;
import dev.modolus.util.ui.component.properties.*;

@UI(GroupComponent.class) @ProvideSingleton(TestUI.class)
@CreateOnRuntime
public class TestUI extends AbstractTestUI {

  public TestUI() {
    super(
        group()
            .withProperties(
                component ->
                    component
                        .property(AnchorProperties.ofMargin(100, 100))
                        .namedProperty("test", ComponentPropertyType.BACKGROUND))
            .orElseThrow()
            .child(
                textButton("testButton")
                    .withProperties(
                        component -> component.property(TextProperties.of("Hello world!")))
                    .orElseThrow()));

    constant("test", PatchStyle.ofAllSideBorder("Common/Button.png", 10));
    updateInlineUICache();
  }
}
