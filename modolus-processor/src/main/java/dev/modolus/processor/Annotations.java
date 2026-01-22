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

package dev.modolus.processor;

import lombok.experimental.UtilityClass;

@UtilityClass
final class Annotations {

  static final String COMMAND_ANNOTATION = "dev.modolus.annotations.command.Command";
  static final String UI_ANNOTATION = "dev.modolus.annotations.ui.UI";
  static final String EVENT_LISTENER_ANNOTATION = "dev.modolus.annotations.event.EventListener";
  static final String PROVIDE_SINGLETONS_ANNOTATION =
      "dev.modolus.annotations.singleton.ProvideSingletons";
  static final String PROVIDE_SINGLETON_ANNOTATION =
      "dev.modolus.annotations.singleton.ProvideSingleton";
  static final String INJECT_SINGLETONS_ANNOTATION =
      "dev.modolus.annotations.singleton.InjectSingletons";
  static final String INJECT_SINGLETON_ANNOTATION =
      "dev.modolus.annotations.singleton.InjectSingleton";
  static final String CREATE_ON_RUNTIME_ANNOTATION =
      "dev.modolus.annotations.singleton.CreateOnRuntime";
  static final String SCOPE_ANNOTATION = "dev.modolus.annotations.singleton.Scope";
  static final String HYTALE_PLUGIN_ANNOTATION = "dev.modolus.annotations.plugin.HytalePlugin";
}
