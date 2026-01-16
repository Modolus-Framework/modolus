package com.modolus.processor.manifest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder(access = AccessLevel.PACKAGE)
public class PluginManifest {

    @JsonProperty("Group")
    private String group;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Version")
    private String version;

    @JsonProperty("Description")
    private String description;

    @JsonProperty("Authors")
    private List<PluginManifestAuthor> authors;

    @JsonProperty("Website")
    private String website;

    @JsonProperty("Main")
    private String main;

    @JsonProperty("ServerVersion")
    private String serverVersion;

    @JsonProperty("Dependencies")
    private Map<String, String> dependencies;

    @JsonProperty("OptionalDependencies")
    private Map<String, String> optionalDependencies;

    @JsonProperty("LoadBefore")
    private Map<String, String> loadBefore;

    @JsonProperty("DisabledByDefault")
    private boolean disabledByDefault;

    @JsonProperty("IncludesAssetPack")
    private boolean includesAssetPack;

}
