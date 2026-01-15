package com.modolus.command.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface DefaultArg {

    Class<?> type();

    String argTypeName();

    String defaultValue();

    String defaultValueDescription();

    boolean ignore() default false;

}
