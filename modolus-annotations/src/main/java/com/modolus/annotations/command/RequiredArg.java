package com.modolus.annotations.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface RequiredArg {

    Class<?> type();

    String argTypeName();

    boolean ignore() default false;

}
