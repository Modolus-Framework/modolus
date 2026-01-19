package dev.modolus.annotations.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface FlagArg {

    boolean ignore() default false;

}
