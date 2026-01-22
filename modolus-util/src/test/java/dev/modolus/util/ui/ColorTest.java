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

package dev.modolus.util.ui;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ColorTest {

  @Test
  void fromRGBHex() {
    var color = Color.fromRGBHex("#ffffff");
    assertEquals("#ffffff", color.serialize());

    color = Color.fromRGBHex("ffffff");
    assertEquals("#ffffff", color.serialize());
  }

  @Test
  void fromRGBAHex() {
    var color = Color.fromRGBAHex("#ffffff80");
    assertEquals("#ffffff(0.50)", color.serialize());

    color = Color.fromRGBAHex("ffffff80");
    assertEquals("#ffffff(0.50)", color.serialize());
  }
}
