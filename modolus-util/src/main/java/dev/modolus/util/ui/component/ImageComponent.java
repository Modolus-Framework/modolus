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

/**
 * @deprecated use a {@link GroupComponent} with a {@link
 *     dev.modolus.util.ui.component.properties.BackgroundProperties} instead
 */
@Deprecated(since = "0.0.2")
public class ImageComponent extends AbstractComponent<ImageComponent> {

  public ImageComponent() {
    this(null);
  }

  public ImageComponent(@Nullable String id) {
    super(
        "Image",
        id,
        Set.of(
            ComponentPropertyType.TEXTURE_PATH,
            ComponentPropertyType.ANCHOR,
            ComponentPropertyType.TINT,
            ComponentPropertyType.OPACITY));
  }

  @Override
  protected ImageComponent getInstance() {
    return this;
  }
}
