package com.modolus.annotations.plugin;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface PluginAuthor {

    String name();

    String email();

    String url();

}
