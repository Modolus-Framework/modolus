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

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Color {

  private static final String RGB_PATTERN = "^#?[a-fA-F0-9]{6}$";
  private static final String RGBA_PATTERN = "^#?[a-fA-F0-9]{8}$";

  private final int red;
  private final int green;
  private final int blue;
  private final int alpha;

  public @NotNull String serialize() {
    if (alpha == 0xFF) {
      return String.format("#%02x%02x%02x", red, green, blue);
    }

    float alphaPart = (float) alpha / (float) 255;
    return String.format("#%02x%02x%02x(%.2f)", red, green, blue, alphaPart);
  }

  @Contract("_ -> new")
  public static @NotNull Color fromRGBHex(@NotNull @Pattern(RGB_PATTERN) final String hex) {
    var raw = removePrefix(hex);

    int r = Integer.parseInt(raw.substring(0, 2), 16);
    int g = Integer.parseInt(raw.substring(2, 4), 16);
    int b = Integer.parseInt(raw.substring(4, 6), 16);

    return new Color(r, g, b, 0xFF);
  }

  @Contract("_ -> new")
  public static @NotNull Color fromRGBAHex(@NotNull @Pattern(RGBA_PATTERN) final String hex) {
    var raw = removePrefix(hex);

    int r = Integer.parseInt(raw.substring(0, 2), 16);
    int g = Integer.parseInt(raw.substring(2, 4), 16);
    int b = Integer.parseInt(raw.substring(4, 6), 16);
    int a = Integer.parseInt(raw.substring(6, 8), 16);

    return new Color(r, g, b, a);
  }

  @Contract("_, _, _ -> new")
  public static @NotNull Color fromRGB(
      @Range(from = 0, to = 255) int red,
      @Range(from = 0, to = 255) int green,
      @Range(from = 0, to = 255) int blue) {
    return new Color(red, green, blue, 0xFF);
  }

  @Contract("_, _, _, _ -> new")
  public static @NotNull Color fromRGBA(
      @Range(from = 0, to = 255) int red,
      @Range(from = 0, to = 255) int green,
      @Range(from = 0, to = 255) int blue,
      @Range(from = 0, to = 255) int alpha) {
    return new Color(red, green, blue, alpha);
  }

  private static @NotNull String removePrefix(@NotNull String hex) {
    return hex.startsWith("#") ? hex.substring(1) : hex;
  }
}
