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

package dev.modolus.processor.singleton;

import dev.modolus.annotations.singleton.InjectSingletons;
import dev.modolus.processor.ProcessorUtils;
import dev.modolus.processor.SharedContext;
import dev.modolus.processor.SourceFileWriter;
import java.util.Arrays;
import java.util.Map;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import org.jetbrains.annotations.NotNull;

public class InjectSingletonsProcessor extends InjectSingletonProcessor {

  public InjectSingletonsProcessor(ProcessingEnvironment processingEnv) {
    super(processingEnv);
  }

  @Override
  public void processSingle(
      @NotNull Element annotated,
      String className,
      Map<String, SourceFileWriter> writers,
      @NotNull SharedContext sharedContext) {
    var singletonNeededCollection = annotated.getAnnotation(InjectSingletons.class);
    assert singletonNeededCollection != null;

    ProcessorUtils.ensureBaseFileExists(writers, className, annotated);

    writers
        .values()
        .forEach(
            sourceFileWriter ->
                Arrays.stream(singletonNeededCollection.value())
                    .distinct()
                    .map(this::addNeeded)
                    .forEach(sourceFileWriter::addField));
  }
}
