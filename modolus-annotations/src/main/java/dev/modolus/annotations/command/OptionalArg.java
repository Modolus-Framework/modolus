package dev.modolus.annotations.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface OptionalArg {

    Class<?> type();

    String argTypeName();

    boolean ignore() default false;

}
