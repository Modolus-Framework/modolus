package dev.modolus.annotations.plugin;

import org.intellij.lang.annotations.Pattern;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface PluginDependency {

    String name();

    @Pattern(Patterns.SEMVER_REGEX)
    String version();

}
