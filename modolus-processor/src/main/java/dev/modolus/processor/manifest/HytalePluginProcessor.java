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
import dev.modolus.processor.Processor;
import dev.modolus.processor.SharedContext;
import dev.modolus.processor.SourceFileWriter;
import java.util.Map;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import org.jetbrains.annotations.NotNull;

public class HytalePluginProcessor extends Processor {

  private static final String JAVA_PLUGIN_CLASS_NAME =
      "com.hypixel.hytale.server.core.plugin.JavaPlugin";
  private static final String BASE_PLUGIN_CLASS_NAME = "dev.modolus.core.BasePlugin";

  public HytalePluginProcessor(ProcessingEnvironment processingEnv) {
    super(processingEnv);
  }

  @Override
  public void processSingle(
      @NotNull Element annotated,
      String className,
      Map<String, SourceFileWriter> writers,
      SharedContext sharedContext) {
    var hytalePluginAnnotation = annotated.getAnnotation(HytalePlugin.class);
    assert hytalePluginAnnotation != null;

    if (!isPlugin(annotated)) {
      processingEnv
          .getMessager()
          .printMessage(
              javax.tools.Diagnostic.Kind.ERROR, "Class " + className + " is not a JavaPlugin");
      return;
    }

    var version = hytalePluginAnnotation.version();
    if (version.equals("GRADLE_PROJECT_VERSION")) {
      version = processingEnv.getOptions().get("projectVersion");

      if (version == null) {
        processingEnv
            .getMessager()
            .printMessage(
                javax.tools.Diagnostic.Kind.ERROR,
                "Could not find project version, please set the compiler option -AprojectVersion=VERSION");
        return;
      }
    }

    var manifest =
        PluginManifestConverter.fromAnnotation(hytalePluginAnnotation, version, className);
    sharedContext.pluginManifest().setValue(manifest);
  }

  private boolean isPlugin(@NotNull Element annotated) {
    var javaPlugin =
        processingEnv.getElementUtils().getTypeElement(JAVA_PLUGIN_CLASS_NAME).asType();
    var basePlugin =
        processingEnv.getElementUtils().getTypeElement(BASE_PLUGIN_CLASS_NAME).asType();

    return processingEnv.getTypeUtils().isSubtype(annotated.asType(), javaPlugin)
        || processingEnv.getTypeUtils().isSubtype(annotated.asType(), basePlugin);
  }
}
