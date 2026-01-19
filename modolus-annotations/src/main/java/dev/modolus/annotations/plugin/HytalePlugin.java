package dev.modolus.annotations.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface HytalePlugin {

    String group();

    String name();

    String version() default "GRADLE_PROJECT_VERSION";

    String description();

    PluginAuthor[] authors();

    String website();

    String serverVersion() default "*";

    PluginDependency[] dependencies() default {};

    PluginDependency[] optionalDependencies() default {};

    PluginDependency[] loadBefore() default {};

    boolean disabledByDefault() default false;

    boolean includesAssetPack() default false;

}
