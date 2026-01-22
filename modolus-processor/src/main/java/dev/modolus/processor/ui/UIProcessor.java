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

import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.ParameterSpec;
import com.palantir.javapoet.ParameterizedTypeName;
import dev.modolus.annotations.ui.UI;
import dev.modolus.processor.Processor;
import dev.modolus.processor.ProcessorUtils;
import dev.modolus.processor.SharedContext;
import dev.modolus.processor.SourceFileWriter;
import dev.modolus.util.ui.UIRoot;
import java.util.Map;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import org.jetbrains.annotations.NotNull;

public class UIProcessor extends Processor {

  private static final ClassName UI_ROOT_NAME = ClassName.get(UIRoot.class);

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
        .superclass(ParameterizedTypeName.get(UI_ROOT_NAME, ClassName.get(rootComponent)));

    writer
        .getConstructor()
        .addParameter(ParameterSpec.builder(ClassName.get(rootComponent), "root").build())
        .addStatement("super(root)");
  }
}
