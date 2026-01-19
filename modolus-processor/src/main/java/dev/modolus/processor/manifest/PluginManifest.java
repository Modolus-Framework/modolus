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

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Builder;

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
