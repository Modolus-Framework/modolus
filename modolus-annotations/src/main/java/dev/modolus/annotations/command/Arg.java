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

package dev.modolus.annotations.command;

import java.lang.annotation.*;

@Repeatable(Args.class)
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Arg {

  String name();

  String description();

  DefaultArg defaultArg() default
      @DefaultArg(
          ignore = true,
          type = Void.class,
          argTypeName = "",
          defaultValue = "",
          defaultValueDescription = "");

  OptionalArg optionalArg() default
      @OptionalArg(ignore = true, type = Void.class, argTypeName = "");

  RequiredArg requiredArg() default
      @RequiredArg(ignore = true, type = Void.class, argTypeName = "");

  FlagArg flagArg() default @FlagArg(ignore = true);
}
