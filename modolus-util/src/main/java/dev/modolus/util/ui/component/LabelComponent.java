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

package dev.modolus.util.ui.component;

import dev.modolus.util.ui.component.properties.ComponentPropertyType;
import java.util.Set;
import org.jetbrains.annotations.Nullable;

public class LabelComponent extends AbstractComponent<LabelComponent> {

  public LabelComponent() {
    this(null);
  }

  public LabelComponent(@Nullable String id) {
    super(
        "Label",
        id,
        Set.of(
            ComponentPropertyType.TEXT,
            ComponentPropertyType.ANCHOR,
            ComponentPropertyType.VISIBLE,
            ComponentPropertyType.STYLE));
  }

  @Override
  protected LabelComponent getInstance() {
    return this;
  }
}
