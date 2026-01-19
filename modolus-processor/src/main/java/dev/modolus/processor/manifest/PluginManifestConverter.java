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

package dev.modolus.processor.manifest;

import dev.modolus.annotations.plugin.HytalePlugin;
import dev.modolus.annotations.plugin.PluginAuthor;
import dev.modolus.annotations.plugin.PluginDependency;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class PluginManifestConverter {

  public static @NotNull PluginManifest fromAnnotation(
      @NotNull HytalePlugin hytalePlugin,
      @NotNull String version,
      @NotNull String pluginMainClass) {
    return PluginManifest.builder()
        .group(hytalePlugin.group())
        .name(hytalePlugin.name())
        .version(version)
        .description(hytalePlugin.description())
        .authors(
            Arrays.stream(hytalePlugin.authors())
                .map(PluginManifestConverter::fromAnnotation)
                .toList())
        .website(hytalePlugin.website())
        .main(pluginMainClass)
        .serverVersion(hytalePlugin.serverVersion())
        .dependencies(convertDependencies(hytalePlugin.dependencies()))
        .optionalDependencies(convertDependencies(hytalePlugin.optionalDependencies()))
        .loadBefore(convertDependencies(hytalePlugin.loadBefore()))
        .disabledByDefault(hytalePlugin.disabledByDefault())
        .includesAssetPack(hytalePlugin.includesAssetPack())
        .build();
  }

  private static @NotNull Map<String, String> convertDependencies(PluginDependency[] dependencies) {
    return Arrays.stream(dependencies)
        .map(dependency -> new AbstractMap.SimpleEntry<>(dependency.name(), dependency.version()))
        .collect(
            Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (existing, _) -> existing));
  }

  private static @NotNull PluginManifestAuthor fromAnnotation(@NotNull PluginAuthor author) {
    return PluginManifestAuthor.builder()
        .name(author.name())
        .url(author.url())
        .email(author.email())
        .build();
  }
}
