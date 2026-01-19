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

package dev.modolus.annotations.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface HytalePlugin {

  String group();

  String name();

  String version() default "GRADLE_PROJECT_VERSION";

  String description();

  PluginAuthor[] authors();

  String website();

  String serverVersion() default "*";

  PluginDependency[] dependencies() default {};

  PluginDependency[] optionalDependencies() default {};

  PluginDependency[] loadBefore() default {};

  boolean disabledByDefault() default false;

  boolean includesAssetPack() default false;
}
