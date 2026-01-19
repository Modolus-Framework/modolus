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

import dev.modolus.processor.manifest.PluginManifest;
import java.util.Map;
import java.util.Set;

/**
 * Map an incoming source file class name to multiple outgoing source file class names with separate
 * source file writers
 */
public record SharedContext(
    Map<String, Map<String, SourceFileWriter>> sourceFileWriters,
    Set<String> createOnRuntimeClasses,
    Set<String> scopePackages,
    Holder<PluginManifest> pluginManifest) {}
