package com.modolus.command.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface OptionalArg {

    Class<?> type();

    String argTypeName();

    boolean ignore() default false;

}
