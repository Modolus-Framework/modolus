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

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public final class Components {

  @Contract(" -> new")
  public static @NotNull GroupComponent group() {
    return new GroupComponent();
  }

  @Contract("_ -> new")
  public static @NotNull GroupComponent group(@NotNull String id) {
    return new GroupComponent(id);
  }

  @Contract(" -> new")
  public static @NotNull CheckBoxComponent checkBox() {
    return new CheckBoxComponent();
  }

  @Contract("_ -> new")
  public static @NotNull CheckBoxComponent checkBox(@NotNull String id) {
    return new CheckBoxComponent(id);
  }

  @Contract(" -> new")
  public static @NotNull SliderComponent slider() {
    return new SliderComponent();
  }

  @Contract("_ -> new")
  public static @NotNull SliderComponent slider(@NotNull String id) {
    return new SliderComponent(id);
  }

  @Contract(" -> new")
  public static @NotNull DropdownBoxComponent dropdownBox() {
    return new DropdownBoxComponent();
  }

  @Contract("_ -> new")
  public static @NotNull DropdownBoxComponent dropdownBox(@NotNull String id) {
    return new DropdownBoxComponent(id);
  }

  /**
   * @deprecated use a {@link GroupComponent} with a {@link
   *     dev.modolus.util.ui.component.properties.BackgroundProperties} instead
   * @return a new ImageComponent
   */
  @Deprecated(since = "0.0.2")
  @Contract(" -> new")
  public static @NotNull ImageComponent image() {
    return new ImageComponent();
  }

  /**
   * @deprecated use a {@link GroupComponent} with a {@link
   *     dev.modolus.util.ui.component.properties.BackgroundProperties} instead
   * @return a new ImageComponent
   */
  @Deprecated(since = "0.0.2")
  @Contract("_ -> new")
  public static @NotNull ImageComponent image(@NotNull String id) {
    return new ImageComponent(id);
  }

  @Contract(" -> new")
  public static @NotNull LabelComponent label() {
    return new LabelComponent();
  }

  @Contract("_ -> new")
  public static @NotNull LabelComponent label(@NotNull String id) {
    return new LabelComponent(id);
  }

  @Contract(" -> new")
  public static @NotNull NumberFieldComponent numberField() {
    return new NumberFieldComponent();
  }

  @Contract("_ -> new")
  public static @NotNull NumberFieldComponent numberField(@NotNull String id) {
    return new NumberFieldComponent(id);
  }

  @Contract(" -> new")
  public static @NotNull TextFieldComponent textField() {
    return new TextFieldComponent();
  }

  @Contract("_ -> new")
  public static @NotNull TextFieldComponent textField(@NotNull String id) {
    return new TextFieldComponent(id);
  }

  @Contract(" -> new")
  public static @NotNull ScrollViewComponent scrollView() {
    return new ScrollViewComponent();
  }

  @Contract("_ -> new")
  public static @NotNull ScrollViewComponent scrollView(@NotNull String id) {
    return new ScrollViewComponent(id);
  }

  @Contract(" -> new")
  public static @NotNull TextButtonComponent textButton() {
    return new TextButtonComponent();
  }

  @Contract("_ -> new")
  public static @NotNull TextButtonComponent textButton(@NotNull String id) {
    return new TextButtonComponent(id);
  }
}
