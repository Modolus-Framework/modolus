package com.modolus.processor.manifest;

import com.modolus.annotations.plugin.HytalePlugin;
import com.modolus.processor.Processor;
import com.modolus.processor.SharedContext;
import com.modolus.processor.SourceFileWriter;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import java.util.Map;

public class HytalePluginProcessor extends Processor {

    private static final String JAVA_PLUGIN_CLASS_NAME = "com.hypixel.hytale.server.core.plugin.JavaPlugin";

    public HytalePluginProcessor(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    @Override
    public void processSingle(Element annotated, String className, Map<String, SourceFileWriter> writers, SharedContext sharedContext) {
        var hytalePluginAnnotation = annotated.getAnnotation(HytalePlugin.class);
        assert hytalePluginAnnotation != null;

        if (!isPlugin(annotated)) {
            processingEnv.getMessager().printMessage(javax.tools.Diagnostic.Kind.ERROR, "Class " + className + " is not a JavaPlugin");
            return;
        }

        var version = hytalePluginAnnotation.version();
        if (version.equals("GRADLE_PROJECT_VERSION")) {
            version = processingEnv.getOptions()
                    .get("projectVersion");

            if (version == null) {
                processingEnv.getMessager().printMessage(javax.tools.Diagnostic.Kind.ERROR, "Could not find project version, please set the compiler option -AprojectVersion=VERSION");
                return;
            }
        }

        var manifest = PluginManifestConverter.fromAnnotation(hytalePluginAnnotation, version, className);
        sharedContext.pluginManifest().setValue(manifest);
    }

    private boolean isPlugin(Element annotated) {
        var javaPlugin = processingEnv.getElementUtils().getTypeElement(JAVA_PLUGIN_CLASS_NAME).asType();

        return processingEnv.getTypeUtils().isSubtype(annotated.asType(), javaPlugin);
    }

}
