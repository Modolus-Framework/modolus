package dev.modolus.processor.manifest;

import dev.modolus.annotations.plugin.HytalePlugin;
import dev.modolus.annotations.plugin.PluginAuthor;
import dev.modolus.annotations.plugin.PluginDependency;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class PluginManifestConverter {

    public static @NotNull PluginManifest fromAnnotation(@NotNull HytalePlugin hytalePlugin,
                                                         @NotNull String version,
                                                         @NotNull String pluginMainClass) {
        return PluginManifest.builder()
                .group(hytalePlugin.group())
                .name(hytalePlugin.name())
                .version(version)
                .description(hytalePlugin.description())
                .authors(Arrays.stream(hytalePlugin.authors()).map(PluginManifestConverter::fromAnnotation).toList())
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
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (existing, _) -> existing));
    }

    private static @NotNull PluginManifestAuthor fromAnnotation(@NotNull PluginAuthor author) {
        return PluginManifestAuthor.builder()
                .name(author.name())
                .url(author.url())
                .email(author.email())
                .build();
    }

}
