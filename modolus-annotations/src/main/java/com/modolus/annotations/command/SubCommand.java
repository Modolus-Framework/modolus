package com.modolus.annotations.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface SubCommand {

    Class<?> value();

    String singletonIdentifier() default "";

}
