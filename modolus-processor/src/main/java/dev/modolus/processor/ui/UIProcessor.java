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

package dev.modolus.processor.ui;

import com.palantir.javapoet.*;
import dev.modolus.annotations.ui.UI;
import dev.modolus.processor.Processor;
import dev.modolus.processor.ProcessorUtils;
import dev.modolus.processor.SharedContext;
import dev.modolus.processor.SourceFileWriter;
import dev.modolus.util.singleton.Lazy;
import dev.modolus.util.ui.UIRoot;
import java.util.Map;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import org.jetbrains.annotations.NotNull;

public class UIProcessor extends Processor {

  private static final ClassName UI_ROOT_NAME = ClassName.get(UIRoot.class);
  private static final ClassName JAVA_PLUGIN_CLASS_NAME =
      ClassName.get("com.hypixel.hytale.server.core.plugin", "JavaPlugin");
  private static final ClassName LAZY_TYPE_NAME = ClassName.get(Lazy.class);
  private static final ClassName RUNTIME_ASSET_NAME =
      ClassName.get("dev.modolus.util.asset", "RuntimeAsset");
  private static final ClassName COMMON_ASSET_REGISTRY_NAME =
      ClassName.get("com.hypixel.hytale.server.core.asset.common", "CommonAssetRegistry");

  public UIProcessor(ProcessingEnvironment processingEnv) {
    super(processingEnv);
  }

  @Override
  public void processSingle(
      @NotNull Element annotated,
      String className,
      Map<String, SourceFileWriter> writers,
      SharedContext sharedContext) {
    var ui = annotated.getAnnotation(UI.class);
    assert ui != null;

    var rootComponent = ProcessorUtils.getTypeMirror(ui::value);
    assert rootComponent != null;

    var writer = ProcessorUtils.ensureBaseFileExists(writers, className, annotated);

    writer
        .getClassBuilder()
        .superclass(ParameterizedTypeName.get(UI_ROOT_NAME, TypeName.get(rootComponent)));

    writer
        .getConstructor()
        .addParameter(ParameterSpec.builder(TypeName.get(rootComponent), "root").build())
        .addStatement("super(root)");

    String basePath = "UI/Custom/";
    String subPath =
        (ui.name().isBlank() ? annotated.getSimpleName().toString() : ui.name()) + ".ui";

    basePath += subPath;

    writer.addField(
        FieldSpec.builder(ClassName.get(String.class), "PATH")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
            .initializer("$S", subPath)
            .build());

    writer.addMethod(
        MethodSpec.methodBuilder("onInitialization")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override.class)
            .returns(TypeName.VOID)
            .addStatement(
                "var plugin = $T.ofPlugin($T.class).getOrThrow()",
                LAZY_TYPE_NAME,
                JAVA_PLUGIN_CLASS_NAME)
            .addStatement(
                "var asset = new $T($S, getInlineUI().getBytes())", RUNTIME_ASSET_NAME, basePath)
            .addStatement(
                "$T.addCommonAsset(plugin.getIdentifier().toString(), asset)",
                COMMON_ASSET_REGISTRY_NAME)
            .build());
  }
}
